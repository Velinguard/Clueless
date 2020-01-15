package clueless.api.issuer

import clueless.api.controllers.IssuerController
import clueless.api.controllers.models.Person
import clueless.api.licences.DrivingLicence
import clueless.api.hyperledger.HyperledgerIssuer
import clueless.api.hyperledger.HyperledgerLedger
import clueless.api.hyperledger.HyperledgerWallet
import clueless.api.wallet.WalletWrapper
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.ledger.LedgerResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet
import org.mockito.Mockito.*
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class IssuerWrapperTest {
    private var issuer = mock(HyperledgerIssuer::class.java)
    private var wallet = mock(HyperledgerWallet::class.java)
    private var ledger = mock(HyperledgerLedger::class.java)
    private var schema = mock(AnoncredsResults.IssuerCreateSchemaResult::class.java)
    private var walletMockito: Wallet = mock(Wallet::class.java)
    private var poolMockito: Pool = mock(Pool::class.java)
    private var parseResponseResult = mock(LedgerResults.ParseResponseResult::class.java)
    private var anonCreds = mock(AnoncredsResults.IssuerCreateAndStoreCredentialDefResult::class.java)
    private var anonCredsRequest = mock(AnoncredsResults.ProverCreateCredentialRequestResult::class.java)
    private var anonCredResult = mock(AnoncredsResults.IssuerCreateCredentialResult::class.java)


    private lateinit var api: IssuerWrapper
    private lateinit var issuerPerson : Person
    private lateinit var proverPerson: Person
    private lateinit var walletWrapper: WalletWrapper

    private lateinit var drivingLicence: DrivingLicence

    @BeforeTest
    fun setupApi() {
        walletWrapper = WalletWrapper(wallet)

        issuerPerson = Person("issuer-did", walletMockito, "issuer-secret", "issuer-name")
        proverPerson = Person("prover-did", walletMockito, "prover-secret", "prover-name")
        api = IssuerWrapper(schema, walletWrapper, issuer,  ledger, poolMockito)
        drivingLicence = DrivingLicence("test-name", LocalDate.of(1999, 6, 16), 1)


    }

    @Test
    fun `testIssuer Can Create Credentials`() {
        val credDefId = "credDefId"
        val getCredDefRequest = "getCredDefRequest"
        val retrieveResponse = "retrieveResponse"
        val parseResponseResultId = "parseResponseResultId"
        val parseResponseResultObjectJson = "parseResponseResultObjectJson"
        val credOffer = "credOffer"
        val proverGetCredentials = "proverGetCredentials"
        val credentialRequestJson = "credentialRequestJson"


        `when`(
                ledger.buildGetCredDefRequest(issuerPerson.personDid, credDefId)
        ).thenReturn(getCredDefRequest)

        `when`(
                ledger.signAndSubmitRequest(poolMockito, issuerPerson.indyWallet!!, issuerPerson.personDid, getCredDefRequest)
        ).thenReturn(retrieveResponse)

        `when`(
                ledger.parseGetCredDefResponse(retrieveResponse)
        ).thenReturn(parseResponseResult)

        `when`(
                parseResponseResult.id
        ).thenReturn(parseResponseResultId)

        `when`(
                parseResponseResult.objectJson
        ).thenReturn(parseResponseResultObjectJson)

        val credentialDefinition = CredentialDefinition(parseResponseResultId, parseResponseResultObjectJson)

        `when`(
                issuer.issuerCreateCredentialOffer(
                        issuerPerson.indyWallet!!,
                        credentialDefinition.credDefId
                )
        ).thenReturn(credOffer)

        `when`(
                issuer.issuerCreateMasterSecret(proverPerson.indyWallet!!)
        ).thenReturn(proverPerson.masterSecretId)



        `when`(
                issuer.issuerCreateCredentialRequest(
                        proverPerson.indyWallet!!,
                        proverPerson.personDid,
                        credOffer,
                        credentialDefinition.credDefJson,
                        proverPerson.masterSecretId!!
                        )
        ).thenReturn(anonCredsRequest)

        `when`(
                anonCredsRequest.credentialRequestJson
        ).thenReturn(credentialRequestJson)









        val proversCredentialRequest = CredentialRequest(credOffer, credentialDefinition, proverPerson.masterSecretId!!, anonCredsRequest)


        `when`(
                issuer.issuerCreateCredential(
                        issuerPerson.indyWallet!!,
                        credOffer,
                        credentialRequestJson,
                        drivingLicence.getCredentials(),
                        null,
                        -1)
        ).thenReturn(anonCredResult)


        `when`(
                issuer.proverGetCredentials(proversCredentialRequest, anonCredResult, proverPerson.indyWallet!!)
        ).thenReturn(proverGetCredentials)



        `when`(
                wallet.openWallet(WalletWrapper.WalletConfig(id = "issuer-wallet-id"),
                        WalletWrapper.WalletCredentials(key = "issuer-wallet-key"))
        ).thenReturn(walletMockito)

        `when`(
                wallet.openWallet(WalletWrapper.WalletConfig(id = "prover-wallet-id"),
                        WalletWrapper.WalletCredentials(key = "prover-wallet-key"))
        ).thenReturn(walletMockito)

        val emailInfo = api.issuerCreateCredentials(
                "issuer-wallet-id",
                "issuer-wallet-key",
                "prover-wallet-id",
                "prover-wallet-key",
                proverPerson.personDid,
                issuerPerson.personDid,
                credDefId,
                drivingLicence
                )



        val targetEmailInfo = IssuerController.EmailInfo(proverPerson.personDid, proversCredentialRequest.masterSecretId)

        assertEquals(emailInfo, targetEmailInfo)

    }
}