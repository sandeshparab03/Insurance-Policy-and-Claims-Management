package com.insurance.policyclaims.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * An "Entity" is a plain Java class that Spring/Hibernate maps directly onto a
 * database table. Each field below becomes a column. You never write CREATE TABLE
 * SQL yourself - Hibernate reads this class and creates/updates the table for you
 * (that's what "spring.jpa.hibernate.ddl-auto=update" in application.properties does).
 */
@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incrementing primary key
    private Long id;

    @NotBlank(message = "Policy number is required")
    @Column(unique = true)
    private String policyNumber;

    @NotBlank(message = "Holder name is required")
    private String holderName;

    @NotBlank(message = "Holder email is required")
    private String holderEmail;

    @NotBlank(message = "Policy type is required")
    private String policyType; // e.g. "Health", "Motor", "Fire"

    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be greater than zero")
    private Double premiumAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    /**
     * One Policy can have many Claims filed against it.
     * "mappedBy = policy" means the Claim class owns the actual foreign key column;
     * this side is just for convenience so we can call policy.getClaims().
     *
     * @JsonManagedReference + @JsonBackReference (see Claim.java) together stop
     * Jackson (the JSON library) from looping forever: Policy -> Claims -> Policy -> Claims...
     */
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Claim> claims = new ArrayList<>();

    public Policy() {
        // Empty constructor is required by Hibernate/JPA - it creates objects
        // using this constructor before filling in the fields via reflection.
    }

    // ----- Getters and setters -----
    // Spring uses these to convert incoming JSON into a Policy object, and to
    // convert a Policy object back into JSON when sending a response.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getHolderEmail() { return holderEmail; }
    public void setHolderEmail(String holderEmail) { this.holderEmail = holderEmail; }

    public String getPolicyType() { return policyType; }
    public void setPolicyType(String policyType) { this.policyType = policyType; }

    public Double getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(Double premiumAmount) { this.premiumAmount = premiumAmount; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public List<Claim> getClaims() { return claims; }
    public void setClaims(List<Claim> claims) { this.claims = claims; }

    /**
     * A computed (not stored in DB) helper. This is a good example of business
     * logic living in the model: "is this policy currently expired?"
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}
