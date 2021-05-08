package com.oceans.shopowner.ui.demans;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oceans.shopowner.R;
import com.oceans.shopowner.data.QueryData;

import java.util.ArrayList;
import java.util.List;


public class DemandFragment extends Fragment {

    DemandAdapter demandAdapter;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    View view;
    List<QueryData> queryDataList;
    boolean first;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_demand, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("REQUIRED");
        recyclerView = view.findViewById(R.id.recycler);
        queryDataList = new ArrayList<>();
        first = false;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DemandThread demandThread = new DemandThread(getContext());
        new Thread(demandThread).start();
        return view;
    }

    class DemandThread implements Runnable{
        Context context;
        public DemandThread(Context context){
            this.context = context;
        }
        @Override
        public void run() {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    queryDataList.clear();
                    for(DataSnapshot post: dataSnapshot.getChildren()){
                        QueryData queryData = post.getValue(QueryData.class);
                        queryDataList.add(queryData);
                        Log.i("damn", queryData.getDescription());
                        if(first){
                            demandAdapter.notifyDataSetChanged();
                        }else{
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    demandAdapter = new DemandAdapter(context,queryDataList);
                                    recyclerView.setAdapter(demandAdapter);
                                    recyclerView.scheduleLayoutAnimation();
                                    first = true;
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}