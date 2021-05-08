package com.oceans.shopowner.popups;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oceans.shopowner.R;
import com.oceans.shopowner.ui.home.HomeAdapter;

public class DeletePopup extends DialogFragment {
    View view;
    Button delete;
    DatabaseReference databaseReference,dbref;
    FirebaseAuth firebaseAuth;
    String ref1,ref2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.deleteproductpopup, container, false);
        delete = view.findViewById(R.id.delete_product);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SHOP_DATA").child(firebaseAuth.getUid());
        dbref = FirebaseDatabase.getInstance().getReference().child("ALL_PRODUCTS");
        Bundle args = getArguments();
        try {
            ref1 = args.getString("ref1");
            ref2 = args.getString("ref2");
        }catch (Exception e){
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
        //Log.i("ref1",args.getString("ref1"));
        //Log.i("ref2",args.getString("ref2"));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    databaseReference.child(ref1).removeValue();
                    dbref.child(ref2).removeValue();
                    Toast.makeText(getContext(), "Item deleted from Database", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
            }
        });
        return view;
    }

}
