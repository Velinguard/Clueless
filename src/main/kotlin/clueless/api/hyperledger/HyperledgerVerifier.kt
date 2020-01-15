package clueless.api.hyperledger

interface HyperledgerVerifier {

    fun getSchemas(): String

    fun verifyVerifierProof(
            name: String,
            proofJson: String,
            proofRequestJson: String,
            schemas: String,
            credentialDefs: String,
            revocRegDefs: String,
            revocRegs: String
    ): Boolean?
}