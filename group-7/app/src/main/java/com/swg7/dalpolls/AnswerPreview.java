package com.swg7.dalpolls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.swg7.dalpolls.model.AnswerModel;
import com.swg7.dalpolls.model.BallotModel;
import com.swg7.dalpolls.model.ElectionModel;
import com.swg7.dalpolls.model.QuestionModel;
import com.swg7.dalpolls.model.UserModel;
import com.swg7.dalpolls.ui.main.AnswerViewHolder;
import com.swg7.dalpolls.ui.main.QuestionViewHolder;

import java.util.Objects;

public class AnswerPreview extends AppCompatActivity {

    private ElectionModel election;
    private QuestionModel question;
    private BallotModel ballot;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
    private String userId;

    private boolean is_preview;
    private String selectedAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_preview);
        userId = FirebaseAuth.getInstance().getUid();

        db = FirebaseFirestore.getInstance();

        TextView title = findViewById(R.id.questionName);
        election = (ElectionModel) Objects.requireNonNull(getIntent().getExtras()).get("election");
        question = (QuestionModel) Objects.requireNonNull(getIntent().getExtras()).get("question");
        if(getIntent().getExtras() == null) {
            is_preview = false;
        }else {
            is_preview = getIntent().getExtras().getBoolean("is_preview");
        }

        if (userId != null) {
            FirebaseFirestore.getInstance()
                    .collection("elections").document(election.id)
                    .collection("questions").document(question.id)
                    .collection("ballots").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ballot = documentSnapshot.toObject(BallotModel.class);
                    if (ballot != null)
                        selectedAnswer = ballot.answerID;
                    System.out.println("*****Selected Answer******  " + selectedAnswer);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_error, Toast.LENGTH_SHORT).show();
            finish();
        }

        assert question != null;
        title.setText(question.name);


        RecyclerView rv = findViewById(R.id.recyclerAnswers);
        adapter = setUpAdapter(FirebaseFirestore.getInstance());

        setUpRecyclerView(rv,adapter);


    }


    private void setUpRecyclerView(RecyclerView rv, FirestoreRecyclerAdapter adapter){
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
    }


    private FirestoreRecyclerAdapter<AnswerModel, AnswerViewHolder> setUpAdapter(FirebaseFirestore db){

        Query query = db.collection("elections").document(election.id)
                .collection("questions").document(question.id)
                .collection("answers").orderBy("name").limit(50);

        FirestoreRecyclerOptions<AnswerModel> options = new FirestoreRecyclerOptions.Builder<AnswerModel>()
                .setQuery(query, AnswerModel.class)
                .build();

        return new FirestoreRecyclerAdapter<AnswerModel, AnswerViewHolder>(options){
            //For each item in the database connect it to the view
            @Override
            public void onBindViewHolder(AnswerViewHolder holder, int position, final AnswerModel model)
            {
                holder.name.setText(model.name);
                holder.info.setText(model.id);
                String answerID = model.id;

                final AnswerViewHolder h = holder;

                holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.WHITE : Color.rgb(240,240,240));
                //Color the selected answer.

                if(selectedAnswer != null && selectedAnswer.equals(model.id)){
                    h.itemView.setBackgroundColor(Color.rgb(166, 140, 236));
                }


                if(is_preview) holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Would Vote", Toast.LENGTH_SHORT).show();
                    }
                });
                else holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        h.itemView.setBackgroundColor(Color.rgb(166, 140, 236));

                        FirebaseFirestore.getInstance()
                                .collection("elections").document(election.id)
                                .collection("questions").document(question.id)
                                .collection("ballots").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        DocumentReference ref = FirebaseFirestore.getInstance()
                                                .collection("elections").document(election.id)
                                                .collection("questions").document(question.id)
                                                .collection("ballots").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        ballot = documentSnapshot.toObject(BallotModel.class);

                                        if (ballot == null) {
                                            ballot = new BallotModel(FirebaseAuth.getInstance().getCurrentUser().getUid(), model.id);
                                            ref.set(ballot).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Vote Submitted!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Not Submitted!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            ballot.answerID = model.id;
                                            ref.set(ballot).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Vote Updated!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Not Updated!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                        finish();
                    }
                });
            }

            @Override
            public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.answer_list_item, group, false);
                return new AnswerViewHolder(view);
            }
        };

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

//TODO: REMOVE WHEN NO LONGER NEEDED AS REFERENCE
/*                        else{
                            BallotModel bm = new BallotModel(user.id, model.id);
                            ref.set(bm).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Vote Submitted!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Not Submitted!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }*/
