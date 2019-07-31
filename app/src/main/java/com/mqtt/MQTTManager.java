package com.mqtt;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.localgenie.utilities.Constants;
import com.localgenie.utilities.SessionManagerImpl;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import javax.inject.Inject;

import com.localgenie.videocalling.RxCallInfo;
import com.localgenie.videocalling.UtilityVideoCall;
import com.pojo.ChatDataObervable;
import com.pojo.ChatReponce;
import com.pojo.LiveTackPojo;
import com.pojo.LiveTrackObservable;
import com.pojo.MyBookingObservable;
import com.pojo.MyBookingStatus;
import com.pojo.ProviderData;
import com.pojo.ProviderObservable;
import com.pojo.ProviderResponse;
import com.pojo.callpojo.NewCallData;
import com.pojo.callpojo.NewCallMqttResponse;
import com.webRtc.CallingApis;

/**
 * <h1>MQTTManager</h1>
 * This class is used to handle the MQTT data
 * @author 3Embed
 * @since on 21-12-2017.
 */
public class MQTTManager
{
    private static final String TAG = "MQTTManager";
    private IMqttActionListener mMQTTListener;
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    private Context mContext;
    private static volatile ArrayList<ProviderData> providerResponseData = new ArrayList<>();
    private String modelResponce = "";
    private Gson gson;
    private SessionManagerImpl sessionManagerImpl;
    private ProviderObservable rxProviderObserver;
    private MyBookingObservable myObservable;

    @Inject
    public MQTTManager(Context context, SessionManagerImpl sessionManagerImpl,Gson gson
            ,MyBookingObservable myObservable,ProviderObservable rxProviderObserver)
    //,MyBookingObservable myObservable
    {
        mContext = context;
        this.gson = gson;
        this.myObservable = myObservable;
        this.rxProviderObserver=rxProviderObserver;
        this.sessionManagerImpl=sessionManagerImpl;

        mMQTTListener = new IMqttActionListener()
        {
            @Override
            public void onSuccess(IMqttToken asyncActionToken)
            {
                //subscribeToTopic(MqttEvents.Calls.value + "/" + sessionManagerImpl.getSID(), 1);
                // subscribeToTopic(preferenceHelperDataSource.getMqttTopic());
                Log.w(TAG," onSuccessPhone: myqtt client "+asyncActionToken.isComplete());
               // subscribeToTopic(sessionManagerImpl.getChatProId(),1);
                subscribeToTopic(sessionManagerImpl.getSID(),1);
            }
            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                exception.printStackTrace();
                Log.e(TAG, "onFailure: myqtt client "+asyncActionToken.isComplete()
                        +" "+exception.getMessage());
            }
        };
    }

    /**?
     * <h2>subscribeToTopic</h2>
     * This method is used to subscribe to the mqtt topic
     */
    public void subscribeToTopic(String mqttTopic,int qos)
    {
        try
        {
            if (mqttAndroidClient != null)
                mqttAndroidClient.subscribe(mqttTopic, qos);
        }
        catch (MqttException e)
        {
            Log.e(TAG," MqttException "+e);
            e.printStackTrace();
        }
    }

    /**
     * <h2>unSubscribeToTopic</h2>
     * This method is used to unSubscribe to topic already subscribed
     * @param topic Topic name from which to  unSubscribe
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public void unSubscribeToTopic(String topic)
    {
        try
        {
            if (mqttAndroidClient != null)
                mqttAndroidClient.unsubscribe(topic);
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public void onModelResponseSet()
    {
        modelResponce = "";
    }

    /**
     * <h2>isMQTTConnected</h2>
     * This method is used to check whether MQTT is connected
     * @return boolean value whether MQTT is connected
     */
    public boolean isMQTTConnected()
    {
        try
        {
            return mqttAndroidClient != null && mqttAndroidClient.isConnected();

        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <h2>createMQttConnection</h2>
     * This method is used to create the connection with MQTT
     * @param clientId customer ID to connect MQTT
     */
    @SuppressWarnings("unchecked")
    public void createMQttConnection(String clientId,boolean isSetWill)
    {

        Log.w(TAG," createMQttConnection: "+clientId+(System.currentTimeMillis()/1000));
        String serverUri = Constants.MQTT_URL_HOST + ":" + Constants.MQTT_PORT;
        //   MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(mContext, serverUri, clientId+(System.currentTimeMillis()/1000));
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                //  Log.w(TAG," connectionLost: "+cause.getMessage());
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception
            {


                Log.d(TAG, "messageArrived: " + message);
                JSONObject obj = convertMessageToJsonObject(message);
                Log.d(TAG, "messageArrivedTopic: " + topic);
                Log.d(TAG, "messageArrived: " + obj);

                if(topic.equals(sessionManagerImpl.getSID()))
                {
                    handleNewCall(new String(message.getPayload()));
                }else
                {
                    String topicSplit[] = topic.split("/");
                    if(topicSplit[0].equals(MqttEvents.Provider.value))
                    {
                        Log.w(TAG," handleNewVehicleTypesData Provider: "+new String(message.getPayload()));
                        handleNewVehicleTypesData(new String(message.getPayload()));

                    }else if(topicSplit[0].equals(MqttEvents.JobStatus.value))
                    {
                        Log.w(TAG," handleNewVehicleTypesData JobStatus: "+new String(message.getPayload()));
                        handleJobStatus(new String(message.getPayload()));
                    }
                    else if(topicSplit[0].equals(MqttEvents.LiveTrack.value))
                    {
                        handleLiveBookingStatus(new String(message.getPayload()));
                    }else if(topicSplit[0].equals(MqttEvents.Message.value))
                    {
                        handleNewMsg(new String(message.getPayload()));
                    }else if(topicSplit[0].equals(MqttEvents.Calls.value))
                    {
                        handleOnGoingCalls(new String(message.getPayload()));
                    }
                    else if(topicSplit[0].equals(MqttEvents.CallsAvailability.value))
                    {
                        handleCallsAvailability(new String(message.getPayload()));
                        unSubscribeToTopic(topic);
                    }
                    else if(topicSplit[0].equals(MqttEvents.Call.value))
                    {
                        handleActiveCall(new String(message.getPayload()));
                    }
                }

                Log.w(TAG," handleNewVehicleTypesData Topic: "+topic);

            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.w(TAG," deliveryComplete: "+token);
            }
        });

       /* mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);*/


        connectMQTTClient(mContext,isSetWill);
    }

    /*
     * handle new Call data and open incoming screen
     */
    public void handleNewCall(String callDataResponse)
    {
        Log.d(TAG, "messageArrived: "+callDataResponse);
        try
        {
            NewCallMqttResponse newCallMqttResponse  = gson.fromJson(callDataResponse, NewCallMqttResponse.class);
            NewCallData callData = newCallMqttResponse.getData();
           // JSONObject obj = new JSONObject(callDataResponse).getJSONObject("data");
            CallingApis.OpenIncomingCallScreen(callData,mContext);
           // CallingApis.OpenIncomingCallScreen(obj,mContext);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }


    private JSONObject convertMessageToJsonObject(MqttMessage message) {

        JSONObject obj = new JSONObject();
        try {

            obj = new JSONObject(new String(message.getPayload()));
        } catch (JSONException e) {

        }
        return obj;
    }
    /*
     * handle new Call data and open incoming screen
     */
    public void handleActiveCall(String callDataResponse)
    {
        try
        {
            //ActiveCallResponse activeCallResponse = gson.fromJson(callDataResponse,ActiveCallResponse.class);
            //ActiveCallData activeCallData = activeCallResponse.getData();

            //Publish to the rx
            //action type
            /*
            2 call not Answer of left from a call
            3 join on call
            4 call ended.
            */
            String dataRes = new JSONObject(callDataResponse).getJSONObject("data").toString();
            RxCallInfo.getInstance().emitData(dataRes);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    private void handleOnGoingCalls(String payload) {

        Log.d(TAG, "handleOnGoingCalls: "+payload +" myContext "+mContext);

        try
        {

            JSONObject jobs = new JSONObject();


            JSONObject jsonObject = new JSONObject(payload);
            jsonObject.put("eventName",MqttEvents.Calls.value);

            JSONObject obj = new JSONObject();
            if(jsonObject.getString("type").equals("0"))
            {
                jobs.put("status", 0);
                Log.d(TAG, "handleOnGoingCalls: "+jsonObject.getString("type"));
                obj.put("callId",jsonObject.getString("callId"));
                obj.put("callerImage",jsonObject.getString("callerImage"));
                obj.put("callerName",jsonObject.getString("callerName"));
                obj.put("callerId",jsonObject.getString("callerId"));
                obj.put("callType",jsonObject.getString("callType"));
                obj.put("callerIdentifier",jsonObject.getString("callerIdentifier"));
                obj.put("bookingId",jsonObject.getString("bookingId"));
                UtilityVideoCall.getInstance().setActiveOnACall(true, true);
                CallingApis.OpenIncomingCallScreen(jsonObject,mContext);
                publish(MqttEvents.CallsAvailability.value + "/" + sessionManagerImpl.getSID(), jobs, 0, true);//UserId

                Log.d(TAG, "handleOnGoingCallsJSON: "+jsonObject);
            }else
            {
                jobs.put("status", 1);
                RxCallInfo.getInstance().emitData(jsonObject.toString());
                publish(MqttEvents.CallsAvailability.value + "/" + sessionManagerImpl.getSID(), jobs, 0, true);//UserId
            }
            //RxCallInfo.getInstance().emitData(jsonObject.toString());
            // RxCallInfo.getInstance().emitData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleCallsAvailability(String payload)
    {
        Log.d(TAG, "handleCallsAvailability: "+payload);
        try {
            JSONObject jsonObject = new JSONObject(payload);
            jsonObject.put("eventName",MqttEvents.CallsAvailability.value);
            RxCallInfo.getInstance().emitData(jsonObject.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleNewMsg(String jsonObject) {


        Log.d("MESSAGE", "managerMqttMESSGE: "+jsonObject);
        ChatReponce chatReponce = gson.fromJson(jsonObject,ChatReponce.class);
        ChatDataObervable.getInstance().emitData(chatReponce.getData());
        if(chatReponce.getData().getTimestamp() > sessionManagerImpl.getLastTimeStampMsg())
        {
            sessionManagerImpl.setLastTimeStampMsg(chatReponce.getData().getTimestamp());
            if(!Constants.IS_CHATTING_RESUMED )
            {
                sessionManagerImpl.setChatCount(chatReponce.getData().getBid(),sessionManagerImpl.getChatCount(chatReponce.getData().getBid())+1);
                sessionManagerImpl.setChatBookingID(chatReponce.getData().getBid());
                //  String name = chatReponce.getData().get
                //  sessionManagerImpl.setProName(chatReponce.getData().getName());
                sessionManagerImpl.setChatProId(chatReponce.getData().getTargetId());

                //   NotificationHelper.sendChatNotification(context,chatData.getName(),chatData.getContent(),sessionManager.getChatNotificationId());
                //    sessionManagerImpl.setChatNotificationId(sessionManager.getChatNotificationId()+1);

              /*  Intent intent = new Intent();
                intent.setAction(VariableConstant.INTENT_ACTION_NEW_CHAT);
                context.sendBroadcast(intent);*/
            }
        }

    }

    private void handleLiveBookingStatus(String jsonObject)
    {

        Log.d("LIVETRACK", "managerMqttLIVE: " + jsonObject);
        LiveTackPojo liveTrackPojo = new Gson().fromJson(jsonObject, LiveTackPojo.class);
        LiveTrackObservable.getInstance().emitLiveTrack(liveTrackPojo);
        Constants.LiveTrackBookingPid = liveTrackPojo.getPid();
    }

    /**
     * <h1>handleJobStatus</h1>
     * <p>this handles the job status with the provider </p>
     * @param msgResponse job status response
     */
    private void handleJobStatus(String msgResponse)
    {
        try
        {
            MyBookingStatus myEventStatus = gson.fromJson(msgResponse, MyBookingStatus.class);
            //myObservable.emitJobStatus(myEventStatus);
            MyBookingObservable.getInstance().emitJobStatus(myEventStatus);
            // myObservable.emitJobStatus(myEventStatus);
        }catch (Exception e)
        {
            Log.d(TAG, "handleJobStatus: "+e.getMessage());
            e.printStackTrace();
        }

        // MyBookingObservable.getInstance().subscribe();
    }

    /**
     * <h1>handleNewVehicleTypesData</h1>
     * <p>
     *     method to handle & parse config data received from pubnub
     *     and also update the views if it has been changed from
     * </p>
     */
    public void handleNewVehicleTypesData(String configDataNew)
    {
        //  preferenceHelperDataSource.setVehicleDetailsResponse(configDataNew);
        // to store time difference between last received msg and new received msg of a==2
        try
        {
            ProviderResponse mqttResponseData = gson.fromJson(configDataNew, ProviderResponse.class);
            Log.w(TAG, " handleNewVehicleTypesData()  NULL "+mqttResponseData);
                /* mqttResponseData = gson.fromJson(configDataNew, ProviderResponse.class);
                providerResponseData = mqttResponseData;*/

            // if(modelResponce.equalsIgnoreCase(obj))

            if(mqttResponseData.getErrFlag()==0 && mqttResponseData.getErrNum()==200)
            {
                Constants.mqttErrorMsg = "";
                Constants.serverTime = new JSONObject(configDataNew).getLong("serverTime");

                String obj = gson.toJson(mqttResponseData);
                Log.d(TAG, "handleNewVehicleTypesDataOBJ: "+obj);
                if(!modelResponce.equalsIgnoreCase(obj))
                {
                    providerResponseData.clear();
                    providerResponseData.addAll(mqttResponseData.getData());
                    //  postNewVehicleTypes(configDataNew);
                    postNewVehicleTypes(obj);
                    Log.w(TAG, "handleNewVehicleTypesDataCh: ");
                }
                else
                {
                    Log.w(TAG, "handleNewVehicleTypesDataChNot: ");
                }

            }
            else if(mqttResponseData.getErrFlag()==1 && mqttResponseData.getErrNum()==404)
            {
                Constants.mqttErrorMsg = mqttResponseData.getErrMsg();
                modelResponce = "";
                postEmptyVehicle();
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            Log.d(TAG, "handleNewVehicleTypesData() exc: "+exc);
        }
    }
    /**
     * <h2>connectMQTTClient</h2>
     * This method is used to connect to MQTT client
     */
    private void connectMQTTClient(Context mContext,boolean isSetWill) {
        try {
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(false);
            mqttConnectOptions.setAutomaticReconnect(true);
            byte[] payload = sessionManagerImpl.getSID().getBytes();
            mqttConnectOptions.setKeepAliveInterval(45/*Integer.parseInt(sessionManager.getProTimeOut())*/);
            if (isSetWill) {
                mqttConnectOptions.setWill(MqttEvents.WillTopic.value, payload, 0, false);
            }
            mqttAndroidClient.connect(mqttConnectOptions, mContext, mMQTTListener);

        } catch (MqttException e) {

        }
    }



    /*private void connectMQTTClient(Context mContext)
    {
        try
        {
            Log.w(TAG," connectMQTTClient: ");

                mqttAndroidClient.connect(mqttConnectOptions, mContext, mMQTTListener);

        }
        catch (MqttException e)
        {
            Log.e(TAG," MqttException: "+e);
            e.printStackTrace();
        }
    }
*/



    /**
     * <h2>disconnect</h2>
     * This method is used To disconnect the MQtt client
     */
    public void disconnect(String mqttTopic)
    {
        try
        {
            if (mqttAndroidClient != null)
            {
                //  unSubscribeToTopic(mqttTopic);
                mqttAndroidClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    public void publish(String topicName, JSONObject obj, int qos, boolean retained) {

        try {
            mqttAndroidClient.publish(topicName, obj.toString().getBytes(), qos, retained);

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     *<h2>postNewVehicleTypes</h2>
     * This method is used to publish the vehicle types
     * @param configDataNew
     */
    public void postNewVehicleTypes(String configDataNew)
    {
        Log.w(TAG,"postNewVehicleTypes posted ");
        if(providerResponseData != null)
        {
            Log.w(TAG, "postNewVehicleTypes() types: "+ providerResponseData.get(0).getFirstName());
            //ProviderObservable.getInstance().emitData(providerResponseData);
            rxProviderObserver.emitData(providerResponseData);
        }
        modelResponce = configDataNew;
    }

    private void postEmptyVehicle()
    {
        modelResponce = "";
        //ProviderObservable.getInstance().emitData(new ArrayList<ProviderData>());
        rxProviderObserver.emitData(new ArrayList<>());
    }
}
