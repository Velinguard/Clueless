package clueless.api.email

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import org.springframework.stereotype.Component

class AWSEmailImpl(val client: AmazonSimpleEmailService): Email {
    val FROM = "clueless1037@gmail.com"

    // The subject line for the email.
    val SUBJECT = "Clueless"

    override fun sendEmail(to: String, did: String, masterSecretId: String) {
        // The HTML body for the email.
        val HTMLBODY = ("<h1>Clueless</h1>"
                + "<ul><li>Your personal Did: $did</li><li>Your master secret ID: $masterSecretId</li></ul>")
        val TEXTBODY = "Your personal DiD is $did, your master secret id is $masterSecretId"

        try {
            val request = SendEmailRequest()
                    .withDestination(
                            Destination().withToAddresses(to))
                    .withMessage(Message()
                            .withBody(Body()
                                    .withHtml(Content()
                                            .withCharset("UTF-8").withData(HTMLBODY))
                                    .withText(Content()
                                            .withCharset("UTF-8").withData(TEXTBODY)))
                            .withSubject(Content()
                                    .withCharset("UTF-8").withData(SUBJECT)))
                    .withSource(FROM)
            // Comment or remove the next line if you are not using a
            // configuration set
            // .withConfigurationSetName(CONFIGSET);
            client.sendEmail(request)
        } catch (ex: Exception) {
            println("The email was not sent. Error message: " + ex.message)
        }
    }
}