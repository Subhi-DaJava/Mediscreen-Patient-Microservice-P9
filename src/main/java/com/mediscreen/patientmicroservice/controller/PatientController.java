package com.mediscreen.patientmicroservice.controller;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.exception_handler.ResponseMessage;
import com.mediscreen.patientmicroservice.exceptions.PatientNotFoundException;
import com.mediscreen.patientmicroservice.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PatientController handles all HTTP requests related to patients.
 * It exposes the patient-related APIs to the clients.
 * In Java, you could also declare the toString() method inside the Treadmill entity.
 * Note that the formatted method is a feature of Java 15.
 * If you have a lower version of Java, you can use String.format.
 */
@RestController
@RequestMapping("/api")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Retrieve all Patients
     *
     * @return Patient List from DB
     */
    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        logger.debug("getAllPatients from PatientController starts here");
        List<Patient> patients = patientService.getPatients();
        logger.info("All Patients have been successfully retrieved from PatientController");
        return ResponseEntity.ok(patients);
    }

    /**
     * Retrieve Patient by patient ID
     *
     * @param id Patient Id
     * @return Patient or Throws PatientNotFoundException
     */
    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable(name = "id") Long id) {
        logger.debug("getPatientById from PatientController starts here");
        Patient patientById = patientService.getPatientById(id);
        logger.info("Patient with id:{{}} has been successfully retrieved from PatientController", id);
        return ResponseEntity.ok(patientById);
    }

    /**
     * Retrieve Patient by LastName
     *
     * @param lastName Patient LastName
     * @return Patient or Throws PatientNotFoundException
     */

    @GetMapping("/patient")
    public ResponseEntity<Patient> getPatientByLastName(@RequestParam(name = "lastName") String lastName) {
        logger.debug("getPatientByLastName from PatientController starts here");
        Patient patientByLastName = patientService.getPatientByLastName(lastName);
        logger.info("Patient with lastName:{{}} has been successfully retrieved from PatientController", lastName);
        return ResponseEntity.ok(patientByLastName);
    }

    /**
     * Save new Patient, validate the date
     *
     * @param newPatient to add
     * @return Patient
     */
    @PostMapping("/patients")
    public ResponseEntity<Patient> addPatient(@RequestBody @Valid Patient newPatient) {
        logger.debug("addPatient from PatientController starts here");
        Patient savedPatient = patientService.addPatient(newPatient);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPatient.getId())
                .toUri();
        logger.info("Patient with id:{{}} has been successfully added to DB, from PatientController", savedPatient.getId());
        return ResponseEntity.created(location).body(savedPatient);
    }

    /**
     * Update Patient in DB
     *
     * @param id            Existing Patient ID in DB
     * @param patientUpdate Patient to update
     * @return Noting
     */
    @PutMapping("/patients/{id}")
    public ResponseEntity<Void> updatePatientById(@PathVariable(name = "id") Long id, @RequestBody @Valid Patient patientUpdate) {
        logger.debug("updatePatientById from PatientController starts here");
        patientService.updatePatientById(id, patientUpdate);
        logger.info("Patient with id:{{}} has been successfully updated, from PatientController", id);

        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a patient from the database if a patient with the given ID exists.
     *
     * @param id the ID of the patient to delete
     * @return a ResponseEntity with an error message if the patient does not exist, or a success message if the patient is successfully deleted
     * @throws PatientNotFoundException if no patient with the given ID exists in the database
     */
    @DeleteMapping("/patients/{id}")
    public ResponseEntity<ResponseMessage> deletePatientById(@PathVariable Long id) {
        logger.debug("deletePatientById from PatientController starts here with id:{{}}", id);
        Patient patientDeleted = patientService.deletePatientById(id);
        logger.info("Patient with id:{{}} has been successfully deleted from PatientController", id);
        return ResponseEntity.ok(
                new ResponseMessage(
                        200,
                        LocalDateTime.now(),
                        "Patient with id:" + id + " has been successfully deleted from DB!",
                        "Patient with lastName: " + patientDeleted.getLastName() + " and firstName: " + patientDeleted.getFirstName() + " has been successfully deleted from DB!"));
    }
}
