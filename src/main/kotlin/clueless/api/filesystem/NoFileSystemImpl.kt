package clueless.api.filesystem

class NoFileSystemImpl: FileSystem {
    // Returns the data that should be in the file
    override fun readFromFile(bucketName: String, objectName: String): String {
        return objectName
    }

    // Returns the data that would be written to the file
    override fun writeToFile(proverDID: String, proof: String): String? {
        return proof
    }
}