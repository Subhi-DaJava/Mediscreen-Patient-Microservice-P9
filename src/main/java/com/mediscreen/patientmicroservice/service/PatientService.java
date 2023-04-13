package com.mediscreen.patientmicroservice.service;

import com.mediscreen.patientmicroservice.domain.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> getPatients();
    Patient getPatientById(Long id);
    Patient getPatientByLastName(String lastName);
    Patient addPatient(Patient patient);
    void updatePatientById(Long id, Patient patient);
}
