package com.insurance.policyclaims.controller;

import com.insurance.policyclaims.model.Policy;
import com.insurance.policyclaims.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * A Controller's only job is to handle HTTP: read the request, call the
 * service layer to do the actual work, and shape the response. It should
 * contain as little logic as possible - that's why fileClaim's expiry check
 * lives in ClaimService, not here.
 *
 * @RestController = @Controller + @ResponseBody: every method's return value
 * is automatically converted to JSON and written to the HTTP response body.
 */
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    @Autowired
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // GET http://localhost:8080/api/policies
    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    // GET http://localhost:8080/api/policies/3
    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    // GET http://localhost:8080/api/policies/search?holderName=sandesh
    @GetMapping("/search")
    public List<Policy> search(@RequestParam String holderName) {
        return policyService.searchByHolderName(holderName);
    }

    // POST http://localhost:8080/api/policies   (JSON body = new policy details)
    // @Valid triggers the validation annotations (@NotBlank etc.) on the Policy class.
    @PostMapping
    public ResponseEntity<Policy> createPolicy(@Valid @RequestBody Policy policy) {
        Policy created = policyService.createPolicy(policy);
        return new ResponseEntity<>(created, HttpStatus.CREATED); // 201 Created
    }

    // PUT http://localhost:8080/api/policies/3   (JSON body = updated details)
    @PutMapping("/{id}")
    public Policy updatePolicy(@PathVariable Long id, @Valid @RequestBody Policy policy) {
        return policyService.updatePolicy(id, policy);
    }

    // DELETE http://localhost:8080/api/policies/3
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
