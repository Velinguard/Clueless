package api.integrationtest

import clueless.api.ApiApplication
import clueless.api.controllers.*
import clueless.api.email.DummyEmail
import clueless.api.filesystem.FileSystem
import clueless.api.filesystem.LocalFileSystemImpl
import clueless.api.hyperledger.*
import clueless.api.issuer.IssuerWrapper
import clueless.api.master.MasterWrapper
import clueless.api.prover.ProverCredentialsFactory
import clueless.api.prover.ProverWrapper
import clueless.api.verifier.VerifierWrapper
import clueless.api.wallet.WalletWrapper
import org.springframework.beans.factory.annotation.Value
import org.testng.annotations.BeforeTest

class TestEnv(
        awsAccessKey: String = "AWSAccessKey",
        awsSecretKey: String = "AWSSecretKey"
) {
    var application = ApiApplication()
    var fileSystem: FileSystem = LocalFileSystemImpl() // AmazonS3Impl(application.getAwsClient())
    val walletWrapper = WalletWrapper(HyperledgerWalletImpl())
    val exceptionHandler = ExceptionHandler()
    val hyperledgerProver = HyperledgerProverImpl(application.getCredentialSchema()!!)
    var issuerDid = ""

    var proverDid = "VsKV7grR1BUE29mG2Fm2kX";

    val proofController = ProofController()

    val walletController = WalletController(walletWrapper, exceptionHandler)

    val email = DummyEmail()
    lateinit var issuerWrapper: IssuerWrapper
    lateinit var verifierWrapper: VerifierWrapper
    lateinit var proverWrapper: ProverWrapper
    lateinit var issuerController: IssuerController
    lateinit var masterController: MasterController
    lateinit var masterWrapper: MasterWrapper
    lateinit var proverController: ProverController
    lateinit var verifierController: VerifierController

    init {
        // Open pool 1 and create schema 4
        println("Open pool 1 and create schema 4")

        application.setCredentials(
                awsAccessKey,
                awsSecretKey
        )

        application.run(null)

        application.getPool()

        issuerWrapper = application.getCredentialSchema()?.let {
            IssuerWrapper(it, walletWrapper, HyperledgerIssuerImpl(), HyperledgerLedgerImpl(), application.hyperledgerPool!!)
        }!!

        masterWrapper = application.getCredentialSchema()?.let {
            MasterWrapper(HyperledgerIssuerImpl(), HyperledgerLedgerImpl(), walletWrapper, application.hyperledgerPool!!)
        }!!

        verifierWrapper = VerifierWrapper(HyperledgerVerifierImpl(application.getCredentialSchema()!!), masterWrapper!!)

        issuerController = application.getCredentialSchema()?.let {
            IssuerController(
                    issuerWrapper!!,
                    email,
                    exceptionHandler
            )
        }!!

        proverWrapper = ProverWrapper(hyperledgerProver, ProverCredentialsFactory(hyperledgerProver), masterWrapper!!, issuerController)!!

        masterController = application.getCredentialSchema()?.let {
            MasterController(
                    walletWrapper,
                    masterWrapper!!,
                    exceptionHandler
            )
        }!!

        proverController = application.getCredentialSchema()?.let {
            ProverController(
                    proverWrapper,
                    issuerController!!,
                    proofController,
                    walletWrapper,
                    fileSystem,
                    exceptionHandler
            )
        }!!

        verifierController = VerifierController(
                verifierWrapper!!,
                issuerController!!,
                proofController,
                fileSystem,
                walletWrapper,
                exceptionHandler
        )!!
    }
}