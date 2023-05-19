package com.mediscreen.patientmicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.exceptions.PatientAlreadyExistException;
import com.mediscreen.patientmicroservice.exceptions.PatientNotFoundException;
import com.mediscreen.patientmicroservice.service.PatientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private PatientService patientService;

    private List<Patient> patients;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    @AfterEach
    void tearDown() {
        if(patients != null) {
            patients.clear();
        }
    }
    @Test
    void testShouldReturnAllPatients() throws Exception {
        // Given
        LocalDate dateOfBirth1 = LocalDate.of(2022, 8, 31);
        LocalDate dateOfBirth2 = LocalDate.of(2023, 3, 24);

        Patient patient1 = new Patient(1L, "LastName1", "FirstName1", dateOfBirth1, "F", "21 Rue de Paris", "121-262-9599");
        Patient patient2 = new Patient(2L, "LastName2", "FirstName2", dateOfBirth2, "M", "36 Rue Jean Jaurès", "756-311-5416");

        patients = new ArrayList<>(List.of(patient1, patient2));
        // When
        when(patientService.getPatients()).thenReturn(patients);

        // Then
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName", is("LastName1")));

    }

    @Test
    void getPatientByIdShouldReturnPatient() throws Exception {
        // Given
        LocalDate dateOfBirth1 = LocalDate.of(2022, 8, 31);
        LocalDate dateOfBirth2 = LocalDate.of(2023, 3, 24);

        Patient patient1 = new Patient(1L, "LastName1", "FirstName1", dateOfBirth1, "F", "21 Rue de Paris", "121-262-9599");
        Patient patient2 = new Patient(2L, "LastName2", "FirstName2", dateOfBirth2, "M", "36 Rue Jean Jaurès", "756-311-5416");

        patients = new ArrayList<>(List.of(patient1, patient2));
        // When
        when(patientService.getPatientById(anyLong())).thenReturn(patient2);

        // Then
        mockMvc.perform(get("/api/patients/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("LastName2")));
        verify(patientService).getPatientById(anyLong());
    }

    @Test
    void getPatientByIdShouldThrowPatientNotExistingException() throws Exception {
        // Given
        Long id = 2L;
        when(patientService.getPatientById(anyLong())).thenThrow(new PatientNotFoundException("Patient with id:{%d} doesn't exist in DB!".formatted(id)));

        // Then
        mockMvc.perform(get("/api/patients/{id}", 2))
                .andExpect(status().isNotFound());

        verify(patientService).getPatientById(anyLong());

    }

    @Test
    void getPatientByLastName() throws Exception {
        // Given
        LocalDate dateOfBirth1 = LocalDate.of(2022, 8, 31);
        LocalDate dateOfBirth2 = LocalDate.of(2023, 3, 24);

        Patient patient1 = new Patient(1L, "LastName1", "FirstName1", dateOfBirth1, "F", "21 Rue de Paris", "121-262-9599");
        Patient patient2 = new Patient(2L, "LastName2", "FirstName2", dateOfBirth2, "M", "36 Rue Jean Jaurès", "756-311-5416");

        patients = new ArrayList<>(List.of(patient1, patient2));
        // When
        when(patientService.getPatientByLastName(anyString())).thenReturn(patient1);

        // Then
        mockMvc.perform(get("/api/patient")
                        .param("lastName", "LastName1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeAddress", is("21 Rue de Paris")));
        verify(patientService).getPatientByLastName(anyString());
    }

    @Test
    void getPatientByLastNameShouldThrowPatientNotExistingException() throws Exception {
        // Given
        String lastName = "LastName";
        when(patientService.getPatientByLastName(anyString())).thenThrow(new PatientNotFoundException("Patient with lastName:{%n} doesn't exist in DB!".formatted(lastName)));

        // Then
        mockMvc.perform(get("/api/patient")
                        .param("lastName", "LastName1"))
                .andExpect(status().isNotFound());

        verify(patientService).getPatientByLastName(anyString());

    }


    @Test
    void addPatientShouldBeSuccessful() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient patient = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // When
        when(patientService.addPatient(patient)).thenReturn(patient);

        // Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lastName\":\"LastName\",\"firstName\":\"FirstName\",\"dateOfBirth\":\"2023-04-12\",\"sex\":\"F\",\"homeAddress\":\"21 Rue de Paris\",\"phoneNumber\":\"121-262-9599\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/patients/"))
                .andExpect(jsonPath("$.phoneNumber", is("121-262-9599")));

        verify(patientService).addPatient(any());
    }

    @Test
    void addPatientShouldSuccessfulWithObjectMapperJackson() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient newPatient = new Patient("LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // When
        when(patientService.addPatient(newPatient)).thenReturn(newPatient);

        // Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/patients/"))
                .andExpect(jsonPath("$.dateOfBirth", is("2023-04-12")));

        verify(patientService).addPatient(any());
    }

    @Test
    void addPatientShouldReturnInvalidParameterException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient newPatient = new Patient("", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // When
        when(patientService.addPatient(newPatient)).thenThrow(new InvalidParameterException("Last name is mandatory"));

        // Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).addPatient(any());
    }

    @Test
    void addPatientShouldThrowPatientAlreadyExistException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient newPatient = new Patient("ExistingLastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // When
        when(patientService.addPatient(newPatient)).thenThrow(new PatientAlreadyExistException("Patient with lastName:{ExistingLastName} already exits in DB"));

        // Then
        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void updatePatientByIdShouldBeSuccessful() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient patientToUpdate = new Patient(5L,"LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");
        Patient patientToUpdated = new Patient(5L,"LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        // When
        when(patientService.updatePatientById(5L, patientToUpdate)).thenReturn(patientToUpdated);

        // Then
        mockMvc.perform(put("/api/patients/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientToUpdate)))
                .andExpect(status().isOk());

        verify(patientService).updatePatientById(anyLong(),any(Patient.class));
    }

    @Test
    void updatePatientByIdShouldThrowPatientAlreadyExistException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient patientToUpdate = new Patient(5L,"ExistingLastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        doThrow(new PatientAlreadyExistException("Patient with lastName:{%n} already exists in DB".formatted(patientToUpdate.getLastName()))).when(patientService).updatePatientById(5L, patientToUpdate);

        // Then
        mockMvc.perform(put("/api/patients/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientToUpdate)))
                .andExpect(status().isBadRequest());

        // verify(patientService, never()).updatePatientById(anyLong(), any());
    }
    @Test
    void updatePatientByIdShouldThrowInvalidParameterException() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);

        Patient patientToUpdate = new Patient(5L,"ExistingLastName", "FirstName", dateOfBirth, "FFF", "21 Rue de Paris", "121-262-9599");

        doThrow(new InvalidParameterException("The size must be between 1 and 1.")).when(patientService).updatePatientById(5L, patientToUpdate);

        // Then
        mockMvc.perform(put("/api/patients/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientToUpdate)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).updatePatientById(anyLong(), any());
    }
    @Test
    void deletePatientByIdWithSuccess() throws Exception {
        // Given
        LocalDate dateOfBirth = LocalDate.of(2023, 4, 12);
        Long id = 5L;
        Patient patientDeleted = new Patient(id,"LastName", "FirstName", dateOfBirth, "F", "21 Rue de Paris", "121-262-9599");

        when(patientService.deletePatientById(anyLong())).thenReturn(patientDeleted);

        // When
        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Patient with id:" + id + " has been successfully deleted from DB!"));

        verify(patientService).deletePatientById(anyLong());
    }

    @Test
    void deletePatientByIdThrowPatientNotFoundException() throws Exception {
        // Given
        Long id = 5L;
        when(patientService.deletePatientById(anyLong())).thenThrow(new PatientNotFoundException("Patient with id:{%d} doesn't exist in DB!".formatted(id)));

        // When
        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Patient with id:{%d} doesn't exist in DB!".formatted(id)))
                .andExpect(jsonPath("$.description").value("uri=/api/patients/%d".formatted(id)));
    }
}