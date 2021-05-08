package com.oceans.shopowner.ui.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oceans.shopowner.R;
import com.oceans.shopowner.data.OrderData;
import com.oceans.shopowner.databinding.FragmentPreviousOrdersBinding;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PreviousOrdersFragment extends Fragment {
    List<OrderData> previousList;
    FragmentPreviousOrdersBinding binding;
    View root;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    OrderAdapter previousOrderAdapter;
    boolean first;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPreviousOrdersBinding.inflate(inflater,container,false);
        root = binding.getRoot();
        previousList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDERS");
        firebaseAuth = FirebaseAuth.getInstance();
        first = false;

        binding.previousrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.previousrecyclerview.setHasFixedSize(true);
        try {
            getOrders();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    public void getOrders() throws Exception {
        if (firebaseAuth.getUid() == null) {
            Toast.makeText(getContext(), "No internet access", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.orderByChild("shop_uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    OrderData orderData = post.getValue(OrderData.class);
                    if (orderData.isShopCompleted() && orderData.isUserCompleted()) {
                        previousList.add(orderData);
                    }
                    if (first) {
                        previousOrderAdapter.notifyDataSetChanged();
                        binding.previousrecyclerview.scheduleLayoutAnimation();
                    } else {
                        previousOrderAdapter = new OrderAdapter(previousList, getContext(), PreviousOrdersFragment.this);
                        binding.previousrecyclerview.setAdapter(previousOrderAdapter);
                        binding.previousrecyclerview.scheduleLayoutAnimation();
                        first = true;
                    }
                }
                if (previousList.isEmpty()) {
                    binding.notice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}