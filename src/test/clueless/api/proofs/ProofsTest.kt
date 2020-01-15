package api.proofs

import clueless.api.proofs.*
import com.google.gson.Gson
import org.json.JSONObject
import org.junit.Test
import kotlin.test.assertEquals

class ProofsTest {
    @Test
    fun `Json Object created correctly`() {
        val proof = Proof(
                nonce = "0",
                name = "driving-licence-age-over-18",
                version = "0.1",
                requestedAttributes = mutableListOf(
                        RequestedAttributes(
                                attribute = Attribute("name", null), revealed = true),
                        RequestedAttributes(
                                attribute = Attribute("age", null), revealed = false)
                ),
                requestedPredicates = mutableListOf(RequestedPredicates(
                        predicate = Predicate(
                                name = "age",
                                type = ">=",
                                value = 18,
                                restrictions = null
                        )
                )),
                credDefIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:3:CL:10:Tag1"),
                schemaIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0")
        )
        val manualProof = JSONObject()
                .put("nonce", "0")
                .put("name", "driving-licence-age-over-18")
                .put("version", "0.1")
                .put("requested_attributes", JSONObject()
                        .put("attr1_referent", JSONObject().put("name", "name"))
                        .put("attr2_referent", JSONObject().put("name", "age"))
                )
                .put("requested_predicates", JSONObject()
                        .put("predicate1_referent", JSONObject()
                                .put("name", "age")
                                .put("p_type", ">=")
                                .put("p_value", 18)
                        )
                )
                .toString()

        val manualProofToGson = Gson().fromJson(manualProof, Proof::class.java)

        assertEquals(manualProofToGson, proof)
        assertEquals(Gson().toJson(manualProofToGson).toString(), Gson().toJson(proof).toString())
    }
}