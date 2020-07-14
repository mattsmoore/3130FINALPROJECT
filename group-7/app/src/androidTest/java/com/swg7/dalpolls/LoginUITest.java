package com.swg7.dalpolls;

import androidx.annotation.NonNull;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Verify;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swg7.dalpolls.data.IPersistentUser;
import com.swg7.dalpolls.data.PersistantUserHandler;
import com.swg7.dalpolls.model.UserModel;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.assertNoUnverifiedIntents;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;


import org.junit.Test;

public class LoginUITest extends NonPersistentUITest {
    public LoginUITest() {
        super(MainActivity.class);
    }

    //@Test
    public void verifyAdminDirectedToAdminPage() throws Exception {
        String email = "admin@dal.ca";
        String password = "ProfessionalPassword";
        IPersistentUser auth = new PersistantUserHandler();
        Task<AuthResult> newTask = auth.createUserWithEmailAndPassword(email, password);
        while(!newTask.isComplete()) Thread.sleep(10);
        UserModel userModel = new UserModel();
        userModel.admin = true;
        userModel.email = email;
        userModel.id = auth.getUid();
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        Task anotherTask =  store.collection("users").document(userModel.id).set(userModel);
        while (!anotherTask.isComplete()) Thread.sleep(10);
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.create_election_btn)).check(matches(isDisplayed()));
    }

    //@Test
    public void verifyVoterDirectedToVoterPage() throws Exception {
        String email = "user@dal.ca";
        String password = "ProfessionalPassword";
        IPersistentUser auth = new PersistantUserHandler();
        Task<AuthResult> newTask = auth.createUserWithEmailAndPassword(email, password);
        while(!newTask.isComplete()) Thread.sleep(10);
        UserModel userModel = new UserModel();
        userModel.admin = false;
        userModel.email = email;
        userModel.id = auth.getUid();
        FirebaseFirestore store = FirebaseFirestore.getInstance();
        Task anotherTask =  store.collection("users").document(userModel.id).set(userModel);
        while (!anotherTask.isComplete()) Thread.sleep(10);
        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.create_election_btn)).check(doesNotExist());
    }
}
