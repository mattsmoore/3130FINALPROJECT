package com.swg7.dalpolls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.swg7.dalpolls.model.ElectionModel;
import com.swg7.dalpolls.model.UserModel;
import com.swg7.dalpolls.ui.main.ElectionViewHolder;

import java.util.Objects;

public class VoterMainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<ElectionModel, ElectionViewHolder> adapter;

    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_main);


        RecyclerView recyclerView = findViewById(R.id.recyclerElections);
        adapter = setUpAdapter(FirebaseFirestore.getInstance());
        //Fill the user model
        user = (UserModel) Objects.requireNonNull(getIntent().getExtras()).get("user");

        setUpRecyclerView(recyclerView,adapter);

    }

    public void signOutButtonPressed(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
    }

    //Creates a Firestore adapter to be used with a Recycler view.
    //We will see adapter in more details after the midterm
    //More info on this: https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md
    private FirestoreRecyclerAdapter<ElectionModel, ElectionViewHolder> setUpAdapter(FirebaseFirestore db){

        //Only display open elections with this query!!
        Query query = db.collection("elections")/*.whereEqualTo("isOpen", true)*/.orderBy("name").limit(50);
        FirestoreRecyclerOptions<ElectionModel> options = new FirestoreRecyclerOptions.Builder<ElectionModel>()
                .setQuery(query,ElectionModel.class)
                .build();

        return new FirestoreRecyclerAdapter<ElectionModel,ElectionViewHolder>(options){
            //For each item in the database connect it to the view
            @Override
            public void onBindViewHolder(ElectionViewHolder holder, int position, final ElectionModel model)
            {
                holder.name.setText(model.name);
                holder.info.setText(model.id);
                holder.itemView.setBackgroundColor( position % 2 == 0 ? Color.WHITE : Color.rgb(240,240,240));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), ElectionPreview.class);
                        i.putExtra("election", model);
                        i.putExtra("user", user);
                        startActivity(i);
                    }
                });
            }
            @Override
            public ElectionViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i)
            {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.election_list_item,group,false);
                return new ElectionViewHolder(view);
            }
        };

    }

    //Connects our recycler view with the viewholder (how we want to show our model[data])
    // and the firestore adapter
    private void setUpRecyclerView(RecyclerView rv, FirestoreRecyclerAdapter adapter){
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null) {
            adapter.stopListening();
        }
    }


}
