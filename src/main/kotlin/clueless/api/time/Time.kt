package clueless.api.time

import java.time.Instant
import java.time.format.DateTimeFormatter

open class Time {
    open fun getTime(): String? {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now())
    }
}