package clueless.api.filesystem

import clueless.api.controllers.ProverController
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import java.io.File

class LocalFileSystemImpl: FileSystem {
    override fun writeToFile(proverDID: String, proof: String): String? {
        val file = createTempFile()
        file.writeText(proof)
        LOGGER.info("Writing ${file.absolutePath} from Local File System")

        return Gson().toJson(ProverController.S3Info("zero-knowledge-proof-json-files", file.absolutePath))
    }

    override fun readFromFile(bucketName: String, objectName: String): String {
        LOGGER.info("Reading $objectName from Local File System")
        return File(objectName).bufferedReader().readLines().joinToString()
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(LocalFileSystemImpl::class.java)
    }
}