package clueless.api.proofs

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Attribute(
        @SerializedName("name")
        @Expose(serialize = true)
        var name: String,

        @SerializedName("restrictions")
        @Expose(serialize = true)
        var restrictions: Restrictions?
)