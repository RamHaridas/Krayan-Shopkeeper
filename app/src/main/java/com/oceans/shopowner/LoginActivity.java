package com.oceans.shopowner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oceans.shopowner.data.ShopData;
import com.oceans.shopowner.databinding.ActivityLoginBinding;
import com.oceans.shopowner.popups.LoginSucessPopup;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    SharedPreferences login;
    DatabaseReference databaseReference;
    SharedPreferences.Editor login_editor;
    boolean isUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        isUser = false;
        View view = binding.getRoot();
        login = getSharedPreferences("login",MODE_PRIVATE);
        login_editor = login.edit();
        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        binding.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                try {
                    loginUserToApp(v);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        textListener();
        setContentView(view);
    }

    public void loginUserToApp(View view) throws Exception{

        String email = binding.usernameEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();
        if(email.isEmpty()){
            binding.usernameEt.setError("Cannot be empty");
            return;
        }else if(password.isEmpty()){
            binding.passwordEt.setError("Cannot be empty");
            return;
        }else if(!isUser()){
            binding.usernameEt.setError("Invalid User");
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    login_editor.putBoolean("login",true);
                    login_editor.apply();
                    LoginSucessPopup loginSucessPopup = new LoginSucessPopup();
                    loginSucessPopup.show(getSupportFragmentManager(),"login");
                }else{
                    Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void checkUser(String email){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SHOPKEEPERS");
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ShopData userData = dataSnapshot.getValue(ShopData.class);
                if(userData == null){
                    setUser(false);
                }else{
                    setUser(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Log.i("USER", String.valueOf(isUser));
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public boolean isUser() {
        return isUser;
    }
    public void textListener(){
        binding.usernameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkUser(s.toString());
            }
        });
    }
    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}