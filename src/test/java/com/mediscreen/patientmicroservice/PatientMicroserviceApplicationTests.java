package com.mediscreen.patientmicroservice;

import com.mediscreen.patientmicroservice.controller.PatientController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PatientMicroserviceApplicationTests {
    @Autowired
    private PatientController patientController;

    @Test
    void contextLoads() {
        assertThat(patientController).isNotNull();
    }

}
