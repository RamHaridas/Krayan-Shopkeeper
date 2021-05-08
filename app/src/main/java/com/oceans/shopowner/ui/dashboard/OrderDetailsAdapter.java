package com.oceans.shopowner.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oceans.shopowner.data.OrderProduct;
import com.oceans.shopowner.data.ProductData;
import com.oceans.shopowner.databinding.OrderDetailCardBinding;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {

    List<OrderProduct> mList;
    Context mContext;
    OrderDetailCardBinding binding;
    public OrderDetailsAdapter(List<OrderProduct> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = OrderDetailCardBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new OrderDetailsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position) {
        final OrderProduct currItem = mList.get(position);
        holder.binding.orderpname.setText(currItem.getName());
        holder.binding.pqt.setText("Count: "+currItem.getCount());
        holder.binding.quantity.setText("\u20B9 "+currItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class OrderDetailsViewHolder extends RecyclerView.ViewHolder{
        OrderDetailCardBinding binding;
        public OrderDetailsViewHolder(@NonNull OrderDetailCardBinding b) {
            super(b.getRoot());
            binding=b;
        }
    }
}
