package clueless.api.controllers

import clueless.api.filesystem.FileSystem
import clueless.api.controllers.models.Person
import clueless.api.issuer.CredentialDefinition
import clueless.api.proofs.Proofs
import clueless.api.verifier.VerifierWrapper
import clueless.api.wallet.WalletWrapper
import com.amazonaws.AmazonServiceException
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.hyperledger.indy.sdk.IndyException
import org.json.JSONException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.stream.Collectors

@RestController("verifier")
class VerifierController(
        @Autowired var verifierWrapper: VerifierWrapper,
        @Autowired var issuerController: IssuerController,
        @Autowired var proofController: ProofController,
        @Autowired var fileSystem: FileSystem,
        @Autowired var walletWrapper: WalletWrapper,
        @Autowired var exceptionHandler: ExceptionHandler
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(VerifierController::class.java)
    }

    @ApiOperation(value = "Verifies a proof for the verifier using a proof stored on the File System")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The status of the verification", response = Boolean::class),
        ApiResponse(code = 400, message = "File System error"),
        ApiResponse(code = 404, message = "Error objectName-bucketName pairing not found"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @GetMapping("prove-s3")
    fun verifyProofFromS3(
            name: String,
            verifierDid: String,
            verifierWalletKey: String,
            verifierWalletId: String,
            bucketName: String?,
            objectName: String,
            proof: String
    ): ResponseEntity<*> {
        LOGGER.info("Verifying credentials for $name")
        val currProof = Proofs.values().find { it.proofName == proof }!!.proof
        val proofRequestJson = currProof.getJsonObject()
        return try {
            val verifier = Person(verifierDid, walletWrapper, verifierWalletId, verifierWalletKey)
            try {
                val credDefsList = currProof.credDefIds.stream().map {
                    i -> issuerController.getCredentialDefinition(verifier, i)
                }.collect(Collectors.toList())

                val schemaIds = currProof.schemaIds

                // Bucket name is only required for S3 Implementations
                val proofJson = bucketName
                        ?.let { fileSystem.readFromFile(it, objectName) }
                        ?: fileSystem.readFromFile(objectName = objectName)

                ResponseEntity.ok(
                        verifierWrapper.verifyProof(
                                name,
                                proofJson,
                                proofRequestJson,
                                verifier,
                                schemaIds,
                                credDefsList
                        )
                )
            } catch (e: AmazonServiceException) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found on AWS")
            } catch (e: IllegalArgumentException) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No bucket name supplied for AWS file system")
            } catch (e: IndyException) {
                ResponseEntity.badRequest().body("Unable to process proof request")
            } catch (e: FileNotFoundException) {
                ResponseEntity.badRequest().body("File at $objectName does not exist")
            } finally {
                walletWrapper.closeWallet(verifier.indyWallet!!)
            }
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "verifying proof")
        }
    }


    @ApiOperation(value = "Verifies a proof for the verifier")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The status of the verification", response = Boolean::class),
        ApiResponse(code = 400, message = "Error processing proof"),
        ApiResponse(code = 404, message = "Error object not found"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @GetMapping("prove")
    fun verifyProof(
            name: String,
            proofJson: String,
            proofRequestJson: String = proofController.getProofRequest(),
            credentialDef: CredentialDefinition? = null,
            verifierDid: String,
            verifierWalletKey: String,
            verifierWalletId: String,
            credDefId: String,
            schemaId: String
    ): ResponseEntity<*> {
        LOGGER.info("Verifying credentials for $name")
        return try {
            val verifier = Person(verifierDid, walletWrapper, verifierWalletId, verifierWalletKey)
            try {
                ResponseEntity.ok(verifierWrapper.verifyProof(
                        name,
                        proofJson,
                        proofRequestJson,
                        verifier,
                        ArrayList(),
                        ArrayList()
                ))
            } catch (e: JSONException) {
                ResponseEntity.badRequest().body("Invalid Proof")
            } catch (e: IndyException) {
                ResponseEntity.badRequest().body("Unable to process proof request")
            } finally {
                verifier.indyWallet!!.closeWallet()
            }
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "verifying proof")
        }

    }

}