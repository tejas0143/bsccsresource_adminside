package com.my.csresourceadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.io.File;
import java.util.HashMap;

public class AddQP extends AppCompatActivity {   private CardView addPdf;
    private final int REQ =1;
    private Uri pdfData;
    private EditText pdfTitle;
    private Button uploadPdfButton;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    String downloadUrl ="" ;
    private ProgressDialog pd;
    private TextView pdfTextView;
    private String pdfName,title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_practical);

        addPdf = findViewById(R.id.addPdf);
        pdfTextView = findViewById(R.id.pdfTextView);
        pdfTitle= findViewById(R.id.pdfTitle);
        uploadPdfButton= findViewById(R.id.uploadPracticalButton);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pd= new ProgressDialog(this);

        uploadPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = pdfTitle.getText().toString();
                if (title.isEmpty()){
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();
                }else if (pdfData == null){
                    Toast.makeText(AddQP.this, "Upload Pdf First", Toast.LENGTH_SHORT).show();
                }else {
                    uploadPdf();
                }

            }
        });

        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

    }

    private void uploadPdf() {
        pd.setTitle("Please Wait .....");
        pd.setMessage("Uploading Pdf");
        pd.show();
        StorageReference reference = storageReference.child("QP/"+ pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddQP.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String valueOf) {
        String uniqueKey =databaseReference.child("QP").push().getKey();

        HashMap data = new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",valueOf);

        databaseReference.child("QP").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(AddQP.this, "pdf uploaded succesfully", Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddQP.this, "Failed to Upload Pdf", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("pdf/docs/ppt");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Pdf File"),REQ);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode == RESULT_OK){
            pdfData =data.getData();

            if (pdfData.toString().startsWith("content://")){
                Cursor cursor =null;
                try {
                    cursor = AddQP.this.getContentResolver().query(pdfData,null,null,null,null);

                    if (cursor != null && cursor.moveToFirst()) {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            } else if(pdfData.toString().startsWith("files//")){
                pdfName =new File(pdfData.toString()).getName();
            }
            pdfTextView.setText(pdfName);
        }
    }
}
