package com.swg7.dalpolls.ui.main;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.swg7.dalpolls.R;
import com.swg7.dalpolls.model.ElectionModel;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListElectionFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private FirestoreRecyclerAdapter<ElectionModel, ElectionViewHolder> adapter;
    private FirebaseFirestore database;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListElectionFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ListElectionFragment newInstance(int columnCount) {
        ListElectionFragment fragment = new ListElectionFragment();
        return fragment;
    }

    @SuppressWarnings("unused")
    public static ListElectionFragment newInstance() {
        return newInstance(1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ElectionModel item);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_election_list, container, false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(view.getContext());
        RecyclerView rv = (RecyclerView)view;
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = setUpAdapter(database);
        rv.setAdapter(adapter);
        return view;
    }



    //Creates a Firestore adapter to be used with a Recycler view.
    //We will see adapter in more details after the midterm
    //More info on this: https://github.com/firebase/FirebaseUI-Android/blob/master/firestore/README.md
    private FirestoreRecyclerAdapter<ElectionModel, ElectionViewHolder> setUpAdapter(FirebaseFirestore db){
        Query query = db.collection("elections").orderBy("name").limit(50);
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
                if(model!=null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference ref = db.collection("elections")
                                    .document(model.id);

                            final View v = view;

                            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    ElectionModel e = documentSnapshot.toObject(ElectionModel.class);
                                    if (!e.isEditing) {
                                        mListener.onListFragmentInteraction(model);
                                    } else {
                                        Toast.makeText(v.getContext(), "Election is being edited"
                                                + " by another user", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(v.getContext(),
                                            "Can not check", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
                }
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


}
