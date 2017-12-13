package com.example.aman.chatapp;

/**
 * Created by aman on 8/10/17.
 */

public class Messages
{
   String message;
    String seen;
    Long time;
    String type;
    public Messages() {
    }

    public Messages(String message, String seen, Long time, String type) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }



    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }


    public Long getTime() {

        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
