package clueless.api.controllers

import clueless.api.filesystem.FileSystem
import clueless.api.controllers.models.Person
import clueless.api.proofs.Proofs
import clueless.api.prover.ProverWrapper
import clueless.api.wallet.WalletWrapper
import com.google.gson.annotations.SerializedName
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception

@RestController("prover")
class ProverController(
        @Autowired var proverWrapper: ProverWrapper,
        @Autowired var issuerController: IssuerController,
        @Autowired var proofController: ProofController,
        @Autowired var walletWrapper: WalletWrapper,
        @Autowired var fileSystem: FileSystem,
        @Autowired var exceptionHandler: ExceptionHandler
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(ProverController::class.java)
    }

    @ApiOperation(value = "Creates a proof request for the prover stored on the file system S3")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The location of the file on the file system, " +
                "for S3 this is in two parts the bucket name and object name; otherwise this value should be the " +
                "object name given to the Verifier."),
        ApiResponse(code = 404, message = "Error prover not found"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @GetMapping("credentials-for-default-proof")
    fun proverGetDefaultCredentials(
            proverDID: String,
            proverWalletKey: String,
            proverWalletID: String,
            masterSecretId: String,
            proof: String
    ): ResponseEntity<String?> {
        LOGGER.info("Generating proof request for $proverWalletID")
        return try {
            val prover = Person(proverDID, walletWrapper, proverWalletID, proverWalletKey)
            try {
                val proofObj = Proofs.values().find { it.proofName == proof }!!

                val credAndSchema = proverWrapper.getCredAndSchemaDef(
                        Anoncreds.proverGetCredentialsForProofReq(
                                prover.indyWallet!!,
                                proofObj.proof.getJsonObject()
                        ).get(),
                        prover
                )

                ResponseEntity.ok(
                        fileSystem.writeToFile(
                                proverDID,
                                proverWrapper.proverGetProofCredentials(
                                        prover,
                                        masterSecretId,
                                        proofObj.proof.getJsonObject(),
                                        proofObj,
                                        credAndSchema.second,
                                        credAndSchema.first
                                )
                        )
                )
            } finally {
                prover.indyWallet!!.closeWallet()
            }
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Creating proof request JSON")
        }
    }

    data class S3Info(
            @SerializedName("bucket_name") val bucketName: String,
            @SerializedName("file_name") val fileName: String
    )

    @Deprecated("No longer part of the pipeline")
    @GetMapping("credentials-for-proof")
    fun proverGetProofCredentials(
            proverDID: String,
            proverWalletKey: String,
            proverWalletID: String,
            masterSecretId: String,
            proofRequestJson: String?,
            credDefId: String,
            schemaId: String
    ): String {
        val proofRequestJson = proofRequestJson ?: proofController.getProofRequest()
        val proofObj = Proofs.values().find { it.proofName == "Person is over the age of 18" }!!

        LOGGER.info("Generating proof request for $proverWalletID")
        val prover = Person(proverDID, walletWrapper, proverWalletID, proverWalletKey)
        issuerController.getCredentialDefinition(prover, credDefId)
        val proof = proverWrapper.proverGetProofCredentials(
                prover,
                masterSecretId,
                proofRequestJson,
                proofObj,
                HashSet(),
                ArrayList()
        )
        LOGGER.info("Proof request generated for $proverWalletID")
        prover.indyWallet!!.closeWallet()
        return proof
    }
}
