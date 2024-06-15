package com.example.mypractice.data

import com.example.mypractice.model.DoctorModel

//holds the data for the currently logged in doctor
object DoctorDataHolder {
    private var loggedInDoctor: DoctorModel? = null

    fun setLoggedInDoctor(doctor: DoctorModel) {
        loggedInDoctor = doctor
    }

    fun getLoggedInDoctor(): DoctorModel? {
        return loggedInDoctor
    }
}
