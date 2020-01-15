package clueless.api.prover

import clueless.api.hyperledger.HyperledgerProver
import clueless.api.proofs.Proof
import org.hyperledger.indy.sdk.wallet.Wallet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class ProverCredentialsFactory(
        @Autowired var Prover: HyperledgerProver
) {
    open fun getProverCredentials(
            wallet: Wallet,
            proofRequestJson: String,
            proof: Proof
    ): ProverCredentials {
        return ProverCredentials(Prover, wallet, proofRequestJson, proof)
    }
}