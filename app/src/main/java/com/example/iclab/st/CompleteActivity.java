package com.example.iclab.st;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


import static com.example.iclab.st.IntroActivity.httpAddr;
import static com.example.iclab.st.NamesrchActivity.checkAdd;
import static com.example.iclab.st.NewplaceActivity.GCSurvey;
import static com.example.iclab.st.SurveyActivity.frameId;
import static com.example.iclab.st.SurveyActivity.plateId;
import static com.example.iclab.st.ValueprintActivity.is_appended;

// 실측완료를 누르면 최종 결과 값이 출력되는 액티비티
public class CompleteActivity extends AppCompatActivity{

    String extraData;
    String firstData;
    String longstr;
    TextView data, extra;
    Button resultBtn,resultBtn2;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        resultBtn = findViewById(R.id.resultBtn);
        resultBtn2= findViewById(R.id.resultBtn2);

        data = findViewById(R.id.dataText);
        extra = findViewById(R.id.extradataText);

        data.setMovementMethod(new ScrollingMovementMethod());
        extra.setMovementMethod(new ScrollingMovementMethod());
        data.setText("현장명 :  " + GCSurvey.siteName+"\n발주처 :  " + GCSurvey.clientName +"\n실측일 :  " + GCSurvey.createdAt+ "\n담당자 :  "+GCSurvey.authorFullName);

        extraData="";
        firstData="";

        for(int i=0;i<GCSurvey.list.size();i++) {
            String pointSum="";
            for(int j=0;j<4&&GCSurvey.list.get(i).points[j]!=null;j++)
                pointSum+=GCSurvey.list.get(i).points[j]+"  ";

            firstData += "No. " + (i + 1) + "\n보호판 이름: " + GCSurvey.list.get(i).plate_id + "\n뿌리 값: " + pointSum +"\n메모: " + GCSurvey.list.get(i).memo  +"\n\n";// 마지막 페이지 출력문

        }




        ArrayList<String> list = new ArrayList<>();
        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<String> list3 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();


        for(int i=0;i<GCSurvey.list.size();i++)
        {
            Log.e("왜안뜨냐", " "+GCSurvey.list.get(i).plate_id);
            if(!list.contains(GCSurvey.list.get(i).plate_id))
            {
                list.add(GCSurvey.list.get(i).plate_id);
                list1.add(1);

            }
            else
            {
                int ch=-1;
                for(int k=0;k<list.size();k++)
                    if(list.get(k).equals(GCSurvey.list.get(i).plate_id))
                        ch=k;
                    list1.set(ch,list1.get(ch)+1);
            }
            if(GCSurvey.list.get(i).framecheck)
            {
                int ch =-1;
                for(int k=0;k<plateId.size();k++) {

                    if(plateId.get(k).contains(GCSurvey.list.get(i).plate_id))
                        ch=k;
                }
                if(!list3.contains(frameId.get(ch))) {
                    list3.add(frameId.get(ch));
                    list2.add(1);
                }
                else {
                    int ch2=-1;
                    for(int j=0;j<list3.size();j++)
                        if(list3.get(j).equals(frameId.get(ch)))
                            ch2=j;
                    list2.set(ch2,list2.get(ch2)+1);
                }

            }

        }
        longstr ="";
        for(int i =0;i<list1.size();i++)
            longstr += list.get(i) +" : " +list1.get(i)+" 조\n";
        longstr += "\n";
        for(int i =0;i<list2.size();i++)
            longstr += list3.get(i) +" : " +list2.get(i)+" 조\n";

        extraData=longstr;
        extra.setText(extraData);
        resultBtn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    if (extraData.equals(longstr)) {
                        extraData = firstData;
                        resultBtn2.setText("기본");
                    } else {
                        resultBtn2.setText("상세");
                        extraData = longstr;
                    }
                    extra.setText(extraData);
                }

                return true;
            }
        });


        // 완료 버튼 누르면 기능선택 화면으로 다시 이동
        resultBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final AsyncHttpClient client = new AsyncHttpClient();
                client.setCookieStore(new PersistentCookieStore(CompleteActivity.this));

                // 지도에 찍혀있는 마커 리스트 초기화
//                MapActivity.markerList.clear();
                // 카운트 초기화
                SurveyList.count = 1;
                StringEntity entity;
                if(checkAdd) {
                    SurveyList tmp=GCSurvey.list.get(GCSurvey.list.size()-1);
                    GCSurvey.list.clear();
                    GCSurvey.list.add(tmp);
                }
                entity = new StringEntity(new Gson().toJson(GCSurvey), "utf-8");
                if(!is_appended)
                {
                    client.post(CompleteActivity.this, httpAddr+"/measureset/new", entity, "application/json", new AsyncHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            // 서버 연결
                            Log.e("SUc ::: ", " "+ statusCode);

                    }
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // 서버 응답 없음
                            Log.e("ERROR ::: ", " "+ statusCode);
                        }
                    });

                }else {
                    client.put(CompleteActivity.this, httpAddr+"/measureset/"+GCSurvey.measureset_id, entity, "application/json", new AsyncHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            // 서버 연결
                            Log.e("TTT","measureset id "+GCSurvey.measureset_id);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // 서버 응답 없음
                        }
                    });

                }
                Log.e("Entity"," "+new Gson().toJson(GCSurvey));
                extraData="";
                is_appended = false;
                SaveSharedPreference.setUserData(CompleteActivity.this, "");
                GCSurvey.list.clear();// 전송 완료후 데이터 초기화
                checkAdd=false;
                Intent intent = new Intent(getApplicationContext(), FunctionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }




}
