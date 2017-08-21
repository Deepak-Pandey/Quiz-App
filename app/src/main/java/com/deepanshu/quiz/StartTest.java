package com.deepanshu.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class StartTest extends AppCompatActivity {

    TextView mtestName, mtestTopic, mDuration, mAuthorId;
    Button mstartTest;
    int flag;
    String qbid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        mtestName = (TextView) findViewById(R.id.tv_test_name);
        mtestTopic = (TextView) findViewById(R.id.tv_test_topic);
        mDuration = (TextView) findViewById(R.id.tv_test_duration);
        mAuthorId = (TextView) findViewById(R.id.tv_author);
        mstartTest = (Button) findViewById(R.id.btn_start_test);

        Bundle bundle = getIntent().getExtras();
        String testId = bundle.getString("TestId");
        flag = 1;
        FetchTask s = new FetchTask();
        s.execute(testId);
        if(flag == 0)
        {
            Toast.makeText(StartTest.this,"Could not connect to server",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StartTest.this,UserActivity.class));
            finish();
        }

        mstartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check_connectivity check = new Check_connectivity(StartTest.this);
                if(check.getInternetStatus())
                {
                    Intent i = new Intent(StartTest.this, StartTest.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("QBId",qbid );
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(StartTest.this,"Internet Connection Problem",Toast.LENGTH_LONG).show();
                }
            }
        });



    }
    private class FetchTask extends AsyncTask<String,String,String>
    {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pd=new ProgressDialog(StartTest.this);
            pd.setTitle("Getting Details");
            pd.setMessage("Please wait...");
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);


            if(s.trim().equals("")){
                flag = 0;
            }
            else if (!("error".equals(s.trim()))){

               //set the data in text views
                try {
                    JSONArray ja = new JSONArray(s);
                    JSONObject jo;
                    jo = ja.getJSONObject(0);

                    String name = jo.getString("TEST_NAME");
                    mtestName.setText(name);
                    name = jo.getString("TOPIC");
                    mtestTopic.setText(name);
                    name = jo.getString("DURATION");
                    mDuration.setText(name);
                    name = jo.getString("USER_ID");
                    mAuthorId.setText(name);
                    qbid = jo.getString("QB_ID");

                } catch (JSONException e) {
                    e.printStackTrace();
                    flag = 0;
                }
            }
            else
            {
                flag = 0;
                Toast.makeText(getBaseContext(),"Error : "+s,Toast.LENGTH_LONG).show();
            }
            pd.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {

            try{
                String test_id = strings[0];
                String data= URLEncoder.encode("test_id","UTF-8") + "=" +
                        URLEncoder.encode(test_id,"UTF-8");

                URL url = new URL("https://contests.000webhostapp.com/php/start_test.php?"+data);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                StringBuffer jsonData = new StringBuffer();

                //READ
                while ((line = bufferedReader.readLine()) != null) {
                    jsonData.append(line).append("\n");
                }

                return jsonData.toString();


            }catch(Exception e){
                e.printStackTrace();
            }

            return "error";
        }
    }

}
//{"QB_ID":"1","USER_ID":"2","ONLINE":"0","TEST_NAME":"dbms","TOPIC":"sql","DURATION":"2"}