package com.mediscreen.patientmicroservice.service;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.repository.PatientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private PatientServiceImpl patientService;
    private List<Patient> patients;

    @BeforeEach
    void init() {
        patients = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        patients.clear();
    }

    @Test
    void testShouldReturnAllPatients() {
        LocalDate dateOfBirth1 = LocalDate.of(2022, 8, 31);
        LocalDate dateOfBirth2 = LocalDate.of(2023, 3, 24);

        Patient patient1 = new Patient(1L, "LastName1", "FirstName1", dateOfBirth1, "F", "21 Rue de Paris", "121-262-9996");
        Patient patient2 = new Patient(2L, "LastName2", "FirstName2", dateOfBirth2, "M", "36 Rue Jean Jaurès", "756-311-4416");
        patients.add(patient1);
        patients.add(patient2);

        // Given
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        List<Patient> patientList = patientService.getPatients();

        // Then
        assertThat(patientList).isNotNull();
        assertThat(patientList.size()).isEqualTo(2);
        assertThat(patientList.get(0).getDateOfBirth()).isEqualTo("2022-08-31");

        assertThat(patientList.get(1).getPhoneNumber()).isEqualTo("756-311-4416");
    }

    @Test
    public void testGetPatientsWhenAnyPatientsInDB() {

        // Given
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        List<Patient> patients = patientService.getPatients();

        // Then

        assertThat(patients.size()).isEqualTo(0);

    }
}