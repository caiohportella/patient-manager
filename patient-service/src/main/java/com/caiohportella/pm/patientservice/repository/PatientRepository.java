package com.caiohportella.pm.patientservice.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caiohportella.pm.patientservice.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);
    Page<Patient> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
            (String name, String email, Pageable pageable);
}
