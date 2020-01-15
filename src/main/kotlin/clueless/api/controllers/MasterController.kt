package clueless.api.controllers

import clueless.api.controllers.models.Person
import clueless.api.licences.Licences
import clueless.api.master.MasterWrapper
import clueless.api.wallet.WalletWrapper
import io.swagger.annotations.ApiKeyAuthDefinition
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController("master")
class MasterController(
        @Autowired var walletWrapper: WalletWrapper,
        @Autowired var masterWrapper: MasterWrapper,
        @Autowired var exceptionHandler: ExceptionHandler
) {
    /**
     * Create a credential definition for the issuer so that they can reference the schema
     * and be able to issue credentials for that scheme. This function should only need to be
     * called once
     */
    @ApiOperation(value = "Create a credential definition for the issuer to reference the schema")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "Credential Definition ID for the given issuer and schema"),
        ApiResponse(code = 404, message = "Error user does not exist"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-credential-definition")
    fun createCredentialDefinition(
            walletId: String,
            walletKey: String,
            personDid: String,
            schemaId: String
    ): ResponseEntity<String?> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED).body(
                    masterWrapper.createCredentialDefinition(
                            Person(
                                    personDid,
                                    walletWrapper,
                                    walletId,
                                    walletKey
                            ),
                            schemaId
                    ))
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Creating a new Issuer")
        }
    }

    @ApiOperation(value = "Creates a new credential schema for the Issuer")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "Credential Schema ID for the given licence"),
        ApiResponse(code = 404, message = "Error user does not exist"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-credential-schema")
    fun createCredSchema(
            defaultStewardDid: String?,
            walletId: String,
            walletKey: String,
            licence: Licences
    ): ResponseEntity<String?> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED).body(
                    masterWrapper.createCredSchema(
                            defaultStewardDid,
                            walletId,
                            walletKey,
                            licence.schema,
                            "1.0",
                            licence.type
                    )
            )
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Creating a new Issuer")
        }
    }

    @ApiOperation(value = "Creates a new issuer for the given ID and Key pair")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "DiD for the issuer with the given ID, key pairing"),
        ApiResponse(code = 409, message = "Error Issuer already exists"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-issuer")
    fun createIssuer(
            walletId: String,
            walletKey: String
    ): ResponseEntity<String?> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED).body(masterWrapper.createIssuer(walletId, walletKey))
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Creating a new Issuer")
        }
    }
}