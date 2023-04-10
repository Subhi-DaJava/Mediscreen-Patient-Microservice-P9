package com.mediscreen.patientmicroservice.service;

import com.mediscreen.patientmicroservice.domain.Patient;
import com.mediscreen.patientmicroservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class PatientServiceImpl implements PatientService {
    private final static Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getPatients() {
        logger.debug("getPatients from PatientServiceImpl starts here");
        return patientRepository.findAll();
    }
}
