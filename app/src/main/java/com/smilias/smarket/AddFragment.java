package com.smilias.smarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    String item,supermarket,category;
    DatabaseReference myRef;
    FirebaseDatabase database;
    int quantity=0, newQuantity;
    double price=0.0, newPrice;
    Button buttonQuantity,buttonPr;
    TextView textViewQuantity,textViewPr;
    EditText editTextQuantity,editTextPr;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFragment() {
        // Required empty public constructor
    }
    public AddFragment(String passedsupermarket,String passedString) {
        item=passedString;
        supermarket=passedsupermarket;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot MainSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snap : MainSnapshot.child("CATEGORIES").getChildren()) {
                    if (snap.hasChild(item)) {
                        category=snap.getKey();
                    }
                }
                try {
                    price = MainSnapshot.child("CATEGORIES").child(category).child(item).child(supermarket).child("PRICE").getValue(double.class);
                    quantity = MainSnapshot.child("CATEGORIES").child(category).child(item).child(supermarket).child("QUANTITY").getValue(int.class);
                }catch (Exception e){}
                textViewPr.setText(String.valueOf(price));
                textViewQuantity.setText(String.valueOf(quantity));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        buttonQuantity=rootView.findViewById(R.id.buttonQuantity);
        buttonPr=rootView.findViewById(R.id.buttonPr);
        textViewQuantity=rootView.findViewById(R.id.textViewQuantity);
        textViewPr=rootView.findViewById(R.id.textViewPr);
        editTextQuantity=rootView.findViewById(R.id.editTextQuantity);
        editTextPr=rootView.findViewById(R.id.editTextPr);
        //textViewPr.setText(item);
        //textViewQuantity.setText(String.valueOf(quantity));
        //textViewPr.setText(String.valueOf(price));

        return rootView;
    }
}