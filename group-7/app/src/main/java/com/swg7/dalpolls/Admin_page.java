package com.swg7.dalpolls;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.swg7.dalpolls.model.ElectionModel;
import com.swg7.dalpolls.model.UserModel;
import com.swg7.dalpolls.ui.main.ListElectionFragment;
import com.swg7.dalpolls.ui.main.SectionsPagerAdapter;

public class Admin_page extends AppCompatActivity implements ListElectionFragment.OnListFragmentInteractionListener {

    private static String uID = "user";
    private static String USER_VERIFIED = " (Verified)";
    private static String USER_NOT_VERIFIED = " (Not Verified)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.create_election_fab);

        //Set default of FAB to be hidden since first tab should not show it.
        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Don't allow non verified users to click this.. IMPLEMENT YEE HAW
                onListFragmentInteraction(null);
            }
        });

        final FloatingActionButton fabToBeHidden = fab;

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    fabToBeHidden.hide();
                }
                else{
                    fabToBeHidden.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if(getIntent() != null && getIntent().getExtras() != null) {
            uID = (String) getIntent().getExtras().get("uID");
        }

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            //Display whether or not the user has a verified email address.
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                uID += USER_VERIFIED;
            } else {
                uID += USER_NOT_VERIFIED;
            }
        }
        else{
            uID = USER_NOT_VERIFIED;
        }
    }

    public void signOutButtonPressed(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onListFragmentInteraction(ElectionModel item) {
        if(item != null) Toast.makeText(this.getApplicationContext(), "Clicked " + item.name + "!\n" + item.id, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), NewElection.class);
        i.putExtra("election", item);
        startActivity(i);
    }

    public static String getuID(){
        return uID;
    }
}
