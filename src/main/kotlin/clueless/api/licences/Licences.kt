package clueless.api.licences

enum class Licences(val licence: String, val type: Licence, val schema: String) {
    IDENTIFICATION("Identification", Identification(), "id-schema"),
    DRIVINGLICENCE("Driving Licence", DrivingLicence(), "driving-schema"),
    TICKET("Ticket", Ticket(), "tickets-schema");

    companion object {
        fun getProofs(licence: String? = null): List<String> {
            return values()
                    .filter { li -> if (licence != null) { li.licence == licence } else { true }}
                    .flatMap { it.type.getProofs() }
                    .map { it.proofName }
        }

        fun getLicence(licence: String? = null): List<Licence> {
            return values()
                    .filter { li -> if (licence != null) { li.licence == licence } else { true }}
                    .map { it.type }
        }
    }
}