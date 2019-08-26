package com.localgenie.zendesk.zendeskpojo;

import java.io.Serializable;

/**
 * <h>TicketOpen</h>
 * Created by Ali on 12/29/2017.
 */

public class TicketOpen implements Serializable
{

    /*"id":27,
"status":"open",
"timeStamp":1514550475,
"subject":"errorGot",
"type":"problem",
"priority":"high",
"description":"i got an error "*/
    private int id;
    private long timeStamp;
    private String status,subject,type,priority,description;

    public int getId() {
        return id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public String getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }

    public String getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }
}
