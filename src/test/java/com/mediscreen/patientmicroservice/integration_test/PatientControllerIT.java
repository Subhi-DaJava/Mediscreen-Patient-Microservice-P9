package com.mediscreen.patientmicroservice.integration_test;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.service.PatientService;
import com.mediscreen.patientmicroservice.test_repository.PatientTestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mysql-test")
@AutoConfigureMockMvc
public class PatientControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private PatientTestRepository patientRepository;


    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    void tearDown() {
        patientRepository.deleteAll();
    }

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
}
