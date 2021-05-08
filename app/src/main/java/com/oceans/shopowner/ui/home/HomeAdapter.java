package com.oceans.shopowner.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oceans.shopowner.R;
import com.oceans.shopowner.animations.Animations;
import com.oceans.shopowner.data.ProductData;
import com.oceans.shopowner.databinding.HomeItemBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.oceans.shopowner.popups.DeletePopup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{
    Context mContext;
    List<ProductData> mList;
    HomeItemBinding binding;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,dbref;
    public HomeAdapter(Context mContext, List<ProductData> mList) {
        this.mContext = mContext;
        this.mList = mList;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SHOP_DATA").child(firebaseAuth.getUid());
        dbref = FirebaseDatabase.getInstance().getReference().child("ALL_PRODUCTS");
    }

    @NotNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = HomeItemBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {
        try {
            holder.binding.hometv.setText(mList.get(position).getName().toUpperCase());
            holder.binding.hometv1.setText(mList.get(position).getPrice());
            boolean check = mList.get(position).isStock();
            if(check){
                binding.instock.setChecked(true);
            }else{
                binding.outstock.setChecked(true);
            }
            Glide.with(mContext)
                    .load(mList.get(position).getImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.homeiv);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        holder.binding.instock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.instock.setChecked(true);
                binding.outstock.setChecked(false);
                Map<String,Object> map = new HashMap<>();
                map.put("stock",true);
                databaseReference.child(mList.get(position).getRef1()).updateChildren(map);
            }
        });
        holder.binding.outstock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.outstock.setChecked(true);
                binding.instock.setChecked(false);
                Map<String,Object> map = new HashMap<>();
                map.put("stock",false);
                databaseReference.child(mList.get(position).getRef1()).updateChildren(map);
            }
        });
        /*holder.binding.stockbt.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if(group.getCheckedButtonId() == binding.instock.getId()){
                    //Toast.makeText(mContext, "In stock: "+isChecked, Toast.LENGTH_SHORT).show();
                    Map<String,Object> map = new HashMap<>();
                    map.put("stock",true);
                    databaseReference.child(mList.get(position).getRef1()).updateChildren(map);
                    //dbref.child(mList.get(position).getRef2()).updateChildren(map);

                }else if(group.getCheckedButtonId() == binding.outstock.getId()){
                    //Toast.makeText(mContext, "Out of stock: "+isChecked, Toast.LENGTH_SHORT).show();
                    Map<String,Object> map = new HashMap<>();
                    map.put("stock",false);
                    databaseReference.child(mList.get(position).getRef1()).updateChildren(map);
                    //dbref.child(mList.get(position).getRef2()).updateChildren(map);
                }
            }
        });*/
        holder.binding.removebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove your item from data base

                DeletePopup deletePopup = new DeletePopup();
                Bundle args = new Bundle();
                args.putString("ref1",mList.get(position).getRef1());
                args.putString("ref2",mList.get(position).getRef2());
                AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
                deletePopup.setArguments(args);
                deletePopup.show(appCompatActivity.getSupportFragmentManager(),"delete");
            }
        });
        holder.binding.homell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProductData productData = mList.get(position);
                    boolean show = toggleLayout(!productData.isExpanded(), holder.binding.viewMoreBtn, holder.binding.homehideRl);
                    productData.setExpanded(show);
                }catch (Exception e){}
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder{
        HomeItemBinding binding;
        public HomeViewHolder(HomeItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
    private boolean toggleLayout(boolean isExpanded, View v, RelativeLayout layoutExpand) {
        Animations.toggleArrow(v, isExpanded);
        if (isExpanded) {
            Animations.expand(layoutExpand);
        } else {
            Animations.collapse(layoutExpand);
        }
        return isExpanded;
    }
    public void filterList(ArrayList<ProductData> arr){
        mList = arr;
        notifyDataSetChanged();
    }
}
