package clueless.api.licences

import com.google.common.hash.Hashing
import com.google.gson.annotations.SerializedName
import java.math.BigInteger
import java.nio.charset.StandardCharsets

class Attribute() {
    @SerializedName("raw")
    lateinit var rawValue: String
    @SerializedName("encoded")
    lateinit var encoded: String

    private fun hash(string: String): BigInteger {
        return Hashing.sha256().hashString(string, StandardCharsets.UTF_8).asLong().toBigInteger()
    }

    constructor(value: String) : this() {
        this.rawValue = value
        this.encoded = "${hash(value)}"
    }

    constructor(value: Int) : this() {
        this.rawValue = "${value}"
        this.encoded = "${value}"
    }
}