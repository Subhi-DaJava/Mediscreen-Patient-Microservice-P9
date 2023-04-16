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
     * Retrieves a list of patient from the database with the given lastName.
     * @param lastName lastName of the patients to retrive
     * @return list of patients
     * @throws PatientNotFoundException if no patient with the given last name is not found in the database
     */
    @Override
    public List<Patient> getPatientByLastName(String lastName) {
        logger.debug("getPatientByLastName from PatientServiceImpl starts here");
        List<Patient> patients = findByLastName(lastName);
        if (patients.isEmpty()) {
            throw new PatientNotFoundException("Patient with lastName:{%s} doesn't exist in DB!".formatted(lastName));
        }
        return patients;
    }

    /**
     * Adds a new patient to the database if the patient does not exist and no other patient with the same lastName, firstName and dateOfBirth
     *
     * @param patient the patient to be added to the database, all attributes should be validated before saving
     * @return saved newPatient
     * @throws PatientAlreadyExistException if a patient with the same lastName, firstName and dateOfBirth already exists in the database
     */
    @Override
    public Patient addPatient(Patient patient) {
        logger.debug("addPatient from PatientServiceImpl starts here");

        List<Patient> checkPatients = findByLastName(patient.getLastName());

        Optional<Patient> existingPatient = checkPatients
                .stream()
                .filter(p -> p.getFirstName().equals(patient.getFirstName())
                        && p.getDateOfBirth().equals(patient.getDateOfBirth()))
                .findFirst();

        existingPatient.ifPresent(p -> {
            logger.error("Patient with lastName:{{}} and with firstName:{{}} already exists in DB", patient.getLastName(), patient.getFirstName());
            //throw new PatientAlreadyExistException(String.format("Patient with lastName:{%s} already exits in DB", patient.getLastName()));
            throw new PatientAlreadyExistException("Patient with lastName:{%s} and firstName:{%s} already exists in DB".formatted(patient.getLastName(), patient.getFirstName()));
        });

        Patient patientSaved = patientRepository.save(patient);
        logger.info("Patient with lastName:{{}} has been successfully saved in DB!, from PatientServiceImpl", patient.getLastName());
        return patientSaved;
    }

    /**
     * Updates a patient in the database, if the patient exists and the lastName, firstName and dateOfBirth
     * should not be the same as other patients in the database. Otherwise, it will throw a PatientAlreadyExistException.
     *
     * @param id ID of the patient to be updated
     * @param updatedPatient updated patient information
     * @throws PatientNotFoundException if the patient with the given ID does not exist in the database
     * @throws PatientAlreadyExistException if a patient with the same lastName, firstName, and dateOfBirth already exists
     *                                      in the database, except for the patient to be updated
     */
    @Override
    public void updatePatientById(Long id, Patient updatedPatient) {
        logger.debug("updatePatientById from PatientServiceImpl starts here");

        Patient existingPatient = findPatientById(id);

        if (existingPatient == null) {
            throw new PatientNotFoundException("Patient with id:{%d} doesn't exist in DB!".formatted(id));
        }

        checkForPatientWithSameFirstNameAndDateOfBirth(updatedPatient, existingPatient);

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
     * Retrieves a list of patients with the specified lastName
     * @param lastName the LastName of the patients to retrieve
     * @return a list of patients with the specified lastName, or PatientNotFoundException if no patients are found
     *
     */
    private List<Patient> findByLastName(String lastName) {
        logger.debug("findByLastName from PatientServiceImpl starts here with lastName:{{}}", lastName);
        List<Patient> patients = patientRepository.findByLastName(lastName)
                .orElseThrow(() -> new PatientNotFoundException("Patient with lastName:{%s} doesn't exist in DB!".formatted(lastName)));

        logger.info("Patient with lastName:{{}} has been successfully retrieved, private method from PatientServiceImpl", lastName);
        return patients;
    }
    /**
     * Checks if there exists another patient in the DB with the same firstName and dateOfBirth as the updated patient, but a different ID.
     * If so, it throws a PatientAlreadyExistException.
     * @param updatedPatient updated patient
     * @param existingPatient existing patient in the DB that is being updated
     * @throws PatientAlreadyExistException if a patient with the same laseName, firstName and dateOfBirth exists in the database with a different ID
     */
    private void checkForPatientWithSameFirstNameAndDateOfBirth(Patient updatedPatient, Patient existingPatient) {
        List<Patient> patientsWithSameLastName = findByLastName(updatedPatient.getLastName());

        if (!patientsWithSameLastName.isEmpty()) {
            for (Patient p : patientsWithSameLastName) {
                if (p.getFirstName().equals(updatedPatient.getFirstName()) && p.getDateOfBirth().equals(updatedPatient.getDateOfBirth()) && !(p.getId().equals(existingPatient.getId()))) {
                    logger.error("Patient with lastName :{{}}, firstName;{{}} and dateOfBirth:{{}} already exists in DB! from updatePatient, PatientServiceImpl", updatedPatient.getLastName(), updatedPatient.getFirstName(), updatedPatient.getDateOfBirth());
                    // throw new PatientAlreadyExistException(String.format("Patient with lastName:{%s} already exists in DB", updatedPatient.getLastName()));
                    throw new PatientAlreadyExistException("Patient with lastName:{%s}, firstName:{%s} and dateOfBirth:{%s} already exists in DB!".formatted(updatedPatient.getLastName(), updatedPatient.getFirstName(), updatedPatient.getDateOfBirth()));
                }
            }
        }
    }

}
