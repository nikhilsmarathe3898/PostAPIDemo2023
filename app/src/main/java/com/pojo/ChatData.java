package com.pojo;

/**
 * <h>ChatData</h>
 * Created by Ali on 12/22/2017.
 */

public class ChatData
{

        private long bid;
        private long timestamp;
        private String content,fromID,targetId;
        private int type,custProType;


    public int getCustProType() {
        return custProType;
    }

    public void setCustProType(int custProType) {
        this.custProType = custProType;
    }

    public long getBid() {
            return bid;
        }

        public void setBid(long bid) {
            this.bid = bid;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFromID() {
            return fromID;
        }

        public void setFromID(String fromID) {
            this.fromID = fromID;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
}
