package com.swg7.dalpolls;

import android.app.Activity;
import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Rule;

public abstract class NonPersistentUITest {

    @Rule
    public ActivityTestRule<? extends Activity> mActivityTestRule;


    NonPersistentUITest(Class<? extends Activity> activityType) {
        mActivityTestRule = new ActivityTestRule<>(activityType);
        mActivityTestRule.launchActivity(new Intent());
        FirebaseOptions old = FirebaseOptions.fromResource(mActivityTestRule.getActivity().getApplicationContext());
        FirebaseApp.clearInstancesForTest();
        FirebaseOptions opt = new FirebaseOptions.Builder()
                .setApplicationId(old.getApplicationId())
                .setApiKey(old.getApiKey())
                .setDatabaseUrl(null)
                .setGaTrackingId(old.getGaTrackingId())
                .setGcmSenderId(old.getGcmSenderId())
                .setStorageBucket(old.getStorageBucket())
                .setProjectId(old.getProjectId())
                .build();
        FirebaseApp.initializeApp(mActivityTestRule.getActivity(), opt);
        Task x = FirebaseFirestore.getInstance().clearPersistence();
        try {
            while (!x.isComplete()) Thread.sleep(10);
            x = FirebaseFirestore.getInstance().disableNetwork();
            while (!x.isComplete()) Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        Task x = FirebaseFirestore.getInstance().clearPersistence();
        while(!x.isComplete()) Thread.sleep(10);
    }

}
