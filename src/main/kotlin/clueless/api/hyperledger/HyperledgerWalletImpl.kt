package clueless.api.hyperledger

import clueless.api.wallet.WalletWrapper
import com.google.gson.Gson
import org.hyperledger.indy.sdk.wallet.Wallet
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutionException

@Component
class HyperledgerWalletImpl : HyperledgerWallet {
    override fun deleteWallet(walletConfig: WalletWrapper.WalletConfig, walletCredentials: WalletWrapper.WalletCredentials) {
        try {
            Wallet.deleteWallet(
                    Gson().toJson(walletConfig),
                    Gson().toJson(walletCredentials)
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun openWallet(walletConfig: WalletWrapper.WalletConfig, walletCredentials: WalletWrapper.WalletCredentials): Wallet? {
        try {
            return Wallet.openWallet(
                    Gson().toJson(walletConfig),
                    Gson().toJson(walletCredentials)
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun createWallet(
            walletConfig: WalletWrapper.WalletConfig,
            walletCredentials: WalletWrapper.WalletCredentials
    ) {
        try {
            Wallet.createWallet(
                    Gson().toJson(walletConfig),
                    Gson().toJson(walletCredentials)
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }
}