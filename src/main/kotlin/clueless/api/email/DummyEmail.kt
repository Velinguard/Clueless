package clueless.api.email

class DummyEmail: Email {
    override fun sendEmail(to: String, did: String, masterSecretId: String) {
        println("Would have sent email to $to")
    }
}