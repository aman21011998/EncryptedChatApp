package com.example.aman.chatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    Toolbar tb;
    FirebaseAuth auth;
    DatabaseReference db;
    FirebaseUser currentuser;
    private RecyclerView r;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb=(Toolbar)findViewById(R.id.maintoolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("ChatApp");
        getSupportActionBar().show();
        db= FirebaseDatabase.getInstance().getReference().child("User");
        r=(RecyclerView)findViewById(R.id.rv);
        r.setHasFixedSize(true);
        r.setLayoutManager(new LinearLayoutManager(this));
        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null)
        {
            sendtostart();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.logout)
        {
            currentuser=auth.getCurrentUser();

            if(currentuser!=null)
            {
                db.child(currentuser.getUid()).child("Online").setValue("false");
                db.child(currentuser.getUid()).child("lastseen").setValue(ServerValue.TIMESTAMP);
                auth.getInstance().signOut();
                sendtostart();
            }
        }

        if(item.getItemId()==R.id.accounts)
        {
            Intent move=new Intent(MainActivity.this,AccountSettings.class);
            startActivity(move);
        }
        if(item.getItemId()==R.id.deactivate)
        {
            id=auth.getCurrentUser().getUid();
           auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {

                        db.child(id).child("Online").setValue("false");
                        db.child(id).child("lastseen").setValue(ServerValue.TIMESTAMP);
                       db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                dataSnapshot.getRef().removeValue();
                                Toast.makeText(getApplicationContext(),"Account deleted successfully",Toast.LENGTH_LONG).show();
                                Intent m=new Intent(MainActivity.this,StartActivity.class);
                                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(m);
                       }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
               }
           });
        }
        return true;

    }

    private void sendtostart()
    {
        Intent start=new Intent(MainActivity.this,StartActivity.class);
        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(start);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Users,Userview> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, Userview>(Users.class,R.layout.user_layout,Userview.class,db) {
            @Override
            protected void populateViewHolder(Userview viewHolder, Users model, int position)
            {
                  viewHolder.setUname(model.getName());
                viewHolder.setImage(model.getImage(),getApplicationContext());
                viewHolder.setStatus(model.getStatus());
                final String uid=getRef(position).getKey();
               viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       Intent i=new Intent(MainActivity.this,ChatActivity.class);
                       i.putExtra("message","nil");
                       i.putExtra("id",uid);
                       startActivity(i);
                   }
               });
            }
        };

        r.setAdapter(firebaseRecyclerAdapter);
    }

    public static class Userview extends RecyclerView.ViewHolder
    {
        View mview;
        public Userview(View itemView) {
            super(itemView);
            mview=itemView;
        }

        public void setUname(String name)
        {
            TextView nm=(TextView)mview.findViewById(R.id.textView11);
            nm.setText(name);
        }

        public void setImage(String image, Context ct)
        {
            ImageView im=(ImageView)mview.findViewById(R.id.imageView3);

                Picasso.with(ct).load(image).placeholder(R.drawable.person).into(im);

        }

        public void setStatus(String status)
        {
            TextView st=(TextView)mview.findViewById(R.id.textView12);
            st.setText(status);
        }
    }
}
