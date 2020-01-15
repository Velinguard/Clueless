package clueless.api.prover

import clueless.api.hyperledger.HyperledgerProver
import clueless.api.proofs.Proof
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONArray
import org.json.JSONObject


open class ProverCredentials(
        var Prover: HyperledgerProver,
        wallet: Wallet,
        proofRequestJson: String,
        var proof: Proof
) {
    private var credentialsSearch: CredentialsSearchForProofReq = Prover.getCredentialsSearch(wallet, proofRequestJson)

    private fun credentialIdForAttribute(attribute: String, schemaId: Any?): String? {

        var s = JSONArray(Prover.fetchCredentials(credentialsSearch, attribute, 100))

        for (i in 0 until s.length()) {
            if (s.getJSONObject(i).getJSONObject("cred_info").getString("schema_id").equals(schemaId)) {
                return s.getJSONObject(i)
                        .getJSONObject("cred_info")
                        .getString("referent")
            }
        }
        return null
    }

    private fun close() {
        Prover.close(credentialsSearch)
    }

    open fun requestedCredentialsJson(): String {
        val reqPredicates = JSONObject()
        for (i in 1..proof.requestedPredicates.size) {
            reqPredicates.put("predicate${i}_referent", JSONObject()
                    .put("cred_id", credentialIdForAttribute("predicate${i}_referent", proof.requestedPredicates.get(i-1).predicate.restrictions?.schemaId)!!)
            )

        }

        val reqAttributes = JSONObject()
        for (i in 1..proof.requestedAttributes.size) {
            reqAttributes.put("attr${i}_referent", JSONObject()
                    .put("cred_id", credentialIdForAttribute("attr${i}_referent", proof.requestedAttributes.get(i-1).attribute.restrictions?.schemaId))
                    .put("revealed", proof.requestedAttributes.get(i-1).revealed)
            )
        }

        val json = JSONObject()
                .put("self_attested_attributes", JSONObject())
                .put("requested_attributes", reqAttributes
                )
                .put("requested_predicates", reqPredicates
                )
                .toString()
        close()
        return json
    }
}
