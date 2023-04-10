package com.mediscreen.patientmicroservice.repository;

import com.mediscreen.patientmicroservice.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
