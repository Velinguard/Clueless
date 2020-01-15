package clueless.api.hyperledger

import clueless.api.issuer.CredentialRequest
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.ledger.LedgerResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet

interface HyperledgerIssuer {
    fun issuerCreateAndStoreCredentialDef(
            wallet: Wallet,
            issuerDid: String,
            schemaJson: String,
            tag: String,
            signature_type: String?,
            configJson: String
    ): AnoncredsResults.IssuerCreateAndStoreCredentialDefResult?

    fun proverGetCredentials(
            credentialRequest: CredentialRequest,
            credentialResult: AnoncredsResults.IssuerCreateCredentialResult,
            proverWallet: Wallet
    ): String

    fun issuerCreateCredential(
            wallet: Wallet,
            credOfferJson: String,
            credReqJson: String,
            credValuesJson: String,
            revRegId: String?,
            blobStorageReaderHandle: Int
    ): AnoncredsResults.IssuerCreateCredentialResult?

    fun issuerCreateMasterSecret(
            wallet: Wallet,
            masterSecretId: String? = null
    ): String?

    fun issuerCreateCredentialOffer(
            wallet: Wallet,
            credDefId: String
    ): String?

    fun issuerCreateCredentialRequest(
            wallet: Wallet,
            did: String,
            credOffer: String,
            credDefJson: String,
            masterSecretId: String
    ): AnoncredsResults.ProverCreateCredentialRequestResult?

    fun ledgerBuildCredDefRequest(
            issuerDid: String,
            credDefJson: String
    ): String

    fun ledgerSignAndSubmitRequest(
            pool: Pool,
            indyWallet: Wallet,
            issuerDid: String,
            request: String
    ): String

    fun ledgerGetCredDefRequest(
            issuerDid: String,
            credDefId: String
    ): String

    fun ledgerParseRequest(
            response: String
    ): LedgerResults.ParseResponseResult

    fun issuerCreateSchema(defaultStewardDid: String,
                           schemaName: String,
                           schemaVersion: String,
                           schemaAttributes: String
    ): AnoncredsResults.IssuerCreateSchemaResult
}
