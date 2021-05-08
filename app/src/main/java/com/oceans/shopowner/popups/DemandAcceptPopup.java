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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oceans.shopowner.LoginActivity;
import com.oceans.shopowner.R;
import com.oceans.shopowner.ui.demans.DemandFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandAcceptPopup extends DialogFragment {
    Button button;
    TextView textView;
    View view;
    String uid;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    List<String> list;
    public DemandAcceptPopup(String uid, List<String> list){
        this.uid = uid;
        this.list = list;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.demandacceptpopup, container, false);


        textView = view.findViewById(R.id.question);
        button = view.findViewById(R.id.delete);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("REQUIRED");
        Dialog dialog = getDialog();
        //Bundle args = getArguments();
        //String text = args.getString("token","");
        //textView.setText(text);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uid == null || uid.isEmpty()){
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(list == null){
                    list = new ArrayList<>();
                }
                list.add(firebaseAuth.getUid());
                Map<String,Object> map = new HashMap<>();
                map.put("shopkeepers",list);
                databaseReference.child(uid).updateChildren(map);
                Toast.makeText(getContext(), "Your response has been recorded successfully", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
        return view;
    }
}
