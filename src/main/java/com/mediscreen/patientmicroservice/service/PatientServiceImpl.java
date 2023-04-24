package com.mediscreen.patientmicroservice.service;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.exceptions.PatientAlreadyExistException;
import com.mediscreen.patientmicroservice.exceptions.PatientNotFoundException;
import com.mediscreen.patientmicroservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {
    private final static Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Retrieves a list of all patients from the database.
     *
     * @return all patients
     */
    @Override
    public List<Patient> getPatients() {
        logger.debug("getPatients from PatientServiceImpl starts here");
        List<Patient> patients = patientRepository.findAll();
        logger.info("AllPatients have been successfully retrieved, from PatientServiceImpl");
        return patients;
    }

    /**
     * Retrieves a patient from the database by hid ID.
     *
     * @param id ID of the patient to retrieve.
     * @return the patient with the given ID.
     * @throws PatientNotFoundException if no patient is found with the given ID.
     */
    @Override
    public Patient getPatientById(Long id) {
        logger.debug("getPatientById from PatientServiceImpl starts here");
        Patient patient = findPatientById(id);
        logger.info("Patient with id:{{}} has been successfully retrieved, form PatientServiceImpl", id);
        return patient;
    }

    /**
     * Retrieves a patient from the database by his lastName.
     *
     * @param lastName lastName of the patient to retrieve
     * @return the patient object retrieved from the database
     * @throws PatientNotFoundException if a patient with the given last name is not found in the database
     */
    @Override
    public Patient getPatientByLastName(String lastName) {
        logger.debug("getPatientByLastName from PatientServiceImpl starts here");
        Optional<Patient> patient = findByLastName(lastName);

        if (patient.isEmpty()) {
            logger.error("Patient doesn't exist in DB with lastName:{{}}", lastName);
            //throw new PatientNotFoundException(String.format("Patient with lastName:{%s} doesn't exist in DB!", lastName));
            throw new PatientNotFoundException("Patient with lastName:{%s} doesn't exist in DB!".formatted(lastName));
        }
        logger.info("Patient has been retrieved successfully by lastName:{{}}, from PatientServiceImpl", lastName);
        return patient.get();
    }

    /**
     * Adds a new patient to the database if the patient does not exist.
     *
     * @param patient the patient to be added to the database
     * @return added patient
     * @throws PatientAlreadyExistException if a patient with the sameLast name already exists in the database
     */
    @Override
    public Patient addPatient(Patient patient) {
        logger.debug("addPatient from PatientServiceImpl starts here");
        Optional<Patient> checkPatient = findByLastName(patient.getLastName());
        if (checkPatient.isPresent()) {
            logger.error("Patient with lastName:{{}} already exists in DB", patient.getLastName());
            //throw new PatientAlreadyExistException(String.format("Patient with lastName:{%s} already exits in DB", patient.getLastName()));
            throw new PatientAlreadyExistException("Patient with lastName:{%s} already exits in DB".formatted(patient.getLastName()));
        }
        Patient patientSaved = patientRepository.save(patient);
        logger.info("Patient with lastName:{{}} has been successfully saved in DB!, from PatientServiceImpl", patient.getLastName());
        return patientSaved;
    }

    /**
     * Updates a patient in the database, if it exists and the last name is unique.
     * If a patient with the same lastName already exists in the database, it will throw a PatientAlreadyExistException.
     *
     * @param id             Patient Id
     * @param updatedPatient updated patient
     * @throws PatientNotFoundException     If the patient record with the given id does not exist in the database
     * @throws PatientAlreadyExistException If a patient with the same lastName already exists in the database, except for the patient to be updated
     */
    @Override
    public void updatePatientById(Long id, Patient updatedPatient) {
        logger.debug("updatePatientById from PatientServiceImpl starts here");

        Patient existingPatient = findPatientById(id);

        Optional<Patient> patientWithSameLastName = findByLastName(updatedPatient.getLastName());

        if (patientWithSameLastName.isPresent() && !patientWithSameLastName.get().getId().equals(id)) {
            logger.error("Patient with lastName with:{{}} already exists in DB! from updatePatient, PatientServiceImpl", updatedPatient.getLastName());
            // throw new PatientAlreadyExistException(String.format("Patient with lastName:{%s} already exists in DB", updatedPatient.getLastName()));
            throw new PatientAlreadyExistException("Patient with lastName:{%s} already exists in DB".formatted(updatedPatient.getLastName()));
        }

        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setDateOfBirth(updatedPatient.getDateOfBirth());
        existingPatient.setSex(updatedPatient.getSex());
        existingPatient.setHomeAddress(updatedPatient.getHomeAddress());
        existingPatient.setPhoneNumber(updatedPatient.getPhoneNumber());

        patientRepository.save(existingPatient);
        logger.info("Patient with id:{{}} has been successfully updated!, from PatientServiceImpl", existingPatient.getId());
    }

    /**
     * Deletes a Patient by given id if it exists in the database
     * @param id Patient ID in DB
     * @return Patient object that has been deleted
     * @throws PatientNotFoundException if no Patient with the given id is found in the database
     */
    @Override
    public Patient deletePatientById(Long id) {
        logger.debug("deletePatientById from PatientServiceImpl starts here with id:{{}}", id);
        Patient patientDeleted = findPatientById(id);

        patientRepository.deleteById(id);
        logger.info("Patient with id:{{}} has been successfully deleted, method from PatientServiceImpl", id);

        return patientDeleted;
    }


    /**
     * Retrieves the Patient with the specified ID from the database.
     *
     * @param id Patient's Id to retrieve
     * @return Patient with the specified ID
     * @throws PatientNotFoundException if the patient with the specified ID cannot be found in the database
     */
    private Patient findPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> {
            logger.error("Patient with id:{{}} doesn't exist in DB!, findPatientById privateMethode, from PatientServiceImpl", id);
            //return new PatientNotFoundException(String.format("Patient with id:{%d} doesn't exist in DB!", id));
            return new PatientNotFoundException("Patient with id:{%d} doesn't exist in DB!".formatted(id));
        });
    }

    /**
     * Retrieves a patient by his lastName.
     *
     * @param lastName lastName of the patient to retrieve
     * @return an optional containing the patient with the specified lastName, or an empty optional if not found
     */
    private Optional<Patient> findByLastName(String lastName) {
        logger.debug("findByLastName from PatientServiceImpl starts here with lastName:{{}}", lastName);
        Optional<Patient> patient = patientRepository.findByLastName(lastName);
        if (patient.isPresent()) {
            logger.info("Patient with lastName:{{}} has been successfully retrieved, private method from PatientServiceImpl", lastName);
        } else {
            logger.warn("Patient with lastName:{{}} not found in DB, private methode from PatientServiceImpl", lastName);
            // throw new PatientNotFoundException("Patient with lastName:{%s} doesn't exist in DB!".formatted(lastName));
        }
        return patient;
    }

}
