package com.gtsr.gtsr.testModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gtsr.gtsr.R;
import com.gtsr.gtsr.dataController.ApiCallDataController;
import com.gtsr.gtsr.model.TestItemsModel;
import com.gtsr.gtsr.model.TestItemsResponseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    StripAdapter stripAdapter;
    RecyclerView recyclerView;
    int selectedPosition = -1;
    Button btnNext;
    ImageView back;
    //  ArrayList<String> scFilesArray=new ArrayList<>();
    ArrayList<TestItemsModel> urineList = new ArrayList<>();
    ArrayList<TestItemsModel> bloodList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        inti();
    }
    public void  inti(){
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition > -1) {
                    startActivity(new Intent(TestActivity.this, PairDeviceViewController.class));
                }else{
                  //  startActivity(new Intent(TestActivity.this, PairDeviceViewController.class));
                    Toast.makeText(getApplicationContext(),"Please select testType",Toast.LENGTH_SHORT).show();
                }
            }
        });
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ApiCallDataController.getInstance().fillContent(getApplicationContext());
        loadRecyclerView();
        loadDummyTestItems();
       /* accessResponseFromInterface();
        fetchTestItemsApi();*/

    }
    private void loadDummyTestItems(){
        TestItemsModel itemsModel=new TestItemsModel();
        itemsModel.setTestName("Urine Analysis");
        /*itemsModel.setTestName("Urine Sediment");
        itemsModel.setTestName("Drug abuse screening");*/
        urineList.add(itemsModel);
    }
    private void fetchTestItemsApi() {
        JSONObject params = new JSONObject();
        try {
            params.put("byWhom", "patient");
            params.put("byWhomID", "PIDRpo0j");
            params.put("hospital_reg_num", "AP2317293903");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject) jsonParser.parse(params.toString());
        Log.e("ServiceResponse", "onResponse: " + gsonObject.toString());
        ApiCallDataController.getInstance().loadjsonApiCall(ApiCallDataController.getInstance().serverJsonApi.fetchTestItemsApi("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVkYTQ1ZDU4MjFkNzA3NmI4MGMzYjdjZSIsImlhdCI6MTU3MTA1MzI1NH0.2FPwjqLs5uafG4f-kgADuX7dgMGhrDwsna_zHiUjHeo",gsonObject), "fetch");
    }

    private void accessResponseFromInterface() {
        ApiCallDataController.getInstance().initializeServerInterface(new ApiCallDataController.ServerResponseInterface() {
            @Override
            public void successCallBack(JSONObject jsonObject, String curdOpetaton) {
                Log.e("asdf","adf");
                if (curdOpetaton.equals("fetch")) {
                    try {
                        if (jsonObject.getString("response").equals(String.valueOf("3"))) {
                            JSONArray recordsObj = jsonObject.getJSONArray("records");
                            if(recordsObj.length()>0) {
                                Gson gson = new Gson();
                                TestItemsResponseModel testItemsResponseModel = gson.fromJson(recordsObj.get(0).toString(), TestItemsResponseModel.class);
                                Log.e("testResponse", "call" + testItemsResponseModel.getTestItems().size());
                                ArrayList<TestItemsModel> list=testItemsResponseModel.getTestItems();
                                urineList.clear();
                                for(int i=0;i<list.size();i++){
                                    TestItemsModel itemsModel=list.get(i);
                                    if(itemsModel.getSpecimenType().equals("Urine")){
                                        urineList.add(itemsModel);
                                    }else{
                                        bloodList.add(itemsModel);
                                    }
                                }
                                loadRecyclerView();
                                stripAdapter.notifyDataSetChanged();
                            }
                            //refreshShowingDialog.hideRefreshDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void failureCallBack(String failureMsg) {
                Log.e("error","df"+failureMsg);
               // Toast.makeText(TestActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                // refreshShowingDialog.hideRefreshDialog();
            }
        });
    }

    private void loadRecyclerView() {
       /* scFilesArray.add("Urine Routine");
        scFilesArray.add("Urine Routine");
*/
        stripAdapter = new StripAdapter(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.listone);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(stripAdapter);
        recyclerView.setHasFixedSize(true);

    }

    public class StripAdapter extends RecyclerView.Adapter<StripAdapter.ViewHolder> {
        Context ctx;
        public StripAdapter(Context ctx) {
            this.ctx = ctx;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testmenitems, parent, false);
            ViewHolder myViewHolder = new ViewHolder(view);
            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(urineList.get(position).getTestName());
            holder.params.setText("11"+" Parameters");
            if (selectedPosition == position) {
                Log.e("if", "" + position);
                holder.image.setVisibility(View.VISIBLE);
               // ApiCallDataController.getInstance().selectedTestItem=urineList.get(selectedPosition);
            } else {
                Log.e("else", "" + position);
                holder.image.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedPosition != position) {
                        selectedPosition = position;
                        notifyDataSetChanged();
                    } else {
                        selectedPosition = -1;
                        notifyDataSetChanged();
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            if (urineList.size() > 0) {
                return urineList.size();
            } else {
                return 0;
            }
        }
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name,params;
            ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.testName);
                params = (TextView) itemView.findViewById(R.id.params);
                image = (ImageView) itemView.findViewById(R.id.image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }

}