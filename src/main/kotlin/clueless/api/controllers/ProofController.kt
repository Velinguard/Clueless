package clueless.api.controllers

import clueless.api.licences.Licences
import clueless.api.proofs.Proofs
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("proof")
class ProofController {
    @ApiOperation(value = "A list of licences available")
    @GetMapping("get-licence-type")
    fun getLicences(): List<String> {
        return Licences.values().map { it.licence }
    }

    @ApiOperation(value = "A list of available proofs for a licence")
    @GetMapping("get-proofs")
    fun getProofs(licence: String): List<String> {
        return Licences.getProofs(licence)
    }

    @Deprecated("The Client should never need the Proof JSON")
    @ApiOperation(value = "A proof json for a given proof")
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "Proof JSON for the given proof"),
        ApiResponse(code = 404, message = "Error proof not found, use get-proofs to get the correct proof name")
    ])
    @GetMapping("get-proof")
    fun getProofJson(proof: String): ResponseEntity<String> {
        return Proofs.values().find { it.proofName == proof }?.proof?.let { ResponseEntity.ok(it.getJsonObject()) }
                ?: ResponseEntity.notFound().build()
    }

    @Deprecated("No longer part of the pipeline")
    @GetMapping("get-proof-request")
    fun getProofRequest(): String {
        return Proofs.AGE_OVER_18.proof.getJsonObject()
    }
}