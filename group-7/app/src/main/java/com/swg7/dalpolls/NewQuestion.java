package com.swg7.dalpolls;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.swg7.dalpolls.model.AnswerModel;
import com.swg7.dalpolls.model.ElectionModel;
import com.swg7.dalpolls.model.QuestionModel;
import com.swg7.dalpolls.ui.main.AnswerViewHolder;
import com.swg7.dalpolls.ui.main.QuestionViewHolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class NewQuestion extends AppCompatActivity {

    private QuestionModel question;
    private ElectionModel election;
    private AnswerModel answer;

    private FirestoreRecyclerAdapter adapter;

    private String electionID;
    private String questionID;

    private EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.editQuestionTitle);

        election = (ElectionModel) getIntent().getExtras().get("election");
        question = (QuestionModel) getIntent().getExtras().get("question");

        FloatingActionButton fab = findViewById(R.id.create_question_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Update Answer INTENT and ACTIVITY copy from NewElection.java
                Intent i = new Intent(getApplicationContext(), NewAnswer.class);
                i.putExtra("election",election);
                i.putExtra("question",question);
                i.putExtra("answer", new AnswerModel());
                startActivity(i);
            }
        });

        if (election != null && question !=null) {
            title.setText(question.name);
            questionID = question.id;
            electionID = election.id;

            //Do not load recycler view if this is a newly made question.
            if(question.id != null){
                RecyclerView recyclerView = findViewById(R.id.recyclerView_answers);
                adapter = setUpAdapter(FirebaseFirestore.getInstance());

                setUpRecyclerView(recyclerView, adapter);
            }

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(election.name);
    }

    public void save(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        if (question.id == null) {
            // if the election does not yet exist
            ref = db.collection("elections").document(electionID)
                    .collection("questions").document();
            question.id = ref.getId();
        } else {
            ref = db.collection("elections").document(electionID)
                    .collection("questions").document(questionID);
        }
        if (!title.getText().toString().equals("")) {
            question.name = title.getText().toString();
            ref.set(question).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Question Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();

        }
        finish();

    }

    public void delete(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        if (question.id == null) {
            Toast.makeText(view.getContext(), "No ID", Toast.LENGTH_SHORT).show();
            finish();
            // if the election does not yet exist
            return;
        } else {
            ref = db.collection("elections").document(election.id)
                    .collection("questions").document(question.id);
        }
        final View v = view;
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(v.getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        finish();
    }

    //Creates a Firestore adapter to be used with a Recycler view.
    //We will see adapter in more details after the midterm
    //More info on this: https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md
    private FirestoreRecyclerAdapter<AnswerModel, AnswerViewHolder> setUpAdapter(FirebaseFirestore db){
        Query query = db.collection("elections").document(election.id)
                .collection("questions").document(question.id)
                .collection("answers").orderBy("name").limit(50);

        FirestoreRecyclerOptions<AnswerModel> options = new FirestoreRecyclerOptions.Builder<AnswerModel>()
                .setQuery(query,AnswerModel.class)
                .build();

        return new FirestoreRecyclerAdapter<AnswerModel,AnswerViewHolder>(options){
            //For each item in the database connect it to the view
            @Override
            public void onBindViewHolder(AnswerViewHolder holder, int position, final AnswerModel model)
            {
                holder.name.setText(model.name);
                System.out.println("*****-> model.name");
                holder.info.setText(model.id);
                holder.itemView.setBackgroundColor( position % 2 == 0 ? Color.WHITE : Color.rgb(240,240,240));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent i = new Intent(getApplicationContext(), NewAnswer.class);

                        //Adds an extra for Election and Model and Answer
                        i.putExtra("election",election);
                        i.putExtra("question", question);
                        i.putExtra("answer", model);
                        startActivity(i);
                    }
                });
            }
            @Override
            public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i)
            {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.answer_list_item,group,false);
                return new AnswerViewHolder(view);
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
