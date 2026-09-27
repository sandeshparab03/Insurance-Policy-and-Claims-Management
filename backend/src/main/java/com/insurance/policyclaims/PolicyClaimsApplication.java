package com.insurance.policyclaims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the entry point of the whole backend application.
 * Running this file's main() method starts an embedded web server (Tomcat)
 * on port 8080 and wires up everything else (controllers, services, database
 * connections) automatically - that's what @SpringBootApplication does behind
 * the scenes: it scans this package and all sub-packages for Spring components.
 */
@SpringBootApplication
public class PolicyClaimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolicyClaimsApplication.class, args);
        System.out.println("Policy & Claims backend is running on http://localhost:8080");
    }
}
