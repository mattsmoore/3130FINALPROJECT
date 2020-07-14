package com.swg7.dalpolls.data;

import com.google.firebase.auth.FirebaseAuth;

public class PersistantUserHandler extends FirebaseAuth implements IPersistentUser {
    public PersistantUserHandler(){
        super(FirebaseAuth.getInstance().getApp());
    }
}
