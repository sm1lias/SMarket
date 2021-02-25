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
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    DatabaseReference myRef;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    ArrayList<String> categories= new ArrayList<>();
    ArrayList<String> items= new ArrayList<>();
    LinearLayoutManager layoutManager;
    MyRecyclerViewAdapter adapter;
    String item,supermarket="consumer";
    boolean con,con2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemsFragment() {
        // Required empty public constructor
    }
    public ItemsFragment(ArrayList<String> itemsPassed) {
        // Required empty public constructor
        items=itemsPassed;
        con=false;
    }

    public ItemsFragment(String passedString) {
        // Required empty public constructor
        item=passedString;
        con=true;
    }

    public ItemsFragment(String passedSupermarket,String passedString) {
        // Required empty public constructor
        supermarket=passedSupermarket;
        item=passedString;
        con=true;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemsFragment newInstance(String param1, String param2) {
        ItemsFragment fragment = new ItemsFragment();
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


    }
    @Override
    public void onStart() {
        super.onStart();
        try {
            if (con == false) {
                adapter = new MyRecyclerViewAdapter(getActivity(), items);
                adapter.setClickListener(ItemsFragment.this::onItemClick);
                recyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(con) {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot MainSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot snapshot : MainSnapshot.child("CATEGORIES").child(item).getChildren()) {

//                    categories.add(snapshot.getValue(String.class).toString());
                        categories.add(snapshot.getKey());
                    }
                    try {
                        adapter = new MyRecyclerViewAdapter(getActivity(), categories);
                        adapter.setClickListener(ItemsFragment.this::onItemClick);
                        recyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
        if(supermarket.equals("consumer")){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flFragment, new ISupermarketsFragment(/*item,*/adapter.getItem(position)), "findThisFragment")
                .commit();
        }else{
        }
    }
}