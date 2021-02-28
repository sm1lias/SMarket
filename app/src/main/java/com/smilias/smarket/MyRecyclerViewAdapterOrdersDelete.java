package com.smilias.smarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MyRecyclerViewAdapterOrdersDelete extends RecyclerView.Adapter<MyRecyclerViewAdapterOrdersDelete.ViewHolder> {

    private final DatabaseReference myRef;
    private final Context context;
    private String q;
    private List<String> mItem;
    private List<Integer> mQuantity;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int quantity=0;
    private String uid;
    private String supermarket;
    String item;


    // data is passed into the constructor
        MyRecyclerViewAdapterOrdersDelete(Context context, List<String> items, List<Integer> quantitylist, String uid, String smarket, DatabaseReference myRf) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.mItem = items;
            this.mQuantity = quantitylist;
            this.myRef=myRf;
            this.supermarket=smarket;
            this.uid = uid;
        }

        // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row_delete, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        item = mItem.get(position);
        quantity=mQuantity.get(position);
        holder.textViewQuantity.setText(String.valueOf(quantity));
        holder.myTextViewItem.setText(context.getString(R.string.item).toUpperCase()+": "+item);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mItem.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextViewItem, textViewQuantity;
        Button button;

        ViewHolder(View itemView) {
            super(itemView);

            myTextViewItem = itemView.findViewById(R.id.priceTextView);
            textViewQuantity = itemView.findViewById(R.id.textViewQ);
            button = itemView.findViewById(R.id.button3);
            button.setText(context.getString(R.string.deliver).toUpperCase());

            itemView.setOnClickListener(this);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            if (view.getId()==button.getId()){
                int i=getAdapterPosition();
                myRef.child("ORDERS").child(uid).child(supermarket).child(mItem.get(i)).child("QUANTITY").removeValue();
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mItem.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
