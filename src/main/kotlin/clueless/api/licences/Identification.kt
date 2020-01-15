package clueless.api.licences

import clueless.api.proofs.Proofs
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

open class Identification(
        name: String = "",
        val dateOfBirth: LocalDate = LocalDate.now()
) : LicenceImpl(name), Licence {
    override fun getProofs(): MutableList<Proofs> {
        val proofs = super.getProofs()
        proofs.addAll(0,
                Proofs.values()
                        .filter { it.type.javaClass == Identification::class.java }
        )
        return proofs
    }

    private fun getAge(): Long {
        return ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now())
    }

    override fun getCredentialsJSON(): JSONObject {
        map.put("age", Attribute(getAge().toInt()))
        return super.getCredentialsJSON()
                .put("age", (Attribute(getAge().toInt())))
    }

    override fun getSchemaJSON(): JSONArray {
        return super.getSchemaJSON().put("age")
    }
}