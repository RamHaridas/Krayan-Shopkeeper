package com.oceans.shopowner.popups;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oceans.shopowner.LoginActivity;
import com.oceans.shopowner.R;

import java.util.HashMap;
import java.util.Map;

public class DeliveryPopup extends DialogFragment {
    Button button;
    TextView textView;
    View view;
    String name;
    Fragment fragment;
    DatabaseReference databaseReference;
    public DeliveryPopup(String name,Fragment fragment){
        this.name = name;
        this.fragment = fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.getdriverpopup, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORDERS");
        textView = view.findViewById(R.id.question);
        button = view.findViewById(R.id.delete);
        Dialog dialog = getDialog();
        //Bundle args = getArguments();
        //String text = args.getString("token","");
        //textView.setText(text);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name == null || name.isEmpty()){
                    Toast.makeText(getContext(), "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String,Object> map = new HashMap<>();
                map.put("driver",true);
                databaseReference.child(name).updateChildren(map);
                NavHostFragment.findNavController(fragment).popBackStack();
                getDialog().dismiss();
            }
        });
        return view;
    }
}
