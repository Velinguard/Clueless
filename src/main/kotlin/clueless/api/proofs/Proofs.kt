package clueless.api.proofs

import clueless.api.licences.DrivingLicence
import clueless.api.licences.Identification
import clueless.api.licences.LicenceImpl
import clueless.api.licences.Ticket

enum class Proofs(val proofName: String, val proof: Proof, val type: LicenceImpl) {
    AGE_OVER_18(
            "Person is over the age of 18",
            Proof(
                    nonce = "${Math.random().toInt()}",
                    name = "driving-licence-age-over-18",
                    version = "0.1",
                    requestedAttributes = mutableListOf(
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "name",
                                            restrictions = Restrictions(
                                                schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                                issuer = null
                                            )),
                                    revealed = true),
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "age", restrictions = Restrictions(
                                            schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                            issuer = null
                                    )),
                                    revealed = false)
                    ),
                    requestedPredicates = mutableListOf(RequestedPredicates(
                            predicate = Predicate(
                                    name = "age",
                                    type = ">=",
                                    value = 18,
                                    restrictions = Restrictions(
                                            schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                            issuer = null
                                    )
                            )
                    )),
                    credDefIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:3:CL:10:Tag1"),
                    schemaIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0")

            ),
            Identification()
    ),
    LICENCE_LEVEL_GREATER_THAN_0(
            "Has a licence",
            Proof(
                    nonce = "${Math.random().toInt()}",
                    name = "driving-licence-has-a-licence",
                    version = "0.1",
                    requestedAttributes = mutableListOf(
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "name",
                                            restrictions = Restrictions(
                                                    schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                                    issuer = null
                                            )),
                                    revealed = true),
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "license-level",
                                            restrictions = Restrictions(
                                                    schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                                    issuer = null
                                            )),
                                    revealed = false)),                    requestedPredicates = mutableListOf(RequestedPredicates(
                            predicate = Predicate(
                                    name = "licence-level",
                                    type = ">=",
                                    value = 1,
                                    restrictions = Restrictions(
                                            schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                            issuer = null
                                    )
                            )
                    )),
                    credDefIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:3:CL:10:Tag1"),
                    schemaIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0")
            ),
            DrivingLicence()
    ),
    HAS_TICKET_AND_OVER_18(
            "Has ticket and is over 18",
            Proof(
                    nonce = "${Math.random().toInt()}",
                    name = "ticket-holder-age-over-18",
                    version = "0.1",
                    requestedAttributes = mutableListOf(
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "name",
                                            restrictions = Restrictions(
                                                    schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                                    issuer = null
                                            )),
                                    revealed = true),
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "age", restrictions = Restrictions(
                                            schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                            issuer = null
                                    )),
                                    revealed = false),
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "ticket-level", restrictions = Restrictions(
                                            schemaId = "Th7MpTaRZVRYnPiabds81Y:2:tickets-schema:1.0",
                                            issuer = null
                                    )),
                                    revealed = false)
                    ),
                    requestedPredicates = mutableListOf(
                            RequestedPredicates(
                                    Predicate(
                                            name = "age",
                                            type = ">=",
                                            value = 18,
                                            restrictions = Restrictions(
                                                    schemaId = "Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0",
                                                    issuer = null
                                            )                                    )
                            ),
                            RequestedPredicates(
                                    Predicate(
                                            name = "ticket-level",
                                            type = ">=",
                                            value = 0,
                                            restrictions = Restrictions(
                                                    schemaId = "Th7MpTaRZVRYnPiabds81Y:2:tickets-schema:1.0",
                                                    issuer = null
                                            )                                    )
                            )),
                            credDefIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:3:CL:10:Tag1","Th7MpTaRZVRYnPiabds81Y:3:CL:557:Tag1"),
                            schemaIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:2:driving-schema:1.0", "Th7MpTaRZVRYnPiabds81Y:2:tickets-schema:1.0")
            ),
            Identification()
    ),
    TICKET_DATE_MATCHES(
            "Ticket Date Matches",
            Proof(
                    nonce = "${Math.random().toInt()}",
                    name = "ticket-date-matches",
                    version = "0.1",
                    requestedAttributes = mutableListOf(
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "name",
                                            restrictions = Restrictions(
                                                    schemaId = "Th7MpTaRZVRYnPiabds81Y:2:tickets-schema:1.0",
                                                    issuer = null
                                            )),
                                    revealed = true),
                            RequestedAttributes(
                                    attribute = Attribute(
                                            name = "ticket-level", restrictions = Restrictions(
                                            schemaId = "Th7MpTaRZVRYnPiabds81Y:2:tickets-schema:1.0",
                                            issuer = null
                                    )),
                                    revealed = false)
                    ),
                    requestedPredicates = mutableListOf(
                            RequestedPredicates(
                                    Predicate(
                                            name = "ticket-level",
                                            type = ">=",
                                            value = 0,
                                            restrictions = Restrictions(
                                                    schemaId = "Th7MpTaRZVRYnPiabds81Y:2:tickets-schema:1.0",
                                                    issuer = null
                                            )                                    )
                            )),
                    credDefIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:3:CL:557:Tag1"),
                    schemaIds = mutableListOf("Th7MpTaRZVRYnPiabds81Y:2:tickets-schema:1.0")),
            Ticket()
    ),
}
