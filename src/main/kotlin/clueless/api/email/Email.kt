package clueless.api.email

interface Email {
    fun sendEmail(to: String, did: String, masterSecretId: String)
}