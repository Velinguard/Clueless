package api.integrationtest

import clueless.api.controllers.IssuerController
import clueless.api.controllers.ProverController
import clueless.api.controllers.models.Person
import clueless.api.filesystem.AmazonS3Impl
import clueless.api.filesystem.TemporaryFileImpl
import clueless.api.licences.Licences
import clueless.api.time.Time
import com.google.gson.Gson
import org.hyperledger.indy.sdk.wallet.WalletItemAlreadyExistsException
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

class TicketIntegrationTest {
    lateinit var testEnv: TestEnv

    @BeforeTest
    fun initiatePool() {
        testEnv = TestEnv()
    }

    @AfterTest
    fun killPool() {
        // Close pool 16
        println("Close pool 16")
        testEnv.application.hyperledgerPool!!.closePoolLedger().get()
    }

    fun issuer(proverWalletId: String, proverWalletKey: String, issuerWalletId: String, issuerWalletKey: String, credDefId: String): IssuerController.EmailInfo {
        // Issuer logs in
        val issuer = testEnv.walletController.login(issuerWalletId, issuerWalletKey, testEnv.issuerDid)

        // Issuer
        val emailInfo = testEnv.issuerController!!.issuerCreateCredentials(
                issuerWalletId = issuerWalletId,
                issuerWalletKey = issuerWalletKey,
                proverWalletId = proverWalletId,
                proverWalletKey = proverWalletKey,
                credDefId = credDefId!!,
                name = "test-name",
                dateOfBirth = "1999-06-16",
                licenceLevel = "1",
                issuerDid = testEnv.issuerDid,
                proverDid = null
        )
        testEnv.proverDid = (emailInfo!!.body!! as IssuerController.EmailInfo).personDid

        return emailInfo.body!! as IssuerController.EmailInfo
    }

    fun issuerTicket(proverWalletId: String, proverWalletKey: String, issuerWalletId: String, issuerWalletKey: String, credDefId: String, masterSecretId: String?): IssuerController.EmailInfo {
        // Issuer logs in
        val issuer = testEnv.walletController.login(issuerWalletId, issuerWalletKey, testEnv.issuerDid)

        // Issuer
        val emailInfo = testEnv.issuerController!!.issuerCreateTicketCredentials(
                issuerWalletId = issuerWalletId,
                issuerWalletKey = issuerWalletKey,
                proverWalletId = proverWalletId,
                proverWalletKey = proverWalletKey,
                credDefId = credDefId!!,
                name = "test-name",
                ticketLevel = "1",
                issuerDid = testEnv.issuerDid,
                proverDid = testEnv.proverDid
        )

        testEnv.proverDid = (emailInfo.body!! as IssuerController.EmailInfo).personDid


        return emailInfo.body!! as IssuerController.EmailInfo
    }

    fun prover(personDid: String, masterSecretId: String, proverWalletId: String, proverWalletKey: String, schemaId: String, credDefId: String, issuerDid: String, schemaIDs: List<String>): String {
        // Setup proof 11
        println("Setup proof 11")
        val prover = testEnv.walletController.login(proverWalletId, proverWalletKey, personDid, masterSecretId).body!! as Person

        val s3Json = testEnv.proverController!!.proverGetDefaultCredentials(
                personDid,
                proverWalletKey,
                proverWalletId,
                masterSecretId,
                "Has ticket and is over 18"
        )

        println("closing wallet")
        prover.indyWallet!!.closeWallet()
        return s3Json.body!!
    }

    fun verifier(
            verifierId: String,
            verifierKey: String,
            verifierDid: String,
            schemaId: String,
            credDefId: String,
            s3Json: String
    ): Boolean? {
        // Verifier verifies proof 13
        println("Verifier verifies proof 13")
        val s3Json = Gson().fromJson(s3Json, ProverController.S3Info::class.java)

        val verified = testEnv.verifierController.verifyProofFromS3(
                "test-name",
                verifierDid,
                verifierKey,
                verifierId,
                s3Json.bucketName,
                s3Json.fileName,
                "Has ticket and is over 18"

        )

        return verified.body as Boolean
    }

    @Test(enabled = true)
    fun `Integration Test - Verifier can prove user has a ticket using AWS`() {
        testEnv.fileSystem = AmazonS3Impl(testEnv.application.getAwsClient(), Time(), TemporaryFileImpl())

        testEnv.proverController.fileSystem = testEnv.fileSystem

        testEnv.verifierController.fileSystem = testEnv.fileSystem
        `Integration Test - Verifier can prove user has a ticket`()
    }

    @Test(enabled = true)
    fun `Integration Test - Verifier can prove user has a ticket`() {
        val issuerWalletId = "issuer-id-${Math.random()}"
        val issuerWalletKey = "issuer-key"
        // Set up
        println("Open Issuers Wallet 2")
        try {


            testEnv.issuerDid = testEnv.masterController.createIssuer(issuerWalletId, issuerWalletKey).body!!
        } catch (e: Exception) {
//            walletController.deleteWallet(issuerWalletId, issuerWalletKey)
//            issuerDid = issuerController.createIssuer(issuerWalletId, issuerWalletKey)!!

            println("Issuer wallet already exists")
        }

        // User enters prover id and prover password
        val proverWalletId = "prover-" + Math.random()
        val proverWalletKey = "prover-key"

        try {
//            walletController.createWallet(proverWalletId, proverWalletKey)

        } catch (e: WalletItemAlreadyExistsException) {
            println("Prover wallet already exists")
        }

        // User enters prover id and prover password
        val verifierWalletId = "verifier-" + Math.random()
        val verifierWalletKey = "verifier-key"

        var verifierDid = ""
        try {
            verifierDid = testEnv.walletController.createWalletWithDid(verifierWalletId, verifierWalletKey).body!!

        } catch (e: WalletItemAlreadyExistsException) {
            println("Verifier wallet already exists")
        }
        println("Wallet did is " + verifierDid)

        var verified: Boolean? = false
        // ------ Master ------

        // Create schema
        val credSchemaId = testEnv.masterController!!.createCredSchema(
                defaultStewardDid = testEnv.issuerDid,
                walletId = issuerWalletId,
                walletKey = issuerWalletKey,
                licence = Licences.DRIVINGLICENCE
        ).body

        val credDefId = testEnv.masterController!!.createCredentialDefinition(
                walletId = issuerWalletId,
                walletKey = issuerWalletKey,
                personDid = testEnv.issuerDid,
                schemaId = credSchemaId!!
        ).body!!

        val credTicketSchemaId = testEnv.masterController!!.createCredSchema(
                defaultStewardDid = testEnv.issuerDid,
                walletId = issuerWalletId,
                walletKey = issuerWalletKey,
                licence = Licences.TICKET
        ).body

        println("cred ticket schema id is " + credTicketSchemaId)

        val credTicketDefId = testEnv.masterController!!.createCredentialDefinition(
                walletId = issuerWalletId,
                walletKey = issuerWalletKey,
                personDid = testEnv.issuerDid,
                schemaId = credTicketSchemaId!!
        ).body!!

        // ------ Issuer ------
//        try {
//            println("Issuer")



            val emailInfo = issuer(
                    proverWalletId = proverWalletId,
                    proverWalletKey = proverWalletKey,
                    issuerWalletId = issuerWalletId,
                    issuerWalletKey = issuerWalletKey,
                    credDefId = credDefId!!)

        val emailInfo2 = issuerTicket(
                proverWalletId = proverWalletId,
                proverWalletKey = proverWalletKey,
                issuerWalletId = issuerWalletId,
                issuerWalletKey = issuerWalletKey,
                credDefId = credTicketDefId!!,
                masterSecretId = emailInfo.masterSecretId)
        println("--------------------------------")
//

//
//        println(emailInfo2.masterSecretId)
        println("--------------------------------")

//            // ------ Prover ------
//            println("Prover")
            val proof = prover(
                    personDid = emailInfo2.personDid,
                    masterSecretId = emailInfo2.masterSecretId,
                    proverWalletId = proverWalletId,
                    proverWalletKey = proverWalletKey,
                    schemaId= credTicketSchemaId,
                    credDefId = credTicketDefId,
                    issuerDid = testEnv.issuerDid,
                    schemaIDs = mutableListOf(credTicketSchemaId)
            )
        println()
//
//            // ----- Verifier -----
//            println("Verifier")
            verified = verifier(verifierWalletId, verifierWalletKey, verifierDid, credTicketSchemaId, credTicketDefId, proof)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        finally {
//            println(verified)

            // Close and delete issuer wallet 14
            println("Close and delete issuer wallet 14")
        testEnv.walletController.deleteWallet(issuerWalletId, issuerWalletKey)

            // Close and delete provers wallet 15
            println("Close and delete provers wallet 15")
//            walletController.deleteWallet(proverWalletId, proverWalletKey)
//        }

//        assertTrue { verified!! }
    }


}