package clueless.api.controllers

import clueless.api.controllers.models.Person
import clueless.api.email.AWSEmailImpl
import clueless.api.email.Email
import clueless.api.issuer.CredentialDefinition
import clueless.api.issuer.IssuerWrapper
import clueless.api.licences.DrivingLicence
import clueless.api.licences.Licence
import clueless.api.licences.Licences
import clueless.api.licences.Ticket
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception
import java.time.LocalDate

@RestController("issuer")
class IssuerController(
        @Autowired var issuerWrapper: IssuerWrapper,
        @Autowired var emailClient: Email,
        @Autowired var exceptionHandler: ExceptionHandler
) {
    companion object {
        val LOGGER = LoggerFactory.getLogger(IssuerController::class.java)
    }

    // Issuing Driving Licences
    @ApiOperation(value = "Issues a Driving Licence to a prover, emailing the response")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The credentials were emailed."),
        ApiResponse(code = 404, message = "Error issuer not found"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-email")
    fun issuerEmailCreatedCredentials(
            email: String,
            issuerWalletId: String,
            issuerWalletKey: String,
            proverWalletId: String,
            proverWalletKey: String,
            credDefId: String,
            name: String,
            dateOfBirth: String,
            licenceLevel: String,
            issuerDid: String? = null,
            proverDid: String? = null
    ): ResponseEntity<String?> {
        IssuerController.LOGGER.info("Issuing Driving Licence credentials for $name by $issuerWalletId")
        return try {
            issuerWrapper.issuerCreateCredentials(
                    issuerWalletId,
                    issuerWalletKey,
                    proverWalletId,
                    proverWalletKey,
                    proverDid,
                    issuerDid,
                    credDefId,
                    DrivingLicence(
                            name,
                            LocalDate.parse(dateOfBirth),
                            licenceLevel.toInt()
                    )
            )?.let {
                emailClient.sendEmail(email, it.personDid, it.masterSecretId)
            }
            ResponseEntity.ok(
                    "Successfully emailed Credentials"
            )
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Issuing a Driving Licence")
        }
    }

    /**
     * Create a credential given a provers wallet details and the relevant parameters
     * for the new driving license
     */
    @ApiOperation(value = "Issues a Driving Licence to a prover")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The master secret-id and DiD associated with the Driving Licence credentials.",
                response = EmailInfo::class),
        ApiResponse(code = 404, message = "Error issuer not found"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create")
    fun issuerCreateCredentials(
            issuerWalletId: String,
            issuerWalletKey: String,
            proverWalletId: String,
            proverWalletKey: String,
            credDefId: String,
            name: String,
            dateOfBirth: String,
            licenceLevel: String,
            issuerDid: String? = null,
            proverDid: String? = null
    ): ResponseEntity<*> {
        IssuerController.LOGGER.info("Issuing Driving Licence credentials for $name by $issuerWalletId")
        return try {
            ResponseEntity.ok(
                    issuerWrapper.issuerCreateCredentials(
                            issuerWalletId,
                            issuerWalletKey,
                            proverWalletId,
                            proverWalletKey,
                            proverDid,
                            issuerDid,
                            credDefId,
                            DrivingLicence(
                                    name,
                                    LocalDate.parse(dateOfBirth),
                                    licenceLevel.toInt()
                            )
                    )
            )
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Issuing a Driving Licence")
        }
    }

    // Issuing Tickets
    @ApiOperation(value = "Issues a ticket to a prover, emailing the response")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The credentials were emailed."),
        ApiResponse(code = 404, message = "Error issuer not found"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-email-ticket")
    fun issuerEmailCreatedTicketCredentials(
            email: String,
            issuerWalletId: String,
            issuerWalletKey: String,
            proverWalletId: String,
            proverWalletKey: String,
            credDefId: String,
            name: String,
            ticketLevel: String,
            issuerDid: String? = null,
            proverDid: String? = null
    ): ResponseEntity<String?> {
        IssuerController.LOGGER.info("Issuing Ticket credentials for $name by $issuerWalletId")
        return try {
            val credentials = issuerWrapper.issuerCreateCredentials(
                    issuerWalletId,
                    issuerWalletKey,
                    proverWalletId,
                    proverWalletKey,
                    proverDid,
                    issuerDid,
                    credDefId,
                    Ticket(
                            name,
                            ticketLevel.toInt()
                    )
            )
            credentials?.let { emailClient.sendEmail(email, it.personDid, it.masterSecretId) }
            ResponseEntity.ok(
                    "Successfully emailed credentials"
            )
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Creating a new Ticket credential")
        }
    }

    @ApiOperation(value = "Issues a ticket to a prover")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "The master secret-id and DiD associated with the Ticket credentials.",
                response = EmailInfo::class),
        ApiResponse(code = 404, message = "Error issuer not found"),
        ApiResponse(code = 403, message = "Error incorrect id-key pair"),
        ApiResponse(code = 409, message = "Error user is already performing an operation"),
        ApiResponse(code = 422, message = "Error processing request")
    ])
    @PutMapping("create-ticket")
    fun issuerCreateTicketCredentials(
            issuerWalletId: String,
            issuerWalletKey: String,
            proverWalletId: String,
            proverWalletKey: String,
            credDefId: String,
            name: String,
            ticketLevel: String,
            issuerDid: String? = null,
            proverDid: String? = null
    ): ResponseEntity<*> {
        IssuerController.LOGGER.info("Issuing Ticket credentials for $name by $issuerWalletId")
        return try {
            ResponseEntity.ok(
                    issuerWrapper.issuerCreateCredentials(
                            issuerWalletId,
                            issuerWalletKey,
                            proverWalletId,
                            proverWalletKey,
                            proverDid,
                            issuerDid,
                            credDefId,
                            Ticket(
                                    name,
                                    ticketLevel.toInt()
                            )
                    )
            )
        } catch (e: Exception) {
            exceptionHandler.handleWalletException(e, "Creating a new Ticket credential")
        }
    }

    data class EmailInfo(
            val personDid: String,
            val masterSecretId: String
    ) {
        override fun equals(other: Any?): Boolean {
            return (other is EmailInfo &&
                    personDid == other.personDid &&
                    masterSecretId == other.masterSecretId)
        }
    }

    @Deprecated("No longer needed")
    @PutMapping("get-credential-definition")
    fun getCredentialDefinition(
            prover: Person,
            credDefId: String
    ): CredentialDefinition {
        return issuerWrapper.getCredentialDefinition(prover, credDefId)
    }
}