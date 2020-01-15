package clueless.api.proofs

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestedPredicates(
        @SerializedName(value ="predicate1_referent", alternate=["predicate2_referent", "predicate3_referent"])
        @Expose(serialize = false)
        val predicate: Predicate
) {
    fun setRestrictions(restrictions: Restrictions) {
        predicate.restrictions = restrictions
    }
}