package api.licences

import clueless.api.licences.DrivingLicence
import clueless.api.licences.Identification
import clueless.api.proofs.Proofs
import org.json.JSONArray
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LicencesTest {
    @Test
    fun `Can inherit a proof JSON`() {
        assertTrue { DrivingLicence().getProofs().contains(Proofs.AGE_OVER_18) }
        assertTrue { DrivingLicence().getProofs().contains(Proofs.LICENCE_LEVEL_GREATER_THAN_0) }
    }

    @Test
    fun `Cannot inherit a side licences proof JSON`() {
        assertFalse { Identification().getProofs().contains(Proofs.LICENCE_LEVEL_GREATER_THAN_0) }
    }

    @Test
    fun `Schema is consistent`() {
        assertEquals(
                JSONArray().put("name").put("age").put("licence-level").toString(),
                DrivingLicence().getSchema()
        )
    }
}