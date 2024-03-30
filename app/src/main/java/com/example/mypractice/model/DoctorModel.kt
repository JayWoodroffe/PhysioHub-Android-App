package com.example.mypractice.model
//model for retrieving existing doctor data and adding new doctors
data class DoctorModel(

    val name: String? = "",
    val email: String? = "",
    val number: String? = "",
    val certId: String? = "",
    val practiceId: String? = ""

) {
    constructor() : this("", "", "", "",  "") {}
}


