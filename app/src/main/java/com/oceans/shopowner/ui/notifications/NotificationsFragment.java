package com.oceans.shopowner.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;
import com.oceans.shopowner.LoginActivity;
import com.oceans.shopowner.R;
import com.oceans.shopowner.data.ShopData;
import com.oceans.shopowner.databinding.FragmentNotificationsBinding;
import com.oceans.shopowner.popups.LogoutPopup;

public class NotificationsFragment extends Fragment {
    FragmentNotificationsBinding binding;
    View root;
    SharedPreferences login;
    SharedPreferences.Editor editor;
    boolean status;
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        binding=FragmentNotificationsBinding.inflate(inflater,container,false);
        root=binding.getRoot();
        status=false;
        login = this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = login.edit();
        binding.pusernameEt.setEnabled(false);
        binding.paddressEt.setEnabled(false);
        binding.pnumberEt.setEnabled(false);
        binding.psnameEt.setEnabled(false);
        binding.ptypeSp.setEnabled(false);
        binding.plocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        binding.profileCIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
     /*   binding.peditbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status=true;
                binding.peditbt.setVisibility(View.GONE);
                binding.pdonebt.setVisibility(View.VISIBLE);
                binding.pusernameEt.setEnabled(true);
                binding.paddressEt.setEnabled(true);
                binding.pnumberEt.setEnabled(true);
                binding.psnameEt.setEnabled(true);
                binding.ptypeSp.setEnabled(true);
                binding.pprog.setVisibility(View.GONE);

            }
        });
        binding.pdonebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status=false;
                binding.peditbt.setVisibility(View.VISIBLE);
                binding.pdonebt.setVisibility(View.GONE);
                binding.pusernameEt.setEnabled(false);
                binding.paddressEt.setEnabled(false);
                binding.pnumberEt.setEnabled(false);
                binding.psnameEt.setEnabled(false);
                binding.ptypeSp.setEnabled(false);
                binding.pprog.setVisibility(View.VISIBLE);
            }
        });*/
        binding.Logoutbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutPopup logoutPopup = new LogoutPopup();
                logoutPopup.show(getActivity().getSupportFragmentManager(),"logout");
            }
        });
        binding.progressAnim.setVisibility(View.VISIBLE);
        try {
            getData();
        }catch (Exception e){
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            binding.progressAnim.setVisibility(View.INVISIBLE);
        }
        listener();
        return root;
    }

    public void getData() throws Exception{
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SHOPKEEPERS");

        databaseReference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ShopData shopData = dataSnapshot.getValue(ShopData.class);
                try {
                    binding.pusernameEt.setText(shopData.getEmail());
                    binding.pnumberEt.setText(shopData.getNumber());
                    binding.psnameEt.setText(shopData.getName());
                    binding.paddressEt.setText(shopData.getAddress());
                    binding.shopkanaam.setText(shopData.getName());
                    Glide.with(getActivity())
                            .load(shopData.getImage())
                            .centerCrop()
                            .into(binding.profileCIV);
                    binding.progressAnim.setVisibility(View.INVISIBLE);
                }catch (Exception e){ }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void listener(){
        binding.aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(NotificationsFragment.this).navigate(R.id.action_navigation_notifications_to_nav_help);
            }
        });
    }
}