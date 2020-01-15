package clueless.api.hyperledger

import org.hyperledger.indy.sdk.did.DidResults
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.ledger.LedgerResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet

interface HyperledgerLedger {

    fun buildGetSchemaRequest(
            personDid: String,
            schemaId: String
    ): String

    fun signAndSubmitRequest(
            pool: Pool,
            indyWallet: Wallet,
            personDid: String,
            getSchemaRequest: String
    ): String

    fun parseGetSchemaResponse(
            getSchemaResponse: String
    ): LedgerResults.ParseResponseResult

    fun buildCredDefRequest(
            personDid: String,
            credDefJson: String
    ): String

    fun buildGetCredDefRequest(personDid: String,
                               credDefId: String
    ): String

    fun parseGetCredDefResponse(retrieveResponse: String
    ): LedgerResults.ParseResponseResult

    fun buildSchemaRequest(defaultStewardDid: String,
                           schema: String
    ): String

    fun buildNymRequest(defaultStewardDid: String,
                        trustAnchorDID: String,
                        trustAnchorVerkey: String,
                        alias: String?,
                        role: String
    ): String

    fun createAndStoreMyDid(myWallet: Wallet,
                            didJson: String
    ):  DidResults.CreateAndStoreMyDidResult


}