package clueless.api.hyperledger

import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutionException

@Component
class HyperledgerVerifierImpl(
        @Autowired var schema: AnoncredsResults.IssuerCreateSchemaResult
) : HyperledgerVerifier {

    override fun getSchemas(): String {
        try {
            return JSONObject().put(
                    schema.schemaId,
                    JSONObject(schema.schemaJson)
            ).toString()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    override fun verifyVerifierProof(
            name: String,
            proofJson: String,
            proofRequestJson: String,
            schemas: String,
            credentialDefs: String,
            revocRegDefs: String,
            revocRegs: String
    ): Boolean? {
        try {
            return Anoncreds.verifierVerifyProof(
                    proofRequestJson,
                    proofJson,
                    schemas,
                    credentialDefs,
                    revocRegDefs,
                    revocRegs
            ).get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

}