package com.caiohportella.pm.patientservice.service;

import com.caiohportella.pm.patientservice.dto.PagedPatientResponseDTO;
import com.caiohportella.pm.patientservice.dto.PatientRequestDTO;
import com.caiohportella.pm.patientservice.dto.PatientResponseDTO;
import com.caiohportella.pm.patientservice.exception.EmailAlreadyExistsException;
import com.caiohportella.pm.patientservice.exception.PatientNotFoundException;
import com.caiohportella.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.caiohportella.pm.patientservice.kafka.KafkaProducer;
import com.caiohportella.pm.patientservice.mapper.PatientMapper;
import com.caiohportella.pm.patientservice.model.Patient;
import com.caiohportella.pm.patientservice.repository.PatientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    @Cacheable(
            value = "patients",
            key = "#page + '-' + #size + '-' + #sort + '-' + #sortField",
            condition = "#searchValue == ''"
    )
    public PagedPatientResponseDTO getPatients(int page, int size, String sort, String sortField, String searchValue) {
        System.out.println("[REDIS]: Cached miss - fetching from DB");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        Pageable pageable = PageRequest.of(
                page - 1, size, sort.equalsIgnoreCase("desc")
                        ? Sort.by(sortField).descending()
                        : Sort.by(sortField).ascending()
        );

        Page<Patient> patientsPage;

        if (searchValue == null || searchValue.isBlank()) {
            patientsPage = patientRepository.findAll(pageable);
        } else {
            patientsPage = patientRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchValue, searchValue, pageable);
        }

        List<PatientResponseDTO> patientResponseDtos = patientsPage.getContent()
                .stream()
                .map(PatientMapper::toDTO)
                .toList();

        return new PagedPatientResponseDTO(
                patientResponseDtos,
                patientsPage.getNumber() + 1,
                patientsPage.getSize(),
                patientsPage.getTotalPages(),
                patientsPage.getTotalElements()
        );
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email " + "already exists"
                            + patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(
                PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id,
                                            PatientRequestDTO patientRequestDTO) {

        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with ID: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),
                id)) {
            throw new EmailAlreadyExistsException(
                    "A patient with this email " + "already exists"
                            + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}