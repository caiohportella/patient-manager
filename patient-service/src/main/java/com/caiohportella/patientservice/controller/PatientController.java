package com.caiohportella.patientservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caiohportella.patientservice.dto.PatientRequestDTO;
import com.caiohportella.patientservice.dto.PatientResponseDTO;
import com.caiohportella.patientservice.dto.validators.CreatePatientValidationGroup;
import com.caiohportella.patientservice.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "Patient management API")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        List<PatientResponseDTO> patients = patientService.getAllPatients();

        return ResponseEntity.ok().body(patients);
    }

    @PostMapping
    @Operation(summary = "Create a new patient", description = "Create a new patient with the provided details")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patient) {
        PatientResponseDTO createdPatient = patientService.createPatient(patient);

        return ResponseEntity.ok().body(createdPatient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing patient", description = "Update the details of an existing patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDTO patient) {
        PatientResponseDTO updatedPatient = patientService.updatePatient(id, patient);

        return ResponseEntity.ok().body(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Delete a patient by their ID")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }
}
