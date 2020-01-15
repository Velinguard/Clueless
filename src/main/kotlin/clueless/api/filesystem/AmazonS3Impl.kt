package clueless.api.filesystem

import clueless.api.controllers.ProverController
import clueless.api.time.Time
import com.amazonaws.services.s3.AmazonS3
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.format.DateTimeFormatter

class AmazonS3Impl(
        val aws: AmazonS3,
        val time: Time,
        val tempFile: TemporaryFileImpl
): FileSystem {
    override fun writeToFile(proverDID: String, proof: String): String? {
        val timestamp = time.getTime()

        val filename = "$proverDID$timestamp.json"
        LOGGER.info("Writing $filename from AmazonS3")

        val file = tempFile.getTempFile()
        file.writeText(proof)
        try {
            aws.putObject(
                    "zero-knowledge-proof-json-files",
                    filename,
                    file
            )
        } catch (e: Exception) {
            return "could not upload to s3"
        }

        return Gson().toJson(ProverController.S3Info("zero-knowledge-proof-json-files", filename))
    }

    override fun readFromFile(bucketName: String, objectName: String): String {
        if (bucketName == "") throw IllegalArgumentException("No bucket name provided")

        LOGGER.info("Reading $objectName from AmazonS3")
        return aws
                .getObject(bucketName, objectName)
                .objectContent
                .bufferedReader()
                .use(BufferedReader::readText)
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(AmazonS3Impl::class.java)
    }
}