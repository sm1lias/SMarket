package com.smilias.smarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapterDelete extends RecyclerView.Adapter<MyRecyclerViewAdapterDelete.ViewHolder> {

    private final DatabaseReference myRef;
    private String q;
    private List<String> mItem;
    private List<Integer> mQuantity;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int quantity=0;
    private String category;
    private String supermarket;

    String item;


    // data is passed into the constructor
        MyRecyclerViewAdapterDelete(Context context, List<String> item, List<Integer> quantitylist, String cat, String smarket, DatabaseReference myRf) {
            this.mInflater = LayoutInflater.from(context);
            this.mItem = item;
            this.mQuantity = quantitylist;
            this.myRef=myRf;
            this.supermarket=smarket;
            this.category = cat;
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
        holder.myTextViewItem.setText("ITEM: "+item);
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

            itemView.setOnClickListener(this);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            if (view.getId()==button.getId()){
                int i=getAdapterPosition();
                myRef.child("CATEGORIES").child(category).child(mItem.get(i)).child(supermarket).removeValue();
//                db.execSQL("DELETE FROM cart WHERE item=?  AND supermarket =?", new String[]{mItem.get(i), mSupermarket.get(i)});
                //setItems(mItem,mSupermarket,mQuantity);
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
