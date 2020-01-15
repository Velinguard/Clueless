package clueless.api.wallet

import clueless.api.controllers.WalletController
import clueless.api.hyperledger.HyperledgerWallet
import com.fasterxml.jackson.annotation.JsonProperty
import org.hyperledger.indy.sdk.wallet.Wallet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class WalletWrapper(
        val Wallet: HyperledgerWallet
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(WalletController::class.java)
    }

    fun createWallet(
            id: String,
            key: String
    ) {
        LOGGER.info("Create new wallet for $id")
        Wallet.createWallet(
                WalletConfig(id = id),
                WalletCredentials(key = key)
        )
    }

    fun getWallet(
            id: String,
            key: String
    ): Wallet? {
        LOGGER.info("Getting wallet for $id")

        return Wallet.openWallet(
                WalletConfig(id = id),
                WalletCredentials(key = key)
        )!!
    }

    fun closeWallet(
            wallet: Wallet
    ) {
        LOGGER.info("Closing a wallet")
        wallet.closeWallet().get()
    }

    fun deleteWallet(
            id: String,
            key: String
    ) {
        LOGGER.info("Deleting wallet for $id")

        Wallet.deleteWallet(
                WalletConfig(id = id),
                WalletCredentials(key = key)
        );
    }

    data class WalletConfig(
            val id: String = "myWallet",
            @JsonProperty("storage_type") val storageType: String = "Default"
    )

    data class WalletCredentials(
            val key: String = "issuer_wallet_key"
    )
}