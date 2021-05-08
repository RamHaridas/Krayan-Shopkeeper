package com.oceans.shopowner.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oceans.shopowner.data.OrderData;
import com.oceans.shopowner.data.OrderProduct;
import com.oceans.shopowner.databinding.FragmentViewOrderBinding;
import com.oceans.shopowner.popups.DeliveryPopup;
import com.oceans.shopowner.popups.OrderCompletedPopup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ViewOrderFragment extends Fragment {
    FragmentViewOrderBinding binding;
    Double myLatitude = 0.0;
    Double myLongitude = 0.0;
    String labelLocation = "address";
    List<OrderDetailsItem> productList;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewOrderBinding.inflate(inflater,container,false);
        productList = new ArrayList<>();

        binding.viewOrdersRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + myLatitude  + ">,<" + myLongitude + ">?q=<" + myLatitude  + ">,<" + myLongitude + ">(" + labelLocation + ")"));
                startActivity(intent);
            }
        });
        binding.completeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OrderAdapter.orderData.getUnique_id() == null){
                    Toast.makeText(getContext(), "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                    return;
                }
                OrderCompletedPopup op = new OrderCompletedPopup(OrderAdapter.orderData.getUnique_id(),ViewOrderFragment.this);
                op.show(getActivity().getSupportFragmentManager(),"done");
            }
        });
        if(OrderAdapter.orderData.isDriver()){
            binding.getdriver.setEnabled(false);
        }
        binding.getdriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OrderAdapter.orderData.getUnique_id() == null){
                    Toast.makeText(getContext(), "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                    return;
                }
                DeliveryPopup dp = new DeliveryPopup(OrderAdapter.orderData.getUnique_id(),ViewOrderFragment.this);
                dp.show(getActivity().getSupportFragmentManager(),"driver");
            }
        });
        /*binding.outdelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.completed.setVisibility(View.VISIBLE);
                binding.outdelivery.setVisibility(View.GONE);
            }
        });
        binding.completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.completed.setVisibility(View.GONE);
                binding.comp.setVisibility(View.VISIBLE);
            }
        });*/
        setData();
        return binding.getRoot();
    }

    public void setData(){
        int total = 0;
        try {
            binding.address.setText(OrderAdapter.orderData.getUser_address1() + ", " + OrderAdapter.orderData.getUser_address2());
            binding.uname.setText(OrderAdapter.orderData.getUser_name());
            binding.timeanddate.setText(OrderAdapter.orderData.getDate() + ", " + OrderAdapter.orderData.getTime());
            binding.contact.setText(OrderAdapter.orderData.getUser_mobile());
            myLatitude = OrderAdapter.orderData.getUserLat();
            myLongitude = OrderAdapter.orderData.getUserLong();
            binding.viewOrdersRecycle.setAdapter(new OrderDetailsAdapter(OrderAdapter.orderData.getProductDataList(),getContext()));
            for(OrderProduct p : OrderAdapter.orderData.getProductDataList()){
                total = total + p.getCount() * Integer.parseInt(p.getPrice());
            }
            int t = Integer.parseInt(OrderAdapter.orderData.getDelivery_charge()) + total;
            binding.amount.setText("\u20B9 "+t);
            if(OrderAdapter.orderData.isUserCompleted() && OrderAdapter.orderData.isShopCompleted()){
                binding.getdriver.setEnabled(false);
                binding.completeys.setEnabled(false);
            }
            if(OrderAdapter.orderData.isShopCompleted()){
                binding.completeys.setEnabled(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}