package com.my.csresourceadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NoticeActivity extends AppCompatActivity {
    private CardView addimage;
    private final int REQ =1;
    private Bitmap bitmap;
    private EditText noticeTitle;
    private ImageView imageView;
    private Button uploadNoticeButton;
    private DatabaseReference refrence;
    private StorageReference storageReference;
    String downloadUrl ="" ;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        addimage = findViewById(R.id.addNotice);
        imageView= findViewById(R.id.NoticeImageview);
        noticeTitle= findViewById(R.id.noticeTitle);
        uploadNoticeButton= findViewById(R.id.uploadNoticeButton);

        refrence = FirebaseDatabase.getInstance().getReference();
        storageReference =FirebaseStorage.getInstance().getReference();

        pd= new ProgressDialog(this);

        uploadNoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticeTitle.getText().toString().isEmpty()){
                    noticeTitle.setError("Empty");
                    noticeTitle.requestFocus();
                }
                else if(bitmap == null){
                    uploadData();
                }
                else{
                    uploadImage();
                }
            }
        });

        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
    }
    public void uploadData(){

        refrence= refrence.child("Notice");
        final  String uniqueKey =refrence.push().getKey();

        String title = noticeTitle.getText().toString();

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh-mm");
        String time = currentTime.format(calForTime.getTime());

        NoticeData noticeData= new NoticeData(title,downloadUrl,date,time,uniqueKey);

        refrence.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(NoticeActivity.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(NoticeActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

            }
        });


    }
    public void uploadImage(){
        pd.setMessage("uploading......");
        pd.show();
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
        byte[] finalimage =baos.toByteArray();
        final StorageReference filepath;
        filepath=storageReference.child("Notice").child(finalimage+"jpg");
        final UploadTask uploadTask= filepath.putBytes(finalimage);
        uploadTask.addOnCompleteListener(NoticeActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    uploadData();

                                }
                            });
                        }
                    });
                }else {
                    pd.dismiss();
                  Toast.makeText(NoticeActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void openGallery(){
        Intent pickimage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickimage,REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);

        }
    }
}