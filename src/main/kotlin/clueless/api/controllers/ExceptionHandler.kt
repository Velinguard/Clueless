package clueless.api.controllers

import org.hyperledger.indy.sdk.wallet.WalletAccessFailedException
import org.hyperledger.indy.sdk.wallet.WalletAlreadyOpenedException
import org.hyperledger.indy.sdk.wallet.WalletItemAlreadyExistsException
import org.hyperledger.indy.sdk.wallet.WalletNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class ExceptionHandler {
    fun handleWalletException(e: Exception, action: String): ResponseEntity<String?> {
        return when (e) {
            is WalletNotFoundException -> ResponseEntity.notFound().build()
            is WalletAccessFailedException -> ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect ID-Key pairing")
            is WalletAlreadyOpenedException -> ResponseEntity.status(HttpStatus.CONFLICT).body("This user is already performing an operation")
            is WalletItemAlreadyExistsException -> ResponseEntity.status(HttpStatus.CONFLICT).body("A user with this ID already exists")
            else -> ResponseEntity.unprocessableEntity().body("Error $action")
        }
    }
}