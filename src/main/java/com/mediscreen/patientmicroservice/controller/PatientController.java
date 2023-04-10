package com.mediscreen.patientmicroservice.controller;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        logger.debug("getAllPatients from PatientController starts here");
        return ResponseEntity.ok(patientService.getPatients());
    }
}
