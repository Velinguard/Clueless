package clueless.api.licences

import clueless.api.proofs.Proofs
import com.google.common.hash.Hashing
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DrivingLicence(
        name: String = "",
        dateOfBirth: LocalDate = LocalDate.now(),
        val licenceLevel: Int = 0
): Identification(name, dateOfBirth), Licence {
    override fun getProofs(): MutableList<Proofs> {
        val proofs = super.getProofs()
        proofs.addAll(0,
                Proofs.values()
                        .filter { it.type.javaClass == DrivingLicence::class.java }
        )
        return proofs
    }

    override fun getCredentialsJSON(): JSONObject {
        map.put("licence-level", Attribute(licenceLevel))
        return super.getCredentialsJSON()
                .put("licence-level", Attribute(licenceLevel))
    }

    override fun getSchemaJSON(): JSONArray {
        return super.getSchemaJSON().put("licence-level")
    }

    fun getSchema(): String {
        return getSchemaJSON().toString()
    }
}