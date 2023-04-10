package com.mediscreen.patientmicroservice.test_repository;

import com.mediscreen.patientmicroservice.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientTestRepository extends JpaRepository<Patient, Long> {
}
