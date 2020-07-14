package com.swg7.dalpolls.users;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.swg7.dalpolls.data.IPersistentUser;

import androidx.annotation.NonNull;

public class AccountCreator {

    private static final int MIN_PASSWORD_LENGTH = 6;



    IPersistentUser userAuth;



    public AccountCreator(IPersistentUser userAuth) {
        this.userAuth = userAuth;
    }

    public NewAccountStatus validateNewAccount(String email, String password) {
        if (!isValidEmail(email)) {
            return NewAccountStatus.INVALID_EMAIL;
        }

        if(!isValidPassword(password)){
            return NewAccountStatus.INVALID_PASSWORD;
        }

        return NewAccountStatus.VALID; //TODO: implement
    }

    private boolean isValidEmail(String email){
        return email != null && email.matches("^[^@\\s]+@dal\\.ca$");
    }

    private boolean isValidPassword(String password){
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
}
