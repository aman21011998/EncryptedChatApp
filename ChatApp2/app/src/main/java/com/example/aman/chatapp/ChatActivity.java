package com.example.aman.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ChatActivity extends AppCompatActivity {
    Toolbar mtoolbar;
    ActionBar actionBar;
    String uid;
    DatabaseReference db,chatdatabase,getkey;
    FirebaseAuth mauth;
    TextView uname,lastseen;
    ImageButton dbtn;
    ImageButton add,send;
    EditText mssg;
    ImageView uimage;
    RecyclerView  mMessagesList;
    private final List<Messages> messagesList=new ArrayList<>();
    LinearLayoutManager mLinearLayout;
    MessageAdapter mAdapter;
    public SecretKey secKey;
    String messagestatus;
    String st,key;

    private static final int pick=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mtoolbar=(Toolbar)findViewById(R.id.chattoolbar);
        setSupportActionBar(mtoolbar);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();
        add=(ImageButton)findViewById(R.id.add);
        send=(ImageButton)findViewById(R.id.send);
        mssg=(EditText)findViewById(R.id.editmessage);
        View mview=getLayoutInflater().inflate(R.layout.chat_bar,null);
        mauth=FirebaseAuth.getInstance();
        actionBar.setCustomView(mview);
        mMessagesList=(RecyclerView)findViewById(R.id.message_list);
        mLinearLayout=new LinearLayoutManager(this);
        mAdapter=new MessageAdapter(messagesList);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);
        uname=(TextView)mview.findViewById(R.id.textView13);
        lastseen=(TextView)mview.findViewById(R.id.textView14);
        uimage=(ImageView)findViewById(R.id.imageView4);
        dbtn=(ImageButton)findViewById(R.id.decryptbutton);
        db= FirebaseDatabase.getInstance().getReference().child("User");
        chatdatabase=FirebaseDatabase.getInstance().getReference();
        uid=getIntent().getStringExtra("id");
        messagestatus=getIntent().getStringExtra("message");
        getkey=FirebaseDatabase.getInstance().getReference().child("key");

        getkey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                key=dataSnapshot.getValue().toString();
              //  Toast.makeText(ChatActivity.this,"Before= "+key, Toast.LENGTH_SHORT).show();


                byte[] encodedKey= Base64.decode(key, Base64.DEFAULT);

                secKey= new SecretKeySpec(encodedKey, 0,encodedKey.length, "AES");
               // Toast.makeText(ChatActivity.this,"After= "+secKey, Toast.LENGTH_SHORT).show();

                //System.out.print("sec key= "+secKey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*db.child(mauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("key"))
                {
                    key=dataSnapshot.child("key").getValue().toString();

                    byte[] encodedKey= Base64.decode(key, Base64.DEFAULT);
                        secKey= new SecretKeySpec(encodedKey, 0,encodedKey.length, "AES");

                }
                else
                {
                    secKey= null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });  */
        if(messagestatus.equals("success")) //if fingerprint authentication is successful than decrypt the messages
        {
            //Toast.makeText(getApplicationContext(),"You made it",Toast.LENGTH_SHORT).show();

            chatdatabase.child("messages").child(mauth.getCurrentUser().getUid()).child(uid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    Messages message1=dataSnapshot.getValue(Messages.class);


                    int len = message1.getMessage().length();
                    byte data[]= new byte[0];
                  /*  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        data = message1.getMessage().getBytes(StandardCharsets.UTF_8); //converting message to byte array
                    }*/
                    //byte[] data = new byte[len / 2];
                  /*  for (int i = 0; i < data.length; i++)
                    {

                        data[i] = (byte) Integer.parseInt(message1.getMessage().substring(2 * i, 2 * i + 2),16);
                        /Toast.makeText(ChatActivity.this,String.valueOf(data[i]), Toast.LENGTH_LONG).show();
                        data[i] = (byte) Integer.parseInt(message1.getMessage().substring(2 * i, 2 * i + 2), 16);


                    }*/




                       // Toast.makeText(getApplicationContext(), "Sec key= "+secKey,Toast.LENGTH_LONG).show();
                    String decrypttext= null;
                    try {

                        decrypttext = AESEncryption.decryptText(message1.getMessage(),secKey);
                        Toast.makeText(getApplicationContext(),decrypttext.toString(),Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }





                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        loadMessages();
        db.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uname.setText(dataSnapshot.child("name").getValue().toString());
                String status=dataSnapshot.child("Online").getValue().toString();
                String path=dataSnapshot.child("image").getValue().toString();

                Picasso.with(ChatActivity.this).load(path).into(uimage);
                if(status.equals("true"))
                {
                    lastseen.setText("online");
                }
                else
                {
                    String ls = dataSnapshot.child("lastseen").getValue().toString();
                    GetTimeAgo gto=new GetTimeAgo();
                    long lstseen=Long.parseLong(ls);

                    String lasttime=gto.getTimeAgo(lstseen,getApplicationContext());

                    lastseen.setText(lasttime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatdatabase.child("chat").child(mauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                  if(!dataSnapshot.hasChild(uid))
                  {
                      Map map=new HashMap();
                      map.put("seen","false");
                      map.put("timestamp", ServerValue.TIMESTAMP);

                      Map mchatusermap=new HashMap();

                      mchatusermap.put("chat/"+mauth.getCurrentUser().getUid()+"/"+uid,map);
                      mchatusermap.put("chat/"+uid+"/"+mauth.getCurrentUser().getUid(),map);

                      chatdatabase.updateChildren(mchatusermap, new DatabaseReference.CompletionListener() {
                          @Override
                          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                              if(databaseError!=null)
                              {
                                  Log.d(String.valueOf(databaseError),"Error occured");
                              }
                          }
                      });
                  }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


          send.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  try {
                      sendmessage();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
          });

        dbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i=new Intent(ChatActivity.this,Fingerprint.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("id",uid);
                startActivity(i);

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent=new Intent();
                galleryintent.setType("image/+");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintent,"Choose photo"),pick);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
          if(requestCode==pick && resultCode==RESULT_OK)
          {
              Uri uri=data.getData();

          }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadMessages()
    {
        chatdatabase.child("messages").child(mauth.getCurrentUser().getUid()).child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message=dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendmessage() throws Exception
    {
        String message=mssg.getText().toString();
        Toast.makeText(getApplicationContext(),"Key= "+secKey ,Toast.LENGTH_LONG).show();
        String cipherText = AESEncryption.encryptText(message,secKey);
            //byte[] cipherText = AESEncryption.encryptText(message,secKey);

      /* char[] temp=cipherText.toCharArray();
        temp[0]='/';
        temp[2]='+';
        cipherText= String.valueOf(temp);*/
        Toast.makeText(getApplicationContext(),"Cipher text= "+cipherText,Toast.LENGTH_SHORT).show();
        //org.apache.commons.codec.binary.Hex.decodeHex(cipherText.toString().toCharArray());
            // st= Base64.decodeBase64(cipherText);

       /* for (byte b :cipherText)
        {
            st = String.format("%09X", b&0xff);
        }*/

     /*  for (byte b : cipherText)
       {
            st=Integer.toHexString((int) (b&0xff));
        }*/

       // Toast.makeText(getApplicationContext(),st.toString(),Toast.LENGTH_SHORT).show();



        mssg.setText("");



        if(!TextUtils.isEmpty(message))
        {
            String curentuserref="messages/"+mauth.getCurrentUser().getUid()+"/"+uid;
            String chatuserref="messages/"+uid+"/"+mauth.getCurrentUser().getUid();

            DatabaseReference pushdb=FirebaseDatabase.getInstance().getReference().child("messages").child(mauth.getCurrentUser().getUid()).child(uid).push();
            String pushkey=pushdb.getKey();

            Map m=new HashMap();
            m.put("message",cipherText);  //encrypted text is stored in firebase
            m.put("seen","false");
            m.put("time",ServerValue.TIMESTAMP);
            m.put("type","text");

            Map pushmessage=new HashMap();

            pushmessage.put(curentuserref+"/"+pushkey,m);
            pushmessage.put(chatuserref+"/"+pushkey,m);

            chatdatabase.updateChildren(pushmessage, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    //Log.d("CHAT_LOG",databaseError.getMessage().toString());
                  //  Toast.makeText(ChatActivity.this, "Inside", Toast.LENGTH_SHORT).show();

                }
            });

        }

        else
        {
            Toast.makeText(getApplicationContext(),"Can't send an empty message",Toast.LENGTH_LONG).show();
        }



    }



}
