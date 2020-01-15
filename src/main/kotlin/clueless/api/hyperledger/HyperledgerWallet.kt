package clueless.api.hyperledger

import clueless.api.wallet.WalletWrapper
import org.hyperledger.indy.sdk.wallet.Wallet

interface HyperledgerWallet {
    fun createWallet(walletConfig: WalletWrapper.WalletConfig, walletCredentials: WalletWrapper.WalletCredentials)
    fun openWallet(walletConfig: WalletWrapper.WalletConfig, walletCredentials: WalletWrapper.WalletCredentials): Wallet?
    fun deleteWallet(walletConfig: WalletWrapper.WalletConfig, walletCredentials: WalletWrapper.WalletCredentials)
}