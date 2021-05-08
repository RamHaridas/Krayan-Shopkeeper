package com.oceans.shopowner.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.oceans.shopowner.data.ProductData;
import com.oceans.shopowner.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    NavController navCont;
    View root;
    HomeAdapter hAdapt;
    List<ProductData> productDataList;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    Handler mainhandler;
    boolean first_time_load;
    boolean first;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        first = false;
        first_time_load = false;
        productDataList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("SHOP_DATA").child(firebaseAuth.getUid());
        }catch (Exception s){s.printStackTrace();}
        binding.homerv.setHasFixedSize(true);
        binding.homerv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notice.setVisibility(View.INVISIBLE);
        binding.additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navCont = Navigation.findNavController(root);
                navCont.navigate(R.id.action_navigation_home_to_addItemFragment);
            }
        });
        binding.progressAnim.setVisibility(View.VISIBLE);
        binding.searchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        //running thread to get data from firebase
        /*MyThread myThread = new MyThread();
        new Thread(myThread).start();*/

        try {
            fetchProducts();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        });
    }

    class MyThread implements Runnable{

        @Override
        public void run() {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    productDataList.clear();
                    for(DataSnapshot post : dataSnapshot.getChildren()){
                        ProductData productData = post.getValue(ProductData.class);
                        productDataList.add(productData);
                    }
                    if(productDataList.isEmpty()) {
                        mainhandler = new Handler(Looper.getMainLooper());
                        mainhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.notice.setVisibility(View.VISIBLE);
                                binding.progressAnim.setVisibility(View.INVISIBLE);
                            }
                        });
                    }else{
                        mainhandler = new Handler(Looper.getMainLooper());
                        mainhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.notice.setVisibility(View.INVISIBLE);
                                binding.progressAnim.setVisibility(View.INVISIBLE);
                            }
                        });
                        hAdapt = new HomeAdapter(getContext(),productDataList);
                        binding.homerv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.homerv.setHasFixedSize(true);
                        binding.homerv.setAdapter(hAdapt);
                        //binding.notice.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void filter(String text){
        ArrayList<ProductData> arr = new ArrayList<ProductData>();
        for(ProductData s : productDataList){
            if(s.getName().toLowerCase().contains(text.toLowerCase())){
                arr.add(s);
            }
        }
        hAdapt.filterList(arr);
    }

    public void fetchProducts() throws Exception{
        if(firebaseAuth.getUid() == null){
            Toast.makeText(getContext(), "No Internet Access", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(first_time_load) {
                    //return;
                }
                productDataList.clear();
                for(DataSnapshot post: dataSnapshot.getChildren()){
                    ProductData p = post.getValue(ProductData.class);
                    productDataList.add(p);
                    if(first){
                        hAdapt.notifyDataSetChanged();
                        first_time_load = true;
                        //binding.homerv.scheduleLayoutAnimation();
                    }else{
                        binding.notice.setVisibility(View.INVISIBLE);
                        binding.progressAnim.setVisibility(View.INVISIBLE);
                        hAdapt = new HomeAdapter(getContext(),productDataList);
                        binding.homerv.setAdapter(hAdapt);
                        binding.homerv.scheduleLayoutAnimation();
                        first = true;
                    }
                }
                if(productDataList.isEmpty()){
                    binding.notice.setVisibility(View.VISIBLE);
                    binding.progressAnim.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}