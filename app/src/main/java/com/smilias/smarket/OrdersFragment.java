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
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {
    DatabaseReference myRef;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    ArrayList<Integer> quantity= new ArrayList<>();
    ArrayList<String> categories= new ArrayList<>();
    ArrayList<String> items= new ArrayList<>();
    LinearLayoutManager layoutManager;
    MyRecyclerViewAdapterOrdersDelete adapter;
    String uid,supermarket;
    boolean con,con2;
    boolean from=true;
    int i;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public OrdersFragment(String passedSupermarket, String passedUId) {
        supermarket=passedSupermarket;
        uid=passedUId;

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        i=0;
        super.onResume();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot MainSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : MainSnapshot.child("ORDERS").child(uid).child(supermarket).getChildren()) {
                    String key=snapshot.getKey();
                    items.add(key);
                    quantity.add(snapshot.child("QUANTITY").getValue(Integer.class));
                }
                try {
                    if(i==0) {
                        adapter = new MyRecyclerViewAdapterOrdersDelete(getActivity(), items, quantity,uid,supermarket, myRef);
                        adapter.setClickListener(OrdersFragment.this::onItemClick);
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

    private void onItemClick(View view, int i) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminFragment, new OrdersFragment(supermarket,uid), "findThisFragment")
                .commit();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView2);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return rootView;
    }
}