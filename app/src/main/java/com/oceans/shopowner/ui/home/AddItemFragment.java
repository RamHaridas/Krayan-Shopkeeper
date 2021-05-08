package com.oceans.shopowner.ui.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oceans.shopowner.Dialog_Get_ImageActivity;
import com.oceans.shopowner.Dialog_Get_ImageFragment;
import com.oceans.shopowner.R;
import com.oceans.shopowner.RegisterActivity;
import com.oceans.shopowner.data.ProductData;
import com.oceans.shopowner.data.ShopData;
import com.oceans.shopowner.databinding.FragmentAddItemBinding;
import com.oceans.shopowner.popups.UploadDonePopup;

import java.io.ByteArrayOutputStream;

public class AddItemFragment extends Fragment implements Dialog_Get_ImageFragment.onPhotoSelectedListener, Dialog_Get_ImageFragment.MyDialogCloseListener {
    FragmentAddItemBinding binding;
    NavController navCont;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,dref;
    StorageReference storageReference;
    String name,uri,quantity,price;
    Dialog_Get_ImageFragment dialog_get_imageActivity;
    Uri imageuri = null;
    private static final int REQUEST_CODE = 11;
    Bitmap imagebitmap = null;
    long count;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddItemBinding.inflate(inflater,container,false);
        firebaseAuth = FirebaseAuth.getInstance();
        count = 0;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SHOP_DATA");
        dref = FirebaseDatabase.getInstance().getReference().child("ALL_PRODUCTS");
        storageReference = FirebaseStorage.getInstance().getReference().child("PRODUCT_IMAGES");
        verifyPermissions();
        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadProduct();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                /*navCont= Navigation.findNavController(binding.getRoot());
                navCont.navigate(R.id.action_addItemFragment_to_navigation_home);*/
            }
        });
        binding.pimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle ags = new Bundle();
                ags.putInt("curr",1);
                dialog_get_imageActivity = new Dialog_Get_ImageFragment();
                dialog_get_imageActivity.setArguments(ags);
                dialog_get_imageActivity.setTargetFragment(AddItemFragment.this,1);
                dialog_get_imageActivity.show(getParentFragmentManager(),"Dialog_select_Image");
                Log.e("Image Adder","----"+1);
            }
        });

        return binding.getRoot();
    }

    public void uploadProduct() throws Exception{
        Log.i("count786", String.valueOf(getCount()));
        if(getCount() >= 100){
            Toast.makeText(getContext(), "Sorry, you reached your upload limit", Toast.LENGTH_SHORT).show();
            return;
        }
        name = binding.pnameEt.getText().toString().trim();
        quantity = binding.pquantityEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();

        if(name.isEmpty()){
            binding.pnameEt.setError("Cannot be empty");
            return;
        }else if(quantity.isEmpty() || quantity.equals("0")){
            binding.pquantityEt.setError("Invalid Quantity");
            return;
        }else if(price.isEmpty() || price.equals("0")){
            binding.priceEt.setError("Invalid Price");
            return;
        }else if (imageuri == null && imagebitmap == null){
            Toast.makeText(getContext(), "Please add Image first", Toast.LENGTH_SHORT).show();
            return;
        }
        if(imagebitmap != null){
            uploadBitmap(imagebitmap);
        }
        if(imageuri != null){
            uploadFile(imageuri);
        }
        UploadDonePopup uploadDonePopup = new UploadDonePopup();
        uploadDonePopup.show(getActivity().getSupportFragmentManager(),"upload");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navCont = Navigation.findNavController(binding.getRoot());
                navCont.navigate(R.id.action_addItemFragment_to_navigation_home);
            }
        },2500);

    }

    @Override
    public void getImagePath(Uri imagePath) {
        imageuri = imagePath;
        imagebitmap = null;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        imagebitmap = bitmap;
        imageuri = null;
    }

    @Override
    public void handleDialogClose(int num) {
        if(imagebitmap != null){
            binding.pimage.setImageBitmap(imagebitmap);
        }
        if(imageuri != null){
            binding.pimage.setImageURI(imageuri);
        }
    }
    void verifyPermissions(){
        String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        if((ContextCompat.checkSelfPermission(getContext(),permissions[0])== PackageManager.PERMISSION_GRANTED) &&  (ContextCompat.checkSelfPermission(getContext(),
                permissions[1])== PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(getContext(),
                permissions[2])== PackageManager.PERMISSION_GRANTED){


        }else{
            ActivityCompat.requestPermissions(getActivity(),permissions,REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // upload file currently disabled to save cloud space
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
                        String time = String.valueOf(System.currentTimeMillis());
                        ProductData productData = new ProductData();
                        productData.setImage(task.getResult().toString());
                        productData.setName(name.toLowerCase());
                        productData.setQuantity(quantity);
                        productData.setPrice(price);
                        productData.setStock(true);
                        productData.setShop_uid(firebaseAuth.getUid());
                        String name = firebaseAuth.getUid()+System.currentTimeMillis();
                        productData.setRef1(time);
                        productData.setRef2(name);
                        databaseReference.child(firebaseAuth.getUid()).child(time).setValue(productData);
                        dref.child(name).setValue(productData);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show();
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
                        String time = String.valueOf(System.currentTimeMillis());
                        ProductData productData = new ProductData();
                        productData.setImage(task.getResult().toString());
                        productData.setName(name.toLowerCase());
                        productData.setQuantity(quantity);
                        productData.setPrice(price);
                        productData.setStock(true);
                        productData.setShop_uid(firebaseAuth.getUid());
                        String name = firebaseAuth.getUid()+System.currentTimeMillis();
                        productData.setRef1(time);
                        productData.setRef2(name);
                        databaseReference.child(firebaseAuth.getUid()).child(time).setValue(productData);
                        dref.child(name).setValue(productData);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"ERROR IN UPLOAD: "+e.getMessage(),Toast.LENGTH_SHORT).show();
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
        ContentResolver contentResolver = this.getActivity().getContentResolver();
        MimeTypeMap mimeTypeInfo = MimeTypeMap.getSingleton();
        return  mimeTypeInfo.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public String generatePIN() {

        //generate a 4 digit integer 1000 <10000
        int randomPIN = (int)(Math.random()*9000)+1000;
        Log.i("Random",String.valueOf(randomPIN));
        return String.valueOf(randomPIN);
    }

    public long getCount() throws Exception{

        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.i("COUNT", dataSnapshot.getChildrenCount()+"");
                count = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return count;
    }
}