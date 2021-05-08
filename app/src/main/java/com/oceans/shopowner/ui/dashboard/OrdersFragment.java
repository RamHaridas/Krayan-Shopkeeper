package com.oceans.shopowner.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oceans.shopowner.R;
import com.oceans.shopowner.data.OrderData;
import com.oceans.shopowner.databinding.FragmentOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    FragmentOrdersBinding binding;
    NavController navCont;
    public static List<OrderData> orderList;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    View root;
    OrderAdapter orderAdapter;
    boolean first;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater,container,false);
        root = binding.getRoot();
        first = false;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDERS");
        firebaseAuth = FirebaseAuth.getInstance();
        orderList = new ArrayList<>();
        binding.orderRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.orderRecycle.setHasFixedSize(true);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navCont = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
                navCont.navigate(R.id.action_navigation_dashboard_to_previousOrdersFragment);
            }
        });
        try {
            getOrders();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    public void getOrders() throws Exception{
        if(firebaseAuth.getUid() == null){
            Toast.makeText(getContext(), "No internet access", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.orderByChild("shop_uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    OrderData orderData = post.getValue(OrderData.class);
                    if(!orderData.isShopCompleted() || !orderData.isUserCompleted()) {
                        orderList.add(orderData);
                    }
                    if(first){
                        orderAdapter.notifyDataSetChanged();
                        binding.orderRecycle.scheduleLayoutAnimation();
                    }else{

                        orderAdapter = new OrderAdapter(orderList,getContext(),OrdersFragment.this);
                        binding.orderRecycle.setAdapter(orderAdapter);
                        binding.orderRecycle.scheduleLayoutAnimation();
                        first = true;
                    }
                }
                if(orderList.isEmpty()){
                    binding.notice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}