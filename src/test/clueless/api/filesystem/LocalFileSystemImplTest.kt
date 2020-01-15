package api.filesystem

import clueless.api.controllers.ProverController
import clueless.api.filesystem.FileSystem
import clueless.api.filesystem.LocalFileSystemImpl
import com.google.gson.Gson
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalFileSystemImplTest {
    lateinit var api: FileSystem

    @BeforeTest
    fun init() {
        api = LocalFileSystemImpl()
    }

    @Test
    fun `Writing to a local file system writes the proof to a temporary file`() {
        val file = api.writeToFile("did", "proof")
        val info = Gson().fromJson(file, ProverController.S3Info::class.java)

        assertEquals("proof", File(info.fileName).readLines()[0])
        assertTrue(File(info.fileName).delete())
    }

    @Test
    fun `Reading to no file system returns the input`() {
        val file = createTempFile()
        file.writeText("proof")

        assertEquals("proof", api.readFromFile("bucketName", file.absolutePath))
    }
}