package com.swg7.dalpolls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swg7.dalpolls.model.AnswerModel;
import com.swg7.dalpolls.model.ElectionModel;
import com.swg7.dalpolls.model.QuestionModel;

public class NewAnswer extends AppCompatActivity {

    private QuestionModel question;
    private ElectionModel election;
    private AnswerModel answer;

    private FirestoreRecyclerAdapter adapter;

    private String electionID;
    private String questionID;
    private String answerID;

    private EditText answerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_answer);

        election = (ElectionModel) getIntent().getExtras().get("election");
        question = (QuestionModel) getIntent().getExtras().get("question");
        answer = (AnswerModel) getIntent().getExtras().get("answer");
        answerText = findViewById(R.id.edit_answer);
        answerText.setText(answer.name);

    }

    public void save(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        if (answer.id == null) {
            // if the election does not yet exist
            ref = db.collection("elections").document(election.id)
                    .collection("questions").document(question.id)
                    .collection("answers").document();
            answer.id = ref.getId();
        } else {
            ref = db.collection("elections").document(election.id)
                    .collection("questions").document(question.id)
                    .collection("answers").document(answer.id);
        }

        if (!answerText.getText().toString().equals("")) {
            answer.name = answerText.getText().toString();
            final View v = view;
            ref.set(answer)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void delete(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        if (answer.id == null) {
            Toast.makeText(view.getContext(), "No ID", Toast.LENGTH_SHORT).show();
            finish();
            // if the election does not yet exist
            return;
        } else {
            ref = db.collection("elections").document(election.id)
                    .collection("questions").document(question.id)
                    .collection("answers").document(answer.id);
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
    }


}
