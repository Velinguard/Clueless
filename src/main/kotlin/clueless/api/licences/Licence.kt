package clueless.api.licences

import clueless.api.proofs.Proofs
import org.json.JSONArray
import org.json.JSONObject

interface Licence {
    fun getProofs(): MutableList<Proofs>
    fun getCredentialsJSON(): JSONObject
    fun getSchemaJSON(): JSONArray
    fun getCredentials(): String
}