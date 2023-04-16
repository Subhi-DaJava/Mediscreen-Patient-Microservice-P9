package com.mediscreen.patientmicroservice.repository;

import com.mediscreen.patientmicroservice.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<List<Patient>> findByLastName(String lastName);
}
