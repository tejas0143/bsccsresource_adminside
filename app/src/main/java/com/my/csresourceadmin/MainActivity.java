package com.my.csresourceadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView uploadNotice,addPractical,deleteNotice,addQp;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uploadNotice = findViewById(R.id.addNotice);
        uploadNotice.setOnClickListener(this);

        addPractical = findViewById(R.id.addPracticals);
        addPractical.setOnClickListener(this);

        addQp = findViewById(R.id.addQP);
        addQp.setOnClickListener(this);

        deleteNotice = findViewById(R.id.deleteNotice);
        deleteNotice.setOnClickListener(this);

        mfirebaseAuth =FirebaseAuth.getInstance();
        if (mfirebaseAuth.getCurrentUser() == null){
            Intent intent = new Intent(this,LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addNotice:
                Intent intent = new Intent(MainActivity.this , NoticeActivity.class);
                startActivity(intent);
                break;
            case R.id.deleteNotice:
                Intent intent1 = new Intent(MainActivity.this, DeleteNotice.class);
                startActivity(intent1);
                break;
            case R.id.addPracticals:
                Intent intent2 = new Intent(MainActivity.this , AddPractical.class);
                startActivity(intent2);
                break;
            case R.id.addQP:
                Intent intent3 = new Intent(MainActivity.this , AddQP.class);
                startActivity(intent3);
                break;
        }

    }
    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
    }
    public void logoutMethod(View view){
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startLoginActivity();
                }else {
                    Log.e(TAG, "onComplete: ",task.getException() );
                    
                }



            }
        });


    }


}