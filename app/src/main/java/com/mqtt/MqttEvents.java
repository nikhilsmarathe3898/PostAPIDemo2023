package com.mqtt;

/**
 * <h>MqttEvents</h>
 * Created by ALi on 27/09/17.
 */


public enum MqttEvents {


    Provider("provider"),
    JobStatus("jobStatus"),
    LiveTrack("liveTrack"),
    Message("message"),
    Calls("Calls"),
    CallsAvailability("CallsAvailability"),
    PresenceTopic("PresenceTopic"),
    WillTopic("lastWill"),
    Call("call");
    public String value;

    MqttEvents(String value) {
        this.value = value;
    }


}