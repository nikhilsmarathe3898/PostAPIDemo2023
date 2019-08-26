package com.localgenie.zendesk.zendeskpojo;

import java.io.Serializable;

/**
 * <h>TicketClose</h>
 * Created by Ali on 12/29/2017.
 */

public class TicketClose implements Serializable
{

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
