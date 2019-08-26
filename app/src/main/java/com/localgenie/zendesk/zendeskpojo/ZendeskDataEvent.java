package com.localgenie.zendesk.zendeskpojo;

import java.io.Serializable;

/**
 * <h>ZendeskDataEvent</h>
 * Created by Ali on 1/2/2018.
 */

public class ZendeskDataEvent implements Serializable
{
    /*"id":143618629272,
    "author_id":116770160311,
    "body":"i got an error",
    "timeStamp":1514550475,
    "created_at":"2017-12-29T12:27:55Z",
    "name":"akbar"*/
    private String id,author_id,body,name;
    private long timeStamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
