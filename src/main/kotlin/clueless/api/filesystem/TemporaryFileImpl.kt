package clueless.api.filesystem

import java.io.File

open class TemporaryFileImpl {
    open fun getTempFile(): File {
        return createTempFile()
    }
}