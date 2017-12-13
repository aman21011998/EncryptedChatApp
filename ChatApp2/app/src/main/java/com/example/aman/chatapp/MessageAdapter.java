package com.example.aman.chatapp;

import android.app.ListActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aman on 8/10/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
     private List<Messages> mMessageList;

    public MessageAdapter(List<Messages> mMessageList)
    {
        this.mMessageList=mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position)
    {
        Messages c=mMessageList.get(position);
        holder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
            public TextView messageText;
        public MessageViewHolder(View itemView)
        {
            super(itemView);

            messageText=(TextView)itemView.findViewById(R.id.textView15);

        }
    }
}
