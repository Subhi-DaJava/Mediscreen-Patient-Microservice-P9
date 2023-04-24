package com.mediscreen.patientmicroservice.service;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.exceptions.PatientAlreadyExistException;
import com.mediscreen.patientmicroservice.exceptions.PatientNotFoundException;
import com.mediscreen.patientmicroservice.repository.PatientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Test
    void testGetPatientByIdShouldReturnPatient() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9996");

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));

        // When
        Patient patientById = patientService.getPatientById(1L);

        // Then
        assertThat(patientById.getPhoneNumber()).isEqualTo(patient.getPhoneNumber());
        verify(patientRepository).findById(anyLong());
    }

    @Test
    void testGetPatientByIdShouldThrowsException() {
        // Given
        when(patientRepository.findById(anyLong())).thenThrow(new PatientNotFoundException("This Patient doesn't exist in DB!"));

        // When

        // Then
        assertThatThrownBy(() -> patientService.getPatientById(anyLong()));
        verify(patientRepository).findById(anyLong());
    }

    @Test
    void testGetPatientByLastNameShouldReturnPatient() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9996");

        when(patientRepository.findByLastName(anyString())).thenReturn(Optional.of(patient));

        // When
        Patient patientByLastName = patientService.getPatientByLastName("LastName");

        // Then
        assertThat(patientByLastName.getDateOfBirth()).isEqualTo("2023-04-11");
        verify(patientRepository).findByLastName(anyString());
    }

    @Test
    void testGetPatientByLastNameShouldThrowsException() {
        // Given
        when(patientRepository.findByLastName(anyString())).thenThrow(new PatientNotFoundException("Patient doesn't existe in DB!"));

        // Then
        assertThatThrownBy(() -> patientService.getPatientByLastName(anyString()));
        verify(patientRepository).findByLastName(anyString());

    }

    @Test
    void testAddPatientWithSuccess() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9996");

        when(patientRepository.save(any())).thenReturn(patient);

        // When
        Patient patientSaved = patientService.addPatient(patient);

        // Then
        assertThat(patientSaved.getPhoneNumber()).isEqualTo(patient.getPhoneNumber());
        verify(patientRepository).save(any());
    }

    @Test
    void testAddPatientWithNullPatientObject() {
        // Given
        when(patientRepository.save(new Patient())).thenThrow(new InvalidParameterException("Patient should not be null"));
        // Then
       assertThatThrownBy(() -> patientService.addPatient(new Patient()));
    }

    @Test
    void testAddPatientWithLastNameAlreadyExistingShouldThrowPatientAlreadyExistException() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "ExistingLastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9996");

        when(patientRepository.save(patient)).thenThrow(new PatientAlreadyExistException("Patient with lastName:{ExistingLastName} already exits in DB"));

        // Then
        assertThatThrownBy(() -> patientService.addPatient(patient));

    }
    @Test
    void testAddPatientWithEmptyLastNameShouldThrowInvalidParameterException() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9996");

        when(patientRepository.save(patient)).thenThrow(new InvalidParameterException("Last name should not be empty or null"));

        // Then
        assertThatThrownBy(() -> patientService.addPatient(patient));

    }

    @Test
    void testAddPatientWithEmptyPhoneNumberShouldThrowInvalidParameterException() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "");

        when(patientRepository.save(patient)).thenThrow(new InvalidParameterException("Phone number is mandatory"));

        // Then
        assertThatThrownBy(() -> patientService.addPatient(patient));
        // verify(patientRepository, never()).save(patient);
    }

    @Test
    void testAddPatientWithPhoneNumberInvalidFormatShouldThrowInvalidParameterException() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262");

        when(patientRepository.save(patient)).thenThrow(new InvalidParameterException("Phone number should be with format: xxx-xxx-xxxx"));

        // Then
        assertThatThrownBy(() -> patientService.addPatient(patient));
    }

    @Test
    void testUpdatePatientByIdWithSuccess() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9996");

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));

        // When
        patient.setPhoneNumber("333-444-5555");
        patientService.updatePatientById(1L, patient);

        // Then
        assertThat(patient.getPhoneNumber()).isEqualTo("333-444-5555");
        verify(patientRepository).findById(anyLong());
    }

    @Test
    void testUpdateByIdWithLastNameAlreadyExisting() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9996");

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));

        // When
        patient.setLastName("existingLastName");
        when(patientRepository.findByLastName("existingLastName")).thenThrow(new PatientAlreadyExistException("Another Patient with this lastName already exists in DB!"));

        // Then
        assertThatThrownBy(()-> patientService.updatePatientById(anyLong(), patient));
        verify(patientRepository).findById(anyLong());
    }

    @Test
    void testUpdatePatientByIdWithInvalidPhoneNumberFormatShouldThrowInvalidParameterException() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient patient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-5555");
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        // When
        patient.setPhoneNumber("222-222-");
        when(patientRepository.save(patient)).thenThrow(new InvalidParameterException("Phone Number format should be xxx-xxx-xxxx"));

        // Then
        assertThatThrownBy(()-> patientService.updatePatientById(1L, patient));
        verify(patientRepository).findById(anyLong());
    }

    @Test
    public void testUpdatePatientByIdDuplicateLastNameShouldReturnPatientAlreadyExistException() {
        // When
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient updatedPatient = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-5555");

        Patient existingPatient = new Patient(2L, "LastName", "FirstNameExiting", dateOfBirth, "M", "35 Rue de Paris", "563-262-5556");


        // When
        when(patientRepository.findById(1L)).thenReturn(Optional.of(updatedPatient));
        when(patientRepository.findByLastName("LastName")).thenReturn(Optional.of(existingPatient));

        // Then
        assertThatThrownBy(() -> patientService.updatePatientById(1L, updatedPatient))
                .isInstanceOf(PatientAlreadyExistException.class)
                .hasMessage("Patient with lastName:{LastName} already exists in DB");

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    public void testUpdatePatientByIdShouldReturnPatientNotFoundException() {
        // Given
        when(patientRepository.findById(1L)).thenThrow(new PatientNotFoundException("Patient with id:{1} doesn't exist in DB!"));

        // Then
        assertThatThrownBy(() -> patientService.updatePatientById(1L, any(Patient.class)));
    }

    @Test
    void testUpdatePatientByIdWithNonExistingPatient() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 11);
        Patient updatedPatient = new Patient("LastNameUpdated", "FirstNameUpdated", dateOfBirth, "M", "25 Rue de Paris", "121-262-9996");
        Long id = 1L;

        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> patientService.updatePatientById(id, updatedPatient))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("Patient with id:{%d} doesn't exist in DB!".formatted(id));
    }

    @Test
    void deletePatientByIdWithSuccess() {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 15);
        Patient patient = new Patient(1L,"LastNameUpdated", "FirstNameUpdated", dateOfBirth, "M", "25 Rue de Paris", "121-262-9996");
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).deleteById(1L);

        // When
        Patient patientDeleted = patientService.deletePatientById(1L);

        // Then
        assertThat(patientDeleted.getPhoneNumber()).isEqualTo(patient.getPhoneNumber());
    }
    @Test
    void deletePatientByIdWithNotExistingPatientShouldThrowPatientNotFoundException() {
        // Given
        Long id = 5L;
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> patientService.deletePatientById(5L))
                .isInstanceOf(PatientNotFoundException.class)
                .hasMessageContaining("Patient with id:{%d} doesn't exist in DB!".formatted(id));
    }

}