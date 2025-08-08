package com.healthfirst.healthfirstserver.service;

import com.healthfirst.healthfirstserver.domain.entity.Patient;
import com.healthfirst.healthfirstserver.payload.request.patient.PatientRegistrationRequest;
import com.healthfirst.healthfirstserver.payload.response.patient.PatientRegistrationResponse;

public interface PatientService {
    PatientRegistrationResponse registerPatient(PatientRegistrationRequest request);
}
