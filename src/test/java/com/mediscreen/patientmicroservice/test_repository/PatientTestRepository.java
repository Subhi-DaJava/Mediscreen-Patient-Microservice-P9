package com.mediscreen.patientmicroservice.test_repository;

import com.mediscreen.patientmicroservice.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientTestRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByLastName(String lastName);

}
