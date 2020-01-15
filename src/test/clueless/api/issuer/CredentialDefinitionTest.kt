package clueless.api.issuer

import org.testng.annotations.Test
import kotlin.test.assertEquals

class CredentialDefinitionTest {
    @Test
    fun `getCredDefs() generates json`() {
        val credDefs = CredentialDefinition(
                "cred-def-id",
                "{\"name\": \"cred-def\"}"
        ).getCredDefs()
        assertEquals("{\"cred-def-id\":{\"name\":\"cred-def\"}}", credDefs)
    }
}