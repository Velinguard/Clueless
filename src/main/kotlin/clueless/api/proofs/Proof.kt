package clueless.api.proofs

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Proof(
        @SerializedName( "name")
        @Expose(serialize = true)
        val name: String,

        @SerializedName( "nonce")
        @Expose(serialize = true)
        val nonce: String,

        @SerializedName( "version")
        @Expose(serialize = true)
        val version: String = "0.1",

        @SerializedName("requested_attributes")
        @Expose(serialize = false)
        val requestedAttributes: List<RequestedAttributes>,

        @SerializedName( "requested_predicates")
        @Expose(serialize = false)
        val requestedPredicates: List<RequestedPredicates>,

        @Expose(serialize = false)
        val schemaIds: List<String>,

        @Expose(serialize = false)
        val credDefIds: List<String>
) {


    fun getJsonObject(): String {
        val requestedPredicateString = JsonObject()
        var i =1
        for (requestedPredicate in requestedPredicates) {
            requestedPredicateString.add("predicate${i}_referent", Gson().toJsonTree(requestedPredicates.get(i-1).predicate))
            i ++
        }

        println(requestedAttributes.size.toString() + " " + requestedAttributes)

        val requestedAttributeString = JsonObject()
        var j = 1
        for (requestedAttribute in requestedAttributes) {
            println(requestedAttribute)
            requestedAttributeString.add("attr${j}_referent", Gson().toJsonTree(requestedAttributes.get(j-1).attribute))
            j++
        }


        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        val jsonElement = gson.toJsonTree(this)
        jsonElement.asJsonObject.add("requested_attributes", Gson().toJsonTree(requestedAttributeString))
        jsonElement.asJsonObject.add("requested_predicates", Gson().toJsonTree(requestedPredicateString))


        return jsonElement.toString()
    }
    fun setRestrictions(restrictions: Restrictions) {
        for (a in requestedAttributes) {
            a.setRestrictions(restrictions)
        }
        for (p in requestedPredicates) {
            p.setRestrictions(restrictions)
        }
    }
}