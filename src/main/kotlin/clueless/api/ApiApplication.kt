package clueless.api

import clueless.api.email.AWSEmailImpl
import clueless.api.email.DummyEmail
import clueless.api.email.Email
import clueless.api.filesystem.*
import clueless.api.licences.DrivingLicence
import clueless.api.time.Time
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.identitymanagement.model.AccessKey
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import org.hyperledger.indy.sdk.anoncreds.Anoncreds.issuerCreateSchema
import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults
import org.hyperledger.indy.sdk.pool.Pool
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import springfox.documentation.swagger2.annotations.EnableSwagger2


@SpringBootApplication
@EnableSwagger2
open class ApiApplication: ApplicationRunner {
	@Value("\${fs}")
	private var fileSystem = "aws"
	@Value("\${aws.key.access}")
	private var awsAccessKey: String = ""
	@Value("\${aws.key.secret}")
	private var awsSecretKey: String = ""
	@Value("\${email}")
	private var email: Boolean = true

	fun setCredentials(awsAccessKey: String, awsSecretKey: String) {
		this.awsAccessKey = awsAccessKey
		this.awsSecretKey = awsSecretKey
	}

	@Bean
	open fun getEmailClient(): Email {
		if (email) {
			val awsCreds = BasicAWSCredentials(awsAccessKey, awsSecretKey)
			return AWSEmailImpl(
					AmazonSimpleEmailServiceClientBuilder.standard()
						// Replace US_WEST_2 with the AWS Region you're using for
						// Amazon SES.
						.withRegion(Regions.EU_WEST_1)
						.withCredentials(AWSStaticCredentialsProvider(awsCreds))
						.build()
			)
		}
		return DummyEmail()
	}

    override fun run(args: ApplicationArguments?) { }

    companion object {
		val LOGGER = LoggerFactory.getLogger(ApiApplication::class.java)
	}

	val poolName = "clueless_aws_node_pool"

	var hyperledgerPool: Pool? = null

    @Bean
    open fun getPool(): Pool {
        val poolConfig = "{\"genesis_txn\": \"./docker/docker_pool_transactions_genesis\"}"
        Pool.setProtocolVersion(2)

        try {
            Pool.createPoolLedgerConfig(poolName, poolConfig).get()
            LOGGER.info("Created new pool ledger $poolName")
        } catch (e : Exception) {
		}

        LOGGER.info("Opening pool ledger $poolName")
		hyperledgerPool = Pool.openPoolLedger(poolName, "{\"preordered_nodes\": [\"node1\", \"node2\"]}").get()
		return hyperledgerPool!!
    }
	var issuerDid = "NcYxiDXkpYi6ov5FcYDi1e"

	@Bean
    @DependsOn("getPool")
	open fun getCredentialSchema(): AnoncredsResults.IssuerCreateSchemaResult? {
		//4. Issuer Creates Credential Schema
		val schemaName = "driving-licence"
		val schemaVersion = "1.0"
		val schemaAttributes = DrivingLicence().getSchema()
		val schemaResult = issuerCreateSchema(issuerDid, schemaName, schemaVersion, schemaAttributes).get()

		return schemaResult

	}

	fun getAwsClient(): AmazonS3 {
		val credentials = BasicAWSCredentials(
				awsAccessKey,
				awsSecretKey
		)
		return AmazonS3ClientBuilder
				.standard()
				.withCredentials(AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.EU_WEST_1)
				.build()!!
	}

	@Bean
	open fun getFileSystem(): FileSystem {
		return when(fileSystem) {
			"aws" -> AmazonS3Impl(getAwsClient(), Time(), TemporaryFileImpl())
			"local" -> LocalFileSystemImpl()
			"console" -> NoFileSystemImpl()
			else -> LocalFileSystemImpl()
		}
	}
}

/**
 * To view endpoints go to ${url}/swagger-ui.html
 * Run with --aws=true to store files on AWS
 */
fun main(args: Array<String>) {
	runApplication<ApiApplication>(*args)
}
