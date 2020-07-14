package com.swg7.dalpolls.data;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public interface IPersistentUser {
    FirebaseUser getCurrentUser();
    String getUid();
    Task<AuthResult> createUserWithEmailAndPassword(String email, String password);
    Task<AuthResult> signInWithEmailAndPassword(String email, String password);
    void signOut();
    Task<SignInMethodQueryResult> fetchSignInMethodsForEmail(String email);
}
