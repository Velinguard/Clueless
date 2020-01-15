package api.filesystem

import clueless.api.controllers.ProverController
import clueless.api.filesystem.AmazonS3Impl
import clueless.api.filesystem.FileSystem
import clueless.api.filesystem.NoFileSystemImpl
import clueless.api.filesystem.TemporaryFileImpl
import clueless.api.time.Time
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectResult
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.google.gson.Gson
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.io.BufferedReader
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class AmazonS3ImplTest {
    lateinit var api: FileSystem
    var mock = Mockito.mock(AmazonS3::class.java)
    var time = Mockito.mock(Time::class.java)
    var tmpFile = Mockito.mock(TemporaryFileImpl::class.java)

    @BeforeTest
    fun init() {
        api = AmazonS3Impl(mock, time, tmpFile)
    }

    @Test
    fun `Writing to AWS file system returns the bucket and object name`() {
        val file = createTempFile()
        val fileName = "didtime.json"
        val bucketName = "zero-knowledge-proof-json-files"

        `when`(
                tmpFile.getTempFile()
        ).thenReturn(file)

        `when`(
                time.getTime()
        ).thenReturn("time")

        `when`(
                mock.putObject(
                        bucketName,
                        fileName,
                        file
                )
        ).thenReturn(PutObjectResult())

        val returnedInfo = Gson().fromJson(api.writeToFile("did", "proof"), ProverController.S3Info::class.java)

        assertEquals(fileName, returnedInfo.fileName)
        assertEquals(bucketName, returnedInfo.bucketName)
        assertEquals("proof", file.readLines()[0])
        assertTrue(file.delete())
    }
//    var s3File = Mockito.mock(S3Object::class.java)
//    var s3ObjectResult = Mockito.mock(S3ObjectInputStream::class.java)
//    var bufferedReader = Mockito.mock(BufferedReader::class.java)
//
//    @Test
//    fun `Reading to no file system returns the input`() {
//        `when`(
//                mock.getObject("bucketname", "proof")
//        ).thenReturn(s3File)
//
//        `when`(
//                s3File.objectContent
//        ).thenReturn(
//                s3ObjectResult
//        )
//
//        `when`(
//                s3ObjectResult.bufferedReader()
//        ).thenReturn(
//                bufferedReader
//        )
//
//        `when`(
//                bufferedReader.use(BufferedReader::readText)
//        ).thenReturn(
//                "contents"
//        )
//
//        assertEquals("contents", api.readFromFile("bucketName", "proof"))
//    }
}