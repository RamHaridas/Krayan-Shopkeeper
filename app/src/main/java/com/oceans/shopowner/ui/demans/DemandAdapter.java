package com.oceans.shopowner.ui.demans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.oceans.shopowner.R;
import com.oceans.shopowner.data.QueryData;
import com.oceans.shopowner.popups.DemandAcceptPopup;


import java.util.ArrayList;
import java.util.List;

public class DemandAdapter extends RecyclerView.Adapter<DemandAdapter.OrderViewHolder>{

    public static QueryData static_orderData;
    List<QueryData> list;
    Context context;
    FirebaseAuth firebaseAuth;
    Fragment fragment;
    List<String> stringList;
    public DemandAdapter(Context context, List<QueryData> list){
        this.context = context;
        this.list = list;
        firebaseAuth = FirebaseAuth.getInstance();
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.querycard,parent,false);
        return new DemandAdapter.OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder,  int position) {
        final QueryData orderData = list.get(position);
        String text = "PRODUCT NAME: "+orderData.getName();
        holder.name.setText(text);
        String text2 = "DESCRIPTION: "+orderData.getDescription();
        holder.date.setText(text2);
        if(orderData.getDescription() == null){
            stringList = new ArrayList<>();
        }else{
            stringList = orderData.getShopkeepers();
        }
        try {
            if(stringList.contains(firebaseAuth.getUid())){
                holder.button.setEnabled(false);
            }
        }catch (Exception e){e.printStackTrace();}

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                DemandAcceptPopup dm = new DemandAcceptPopup(orderData.getUid(),stringList);
                dm.show(appCompatActivity.getSupportFragmentManager(),"query");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView name, date;
        Button button;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productname);
            date = itemView.findViewById(R.id.productdesc);
            button = itemView.findViewById(R.id.viewbt);
        }
    }
}
