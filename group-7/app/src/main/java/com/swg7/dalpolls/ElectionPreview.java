package com.swg7.dalpolls;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.swg7.dalpolls.model.ElectionModel;
import com.swg7.dalpolls.model.QuestionModel;
import com.swg7.dalpolls.model.UserModel;
import com.swg7.dalpolls.ui.main.QuestionViewHolder;

import java.util.Iterator;
import java.util.Objects;




public class ElectionPreview extends AppCompatActivity {

    private ElectionModel election;
    private FirestoreRecyclerAdapter adapter;
    private boolean is_preview = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = FirebaseAuth.getInstance().getUid();

        setContentView(R.layout.election_preview);
        TextView title = findViewById(R.id.electionName);
        election = (ElectionModel) Objects.requireNonNull(getIntent().getExtras()).get("election");
        if(getIntent().getExtras() != null)
            is_preview = getIntent().getExtras().getBoolean("is_preview");
        assert election != null;
        title.setText(election.name);

        RecyclerView rv = findViewById(R.id.recyclerQuestions);
        adapter = setUpAdapter(FirebaseFirestore.getInstance());

        setUpRecyclerView(rv,adapter);

    }


    private void setUpRecyclerView(RecyclerView rv, FirestoreRecyclerAdapter adapter){
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
    }


    private FirestoreRecyclerAdapter<QuestionModel, QuestionViewHolder> setUpAdapter(FirebaseFirestore db){

        final Query query = db.collection("elections").document(election.id).collection("questions").orderBy("name").limit(50);
        FirestoreRecyclerOptions<QuestionModel> options = new FirestoreRecyclerOptions.Builder<QuestionModel>()
                .setQuery(query,QuestionModel.class)
                .build();

        return new FirestoreRecyclerAdapter<QuestionModel,QuestionViewHolder>(options){
            //For each item in the database connect it to the view
            @Override
            public void onBindViewHolder(QuestionViewHolder holder, int position, final QuestionModel model)
            {
                holder.name.setText(model.name);
                holder.info.setText(model.id);
                holder.itemView.setBackgroundColor( position % 2 == 0 ? Color.WHITE : Color.rgb(240,240,240));

                final QuestionViewHolder h = holder;
                FirebaseFirestore.getInstance().collection("elections").document(election.id)
                        .collection("questions").document(model.id)
                        .collection("ballots").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        /* Potential fix */
                        if (userId != null) {
                            Iterator i = queryDocumentSnapshots.getDocuments().iterator();
                            while (i.hasNext()) {
                                if (((DocumentSnapshot) i.next()).getData().containsValue(userId)) {
                                    h.itemView.setBackgroundColor(Color.rgb(166, 140, 236));
                                    h.name.setText(model.name + "\t[VOTED]");
                                    break;
                                }
                            }
                        }
                        /* Legacy
                        if (queryDocumentSnapshots.size() > 0){
                            h.itemView.setBackgroundColor(Color.rgb(166, 140, 236));
                            h.name.setText(model.name + "\t[VOTED]");
                        }*/
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), AnswerPreview.class);
                        //Adds an extra for ElectionID and ModelId;
                        i.putExtra("election",election);
                        i.putExtra("question", model);
                        i.putExtra("is_preview", is_preview);
                        startActivity(i);
                    }
                });
            }
            @Override
            public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i){
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.question_list_item,group,false);
                return new QuestionViewHolder(view);
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
