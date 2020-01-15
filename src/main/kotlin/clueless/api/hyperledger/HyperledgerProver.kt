package clueless.api.hyperledger

import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.wallet.Wallet
import java.util.concurrent.CompletableFuture

interface HyperledgerProver {

    fun fetchCredentials(
            credentialsSearch: CredentialsSearchForProofReq,
            itemRef: String,
            count: Int
    ): String

    fun getCredentialsSearch(
            proverWallet: Wallet,
            proofRequestJson: String
    ): CredentialsSearchForProofReq

    fun close(
            credentialsSearch: CredentialsSearchForProofReq
    )

    fun getSchemas(): String

    fun createProofJson(
            proverWallet: Wallet,
            proofRequestJson: String,
            requestedCredentialsJson: String,
            masterSecretId: String,
            schemas: String,
            credentialDefs: String,
            revocStates: String
    ): String
}