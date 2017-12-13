package com.example.aman.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class AccountSettings extends AppCompatActivity {

    ImageView img;
    TextView name,status;
    Button changeimage,changestatus;
    FirebaseAuth firebaseAuth;
    DatabaseReference db,d;
    ProgressDialog pd;
    StorageReference pp;
    String cuid;
    private static final int pick=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        img=(ImageView)findViewById(R.id.imageView2);
        name=(TextView)findViewById(R.id.textView7);
        status=(TextView)findViewById(R.id.textView8);
        changeimage=(Button)findViewById(R.id.button3);
        changestatus=(Button)findViewById(R.id.button4);
        firebaseAuth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        pp= FirebaseStorage.getInstance().getReference();
        d=FirebaseDatabase.getInstance().getReference().child("User");
        d.keepSynced(true);
        db= FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getCurrentUser().getUid());

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                     name.setText(dataSnapshot.child("name").getValue().toString().toUpperCase());
                     status.setText(dataSnapshot.child("status").getValue().toString().toUpperCase());
                     String image=dataSnapshot.child("image").getValue().toString();

                        Picasso.with(AccountSettings.this).load(image).into(img);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent st=new Intent(AccountSettings.this,Status.class);
                startActivity(st);

            }
        });
        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/+");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select photo"),pick);

            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==pick && resultCode==RESULT_OK)
        {
            Uri uri=data.getData();
            CropImage.activity(uri).setAspectRatio(1,1).start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);//getting cropped image
            if(resultCode==RESULT_OK)
            {
                cuid=firebaseAuth.getCurrentUser().getUid();
                pd.setTitle("Uploading Image");
                pd.setMessage("Please wait....");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                final Uri finaluri=result.getUri();
                pp.child("Profile_pictures").child(cuid+".jpg").putFile(finaluri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                        {

                            db.child("image").setValue(finaluri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Picasso.with(AccountSettings.this).load(finaluri).into(img);
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"Successfully uploaded the image",Toast.LENGTH_LONG) .show();
                            }

                            }
                        });

                        }
                    }
                });


            }
        }
        else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
        {

            Toast.makeText(getApplicationContext(),"Error occured while uploading",Toast.LENGTH_LONG).show();
        }


    }
}
