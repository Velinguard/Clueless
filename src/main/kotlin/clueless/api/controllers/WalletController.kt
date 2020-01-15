package clueless.api.controllers

import clueless.api.controllers.models.Person
import clueless.api.wallet.WalletWrapper
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.wallet.Wallet
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*
import org.springframework.http.ResponseEntity as ResponseEntity

@RestController("wallet")
class WalletController(
        @Autowired val walletWrapper: WalletWrapper,
        @Autowired val exceptionHandler: ExceptionHandler
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(WalletController::class.java)
    }

    /**
     * id and key need to be specified as query parameters here. Nothing should be returned as it is a PUT request.
     */

    @ApiOperation(value = "Creates a new wallet inside the distributed ledger")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The status of the creation of the wallet"),
        ApiResponse(code = 400, message = "Error creating wallet"),
        ApiResponse(code = 409, message = "Error user already exists"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-wallet")
    fun createWallet(
            id: String,
            key: String
    ): ResponseEntity<String?> {
        return try {
            walletWrapper.createWallet(id, key)
            ResponseEntity.ok().body("Wallet created")
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "creating wallet")
        }
    }

    @ApiOperation(value = "Creates a new wallet inside the distributed ledger using a specific DiD")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The status of the creation of the wallet"),
        ApiResponse(code = 400, message = "Error creating wallet"),
        ApiResponse(code = 409, message = "Error user already exists"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-wallet-with-did")
    fun createWalletWithDid(
            id: String,
            key: String
    ): ResponseEntity<String?> {
        return try {
            walletWrapper.createWallet(id, key)
            val wallet = walletWrapper.getWallet(id, key)
            val did = Did.createAndStoreMyDid(wallet, "{}").get().did
            wallet!!.close()
            ResponseEntity.status(HttpStatus.CREATED).body(did)
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "creating user with DiD")
        }
    }

    /**
     * The 'wallet' parameter needs to be in the form of a JSON object, and sent in the body of the request.
     * It does not need to return anything to the client.
     */
    @ApiOperation(value = "Closes a wallet inside the distributed ledger, " +
            "this must be done before another operation can be complete")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The status of the closing of the wallet"),
        ApiResponse(code = 400, message = "Error closing wallet"),
        ApiResponse(code = 403, message = "Error incorrect wallet id/key combination"),
        ApiResponse(code = 404, message = "Error the wallet does not exist"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @DeleteMapping("close-wallet")
    fun closeWallet(
            wallet: Wallet
    ): ResponseEntity<String?> {
        return try {
            walletWrapper.closeWallet(wallet)
            ResponseEntity.ok("")
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "closing wallet")
        }
    }

    /**
     * The id and key need to be sent as query parameters in the request.
     * It does not need to return anything to the client.
     */
    @ApiOperation(value = "Deletes a wallet inside the distributed ledger")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The status of the deletion of the wallet"),
        ApiResponse(code = 400, message = "Error closing wallet"),
        ApiResponse(code = 403, message = "Error incorrect wallet id/key combination"),
        ApiResponse(code = 404, message = "Error the wallet does not exist"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @DeleteMapping("delete-wallet")
    fun deleteWallet(
            id: String,
            key: String
    ): ResponseEntity<String?> {
        return try {
            walletWrapper.deleteWallet(id, key)
            ResponseEntity.ok("")
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "deleting wallet")
        }
    }

    @ApiOperation(value = "Logs into a wallet inside the distributed ledger")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The status of logging into a wallet"),
        ApiResponse(code = 400, message = "Error logging into wallet"),
        ApiResponse(code = 403, message = "Error incorrect wallet id/key combination"),
        ApiResponse(code = 404, message = "Error the wallet does not exist"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @GetMapping("login")
    fun login(
            id: String,
            key: String,
            did: String,
            masterDid: String = ""
    ): ResponseEntity<*> {
        return try {
            LOGGER.info("Logging in $id.")
            val person = Person(did, walletWrapper.getWallet(id, key)!!, masterDid)
            LOGGER.info("User $id successfully logged in.")
            person.indyWallet!!.closeWallet()
            ResponseEntity.ok(person)
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "logging into wallet")
        }
    }
}