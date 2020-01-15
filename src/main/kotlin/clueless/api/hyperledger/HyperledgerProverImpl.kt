package clueless.api.hyperledger

import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

@Component
class HyperledgerProverImpl(
        @Autowired var schema: AnoncredsResults.IssuerCreateSchemaResult
) : HyperledgerProver {
    override fun createProofJson(
            proverWallet: Wallet,
            proofRequestJson: String,
            requestedCredentialsJson: String,
            masterSecretId: String,
            schemas: String,
            credentialDefs: String,
            revocStates: String
    ): String {
        try {
            return Anoncreds.proverCreateProof(
                    proverWallet,
                    proofRequestJson,
                    requestedCredentialsJson,
                    masterSecretId,
                    schemas,
                    credentialDefs,
                    revocStates
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun getCredentialsSearch(
            proverWallet: Wallet,
            proofRequestJson: String
    ): CredentialsSearchForProofReq {
        try {
            return CredentialsSearchForProofReq.open(proverWallet, proofRequestJson, null).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun fetchCredentials(
            credentialsSearch: CredentialsSearchForProofReq,
            itemRef: String,
            count: Int
    ): String {
        try {
            return credentialsSearch.fetchNextCredentials(itemRef, count).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun close(
            credentialsSearch: CredentialsSearchForProofReq
    ) {
        try {
            credentialsSearch.close()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun getSchemas(): String {
        try {
            return JSONObject().put(
                    schema.schemaId,
                    JSONObject(schema.schemaJson)
            ).toString()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }
}