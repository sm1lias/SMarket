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
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {
    DatabaseReference myRef;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    ArrayList<String> categories= new ArrayList<>();
    LinearLayoutManager layoutManager;
    MyRecyclerViewAdapter adapter;
    String supermarket="consumer";
    boolean b=true;
    int i;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public CategoriesFragment(String passedsupermarket) {
        supermarket=passedsupermarket;
    }

    public CategoriesFragment(String passedsupermarket, boolean passedb) {
        supermarket=passedsupermarket;
        b = passedb;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(View view, int position) {
        if(supermarket.equals("consumer")){ //apo main
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, new ItemsFragment(adapter.getItem(position)), "findThisFragment")
                    .commit();
        }else if(b) { //apo add
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFragment, new ItemsFragment(supermarket,adapter.getItem(position)), "findThisFragment")
                    .commit();
        }else{ //apo delete
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.adminFragment, new DeleteFragment(supermarket,adapter.getItem(position)), "findThisFragment")
                    .commit();
        }
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

    //xrisimopoiw to i gia na trexei to adapter mono ti 1 fora kai oxi kathe fora pou akouei allagi sto firebase database
    @Override
    public void onStart() {
        i=0;
        super.onStart();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot MainSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot snapshot : MainSnapshot.child("CATEGORIES").getChildren()) {
                        categories.add(snapshot.getKey());
                    }
                    try {
                        if(i==0){
                        adapter = new MyRecyclerViewAdapter(getActivity(), categories);
                        adapter.setClickListener(CategoriesFragment.this::onItemClick);
                        recyclerView.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
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
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return rootView;
    }

}