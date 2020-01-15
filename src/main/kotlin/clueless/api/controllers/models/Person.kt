package clueless.api.controllers.models

import clueless.api.wallet.WalletWrapper
import org.hyperledger.indy.sdk.wallet.Wallet


class Person(
        var personDid: String,
        var indyWallet: Wallet? = null,
        var masterSecretId: String? = null,
        var name: String? = null
) {
    constructor(personDid: String, walletWrapper: WalletWrapper, walletId: String, walletKey: String) :
            this(personDid) {
        this.name = walletId
        this.indyWallet = walletWrapper.getWallet(walletId, walletKey)
    }
}