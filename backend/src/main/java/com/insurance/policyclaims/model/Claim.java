package com.insurance.policyclaims.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String claimNumber; // we generate this ourselves in the service layer

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Claim amount is required")
    @Positive(message = "Claim amount must be greater than zero")
    private Double claimAmount;

    @Enumerated(EnumType.STRING) // stores "PENDING"/"APPROVED"/"REJECTED" as text, not 0/1/2
    private ClaimStatus status;

    private LocalDate filedDate;

    /**
     * Many Claims belong to one Policy. This is the "owning" side of the
     * relationship - the "policy_id" foreign key column lives in the claims table.
     * @JsonBackReference pairs with @JsonManagedReference in Policy.java to avoid
     * infinite JSON recursion (see the comment there for details).
     */
    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonBackReference
    private Policy policy;

    public Claim() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getClaimAmount() { return claimAmount; }
    public void setClaimAmount(Double claimAmount) { this.claimAmount = claimAmount; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public LocalDate getFiledDate() { return filedDate; }
    public void setFiledDate(LocalDate filedDate) { this.filedDate = filedDate; }

    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
}
