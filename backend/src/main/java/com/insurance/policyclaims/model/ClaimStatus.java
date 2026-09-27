package com.insurance.policyclaims.model;

/**
 * An enum restricts a field to a fixed set of valid values.
 * A claim can only ever be in one of these three states - this prevents
 * bugs like someone accidentally saving status = "aproved" (typo) in the database.
 */
public enum ClaimStatus {
    PENDING,
    APPROVED,
    REJECTED
}
