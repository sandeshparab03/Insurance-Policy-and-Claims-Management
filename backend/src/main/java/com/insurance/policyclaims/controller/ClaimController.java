package com.insurance.policyclaims.controller;

import com.insurance.policyclaims.model.Claim;
import com.insurance.policyclaims.model.ClaimStatus;
import com.insurance.policyclaims.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClaimController {

    private final ClaimService claimService;

    @Autowired
    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // GET http://localhost:8080/api/claims
    @GetMapping("/claims")
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    // GET http://localhost:8080/api/claims/5
    @GetMapping("/claims/{id}")
    public Claim getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id);
    }

    // GET http://localhost:8080/api/policies/3/claims
    @GetMapping("/policies/{policyId}/claims")
    public List<Claim> getClaimsForPolicy(@PathVariable Long policyId) {
        return claimService.getClaimsForPolicy(policyId);
    }

    // POST http://localhost:8080/api/policies/3/claims   (JSON body = description, claimAmount)
    @PostMapping("/policies/{policyId}/claims")
    public ResponseEntity<Claim> fileClaim(@PathVariable Long policyId, @Valid @RequestBody Claim claim) {
        Claim filed = claimService.fileClaim(policyId, claim);
        return new ResponseEntity<>(filed, HttpStatus.CREATED);
    }

    // PUT http://localhost:8080/api/claims/5/status?status=APPROVED
    @PutMapping("/claims/{id}/status")
    public Claim updateStatus(@PathVariable Long id, @RequestParam ClaimStatus status) {
        return claimService.updateStatus(id, status);
    }

    // DELETE http://localhost:8080/api/claims/5
    @DeleteMapping("/claims/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        claimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}
