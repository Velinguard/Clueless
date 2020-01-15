package clueless.api.licences

import clueless.api.proofs.Proofs
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

open class LicenceImpl(
        val name: String
): Licence {
    val map = HashMap<String, Attribute>()

    override fun getProofs(): MutableList<Proofs> {
        return Proofs.values().filter { it.type.javaClass == LicenceImpl::class.java }.toMutableList()
    }

    override fun getCredentialsJSON(): JSONObject {
        map.put("name", Attribute(name))
        return JSONObject()
                .put("name", (Attribute(name)))
    }

    override fun getSchemaJSON(): JSONArray {
        return JSONArray().put("name")
    }

    override fun getCredentials(): String {
        getCredentialsJSON()
        return Gson().toJson(map)
    }
}