package clueless.api.issuer

import clueless.api.controllers.IssuerController
import clueless.api.controllers.models.Person
import clueless.api.licences.DrivingLicence
import clueless.api.hyperledger.HyperledgerIssuer
import clueless.api.hyperledger.HyperledgerLedger
import clueless.api.licences.Licence
import clueless.api.licences.Ticket
import clueless.api.wallet.WalletWrapper
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class IssuerWrapper(
        val schema: AnoncredsResults.IssuerCreateSchemaResult,
        val walletWrapper: WalletWrapper,
        val hyperledgerIssuer: HyperledgerIssuer,
        val hyperledgerLedger: HyperledgerLedger,
        val pool: Pool
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(IssuerWrapper::class.java)
    }

    fun getCredentialDefinition(
            issuer: Person,
            credDefId: String
    ): CredentialDefinition {
        val getCredDefRequest = hyperledgerLedger.buildGetCredDefRequest(
                issuer.personDid,
                credDefId
        )
        val retrieveResponse = hyperledgerLedger.signAndSubmitRequest(
                pool,
                issuer.indyWallet!!,
                issuer.personDid,
                getCredDefRequest
        )
        val parsedResponse = hyperledgerLedger.parseGetCredDefResponse(retrieveResponse)
        return CredentialDefinition(parsedResponse.id, parsedResponse.objectJson)
    }

    fun createCredentialsFromProverRequest(
            proverCredentialsRequest: CredentialRequest,
            issuerWallet: Wallet,
            credentials: String
    ): AnoncredsResults.IssuerCreateCredentialResult? {
        return hyperledgerIssuer.issuerCreateCredential(
                issuerWallet,
                proverCredentialsRequest.credOffer,
                proverCredentialsRequest.credRequest!!.credentialRequestJson,
                credentials,
                null,
                -1
        )
    }

    private fun proverStoreCredentials(
            credentialRequest: CredentialRequest,
            credentialResult: AnoncredsResults.IssuerCreateCredentialResult,
            proverWallet: Wallet
    ): String {
        return hyperledgerIssuer.proverGetCredentials(credentialRequest, credentialResult, proverWallet)
    }

    fun issuerCreateProver(
            issuerWalletId: String,
            issuerWalletKey: String,
            proverWalletId: String,
            proverWalletKey: String,
            proverDid: String?
    ): String? {
        val issuerWallet = walletWrapper.getWallet(issuerWalletId, issuerWalletKey)
        try {
            walletWrapper.createWallet(proverWalletId, proverWalletKey)
            val proverWallet = walletWrapper.getWallet(proverWalletId, proverWalletKey)
            val did = proverDid ?: Did.createAndStoreMyDid(proverWallet, "{}").get().did
            walletWrapper.closeWallet(proverWallet!!)

            LOGGER.info("Prover DID for $proverWalletId is $proverDid")
            return did
        } catch (e: Exception) {
            LOGGER.error("Error creating prover $proverWalletId")
        } finally {
            walletWrapper.closeWallet(issuerWallet!!)
        }
        return null
    }

    fun issuerCreateCredentials(
            issuerWalletId: String,
            issuerWalletKey: String,
            proverWalletId: String,
            proverWalletKey: String,
            proverDid: String?,
            issuerDid: String?,
            credDefId: String,
            licence: Licence
    ): IssuerController.EmailInfo? {
        val proverDid = proverDid ?: issuerCreateProver(
                issuerWalletId,
                issuerWalletKey,
                proverWalletId,
                proverWalletKey,
                proverDid
        )

        proverDid?.let {
            val prover = Person(it, walletWrapper, proverWalletId, proverWalletKey)
            val issuer = Person(issuerDid ?: "NcYxiDXkpYi6ov5FcYDi1e", walletWrapper, issuerWalletId, issuerWalletKey)
            try {
                val credentialDefinition = getCredentialDefinition(
                        issuer,
                        credDefId
                )
                // About to generate ProversCredentialRequest

                val proverCredentialRequest = generateProversCredentialRequest(
                        prover,
                        issuer,
                        credentialDefinition
                )

                // About to create credentials
                val credentialResult = createCredentialsFromProverRequest(
                        proverCredentialRequest!!,
                        issuer.indyWallet!!,
                        licence.getCredentials()
                )

                // About to store credentials
                proverStoreCredentials(
                        proverCredentialRequest,
                        credentialResult!!,
                        prover.indyWallet!!
                )

                return IssuerController.EmailInfo(
                        personDid = it,
                        masterSecretId = proverCredentialRequest.masterSecretId
                )
            } catch (e: Exception) {
                LOGGER.error(e.message)
            } finally {
                issuer.indyWallet!!.closeWallet()
                prover.indyWallet!!.closeWallet()
            }
        }
        return null
    }

    fun generateProversCredentialRequest(
            prover: Person,
            issuer: Person,
            credentialDefinition: CredentialDefinition
    ): CredentialRequest? {
        return CredentialRequest(
                prover,
                issuer,
                credentialDefinition,
                hyperledgerIssuer
        )
    }
}