package clueless.api.master

import clueless.api.controllers.models.Person
import clueless.api.hyperledger.HyperledgerIssuer
import clueless.api.hyperledger.HyperledgerLedger
import clueless.api.licences.Licence
import clueless.api.wallet.WalletWrapper
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MasterWrapper(
        val hyperledgerIssuer: HyperledgerIssuer,
        val hyperledgerLedger: HyperledgerLedger,
        val walletWrapper: WalletWrapper,
        val pool: Pool
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(MasterWrapper::class.java)
    }

    fun createCredentialDefinition(
            issuer: Person,
            schemaId: String
    ): String? {
        LOGGER.info("Creating credential definition for $schemaId by ${issuer.name}")
        val getSchemaRequest = hyperledgerLedger.buildGetSchemaRequest(issuer.personDid, schemaId)
        val getSchemaResponse = hyperledgerLedger.signAndSubmitRequest(pool, issuer.indyWallet!!, issuer.personDid, getSchemaRequest)

        val parseSchemaResult = hyperledgerLedger.parseGetSchemaResponse(getSchemaResponse)

        // create and post credential definition

        val createCredDefResult = hyperledgerIssuer.issuerCreateAndStoreCredentialDef(
                issuer.indyWallet!!,
                issuer.personDid,
                parseSchemaResult.objectJson,
                "Tag1",
                null,
                JSONObject().put("support_revocation", false).toString()
        )
        val credDefJson = createCredDefResult!!.credDefJson

        val credDefId = createCredDefResult.credDefId

        val credDefRequest = hyperledgerLedger.buildCredDefRequest(issuer.personDid, credDefJson)
        hyperledgerLedger.signAndSubmitRequest(pool, issuer.indyWallet!!, issuer.personDid, credDefRequest)

        issuer.indyWallet!!.closeWallet()

        return credDefId
    }

    fun getCredSchema(
            personDid: String,
            credSchemaID: String,
            wallet: Wallet,
            schemaJSON: JSONObject
    ): JSONObject? {
        val request = hyperledgerLedger.buildGetSchemaRequest(personDid, credSchemaID)
        val getSchemaResponse = hyperledgerLedger.signAndSubmitRequest(pool, wallet, personDid, request)

        val parseSchemaResult =  hyperledgerLedger.parseGetSchemaResponse(getSchemaResponse)

        val schemasJson = schemaJSON.put(credSchemaID, JSONObject(parseSchemaResult.objectJson))

        return schemasJson
    }

    fun createCredSchema(
            defaultStewardDid: String?,
            walletId: String,
            walletKey: String,
            schemaName: String,
            schemaVersion: String,
            licence: Licence
    ): String? {
        val myWallet = walletWrapper.getWallet(walletId, walletKey)
        try {
            LOGGER.info("Creating credential schema for $schemaName : $schemaVersion by $walletId")
            val schemaAttributes = licence.getSchemaJSON().toString()
            val createSchemaResult = hyperledgerIssuer.issuerCreateSchema(defaultStewardDid!!, schemaName, schemaVersion, schemaAttributes)
            val schema = createSchemaResult.schemaJson
            val schemaId = createSchemaResult.schemaId

            val schemaRequest = hyperledgerLedger.buildSchemaRequest(defaultStewardDid, schema)
            hyperledgerLedger.signAndSubmitRequest(pool, myWallet!!, defaultStewardDid, schemaRequest)

            val getSchemaRequest = hyperledgerLedger.buildGetSchemaRequest(defaultStewardDid, schemaId)
            val getSchemaResponse = hyperledgerLedger.signAndSubmitRequest(pool, myWallet, defaultStewardDid, getSchemaRequest)

            val parseSchemaResult = hyperledgerLedger.parseGetSchemaResponse(getSchemaResponse)

            LOGGER.info("Credential schema created for $schemaName : $schemaVersion by $walletId")
            return parseSchemaResult.id
        } catch (e: Exception) {
            LOGGER.error("Error creating credential schema for $schemaName : $schemaVersion, ${e.message}")
            throw e
        } finally {
            myWallet!!.closeWallet()
        }
    }

    fun createIssuer(
            walletId: String,
            walletKey: String
    ): String? {
        val stewardSeed = "000000000000000000000000Steward1"

        LOGGER.info("Creating Issuer $walletId")
        // Create and Open Issuer Wallet
        walletWrapper.createWallet(walletId, walletKey)

        val myWallet = walletWrapper.getWallet(walletId, walletKey)
        try {
            // Generating and storing steward DID and Verkey
            val did_json = "{\"seed\": \"$stewardSeed\"}"
            val stewardResult = hyperledgerLedger.createAndStoreMyDid(myWallet!!, did_json)
            val defaultStewardDid = stewardResult.did

            // Generating and storing Trust Anchor DID and Verkey
            val trustAnchorResult = hyperledgerLedger.createAndStoreMyDid(myWallet, "{}")
            val trustAnchorDID = trustAnchorResult.did
            val trustAnchorVerkey = trustAnchorResult.verkey

            // Build NYM request to add Trust Anchor to the ledger
            val nymRequest = hyperledgerLedger.buildNymRequest(defaultStewardDid, trustAnchorDID, trustAnchorVerkey, null, "TRUST_ANCHOR")

            hyperledgerLedger.signAndSubmitRequest(pool, myWallet, defaultStewardDid, nymRequest)

            LOGGER.info("Issuer $walletId added to Ledger")

            return defaultStewardDid
        } catch (e: Exception) {
            LOGGER.error(e.message)
        } finally {
            myWallet!!.closeWallet()
        }
        return null
    }
}