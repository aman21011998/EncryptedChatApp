package com.example.aman.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    Toolbar mtoolbar;
    TextInputLayout username,password;
    Button signin;
    FirebaseAuth mauth;
    String uname,pass;
    DatabaseReference db;
    ProgressDialog pd;
    String device_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mtoolbar=(Toolbar) findViewById(R.id.logintoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().show();
        pd=new ProgressDialog(LoginActivity.this);
        mauth=FirebaseAuth.getInstance();
        username=(TextInputLayout)findViewById(R.id.textInputLayout);
        password=(TextInputLayout)findViewById(R.id.textInputLayout2);
        signin=(Button)findViewById(R.id.button);
        db= FirebaseDatabase.getInstance().getReference().child("User");
        db.keepSynced(true);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                uname=username.getEditText().getText().toString();
                pass=password.getEditText().getText().toString();

                pd.setTitle("Login");
                pd.setMessage("Please wait while we are signing in.......");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

            /*    db.child(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        String storedtoken=dataSnapshot.child("device_token").getValue().toString();
                        String status=dataSnapshot.child("Online").getValue().toString();
                        device_token=FirebaseInstanceId.getInstance().getToken().toString();
                        if(storedtoken.equals(device_token) && status.equals("false") || !storedtoken.equals(device_token) && status.equals("false"))
                        {
                            mauth.signInWithEmailAndPassword(uname,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        pd.dismiss();
                                        //device_token= FirebaseInstanceId.getInstance().getToken().toString();
                                        db.child(mauth.getCurrentUser().getUid()).child("Online").setValue("true");
                                        db.child(mauth.getCurrentUser().getUid()).child("device_token").setValue(device_token);
                                        Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_LONG).show();
                                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(),"Error sigining in",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                        else
                        {
                            Toast.makeText(getApplicationContext(),"This user is already logged in",Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });  */
                mauth.signInWithEmailAndPassword(uname,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            db.child(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {

                                    String storedtoken=dataSnapshot.child("device_token").getValue().toString();
                                    String status=dataSnapshot.child("Online").getValue().toString();
                                    device_token=FirebaseInstanceId.getInstance().getToken().toString();

                                    if(storedtoken.equals(device_token) && status.equals("false") || !storedtoken.equals(device_token) && status.equals("false"))
                                    {

                                        device_token= FirebaseInstanceId.getInstance().getToken().toString();
                                        db.child(mauth.getCurrentUser().getUid()).child("Online").setValue("true");
                                        db.child(mauth.getCurrentUser().getUid()).child("device_token").setValue(device_token);
                                        Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_LONG).show();
                                        pd.dismiss();
                                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                    /*else
                                    {
                                        Toast.makeText(getApplicationContext(),"This user is already logged in",Toast.LENGTH_LONG).show();
                                        pd.dismiss();
                                    }*/
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                        else
                        {
                            pd.dismiss();
                            mauth.getInstance().signOut();
                         Toast.makeText(getApplicationContext(),"Error sigining in",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
