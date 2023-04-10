package com.mediscreen.patientmicroservice.service;

import com.mediscreen.patientmicroservice.domain.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> getPatients();
}
