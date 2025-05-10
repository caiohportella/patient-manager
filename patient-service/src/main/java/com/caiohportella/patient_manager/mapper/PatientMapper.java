package com.caiohportella.patient_manager.mapper;

import java.time.LocalDate;

import com.caiohportella.patient_manager.dto.PatientRequestDTO;
import com.caiohportella.patient_manager.dto.PatientResponseDTO;
import com.caiohportella.patient_manager.models.Patient;

public class PatientMapper {
    public static PatientResponseDTO toPatientDTO(Patient patient) {
        PatientResponseDTO patientDTO = new PatientResponseDTO();

        patientDTO.setId(patient.getId().toString());
        patientDTO.setName(patient.getName());
        patientDTO.setEmail(patient.getEmail());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setBirthDate(patient.getBirthDate().toString());

        return patientDTO;
    }

    public static Patient toPatientModel(PatientRequestDTO patientDTO) {
        Patient patient = new Patient();

        patient.setName(patientDTO.getName());
        patient.setEmail(patientDTO.getEmail());
        patient.setAddress(patientDTO.getAddress());
        patient.setBirthDate(LocalDate.parse(patientDTO.getBirthDate()));
        patient.setRegistrationDate(LocalDate.parse(patientDTO.getRegistrationDate()));

        return patient;
    }
}
