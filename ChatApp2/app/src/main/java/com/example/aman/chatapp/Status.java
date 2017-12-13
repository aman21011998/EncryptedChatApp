package com.example.aman.chatapp;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Status extends AppCompatActivity {

    TextInputLayout textInputLayout;
    Button bt;
    FirebaseAuth fa;
    DatabaseReference db;
    Toolbar mtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        textInputLayout=(TextInputLayout)findViewById(R.id.stts);
        bt=(Button)findViewById(R.id.button6);

        mtoolbar=(Toolbar)findViewById(R.id.statustoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().show();

        fa=FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference().child("User");

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status=textInputLayout.getEditText().getText().toString();

                db.child(fa.getCurrentUser().getUid()).child("status").setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        db.child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textInputLayout.getEditText().setText(dataSnapshot.child("status").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
