package clueless.api.hyperledger

import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.did.DidResults
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.ledger.LedgerResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutionException

@Component
class HyperledgerLedgerImpl : HyperledgerLedger {

    override fun buildGetSchemaRequest(
            personDid: String,
            schemaId: String
    ): String {
        try {
            return Ledger.buildGetSchemaRequest(personDid, schemaId).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun signAndSubmitRequest(
            pool: Pool,
            indyWallet: Wallet,
            personDid: String,
            getSchemaRequest: String
    ): String {
        try {
            return Ledger.signAndSubmitRequest(pool, indyWallet, personDid, getSchemaRequest).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun parseGetSchemaResponse(
            getSchemaResponse: String
    ): LedgerResults.ParseResponseResult {
        try {
            return Ledger.parseGetSchemaResponse(getSchemaResponse).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun buildCredDefRequest(
            personDid: String,
            credDefJson: String
    ): String {
        try {
            return Ledger.buildCredDefRequest(personDid, credDefJson).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun buildGetCredDefRequest(
            personDid: String,
            credDefId: String
    ): String {
        try {
            return Ledger.buildGetCredDefRequest(personDid, credDefId).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun parseGetCredDefResponse(
            retrieveResponse: String
    ): LedgerResults.ParseResponseResult {
        try {
            return Ledger.parseGetCredDefResponse(retrieveResponse).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun buildSchemaRequest(defaultStewardDid: String, schema: String): String {
        try {
            return Ledger.buildSchemaRequest(defaultStewardDid, schema).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun buildNymRequest(defaultStewardDid: String, trustAnchorDID: String, trustAnchorVerkey: String, alias: String?, role: String): String {
        try {
            return Ledger.buildNymRequest(defaultStewardDid, trustAnchorDID, trustAnchorVerkey, alias, role).get()
        }   catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun createAndStoreMyDid(myWallet: Wallet, didJson: String): DidResults.CreateAndStoreMyDidResult {
        try {
            return Did.createAndStoreMyDid(myWallet, didJson).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }
}