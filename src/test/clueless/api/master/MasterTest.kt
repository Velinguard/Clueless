package clueless.api.master

import clueless.api.controllers.models.Person
import clueless.api.hyperledger.HyperledgerIssuer
import clueless.api.hyperledger.HyperledgerLedger
import clueless.api.hyperledger.HyperledgerWallet
import clueless.api.licences.DrivingLicence
import clueless.api.wallet.WalletWrapper
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.did.DidResults
import org.hyperledger.indy.sdk.ledger.LedgerResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.time.LocalDate
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

class MasterTest {
    private var issuer = Mockito.mock(HyperledgerIssuer::class.java)
    private var wallet = Mockito.mock(HyperledgerWallet::class.java)
    private var ledger = Mockito.mock(HyperledgerLedger::class.java)
    private var schema = Mockito.mock(AnoncredsResults.IssuerCreateSchemaResult::class.java)
    private var walletMockito: Wallet = Mockito.mock(Wallet::class.java)
    private var poolMockito: Pool = Mockito.mock(Pool::class.java)
    private var parseResponseResult = Mockito.mock(LedgerResults.ParseResponseResult::class.java)
    private var anonCreds = Mockito.mock(AnoncredsResults.IssuerCreateAndStoreCredentialDefResult::class.java)
    private var anonCredResult = Mockito.mock(AnoncredsResults.IssuerCreateSchemaResult::class.java)
    private var parseSchemaResult = Mockito.mock(LedgerResults.ParseResponseResult::class.java)
    private var issuerCreateAndStoreCredDefResult = Mockito.mock(AnoncredsResults.IssuerCreateAndStoreCredentialDefResult::class.java)
    private var stewardDidResults = Mockito.mock(DidResults.CreateAndStoreMyDidResult::class.java)
    private var trustAnchorDidResults = Mockito.mock(DidResults.CreateAndStoreMyDidResult::class.java)


    private lateinit var api: MasterWrapper
    private lateinit var issuerPerson : Person
    private lateinit var proverPerson: Person
    private lateinit var masterPerson: Person

    private lateinit var walletWrapper: WalletWrapper

    private lateinit var drivingLicence: DrivingLicence

    @BeforeTest
    fun setupApi() {
        walletWrapper = WalletWrapper(wallet)

        issuerPerson = Person("issuer-did", walletMockito, "issuer-secret", "issuer-name")
        proverPerson = Person("prover-did", walletMockito, "prover-secret", "prover-name")
        masterPerson = Person("master-did", walletMockito, "master-secret", "master-name")
        api = MasterWrapper(issuer, ledger, walletWrapper, poolMockito)
        drivingLicence = DrivingLicence("test-name", LocalDate.of(1999, 6, 16), 1)

    }

    @Test
    fun `test Master can create a Credential Schema`() {
        val walletId = "issuer-wallet-id"
        val walletKey = "issuer-wallet-key"
        val schemaName = "schemaName"
        val schemaVersion = "schemaVersion"
        val schemaJSON = "schemaJSON"
        val schemaId = "schemaID"
        val schemaRequest = "schemaRequest"
        val getSchemaRequest = "getSchemaRequest"
        val getSchemaResponse = "getSchemaResponse"
        val parseSchemaResultId = "parseSchemaResultId"



        `when`(
                wallet.openWallet(WalletWrapper.WalletConfig(id = walletId),
                        WalletWrapper.WalletCredentials(key = walletKey))
        ).thenReturn(walletMockito)

        `when`(
                issuer.issuerCreateSchema(issuerPerson.personDid, schemaName, schemaVersion, drivingLicence.getSchemaJSON().toString())
        ).thenReturn(anonCredResult)

        `when`(
                anonCredResult.schemaJson
        ).thenReturn(schemaJSON)

        `when`(
                anonCredResult.schemaId
        ).thenReturn(schemaId)

        `when`(
                ledger.buildSchemaRequest(issuerPerson.personDid, schemaJSON)
        ).thenReturn(schemaRequest)

        `when`(
                ledger.buildGetSchemaRequest(issuerPerson.personDid, schemaId)
        ).thenReturn(getSchemaRequest)

        `when`(
                ledger.signAndSubmitRequest(poolMockito, walletMockito, issuerPerson.personDid, getSchemaRequest)
        ).thenReturn(getSchemaResponse)

        `when`(
                ledger.parseGetSchemaResponse(getSchemaResponse)
        ).thenReturn(parseSchemaResult)

        `when`(
                parseSchemaResult.id
        ).thenReturn(parseSchemaResultId)


        `when`(
                walletMockito.closeWallet()
        ).thenReturn(CompletableFuture())


        val actualId = api.createCredSchema(issuerPerson.personDid, walletId, walletKey, schemaName, schemaVersion, drivingLicence)

        assertEquals(actualId, parseSchemaResultId)


    }

    @Test
    fun `testMaster can get Cred Schema`() {
        val schemaId = "schemaID"
        val getSchemaRequest = "getSchemaRequest"
        val getSchemaResponse = "getSchemaResponse"
        val actualSchemaJSONObject = JSONObject()
        val expectedSchemaJSONObject = JSONObject()
        val objectJSON = "{objectJSON:objectJSON}"

        `when`(
                ledger.buildGetSchemaRequest(issuerPerson.personDid, schemaId)
        ).thenReturn(getSchemaRequest)

        `when`(
                ledger.signAndSubmitRequest(poolMockito, walletMockito, issuerPerson.personDid, getSchemaRequest)
        ).thenReturn(getSchemaResponse)

        `when`(
                ledger.parseGetSchemaResponse(getSchemaResponse)
        ).thenReturn(parseSchemaResult)

        `when`(
                parseSchemaResult.objectJson
        ).thenReturn(objectJSON)

        val actualSchemaJson = api.getCredSchema(issuerPerson.personDid, schemaId, walletMockito, actualSchemaJSONObject)

        val expectedSchemaJson = expectedSchemaJSONObject.put(schemaId, JSONObject(objectJSON))

        assertEquals(actualSchemaJson.toString(), expectedSchemaJson.toString())



    }

    @Test
    fun `test Master can create Credential Definition`() {
        val walletId = "issuer-wallet-id"
        val walletKey = "issuer-wallet-key"
        val schemaId = "schemaID"
        val getSchemaRequest = "getSchemaRequest"
        val getSchemaResponse = "getSchemaResponse"
        val parseSchemaResultObjectJson = "parseSchemaResultObjectJson"
        val credDefJSON = "credDefJSON"
        val credDefId = "credDefId"
        val credDefRequest = "credDefRequest"
        val credDefResponse = "credDefResponse"


        `when`(
                wallet.openWallet(WalletWrapper.WalletConfig(id = walletId),
                        WalletWrapper.WalletCredentials(key = walletKey))
        ).thenReturn(walletMockito)


        `when`(
                ledger.buildGetSchemaRequest(issuerPerson.personDid, schemaId)
        ).thenReturn(getSchemaRequest)

        `when`(
                ledger.signAndSubmitRequest(poolMockito, walletMockito, issuerPerson.personDid, getSchemaRequest)
        ).thenReturn(getSchemaResponse)

        `when`(
                ledger.parseGetSchemaResponse(getSchemaResponse)
        ).thenReturn(parseSchemaResult)

        `when`(
                parseSchemaResult.objectJson
        ).thenReturn(parseSchemaResultObjectJson)


        `when`(
                issuer.issuerCreateAndStoreCredentialDef(
                        walletMockito,
                        issuerPerson.personDid,
                        parseSchemaResultObjectJson,
                        "Tag1",
                        null,
                        JSONObject().put("support_revocation", false).toString()
                )
        ).thenReturn(issuerCreateAndStoreCredDefResult)

        `when`(
                issuerCreateAndStoreCredDefResult.credDefId
        ).thenReturn(credDefId)

        `when`(
                issuerCreateAndStoreCredDefResult.credDefJson
        ).thenReturn(credDefJSON)

        `when`(
                ledger.buildCredDefRequest(issuerPerson.personDid, credDefJSON)
        ).thenReturn(credDefRequest)

        `when`(
                ledger.signAndSubmitRequest(poolMockito, walletMockito, issuerPerson.personDid, credDefRequest)
        ).thenReturn(credDefResponse)

        val actualCredDefId = api.createCredentialDefinition(issuerPerson, schemaId)

        assertEquals(actualCredDefId, credDefId)



    }

    @Test
    fun `testMaster can Create Issuer`() {
        val walletId = "issuer-wallet-id"
        val walletKey = "issuer-wallet-key"
        val stewardSeed = "000000000000000000000000Steward1"
        val stewardDid = "steward-did"
        val trustAnchorDid = "trust-anchor-did"
        val trustAnchorVerKey = "trust-anchor-ver-key"
        val nymRequest = "nymRequest"



        val did_json = "{\"seed\": \"$stewardSeed\"}"



        `when`(
                wallet.openWallet(WalletWrapper.WalletConfig(id = walletId),
                        WalletWrapper.WalletCredentials(key = walletKey))
        ).thenReturn(walletMockito)

        `when`(
                ledger.createAndStoreMyDid(walletMockito, did_json)
        ).thenReturn(stewardDidResults)

        `when`(
                stewardDidResults.did
        ).thenReturn(stewardDid)

        `when`(
                ledger.createAndStoreMyDid(walletMockito, "{}")
        ).thenReturn(trustAnchorDidResults)

        `when`(
                trustAnchorDidResults.did
        ).thenReturn(trustAnchorDid)

        `when`(
                trustAnchorDidResults.verkey
        ).thenReturn(trustAnchorVerKey)

        `when`(
                ledger.buildNymRequest(stewardDid, trustAnchorDid, trustAnchorVerKey, null, "TRUST_ANCHOR")
        ).thenReturn(nymRequest)

        val actualStewardDid = api.createIssuer(walletId, walletKey)

        assertEquals(actualStewardDid, stewardDid)



    }
}