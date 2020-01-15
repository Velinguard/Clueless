package clueless.api.hyperledger

import clueless.api.issuer.CredentialRequest
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.ledger.Ledger
import org.hyperledger.indy.sdk.ledger.LedgerResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

@Component
class HyperledgerIssuerImpl : HyperledgerIssuer {
    override fun ledgerBuildCredDefRequest(issuerDid: String, credDefJson: String): String {
        try {
            return Ledger.buildCredDefRequest(issuerDid, credDefJson).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun ledgerSignAndSubmitRequest(pool: Pool, indyWallet: Wallet, issuerDid: String, request: String): String {
        try {
            return Ledger.signAndSubmitRequest(pool, indyWallet, issuerDid, request).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun ledgerGetCredDefRequest(issuerDid: String, credDefId: String): String {
        try {
            return Ledger.buildGetCredDefRequest(issuerDid, credDefId).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun ledgerParseRequest(response: String): LedgerResults.ParseResponseResult {
        try {
            return Ledger.parseGetCredDefResponse(response).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun issuerCreateSchema(defaultStewardDid: String, schemaName: String, schemaVersion: String, schemaAttributes: String): AnoncredsResults.IssuerCreateSchemaResult {
        try {
            return Anoncreds.issuerCreateSchema(defaultStewardDid, schemaName, schemaVersion, schemaAttributes).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun issuerCreateCredentialOffer(wallet: Wallet, credDefId: String): String? {
        try {
            return Anoncreds.issuerCreateCredentialOffer(wallet, credDefId).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun issuerCreateCredentialRequest(
            wallet: Wallet,
            did: String,
            credOffer: String,
            credDefJson: String,
            masterSecretId: String
    ): AnoncredsResults.ProverCreateCredentialRequestResult? {
        try {
            return Anoncreds.proverCreateCredentialReq(
                    wallet,
                    did,
                    credOffer,
                    credDefJson,
                    masterSecretId
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun issuerCreateMasterSecret(wallet: Wallet, masterSecretId: String?): String? {
        try {
            return Anoncreds.proverCreateMasterSecret(wallet, masterSecretId).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun proverGetCredentials(
            credentialRequest: CredentialRequest,
            credentialResult: AnoncredsResults.IssuerCreateCredentialResult,
            proverWallet: Wallet
    ): String {
        try {
            return Anoncreds.proverStoreCredential(
                    proverWallet,
                    null,
                    credentialRequest.credRequest!!.credentialRequestMetadataJson,
                    credentialResult.credentialJson,
                    credentialRequest.credentials.credDefJson,
                    null
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun issuerCreateCredential(
            wallet: Wallet,
            credOfferJson: String,
            credReqJson: String,
            credValuesJson: String,
            revRegId: String?,
            blobStorageReaderHandle: Int
    ): AnoncredsResults.IssuerCreateCredentialResult? {
        try {
            return Anoncreds.issuerCreateCredential(
                    wallet,
                    credOfferJson,
                    credReqJson,
                    credValuesJson,
                    revRegId,
                    blobStorageReaderHandle
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun issuerCreateAndStoreCredentialDef(
            wallet: Wallet,
            issuerDid: String,
            schemaJson: String,
            tag: String,
            signature_type: String?,
            configJson: String
    ): AnoncredsResults.IssuerCreateAndStoreCredentialDefResult? {
        try {
            return Anoncreds.issuerCreateAndStoreCredentialDef(
                    wallet,
                    issuerDid,
                    schemaJson,
                    tag,
                    signature_type,
                    configJson
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }
}
