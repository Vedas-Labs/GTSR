package com.gtsr.gtsr.dataController;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;
import com.gtsr.gtsr.ServerApisInterface;
import com.gtsr.gtsr.model.TestItemsModel;

import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCallDataController {
    public static ApiCallDataController myObj;
    ServerResponseInterface serverResponseInterface;
    public ServerApisInterface serverApi;
    public ServerApisInterface  serverJsonApi;
    public TestItemsModel selectedTestItem=null;
    Context context;

    public static ApiCallDataController getInstance() {
        if (myObj == null) {
            myObj = new ApiCallDataController();
        }
        return myObj;
    }

    public void fillContent(Context context1) {
        context = context1;
        createRetrofotInstance();
        createRetrofotInstance1();
    }

    public void initializeServerInterface(ServerResponseInterface serverResponseInterface1) {
        serverResponseInterface = serverResponseInterface1;
    }
    public void createRetrofotInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serverApi = retrofit.create(ServerApisInterface.class);
    }
    public void loadServerApiCall(Call<JsonObject> apiCall, final String opetation) {
        Call<JsonObject> callable = apiCall;
        callable.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("ffff",""+response.body());
                Log.e("gggg",""+response.message());
                if (response.body() != null) {
                    String responseString = response.body().toString();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(responseString);
                        Log.e("aaaaaaaaaaaa", "onResponse: " + jsonObject.toString());
                        if (jsonObject.getString("response").equals("3")) {
                            serverResponseInterface.successCallBack(jsonObject,opetation);
                        }  else {
                            Log.e("response ","messsage");
                            serverResponseInterface.failureCallBack(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
               // Toast.makeText(context, "please check your network connection", Toast.LENGTH_SHORT).show();
                serverResponseInterface.failureCallBack(t.getMessage());
            }
        });
    }
   public void createRetrofotInstance1(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
       serverJsonApi = retrofit.create(ServerApisInterface.class);
    }
    public void loadjsonApiCall(Call<JsonObject> apiCall, final String opetation) {
        Call<JsonObject> callable = apiCall;
        callable.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("ffff",""+response.code());
                if (response.body() != null) {
                    String responseString = response.body().toString();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(responseString);
                        Log.e("aaaaaaaaaaaa", "onResponse: " + jsonObject.toString());
                        if (jsonObject.getString("response").equals("3")) {
                            serverResponseInterface.successCallBack(jsonObject,opetation);
                        }  else {
                            Log.e("response","message");
                            //serverResponseInterface.failureCallBack(jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                serverResponseInterface.failureCallBack(t.getMessage());
            }
        });
    }
    public interface ServerResponseInterface {
        void successCallBack(JSONObject jsonObject, String opetation);
        void failureCallBack(String failureMsg);
    }
}
