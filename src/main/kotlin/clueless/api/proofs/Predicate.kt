package clueless.api.proofs

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Predicate(
        @SerializedName("name") val name: String,
        @SerializedName("p_type") val type: String,
        @SerializedName("p_value") val value: Int,
        @SerializedName("restrictions") var restrictions: Restrictions?
) {
    fun getJsonObject(): String {
        return Gson().toJson(this)

    }
}
