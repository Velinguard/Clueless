package clueless.api.verifier

import clueless.api.controllers.models.Person
import clueless.api.hyperledger.HyperledgerVerifier
import clueless.api.issuer.CredentialDefinition
import clueless.api.issuer.IssuerWrapper
import clueless.api.master.MasterWrapper
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.testng.Assert

/**
 * Wrapper for the verifier, delegates to the Hyperledger implementation of a verification for a proof.
 */
@Component
class VerifierWrapper(
        @Autowired var Verifier: HyperledgerVerifier,
        @Autowired var masterWrapper: MasterWrapper
) {
    fun verifyProof(
            name: String,
            proofJson: String,
            proofRequestJson: String,
            verifier: Person,
            schemaIds: List<String>,
            credDefsList: List<CredentialDefinition>
    ): Boolean? {
        val proof = JSONObject(proofJson)
        val revealedAttr1 = proof.getJSONObject("requested_proof").getJSONObject("revealed_attrs")
                .getJSONObject("attr1_referent")
        Assert.assertEquals(name, revealedAttr1.getString("raw"))

        Assert.assertNotNull(proof.getJSONObject("requested_proof")
                .getJSONObject("unrevealed_attrs").getJSONObject("attr2_referent")
                .getInt("sub_proof_index"))

        val revocRegDefs = JSONObject().toString()
        val revocRegs = JSONObject().toString()


        var schemaJSON = JSONObject()
        for (schema in schemaIds) {
            schemaJSON = masterWrapper.getCredSchema(verifier.personDid, schema, verifier.indyWallet!!, schemaJSON)!!
        }

        var credDefJson = JSONObject()
        for (credDef in credDefsList) {
            credDefJson.put(credDef.credDefId, JSONObject(credDef.credDefJson))
        }


        return Verifier.verifyVerifierProof(
                name,
                proofJson,
                proofRequestJson,
                schemaJSON!!.toString(),
                credDefJson.toString(),
                revocRegDefs,
                revocRegs
        )
    }

}