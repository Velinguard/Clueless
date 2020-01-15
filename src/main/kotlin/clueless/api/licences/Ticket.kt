package clueless.api.licences

import clueless.api.proofs.Proofs
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

open class Ticket(
        name: String = "",
        val ticketLevel: Int = 0
) : Licence, LicenceImpl(name) {

    override fun getProofs(): MutableList<Proofs> {
        return Proofs.values().filter { it.type.javaClass == Ticket::class.java }.toMutableList()
    }

    override fun getCredentialsJSON(): JSONObject {
        map.put("ticket-level", Attribute(ticketLevel))
        return super.getCredentialsJSON()
                            .put("ticket-level", (Attribute(ticketLevel)))
    }

    override fun getSchemaJSON(): JSONArray {
        return super.getSchemaJSON().put("ticket-level")
    }

    fun getSchema(): String {
        return getSchemaJSON().toString()
    }
}