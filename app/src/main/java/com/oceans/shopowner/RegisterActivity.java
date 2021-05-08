package com.oceans.shopowner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oceans.shopowner.GPS.GpsUtils;
import com.oceans.shopowner.data.ShopData;
import com.oceans.shopowner.databinding.ActivityRegisterBinding;
import com.oceans.shopowner.popups.RegisterSucessPopup;
import com.oceans.shopowner.security.EncryptionUtils;

import java.io.ByteArrayOutputStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements Dialog_Get_ImageActivity.onPhotoSelectedListener, Dialog_Get_ImageActivity.MyDialogCloseListener{

    private static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    private String[] permissions= new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int REQUEST_LOCATION=101;
    Uri profileuri;
    Bitmap profilebitmap;
    Uri imageuri = null;
    double[] loc = {0.0,0.0};
    Bitmap imagebitmap = null;
    ActivityRegisterBinding binding;
    Dialog_Get_ImageActivity dgi;
    String email,number,shop_name,address,password,confirm,cipher,code;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    boolean gps = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SHOPKEEPERS");
        storageReference = FirebaseStorage.getInstance().getReference().child("SHOPS");
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerShop(v);
                }catch (Exception e){
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                //startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });
        binding.shopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle ags=new Bundle();
                ags.putInt("curr",1);
                dgi = new Dialog_Get_ImageActivity();
                dgi.setArguments(ags);
                dgi.show(getSupportFragmentManager(),"Dialog_select_Image");
                Log.e("Image Adder","----"+1);
            }
        });
        new GpsUtils(RegisterActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                gps = isGPSEnable;
            }
        });
        checkPermissions();
        listeners();
        setContentView(view);
    }

    @Override
    public void getImagePath(Uri imagePath) {
        imageuri = imagePath;
        imagebitmap = null;
        profileuri = imageuri;
        profilebitmap = null;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        imagebitmap = bitmap;
        imageuri = null;
        profilebitmap = imagebitmap;
        profileuri = null;
    }

    @Override
    public void handleDialogClose(int num) {
        if(imagebitmap != null){
            binding.shopImage.setImageBitmap(imagebitmap);
        }else if(imageuri != null){
            binding.shopImage.setImageURI(imageuri);
        }
    }

    public void registerShop(View view) throws Exception{

        Log.i("Terms", String.valueOf(binding.terms.isChecked()));

        email = binding.usernameEt.getText().toString().trim();
        number = binding.numberEt.getText().toString().trim();
        shop_name = binding.snameEt.getText().toString().trim();
        address = binding.addressEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        confirm = binding.confirmEt.getText().toString().trim();
        code = binding.codeEt.getText().toString().trim();

        if(number.isEmpty() || number.length() < 10){
            binding.numberEt.setError("Invalid Number");
            return;
        }else if(shop_name.isEmpty()){
            binding.snameEt.setError("Cannot be empty");
            return;
        }else if(address.isEmpty()){
            binding.addressEt.setError("Cannot be empty");
            return;
        }else if(password.isEmpty()){
            binding.passwordEt.setError("Cannot be empty");
            return;
        }else if(password.length() < 6){
            binding.passwordEt.setError("Should be greater than 6 characters!");
            return;
        }else if(!confirm.equals(password)){
            binding.confirmEt.setError("Does not match!");
            return;
        }else if(!isEmailValid(email)){
            binding.usernameEt.setError("Invalid format");
            return;
        }else if(profilebitmap == null && profileuri == null){
            Toast.makeText(this, "Please Select Image of your Shop", Toast.LENGTH_SHORT).show();
            return;
        }else if(loc[0] == 0.0 || loc[1] == 0.0){
            Toast.makeText(this, "Please add your Shop's location", Toast.LENGTH_SHORT).show();
            return;
        }else if(code == null || code.isEmpty()){
            code = "0000";
        }else if(!binding.terms.isChecked()){
            Toast.makeText(this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }
        cipher = password;
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(profilebitmap != null){
                            uploadBitmap(profilebitmap);
                        }else if(profileuri != null){
                            uploadFile(profileuri);
                        }
                        RegisterSucessPopup registerSucessPopup = new RegisterSucessPopup();
                        registerSucessPopup.show(getSupportFragmentManager(),"success");
                    }
                });
    }
    public void uploadFile(Uri uri){
        final String time = generatePIN()+String.valueOf(System.currentTimeMillis());
        final String ext = getFileExtension(uri);
        StorageReference storageReference1 = storageReference.child(time+"."+ext);
        UploadTask uploadTask = storageReference1.putFile(uri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(time+"."+ext).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //url = task.getResult().toString();
                        ShopData shopData = new ShopData();
                        shopData.setImage(task.getResult().toString());
                        shopData.setEmail(email);
                        shopData.setNumber(number);
                        shopData.setName(shop_name);
                        shopData.setAddress(address);
                        shopData.setPassword(cipher);
                        shopData.setLatitude(loc[0]);
                        shopData.setLongitude(loc[1]);
                        shopData.setReferenceCode(code);
                        shopData.setMacAddress(getWifiMacAddress());
                        shopData.setAreaCode("0000");
                        databaseReference.child(firebaseAuth.getUid()).setValue(shopData);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double p = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                //progressBar.setProgress((int)p);
            }
        });
    }
    public void uploadBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] bytes = baos.toByteArray();
        final String time = generatePIN()+String.valueOf(System.currentTimeMillis());
        final String ext = "jpeg";
        StorageReference storageReference1 = storageReference.child(time+"."+ext);
        UploadTask uploadTask = storageReference1.putBytes(bytes);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(time+"."+ext).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        //url = task.getResult().toString();
                        ShopData shopData = new ShopData();
                        shopData.setImage(task.getResult().toString());
                        shopData.setEmail(email);
                        shopData.setNumber(number);
                        shopData.setName(shop_name);
                        shopData.setAddress(address);
                        shopData.setPassword(cipher);
                        shopData.setLatitude(loc[0]);
                        shopData.setLongitude(loc[1]);
                        shopData.setReferenceCode(code);
                        shopData.setMacAddress(getWifiMacAddress());
                        shopData.setAreaCode("0000");
                        databaseReference.child(firebaseAuth.getUid()).setValue(shopData);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double p = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                //progressBar.setProgress((int)p);
            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeInfo = MimeTypeMap.getSingleton();
        return  mimeTypeInfo.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void openMap(View view){
        // open activity to add result
        startActivityForResult(new Intent(RegisterActivity.this,AddLocationActivity.class),31);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 31 && resultCode == 31){
            loc = data.getDoubleArrayExtra("location");
            String text = loc[0]+","+loc[1];
            binding.location.setText(text);
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public String generatePIN() {

        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;

        Log.i("Random",String.valueOf(randomPIN));
        return String.valueOf(randomPIN);
    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
                /*Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);*/
                if(!gps){
                    new GpsUtils(RegisterActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
                        @Override
                        public void gpsStatus(boolean isGPSEnable) {
                            gps = isGPSEnable;
                        }
                    });
                }
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)){
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac==null){
                    return "";
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length()>0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public void listeners(){
        binding.read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("https://krayan.in/krayansehopkeeper_terms&conditions/"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}