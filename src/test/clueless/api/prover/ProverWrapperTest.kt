//package clueless.api.prover
//
//import clueless.api.controllers.models.Person
//import clueless.api.hyperledger.HyperledgerProver
//import clueless.api.issuer.CredentialDefinition
//import org.hyperledger.indy.sdk.wallet.Wallet
//import org.json.JSONObject
//import org.mockito.Mockito
//import org.mockito.Mockito.`when`
//import org.testng.annotations.BeforeTest
//import org.testng.annotations.Test
//import kotlin.test.assertEquals
//
//class ProverWrapperTest {
//    private var prover = Mockito.mock(HyperledgerProver::class.java)
//    private var credDef = Mockito.mock(CredentialDefinition::class.java)
//    private var walletMockito = Mockito.mock(Wallet::class.java)
//    private var proverCredentials = Mockito.mock(ProverCredentials::class.java)
//    private var proverCredentialsFactory = Mockito.mock(ProverCredentialsFactory::class.java)
//
//    private lateinit var api: ProverWrapper
//    private lateinit var person: Person
//
//    @BeforeTest
//    fun setupApi() {
//        api = ProverWrapper(prover, proverCredentialsFactory)
//        person = Person("test-did", walletMockito, "test-secret", "test-name")
//    }
//
//    @Test
//    fun `proverGetProofCredentials() returns a proof`() {
//        `when`(
//                proverCredentials.requestedCredentialsJson()
//        ).thenReturn(
//                "cred-json"
//        )
//        `when`(
//                proverCredentialsFactory.getProverCredentials(walletMockito, "proof-req-json")
//        ).thenReturn(
//                proverCredentials
//        )
//        `when`(
//                prover.getSchemas()
//        ).thenReturn(
//                "schema"
//        )
//        `when`(
//                credDef.getCredDefs()
//        ).thenReturn(
//                "cred-defs"
//        )
//        `when`(
//                prover.createProofJson(
//                        walletMockito,
//                        "proof-req-json",
//                        "cred-json",
//                        "master-secret-id",
//                        "schema",
//                        "cred-defs",
//                        JSONObject().toString()
//                )
//        ).thenReturn(
//                "correct"
//        )
//
//        assertEquals("correct", api.proverGetProofCredentials(
//                person,
//                "master-secret-id",
//                "proof-req-json",
//                credDef
//        ))
//
//    }
//
//}