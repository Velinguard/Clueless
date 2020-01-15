package clueless.api.proofs

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestedAttributes(
        @SerializedName(value ="attr1_referent", alternate=["attr2_referent", "attr3_referent"])
        @Expose(serialize = true)
        val attribute: Attribute,
        val revealed: Boolean

) {
    fun setRestrictions(restrictions: Restrictions) {
        attribute.restrictions = restrictions
    }
}