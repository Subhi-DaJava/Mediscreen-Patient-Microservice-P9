package com.mediscreen.patientmicroservice.controller;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    @MockBean
    private PatientService patientService;

    private List<Patient> patients;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
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
}