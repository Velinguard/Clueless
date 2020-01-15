package clueless.api.prover

import clueless.api.hyperledger.HyperledgerProver
import clueless.api.proofs.Proof
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq
import org.hyperledger.indy.sdk.wallet.Wallet
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.testng.annotations.Test

class ProverCredentialsTest {
    private var prover = Mockito.mock(HyperledgerProver::class.java)
    private var search = Mockito.mock(CredentialsSearchForProofReq::class.java)
    private var walletMockito: Wallet = Mockito.mock(Wallet::class.java)

    @Test
    fun `getRequestedCredentialsJson() returns the credentials json`() {

        val att1 =
                "[{\"interval\":null,\"cred_info\":{\"cred_rev_id\":null,\"rev_reg_id\":null,\"referent\":\"9741947f-1064-4aeb-baed-abd2de8d8ee9\",\"schema_id\":\"NcYxiDXkpYi6ov5FcYDi1e:2:driving-licence:1.0\",\"cred_def_id\":\"NcYxiDXkpYi6ov5FcYDi1e:3:CL:NcYxiDXkpYi6ov5FcYDi1e:2:driving-licence:1.0:Tag1\",\"attrs\":{\"licence-level\":\"1\",\"name\":\"test-name\",\"age\":\"20\"}}}]"
        val att2 =
                "[{\"interval\":null,\"cred_info\":{\"cred_rev_id\":null,\"rev_reg_id\":null,\"referent\":\"9741947f-1064-4aeb-baed-abd2de8d8ee9\",\"schema_id\":\"NcYxiDXkpYi6ov5FcYDi1e:2:driving-licence:1.0\",\"cred_def_id\":\"NcYxiDXkpYi6ov5FcYDi1e:3:CL:NcYxiDXkpYi6ov5FcYDi1e:2:driving-licence:1.0:Tag1\",\"attrs\":{\"licence-level\":\"1\",\"name\":\"test-name\",\"age\":\"20\"}}}]"
        val att3 =
                "[{\"interval\":null,\"cred_info\":{\"cred_rev_id\":null,\"rev_reg_id\":null,\"referent\":\"9741947f-1064-4aeb-baed-abd2de8d8ee9\",\"schema_id\":\"NcYxiDXkpYi6ov5FcYDi1e:2:driving-licence:1.0\",\"cred_def_id\":\"NcYxiDXkpYi6ov5FcYDi1e:3:CL:NcYxiDXkpYi6ov5FcYDi1e:2:driving-licence:1.0:Tag1\",\"attrs\":{\"licence-level\":\"1\",\"name\":\"test-name\",\"age\":\"20\"}}}]"

        `when`(
                prover.fetchCredentials(search, "attr1_referent", 100)
        ).thenReturn(
                att1
        )
        `when`(
                prover.fetchCredentials(search, "attr2_referent", 100)
        ).thenReturn(
                att2
        )
        `when`(
                prover.fetchCredentials(search, "predicate1_referent", 100)
        ).thenReturn(
                att3
        )
        `when`(
                prover.getCredentialsSearch(walletMockito, "proof-req")
        ).thenReturn(
                search
        )

//        ProverCredentials(prover, walletMockito, "proof-req", ).requestedCredentialsJson()

//        verify(prover).close(search)
    }
}