package clueless.api.proofs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Restrictions (
    @SerializedName("issuer_did")
    @Expose(serialize = true)
    val issuer: String?,

    @SerializedName("schema_id")
    @Expose(serialize = true)
    val schemaId: String?
)