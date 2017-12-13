package com.example.aman.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity {
Toolbar mtoolbar;
    TextInputLayout name,username,password;
    String nm,uname,pass;
    Button register;
    ProgressDialog pd;
    FirebaseAuth mauth;
    DatabaseReference userdatabase;
    String id,devicetoken;
    SecretKey secretKey;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mtoolbar=(Toolbar)findViewById(R.id.registertoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();
        mauth=FirebaseAuth.getInstance();
        userdatabase= FirebaseDatabase.getInstance().getReference();
        name=(TextInputLayout)findViewById(R.id.textInputLayout6);
        username=(TextInputLayout)findViewById(R.id.textInputLayout7);
        password=(TextInputLayout)findViewById(R.id.textInputLayout8);

        pd=new ProgressDialog(RegisterActivity.this);

        register=(Button)findViewById(R.id.button2);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                nm=name.getEditText().getText().toString();
                uname=username.getEditText().getText().toString();
                pass=password.getEditText().getText().toString();

                if(nm.isEmpty() || uname.isEmpty() || pass.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please fill all entries",Toast.LENGTH_LONG).show();
                }
                else {
                    pd.setTitle("Creating Account");
                    pd.setMessage("Please wait while we are registering you.....");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                    registration(nm, uname, pass);
                }
            }
        });

    }

    private void registration(final String name, final String uname, String pass)
    {
        mauth.createUserWithEmailAndPassword(uname,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                   /* try {
                         secretKey=AESEncryption.getSecretEncryptionKey();
                        key=secretKey.toString();
                        //key = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    id=mauth.getCurrentUser().getUid();
                    devicetoken= FirebaseInstanceId.getInstance().getToken();
                    HashMap<String,String> map=new HashMap<String, String>();
                   // map.put("key",key);
                    map.put("name",name);
                    map.put("email",uname);
                    map.put("device_token",devicetoken);
                    map.put("Online","true");
                    map.put("image","default");
                    map.put("status","Available");
                    userdatabase.child("User").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_LONG).show();
                                Intent move=new Intent(RegisterActivity.this, MainActivity.class);
                                move.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(move);
                                finish();
                            }
                        }
                    });
                }

                else
                {
                    Toast.makeText(getApplicationContext(),"Error in creating account",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }
        });
    }
}
