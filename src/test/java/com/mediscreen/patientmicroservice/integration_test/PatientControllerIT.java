package com.mediscreen.patientmicroservice.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.test_repository.PatientTestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2-test")
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PatientControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientTestRepository patientRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
        // @Sql(statements = "INSERT INTO patients (last_name, first_name, date_of_birth, home_address, phone_number, sex) VALUES ('LastName1', 'FirstName1', '2022-08-31', '12 rue de Paris', '121-262-9996', 'F'), ('LastName2', 'FirstName2', '2023-03-24', '36 Rue Jean Jaurès', '756-311-4166', 'M')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        // @Sql(statements = "DELETE FROM patients WHERE id = 1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testShouldReturnAllPatients() throws Exception {
        // Given
        LocalDate dateOfBirth1 = LocalDate.of(2022, 8, 31);
        LocalDate dateOfBirth2 = LocalDate.of(2023, 3, 24);

        Patient patient1 = new Patient("LastName1", "FirstName1", dateOfBirth1, "F", "21 Rue de Paris", "121-262-9996");
        Patient patient2 = new Patient("LastName2", "FirstName2", dateOfBirth2, "M", "36 Rue Jean Jaurès", "756-311-4166");
        // When
        patientRepository.save(patient1);
        patientRepository.save(patient2);

        // Then
        mockMvc.perform(get("/api/patients").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName", is("LastName1")))
                .andExpect(jsonPath("$[0].dateOfBirth", is("2022-08-31")))
                .andExpect(jsonPath("$[1].dateOfBirth", is("2023-03-24")));

        assertThat(patientRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void getPatientByIdShouldReturnPatient() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient patient = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");


        // When
        patientRepository.save(patient);

        // Then
        mockMvc.perform(get("/api/patients/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("LastName")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getPatientByIdShouldThrowPatientNotFountException() throws Exception {
        mockMvc.perform(get("/api/patients/{id}", 1))
                .andExpect(status().isNotFound());
        assertAll(
                () -> assertThat(patientRepository.findById(1L)).isEmpty(),
                () -> assertThat(patientRepository.findAll().size()).isEqualTo(0)
        );
    }

    @Test
    void getPatientByLastName() throws Exception {
        // Given
        LocalDate dateOfBirth1 = LocalDate.of(2022, 8, 31);
        LocalDate dateOfBirth2 = LocalDate.of(2023, 3, 24);

        Patient patient1 = new Patient("LastName1", "FirstName1", dateOfBirth1, "F", "21 Rue de Paris", "121-262-9599");
        Patient patient2 = new Patient("LastName2", "FirstName2", dateOfBirth2, "M", "36 Rue Jean Jaurès", "756-311-5416");

        patientRepository.saveAll(List.of(patient1, patient2));

        // Then
        mockMvc.perform(get("/api/patient")
                        .param("lastName", "LastName1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.homeAddress", is("21 Rue de Paris")));
    }

    @Test
    void getPatientByLastNameShouldThrowPatientNotFoundException() throws Exception {

        mockMvc.perform(get("/api/patient")
                        .param("lastName", "LastName"))
                .andExpect(status().isNotFound());
        assertAll(
                () -> assertThat(patientRepository.findByLastName("LastName")).isEmpty(),
                () -> assertThat(patientRepository.findAll().size()).isEqualTo(0)
        );
    }

    @Test
    void addPatientShouldBeSuccessful() throws Exception {

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lastName\":\"LastName\",\"firstName\":\"FirstName\",\"dateOfBirth\":\"2023-04-12\",\"sex\":\"F\",\"homeAddress\":\"21 Rue de Paris\",\"phoneNumber\":\"121-262-9599\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/patients/1"))
                .andExpect(jsonPath("$.phoneNumber", is("121-262-9599")));

    }

    @Test
    void addPatientShouldSuccessfulWithObjectMapperJackson() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient newPatient = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/patients/1"))
                .andExpect(jsonPath("$.dateOfBirth", is("2023-04-12")));

    }

    @Test
    void addPatientShouldReturnInvalidParameterException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient newPatient = new Patient("", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPatientShouldThrowPatientAlreadyExistException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient newPatient = new Patient("ExistingLastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // When
        patientRepository.save(newPatient);

        // Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isBadRequest());

        assertAll(
                () -> assertThat(patientRepository.findByLastName("ExistingLastName")).isPresent(),
                () -> assertThat(patientRepository.findAll().size()).isEqualTo(1)
        );
    }

    @Test
    void updatePatientByIdShouldBeSuccessful() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient patientToUpdate = new Patient(1L, "LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // When
        Patient existingPatient = patientRepository.save(patientToUpdate);
        existingPatient.setSex("M");
        existingPatient.setFirstName("UpdateFirstName");

        // Then
        mockMvc.perform(put("/api/patients/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientToUpdate)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePatientByIdShouldThrowPatientAlreadyExistException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);
        LocalDate existingDateOfBirth = LocalDate.of(2023, 4, 11);

        Patient patientToUpdate = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");
        Patient existingPatient = new Patient("ExistingLastName", "ExistingFirstName", existingDateOfBirth, "M", "25 Rue Jean Jaurès", "352-262-8799");

        patientRepository.save(patientToUpdate);
        patientRepository.save(existingPatient);

        Patient patientUpdating = new Patient();
        patientUpdating.setLastName("ExistingLastName");
        patientUpdating.setFirstName("NewFirstName");
        patientUpdating.setSex("F");
        patientUpdating.setPhoneNumber("121-262-9599");
        patientUpdating.setDateOfBirth(dateOfBirth);
        patientUpdating.setHomeAddress("22 Rue de Paris");

        // Then
        mockMvc.perform(put("/api/patients/{id}", patientToUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientUpdating)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void updatePatientByIdShouldThrowInvalidParameterException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient patientToUpdate = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        Patient patientUpdating = patientRepository.save(patientToUpdate);

        // When
        patientUpdating.setSex("FFF");

        // Then
        mockMvc.perform(put("/api/patients/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientToUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePatientByIdWithSuccess() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);
        Long id = 1L;
        Patient patientDeleted = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        Patient patientSaved = patientRepository.save(patientDeleted);

        // When
        mockMvc.perform(delete("/api/patients/{id}", patientSaved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Patient with id:" + id + " has been successfully deleted from DB!"));

    }

    @Test
    void deletePatientByIdThrowPatientNotFoundException() throws Exception {
        // Given
        Long id = 1L;

        // When
        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Patient with id:{%d} doesn't exist in DB!".formatted(id)))
                .andExpect(jsonPath("$.description").value("uri=/api/patients/%d".formatted(id)));
    }
}
