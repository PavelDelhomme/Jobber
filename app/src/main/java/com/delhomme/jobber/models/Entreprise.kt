package com.delhomme.jobber.models

import java.util.UUID

data class Entreprise(
    val id: String = UUID.randomUUID().toString(),
    val nom: String,
    var contacts: MutableList<Contact> = mutableListOf(),
    var entretiens: MutableList<Entretien> = mutableListOf()
) {
    override fun toString(): String {
        return nom
    }
}
