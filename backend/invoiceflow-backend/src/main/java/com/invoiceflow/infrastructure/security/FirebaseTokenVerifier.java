package com.invoiceflow.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FirebaseTokenVerifier {
    public FirebaseToken verify(String idToken){
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e){
            throw new RuntimeException("Invalid Firebase token", e);
        }
    }
}
