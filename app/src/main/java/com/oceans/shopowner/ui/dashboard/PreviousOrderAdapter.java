package com.oceans.shopowner.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.oceans.shopowner.R;
import com.oceans.shopowner.data.OrderData;
import com.oceans.shopowner.databinding.OrderCardBinding;

import java.util.List;

public class PreviousOrderAdapter extends RecyclerView.Adapter<PreviousOrderAdapter.PreviousOrderViewHolder> {
    List<OrderData> mList;
    Context mContext;
    OrderCardBinding binding;
    NavController navcont;
    public PreviousOrderAdapter(List<OrderData> mList, Context mContext, NavController navcont) {
        this.mList = mList;
        this.mContext = mContext;
        this.navcont=navcont;
    }
    @NonNull
    @Override
    public PreviousOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= OrderCardBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new PreviousOrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousOrderViewHolder holder, int position) {
        final OrderData currItem = mList.get(position);
        holder.binding.ordername.setText(currItem.getUser_name());
        holder.binding.tnd.setText(currItem.getTime());
        holder.binding.viewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Cannot view previous orders", Toast.LENGTH_SHORT).show();
                //navcont.navigate(R.id.action_previousOrdersFragment_to_viewOrderFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class PreviousOrderViewHolder extends RecyclerView.ViewHolder{
        OrderCardBinding binding;

        public PreviousOrderViewHolder(@NonNull OrderCardBinding b) {
            super(b.getRoot());
            binding = b;

        }
    }
}
