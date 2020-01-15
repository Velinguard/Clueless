package api.filesystem

import clueless.api.filesystem.FileSystem
import clueless.api.filesystem.NoFileSystemImpl
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import kotlin.test.assertEquals

class NoFileSystemImplTest {
    lateinit var api: FileSystem

    @BeforeTest
    fun init() {
        api = NoFileSystemImpl()
    }

    @Test
    fun `Writing to no file system returns the input`() {
        assertEquals("proof", api.writeToFile("did", "proof"))
    }

    @Test
    fun `Reading to no file system returns the input`() {
        assertEquals("proof", api.readFromFile("bucketName", "proof"))
    }
}