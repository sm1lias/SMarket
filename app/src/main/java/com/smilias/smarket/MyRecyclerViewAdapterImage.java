package com.smilias.smarket;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapterImage extends RecyclerView.Adapter<MyRecyclerViewAdapterImage.ViewHolder> {

    private String q;
    private List<String> mData,mPrice;
    private List<Integer> mQuantity;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int quantity=0,quan,quantitys;
    private SQLiteDatabase db;
    private Context context;
    private String item, price, pItem;

    // data is passed into the constructor
    MyRecyclerViewAdapterImage(Context context, List<String> data, List<String> prices, List<Integer> quantityList, String pItem, SQLiteDatabase db) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mPrice = prices;
        this.mQuantity = quantityList;
        this.pItem =pItem;
        this.db = db;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row_with_image, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        item = mData.get(position);
        price= mPrice.get(position);
        quantitys = mQuantity.get(position);
        holder.myTextView2.setText(context.getString(R.string.supermarket).toUpperCase()+": "+item);
        holder.myTextView1.setText(context.getString(R.string.price).toUpperCase()+": "+price+"€");
        holder.textViewQuantity.setText("1");
        holder.textViewSQ.setText(context.getString(R.string.in_supermarket).toUpperCase()+": "+quantitys);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView1, myTextView2, textViewQuantity,textViewSQ;
        Button button,buttonAdd,buttonDelete;

        ViewHolder(View itemView) {
            super(itemView);

            textViewSQ = itemView.findViewById(R.id.textViewSQ);
            myTextView1 = itemView.findViewById(R.id.priceTextView);
            myTextView2 = itemView.findViewById(R.id.category);
            textViewQuantity = itemView.findViewById(R.id.textViewQ);
            button = itemView.findViewById(R.id.button3);
            buttonAdd =  itemView.findViewById(R.id.buttonAdd);
            buttonDelete =  itemView.findViewById(R.id.buttonDelete);

            itemView.setOnClickListener(this);
            button.setOnClickListener(this);
            buttonAdd.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            if (view.getId()==button.getId()){
                int i=getAdapterPosition();
                q=textViewQuantity.getText().toString();
                quantity=Integer.parseInt(q);
                Cursor cursor2 = db.rawQuery("SELECT quantity FROM cart WHERE item=? AND supermarket=?", new String[]{pItem, mData.get(i)});
                if (cursor2.getCount()>0){
                    cursor2.moveToFirst();
                    quan=Integer.parseInt(cursor2.getString(0));
                }
                if((quan+quantity)>mQuantity.get(i)){
                    Toast.makeText(context,context.getString(R.string.cant_competed), Toast.LENGTH_SHORT).show();
                }else {
                    Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE item=? AND supermarket=?", new String[]{pItem, mData.get(i)});
                    if (cursor.getCount() > 0) {
                        db.execSQL("UPDATE cart SET quantity = quantity+" + quantity + " WHERE item=?  AND supermarket =?", new String[]{pItem, mData.get(i)});
                    } else {
                        db.execSQL("INSERT INTO cart VALUES('" + pItem + "','" + mData.get(i) + "','" + quantity + "')");
                    }
                    Toast.makeText(context,context.getString(R.string.added).toUpperCase(), Toast.LENGTH_SHORT).show();
                }
            } else if(view.getId()==buttonAdd.getId()){
                q=textViewQuantity.getText().toString();
                quantity=Integer.parseInt(q);
                if(quantity < mQuantity.get(getAdapterPosition()) ) quantity=quantity+1;
                textViewQuantity.setText(String.valueOf(quantity));
            } else if(view.getId()==buttonDelete.getId()){
                q=textViewQuantity.getText().toString();
                quantity=Integer.parseInt(q);
                if(quantity > 1) quantity=quantity-1;
                textViewQuantity.setText(String.valueOf(quantity));
            }
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
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
