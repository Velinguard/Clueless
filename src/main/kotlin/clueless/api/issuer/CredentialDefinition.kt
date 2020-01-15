package clueless.api.issuer

import org.json.JSONObject

open class CredentialDefinition(
        val credDefId: String,
        val credDefJson: String
) {
    open fun getCredDefs(): String {
        return JSONObject().put(
                credDefId,
                JSONObject(credDefJson)
        ).toString()
    }

    override fun equals(other: Any?): Boolean {
        return (other is CredentialDefinition &&
                other.credDefId.equals(credDefId) &&
                other.credDefJson.equals(credDefJson))
    }
}