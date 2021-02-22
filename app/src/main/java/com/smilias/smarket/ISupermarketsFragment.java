package com.smilias.smarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ISupermarketsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ISupermarketsFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    DatabaseReference myRef;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    ArrayList<String> categories= new ArrayList<>();
    LinearLayoutManager layoutManager;
    MyRecyclerViewAdapter adapter;
    String item1, item2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ISupermarketsFragment() {
        // Required empty public constructor
    }

    public ISupermarketsFragment(String passedString1, String passedString2) {
        // Required empty public constructor
        item1=passedString1;
        item2=passedString2;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ISupermarketsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ISupermarketsFragment newInstance(String param1, String param2) {
        ISupermarketsFragment fragment = new ISupermarketsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot MainSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : MainSnapshot.child("CATEGORIES").child(item1).child(item2).getChildren()){  //testing

//                    categories.add(snapshot.getValue(String.class).toString());
                    categories.add(snapshot.getKey());
                }
                adapter = new MyRecyclerViewAdapter(getActivity(), categories);
                adapter.setClickListener(ISupermarketsFragment.this::onItemClick);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}