package clueless.api.filesystem

import org.springframework.stereotype.Component

@Component
interface FileSystem {
    fun writeToFile(proverDID: String, proof: String): String?
    fun readFromFile(bucketName: String = "", objectName: String): String
}