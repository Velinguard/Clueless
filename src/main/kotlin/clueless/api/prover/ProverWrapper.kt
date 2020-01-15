package clueless.api.prover

import clueless.api.controllers.IssuerController
import clueless.api.controllers.models.Person
import clueless.api.hyperledger.HyperledgerProver
import clueless.api.issuer.CredentialDefinition
import clueless.api.issuer.IssuerWrapper
import clueless.api.master.MasterWrapper
import clueless.api.proofs.Proofs
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProverWrapper(
        @Autowired var Prover: HyperledgerProver,
        @Autowired var ProverCredentialsFactory: ProverCredentialsFactory,
        @Autowired var masterWrapper: MasterWrapper,
        @Autowired var issuerController: IssuerController
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(ProverWrapper::class.java)
    }

    fun getCredAndSchemaDef(
            creds: String,
            prover: Person
    ): Pair<ArrayList<CredentialDefinition>, HashSet<String>> {
        val nat = JSONObject(creds)
        val credDefsList = ArrayList<CredentialDefinition>()
        val schemaIDList = HashSet<String>()

        LOGGER.info("Getting credential definitions and schema ID's")
        nat.getJSONObject("attrs").toMap().keys.forEach {
            val cred = nat
                    .getJSONObject("attrs")
                    .getJSONArray(it)
                    .getJSONObject(0)
                    .getJSONObject("cred_info")
            credDefsList.add(
                    issuerController.getCredentialDefinition(
                            prover,
                            cred.getString("cred_def_id")
                    )
            )
            schemaIDList.add(cred.getString("schema_id"))
        }

        LOGGER.info("Successfully obtained credential defintions and schema ID's")
        return Pair(credDefsList, schemaIDList)
    }

    fun proverGetProofCredentials(
            prover: Person,
            masterSecretId: String,
            proofRequestJson: String,
            proofObj: Proofs,
            schemaIDs: Set<String>,
            credentialDefinitions: List<CredentialDefinition>
    ): String {
        LOGGER.info("Found credentials for ${prover.name}")
        val requestedCredentialsJson = ProverCredentialsFactory
                .getProverCredentials(prover.indyWallet!!, proofRequestJson, proofObj.proof)
                .requestedCredentialsJson()

        val schemaJSON = schemaIDs.fold(JSONObject()) { schemaJSON: JSONObject, schema: String ->
            masterWrapper.getCredSchema(
                            prover.personDid,
                            schema,
                            prover.indyWallet!!,
                            schemaJSON
                    )!!
        }

        val credDefJson = JSONObject()
        credentialDefinitions.forEach {
            credDefJson.put(it.credDefId, JSONObject(it.credDefJson))
        }

        LOGGER.info("Found schema for ${prover.name}")

        val revocStates = JSONObject().toString()

        var proofJson = ""

        try {
            proofJson = Prover.createProofJson(
                    prover.indyWallet!!,
                    proofRequestJson,
                    requestedCredentialsJson,
                    masterSecretId,
                    schemaJSON!!.toString(),
                    credDefJson.toString(),
                    revocStates
            )
            LOGGER.info("Generated proof for ${prover.name}")
        } catch (e: Exception) {
            LOGGER.error("Error generating proof for ${prover.name}")
            LOGGER.error(e.toString())
        }

        return proofJson
    }
}