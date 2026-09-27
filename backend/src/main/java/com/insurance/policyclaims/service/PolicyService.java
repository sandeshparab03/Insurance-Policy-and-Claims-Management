package com.insurance.policyclaims.service;

import com.insurance.policyclaims.exception.ResourceNotFoundException;
import com.insurance.policyclaims.model.Policy;
import com.insurance.policyclaims.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Service layer sits between the Controller (which talks HTTP) and the
 * Repository (which talks database). This is where business rules live -
 * e.g. "you can't create two policies with the same policy number."
 * Keeping this logic out of the Controller keeps things testable and organized.
 */
@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    // "Constructor injection": Spring automatically creates a PolicyRepository
    // and passes it in here when it builds this PolicyService object. You never
    // call "new PolicyService(...)" yourself - Spring manages this wiring for you.
    @Autowired
    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
    }

    public List<Policy> searchByHolderName(String holderName) {
        return policyRepository.findByHolderNameContainingIgnoreCase(holderName);
    }

    public Policy createPolicy(Policy policy) {
        policyRepository.findByPolicyNumber(policy.getPolicyNumber()).ifPresent(p -> {
            throw new IllegalArgumentException("A policy with this policy number already exists");
        });
        return policyRepository.save(policy);
    }

    public Policy updatePolicy(Long id, Policy updatedPolicy) {
        Policy existing = getPolicyById(id); // reuses the not-found check above

        existing.setHolderName(updatedPolicy.getHolderName());
        existing.setHolderEmail(updatedPolicy.getHolderEmail());
        existing.setPolicyType(updatedPolicy.getPolicyType());
        existing.setPremiumAmount(updatedPolicy.getPremiumAmount());
        existing.setStartDate(updatedPolicy.getStartDate());
        existing.setExpiryDate(updatedPolicy.getExpiryDate());
        // Note: we deliberately don't let policyNumber be changed after creation -
        // that's a reasonable real-world business rule.

        return policyRepository.save(existing);
    }

    public void deletePolicy(Long id) {
        Policy existing = getPolicyById(id);
        policyRepository.delete(existing);
    }
}
