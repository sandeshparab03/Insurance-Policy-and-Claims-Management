package com.insurance.policyclaims.service;

import com.insurance.policyclaims.exception.InvalidOperationException;
import com.insurance.policyclaims.exception.ResourceNotFoundException;
import com.insurance.policyclaims.model.Claim;
import com.insurance.policyclaims.model.ClaimStatus;
import com.insurance.policyclaims.model.Policy;
import com.insurance.policyclaims.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyService policyService; // reused so we don't duplicate "find policy or throw 404" logic

    @Autowired
    public ClaimService(ClaimRepository claimRepository, PolicyService policyService) {
        this.claimRepository = claimRepository;
        this.policyService = policyService;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    public List<Claim> getClaimsForPolicy(Long policyId) {
        // This also implicitly validates the policy exists, since getPolicyById throws if not.
        policyService.getPolicyById(policyId);
        return claimRepository.findByPolicyId(policyId);
    }

    /**
     * This is the key business rule that makes this project feel like a real
     * insurance system rather than a generic CRUD app: you cannot file a claim
     * against a policy that has already expired.
     */
    public Claim fileClaim(Long policyId, Claim newClaim) {
        Policy policy = policyService.getPolicyById(policyId);

        if (policy.isExpired()) {
            throw new InvalidOperationException(
                    "Cannot file a claim: policy " + policy.getPolicyNumber() + " expired on " + policy.getExpiryDate());
        }

        newClaim.setPolicy(policy);
        newClaim.setStatus(ClaimStatus.PENDING);
        newClaim.setFiledDate(LocalDate.now());
        // Generates something like "CLM-4F2A9B" as a human-readable claim reference.
        newClaim.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());

        return claimRepository.save(newClaim);
    }

    public Claim updateStatus(Long claimId, ClaimStatus newStatus) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(newStatus);
        return claimRepository.save(claim);
    }

    public void deleteClaim(Long id) {
        Claim claim = getClaimById(id);
        claimRepository.delete(claim);
    }
}
