package com.swg7.dalpolls;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.swg7.dalpolls.model.ElectionModel;
import com.swg7.dalpolls.model.QuestionModel;
import com.swg7.dalpolls.model.UserModel;
import com.swg7.dalpolls.notification.NotificationCreator;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class NewElection extends AppCompatActivity {

    private ElectionModel election;
    private QuestionModel question = null;

    private EditText title;
    private TextView startDate;
    private TextView endDate;
    private Switch visable;
    private Switch votable;
    private Switch results;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    private boolean deleting = false;



    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_new_election);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startDate = findViewById(R.id.editElectionStartDate);
        endDate = findViewById(R.id.editElectionEndDate);

        title = findViewById(R.id.editElectionTitle);
        final TextView startDate = findViewById(R.id.editElectionStartDate);
        final TextView endDate = findViewById(R.id.editElectionEndDate);

        visable = findViewById(R.id.toggleVisable);
        votable = findViewById(R.id.toggleVotable);
        results = findViewById(R.id.toggleResults);

        if(getIntent() != null && getIntent().getExtras() != null)
            election = (ElectionModel) getIntent().getExtras().get("election");

        if (election != null) {
            title.setText(election.name);
            if (election.startDate != null && election.endDate != null) {
                startDate.setText(ElectionCreator.DATE_FORMAT.format(election.startDate));
                endDate.setText(ElectionCreator.DATE_FORMAT.format(election.endDate));
            }
            visable.setChecked(election.isPublic);
            votable.setChecked(election.isOpen);
            results.setChecked(election.areResultsVisible);

            RecyclerView recyclerView = findViewById(R.id.recyclerView_answers);
            adapter = setUpAdapter(FirebaseFirestore.getInstance());

            setUpRecyclerView(recyclerView,adapter);

            //Update the database to show that this is currently being edited.
            this.election.isEditing = true;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("elections").document(election.id);

            ref.set(election);


        } else {
            election = new ElectionModel();
        }


        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(startDate, election == null ? null : election.startDate);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDate(endDate, election == null ? null : election.endDate);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView_answers);
        adapter = setUpAdapter(FirebaseFirestore.getInstance());

        setUpRecyclerView(recyclerView,adapter);


        FloatingActionButton fab = findViewById(R.id.create_question_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (election.id != null) {
                    //TODO: Create intent to add real questions!!  Below inserts a dummy question to test recycler view
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference ref;
                    ref = db.collection("elections").document(election.id).
                            collection("questions").document();
                    QuestionModel q = new QuestionModel("Test Question", ref.getId());

                    ref.set(q);

                    Toast.makeText(getApplicationContext(), "Dummy Question Made", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "ElectionID is NULL", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(election.name);
    }

    private void setDate(final TextView e, Date d) {
        final Calendar cal = Calendar.getInstance();
        if(d == null) cal.setTime(new Date());
        else cal.setTime(d);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                cal.set(year,month,day);
                e.setText(ElectionCreator.DATE_FORMAT.format(cal.getTime()));
            }
        }, year, month, day);
        datePickerDialog.show();
    }



    //Connects our recycler view with the viewholder (how we want to show our model[data])
    // and the firestore adapter
    private void setUpRecyclerView(RecyclerView rv, FirestoreRecyclerAdapter adapter){
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
    }

    public void save(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        if (election.id == null) {
            // if the election does not yet exist
            ref = db.collection("elections").document();
            election.id = ref.getId();
        } else {
            ref = db.collection("elections").document(election.id);
        }
        final ElectionCreator electionCreator = new ElectionCreator()
                .setName(title.getText().toString())
                .setStartDate(startDate.getText().toString())
                .setEndDate(endDate.getText().toString())
                .setPublish(visable.isChecked())
                .setOpen(votable.isChecked())
                .setResults(results.isChecked())
                .setId(election.id);
        if (electionCreator.isValid()) {
            election = electionCreator.build();
            final View v = view;
            ref.set(election)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Success", Toast.LENGTH_SHORT).show();
                            if(election.isOpen){
                                NotificationCreator nc = new NotificationCreator(getApplicationContext());
                                nc.createNewNotification(title.getText().toString() + " is now open!");
                            }

                            if(election.areResultsVisible){
                                NotificationCreator nc = new NotificationCreator(getApplicationContext());
                                nc.createNewNotification(title.getText().toString() + "'s results are now available!");
                            }

                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if(!electionCreator.hasValidTitle()){
                Toast.makeText(view.getContext(), R.string.invalid_title_toast_message, Toast.LENGTH_SHORT).show();
            }
            if(!electionCreator.hasValidDate()){
                Toast.makeText(view.getContext(), R.string.invalid_date_toast_message, Toast.LENGTH_SHORT).show();
            }


        }
    }

    //Same as save but with constant new id
    //We should refactor to have less copies of the same code
    public void clone(View view) {
        if (election.id == null) {
            Toast.makeText(view.getContext(), "Election hasn't been finished", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref;
            //Create new id
            ref = db.collection("elections").document();
            //Copy all similar content
            ElectionCreator electionCreator = new ElectionCreator()
                    .setName(title.getText().toString())
                    .setStartDate(startDate.getText().toString())
                    .setEndDate(endDate.getText().toString())
                    .setId(ref.getId());
            if (electionCreator.isValid()) {
                final ElectionModel electionClone = electionCreator.build();
                electionClone.isEditing = false;
                final View v = view;
                //Reference: https://stackoverflow.com/questions/50727254/how-to-retrieve-all-documents-from-a-collection-in-firestore
                db.collection("elections")
                        .document(election.id)
                        .collection("questions")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> questions = task.getResult().getDocuments();
                            Iterator i = questions.iterator();
                            while (i.hasNext()) {
                                DocumentSnapshot a = (DocumentSnapshot) i.next();
                                System.out.println(a.getData().toString());
                                FirebaseFirestore.getInstance().collection("elections")
                                        .document(electionClone.id)
                                        .collection("questions")
                                        .document()
                                        .set(a.getData());
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                ref.set(electionClone)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(), R.string.clone_election_toast_message, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                if (!electionCreator.hasValidTitle()) {
                    Toast.makeText(view.getContext(), R.string.invalid_title_toast_message, Toast.LENGTH_SHORT).show();
                }
                if (!electionCreator.hasValidDate()) {
                    Toast.makeText(view.getContext(), R.string.invalid_date_toast_message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void preview(View view){
        if (election.id != null) {
            Intent i = new Intent(getApplicationContext(), ElectionPreview.class);
            i.putExtra("election",election);
            i.putExtra("is_preview",true);
            startActivity(i);

        } else {
            Toast.makeText(getApplicationContext(), "ElectionID is NULL", Toast.LENGTH_SHORT).show();
        }

    }


    public void delete(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref;
        if (election.id == null) {
            Toast.makeText(view.getContext(), "No ID", Toast.LENGTH_SHORT).show();
            // if the election does not yet exist
            finish();
            return;
        } else {
            ref = db.collection("elections").document(election.id);
        }
        election.name = title.getText().toString();
        final View v = view;
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(v.getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                        deleting = true;
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

    //Creates a Firestore adapter to be used with a Recycler view.
    //We will see adapter in more details after the midterm
    //More info on this: https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md
    private FirestoreRecyclerAdapter<QuestionModel, QuestionViewHolder> setUpAdapter(FirebaseFirestore db){

        if(election.id == null) {
            DocumentReference ref = db.collection("elections").document();
            election.id = ref.getId();
        }

        Query query = db.collection("elections").document(election.id).collection("questions").orderBy("name").limit(50);
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
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), NewQuestion.class);
                        //Adds an extra for ElectionID and ModelId;
                        i.putExtra("election",election);
                        i.putExtra("question", model);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Clicked " + model.name +"!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!deleting) {
            //Update the database to show that this is no longer being edited.
            this.election.isEditing = false;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("elections").document(election.id);

            ref.set(election);
        }
    }

}
