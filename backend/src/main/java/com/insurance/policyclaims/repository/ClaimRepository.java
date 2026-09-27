package com.insurance.policyclaims.repository;

import com.insurance.policyclaims.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // Finds all claims linked to a given policy's ID.
    // Spring Data JPA understands "PolicyId" refers to the "id" field
    // inside the "policy" object on the Claim entity.
    List<Claim> findByPolicyId(Long policyId);
}
