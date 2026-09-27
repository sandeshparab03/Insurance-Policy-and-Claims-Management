package com.insurance.policyclaims.repository;

import com.insurance.policyclaims.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * A Repository is Spring's way of giving you database access without writing SQL.
 * By extending JpaRepository<Policy, Long>, you automatically get methods like
 * save(), findAll(), findById(), deleteById() for free - Spring generates the
 * implementation at runtime, you never write it yourself.
 *
 * For the methods below, Spring Data JPA reads the METHOD NAME and generates the
 * correct SQL query automatically - e.g. "findByPolicyNumber" becomes
 * "SELECT * FROM policies WHERE policy_number = ?". This is called a "query method".
 */
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    List<Policy> findByHolderNameContainingIgnoreCase(String holderName);
}
