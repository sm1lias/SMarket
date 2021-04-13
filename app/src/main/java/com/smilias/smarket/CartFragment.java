package com.smilias.smarket;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements MyRecyclerViewAdapterCart.ItemClickListener {
    private SQLiteDatabase db;
    private ArrayList<String> supermarketArray = new ArrayList<>();
    private ArrayList<String> item=new ArrayList<>();
    private ArrayList<String> category= new ArrayList<>();
    private MyRecyclerViewAdapterCart adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private FirebaseUser cUser;
    private int quantityDb,i;
    private ArrayList<Integer> quantityFirebase = new ArrayList<>();
    private ArrayList<Integer> quantityOrders = new ArrayList<>();
    private ArrayList<Integer> quantity= new ArrayList<>();
    private double price;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private String item2, notItem, supermarket;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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

        cUser = FirebaseAuth.getInstance().getCurrentUser();

        db = getActivity().openOrCreateDatabase("cartDb", Context.MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS cart(item TEXT,supermarket TEXT, quantity INT)");

        Cursor cursor = db.rawQuery("SELECT * FROM cart",null);
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                item.add(cursor.getString(0));
                supermarketArray.add(cursor.getString(1));
                quantity.add(cursor.getInt(2));
            }
        }


    }

    @Override
    public void onStart() {
        super.onStart();
            adapter = new MyRecyclerViewAdapterCart(getActivity(), item, supermarketArray, quantity, db);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        // Inflate the layout for this fragment

        Button btnCheckOut=rootView.findViewById(R.id.btnCheckOut);
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCheckOut();
            }
        });



        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }


    @Override
    public void onItemClick(View view, int position) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flFragment, new CartFragment(), "findThisFragment")
                .commit();
    }

    private void toCheckOut() {
        i=0;
        ArrayList<String> itemList=new ArrayList<>();
        if (cUser != null) {
            price = 0.0;
            Cursor cursor = db.rawQuery("SELECT * FROM cart", null);
            if (cursor.getCount() > 0) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot MainSnapshot) {
                        while (cursor.moveToNext()) {
                            item2 = cursor.getString(0);
                            supermarket = cursor.getString(1);
                            quantityDb = cursor.getInt(2);
                            for (DataSnapshot snap : MainSnapshot.child("CATEGORIES").getChildren()) {
                                if (snap.hasChild(item2)) {
                                    category.add(snap.getKey());
                                }
                            }
                            price = price + (quantityDb * MainSnapshot.child("CATEGORIES").child(category.get(i)).child(item2).child(supermarket).child("PRICE").getValue(double.class));
                            quantityFirebase.add(MainSnapshot.child("CATEGORIES").child(category.get(i)).child(item2).child(supermarket).child("QUANTITY").getValue(int.class));
                            quantityOrders.add(MainSnapshot.child("ORDERS").child(cUser.getUid()).child(supermarket).child(item2).child("QUANTITY").getValue(int.class));
                            if (quantityFirebase.get(i)- quantityDb <0) itemList.add(item2+" in "+ supermarket +", ");
                            i++;
                        }
                        if (!itemList.isEmpty()){
                            StringBuilder builder = new StringBuilder();
                            for (int i=0;i<itemList.size();i++) builder.append(itemList.get(i));
                            notItem=builder.toString();
                            if (itemList.size()==1)
                                Toast.makeText(getActivity(),getString(R.string.the_item)+ notItem + getString(R.string.is_not_available), Toast.LENGTH_LONG).show();
                            else Toast.makeText(getActivity(),getString(R.string.the_items)+ notItem +getString(R.string.are_not_available), Toast.LENGTH_LONG).show();
                        }else {
                            if(i>0){
                                Intent intent = new Intent(getActivity(), CheckOutActivity.class);
                                intent.putExtra("price", price);
                                intent.putExtra("quantityfirebase", quantityFirebase);
                                intent.putExtra("category",category);
                                intent.putExtra("quantityorders", quantityOrders);
                                startActivity(intent);
                            }
                        }
                        i=0;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } else Toast.makeText(getActivity(), getString(R.string.cart_empty).toUpperCase(), Toast.LENGTH_LONG).show();
        } else Toast.makeText(getActivity(), getString(R.string.please_login).toUpperCase(), Toast.LENGTH_LONG).show();
    }
}