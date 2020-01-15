package clueless.api.issuer

import clueless.api.controllers.models.Person
import clueless.api.hyperledger.HyperledgerIssuer
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults

open class CredentialRequest(
        val credOffer: String,
        val credentials: CredentialDefinition,
        val masterSecretId: String,
        var credRequest: AnoncredsResults.ProverCreateCredentialRequestResult? = null
) {
    constructor(prover: Person, issuer: Person, credDef: CredentialDefinition, hyperledgerIssuer: HyperledgerIssuer) :
            this(
                    hyperledgerIssuer.issuerCreateCredentialOffer(
                            issuer.indyWallet!!,
                            credDef.credDefId
                    )!!,
                    credDef,
                    hyperledgerIssuer.issuerCreateMasterSecret(
                            prover.indyWallet!!
                    )!!
            ) {
        this.credRequest = hyperledgerIssuer.issuerCreateCredentialRequest(
                prover.indyWallet!!,
                prover.personDid,
                credOffer,
                credDef.credDefJson,
                masterSecretId
        )
    }

    override fun equals(other: Any?): Boolean {
        return (other is CredentialRequest) &&
                credOffer.equals(other.credOffer) &&
                credentials.equals(other.credentials) &&
                masterSecretId.equals(other.masterSecretId) &&
                other.credRequest!!.equals(credRequest)
    }

}
