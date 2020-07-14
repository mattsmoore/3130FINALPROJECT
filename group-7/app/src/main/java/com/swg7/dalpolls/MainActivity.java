package com.swg7.dalpolls;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swg7.dalpolls.data.IPersistentUser;
import com.swg7.dalpolls.data.PersistantUserHandler;
import com.swg7.dalpolls.notification.NotificationCreator;
import com.swg7.dalpolls.model.UserModel;
import com.swg7.dalpolls.users.AccountCreator;
import com.swg7.dalpolls.users.NewAccountStatus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "DALPOLLS";
    private FirebaseFirestore db;
    private IPersistentUser mAuth;
    private static final String TAG = "MainActivity";
    private AccountCreator ac;

    private UserModel user;

    private EditText e;
    private EditText p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = new PersistantUserHandler();
        e = findViewById(R.id.emailEdit);
        p = findViewById(R.id.passwordEdit);
        ac = new AccountCreator(mAuth);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        // -- Taken from Android Documentation -- //
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }




        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            //TODO: IF currentUser is not equal to null...  Launch either the admin activity or the
            // general voter activity.  If the currentUser is null,  stay on this activity to allow them
            // to sign in or create an account.

            db.collection("users")
                    .document(currentUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            user = documentSnapshot.toObject(UserModel.class);
                            //Toast.makeText(getApplicationContext(), user.id, Toast.LENGTH_SHORT).show();
                            Intent intent;
                            if(user!=null){
                                if(user.admin){
                                    intent = new Intent(getApplicationContext(), Admin_page.class);
                                }
                                else{
                                    intent = new Intent(getApplicationContext(), VoterMainActivity.class);
                                }
                            }
                            else{
                                intent = new Intent(getApplicationContext(), VoterMainActivity.class);
                            }

                            EditText editText = findViewById(R.id.emailEdit);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    });
        }
    }

    public void createAccountButtonPressed(View view){

        final String email = e.getText().toString().trim();
        final String password = p.getText().toString();


        //Only run if NewAccountStatus returns a VALID sign.
        NewAccountStatus validationState = ac.validateNewAccount(email, password);


        if(validationState == NewAccountStatus.VALID) {
            mAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                @Override
                public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                    if (signInMethodQueryResult.getSignInMethods().size() > 0) {
                        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(getApplicationContext(), "Login Successful.",
                                        Toast.LENGTH_SHORT).show();
                                //TODO: Check if the newly signed in user is an admin or not.
                                // Redirect appropriately

                                db.collection("users").document(mAuth.getUid())
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                user = documentSnapshot.toObject(UserModel.class);
                                                Toast.makeText(getApplicationContext(), "UserModel Made", Toast.LENGTH_SHORT).show();
                                                Intent intent;
                                                if(user.admin){
                                                    intent = new Intent(getApplicationContext(), Admin_page.class);
                                                }
                                                else{
                                                    intent = new Intent(getApplicationContext(), VoterMainActivity.class);
                                                }

                                                intent.putExtra("user", user);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Login Unsuccessful.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                UserModel um = new UserModel();
                                um.id = mAuth.getUid();
                                um.admin = false; //User will not be an admin by default
                                um.email = email;
                                final UserModel setUser = um;
                                DocumentReference ref = db.collection("users")
                                        .document(mAuth.getUid());

                                ref.set(um).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getApplicationContext(), "Account Creation" +
                                                        " Successful.",
                                                Toast.LENGTH_SHORT).show();

                                        //TODO: Check if the newly signed in user is an admin or not.
                                        // Redirect appropriately
                                        mAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "Email sent.");
                                                        }
                                                    }
                                                });
                                        Intent intent = new Intent(getApplicationContext(), VoterMainActivity.class);
                                        intent.putExtra("user", setUser);
                                        startActivity(intent);
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Account Creation" +
                                                        " Unsuccessful.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Account Creation" +
                                                " Unsuccessful.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        else{
            switch (validationState){
                case INVALID_EMAIL:
                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                    break;

                case INVALID_PASSWORD:
                    Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


}
