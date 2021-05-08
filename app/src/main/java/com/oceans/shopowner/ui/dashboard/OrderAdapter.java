package com.oceans.shopowner.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.oceans.shopowner.R;
import com.oceans.shopowner.data.OrderData;
import com.oceans.shopowner.databinding.OrderCardBinding;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    List<OrderData> mList;
    Context mContext;
    OrderCardBinding binding;
    NavController navcont;
    public static OrderData orderData;
    Fragment fragment;
    public OrderAdapter(List<OrderData> mList, Context mContext, Fragment fragment) {
        this.mList = mList;
        this.mContext = mContext;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = OrderCardBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        final OrderData currItem = mList.get(position);
        holder.binding.ordername.setText(currItem.getUser_name());
        holder.binding.tnd.setText(currItem.getDate());
        holder.binding.viewbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderData = currItem;
                if(fragment instanceof OrdersFragment) {
                    NavHostFragment.findNavController(fragment).navigate(R.id.action_navigation_dashboard_to_viewOrderFragment);
                }else if(fragment instanceof PreviousOrdersFragment){
                    NavHostFragment.findNavController(fragment).navigate(R.id.action_previousOrdersFragment_to_viewOrderFragment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder{
        OrderCardBinding binding;
        public OrderViewHolder(OrderCardBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
