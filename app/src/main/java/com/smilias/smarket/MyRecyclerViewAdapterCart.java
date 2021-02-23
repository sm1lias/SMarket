package com.smilias.smarket;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapterCart extends RecyclerView.Adapter<MyRecyclerViewAdapterCart.ViewHolder> {

    private String q;
    private List<String> mItem,mSupermarket;
    private List<Integer> mQuantity;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int quantity=0;
    SQLiteDatabase db;
    String item, supermarket;


    // data is passed into the constructor
    MyRecyclerViewAdapterCart(Context context, List<String> item, List<String> supermarket, List<Integer> quantitylist,SQLiteDatabase db) {
        this.mInflater = LayoutInflater.from(context);
        this.mItem = item;
        this.mSupermarket = supermarket;
        this.mQuantity = quantitylist;
        this.db = db;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row_cart, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        item = mItem.get(position);
        supermarket= mSupermarket.get(position);
        quantity=mQuantity.get(position);
        holder.textViewQuantity.setText(String.valueOf(quantity));
        holder.myTextView2.setText("SUPERMARKET: "+item);
        holder.myTextView1.setText("PRICE: "+supermarket);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mItem.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView1, myTextView2, textViewQuantity;
        Button button;


        ViewHolder(View itemView) {
            super(itemView);

            myTextView1 = itemView.findViewById(R.id.price);
            myTextView2 = itemView.findViewById(R.id.category);
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
                db.execSQL("DELETE FROM cart WHERE item=?  AND supermarket =?", new String[]{mItem.get(i), mSupermarket.get(i)});
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
