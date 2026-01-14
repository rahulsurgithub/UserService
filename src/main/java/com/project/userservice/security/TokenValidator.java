package com.project.userservice.security;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

//@Service
//public class TokenValidator {
//    private final RestTemplate restTemplate;
//
//    public TokenValidator(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    /**
//     * Calls user service to validate the token.
//     * If token is not valid, optional is empty.
//     * Else optional contains all of the data in payload
//     * @param token
//     * @return
//     */
//    public Optional<JwtObject> validateToken(String token) {
//        // use restTemplate to call external service if needed
//        return Optional.empty();
//    }
//}