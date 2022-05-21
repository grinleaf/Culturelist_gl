package com.grinleaf.tp14librarysearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Tab2Fragment extends Fragment {

    RecyclerViewAdapter adapter;
    ArrayList<PerformDetailData> datas = new ArrayList<>();
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab2,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView= view.findViewById(R.id.recycler02);
        adapter= new RecyclerViewAdapter(getActivity(), datas);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {    //Tab1 이 화면에 보여질 때 자동 발동 메소드 (라이프사이클)
        super.onResume();
        MainActivity ac= (MainActivity) getActivity();
        if(datas.size()==0) loadData(ac.ids);
    }

    void loadData(ArrayList<PerformID> ids){        //MainActivity 에서 파싱작업하여 가져온 공연 ID 값 배열을 loadDetailData() 메소드로 전달
        for(int i=0; i<ids.size();i++){
            String id= ids.get(i).performId;
            loadDetailData(id);
        }
    }

    void loadDetailData(String id){                 //공연 1개의 ID에 대한 파싱작업
        //상세정보 api url 파싱
        Thread t= new Thread(){
            @Override
            public void run() {

                String apiKey= "c95870c8bfe2437880b4aed38acb6efe";
                //테스트 URL 주소 : 상세정보 API(2)
//              String address= "http://www.kopis.or.kr/openApi/restful/pblprfr/PF188178?service=c95870c8bfe2437880b4aed38acb6efe";

                String address= "http://www.kopis.or.kr/openApi/restful/pblprfr/"
                + id
                + "?service=" + apiKey;

                try {
                    URL url= new URL(address);
                    InputStream is= url.openStream();
                    InputStreamReader isr= new InputStreamReader(is);

                    XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                    XmlPullParser xpp= factory.newPullParser();
                    xpp.setInput(isr);

                    int eventType= xpp.getEventType();
                    PerformDetailData data= null;
                    while(eventType!=XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                                break;
                            case XmlPullParser.START_TAG:
                                String tagName= xpp.getName();
                                if(tagName.equals("db")) {
                                    data= new PerformDetailData();
                                }else if(tagName.equals("poster")) {
                                    xpp.next();
                                    if(data!=null) data.iv= xpp.getText();
                                }else if(tagName.equals("prfnm")){
                                    xpp.next();
                                    if(data!=null) data.title= xpp.getText();
                                }else if(tagName.equals("genrenm")) {
                                    xpp.next();
                                    if(data!=null) data.genre= xpp.getText();
                                }else if(tagName.equals("prfcrew")) {
                                    xpp.next();
                                    if(data!=null) data.author= xpp.getText();
                                }else if(tagName.equals("entrpsnm")) {
                                    xpp.next();
                                    if(data!=null) data.product= xpp.getText();
                                }else if(tagName.equals("prfstate")) {
                                    xpp.next();
                                    if(data!=null) data.state= xpp.getText();
                                }else if(tagName.equals("prfpdfrom")) {
                                    xpp.next();
                                    if(data!=null) data.dateStart= xpp.getText();
                                }else if(tagName.equals("prfpdto")) {
                                    xpp.next();
                                    if(data!=null) data.dateEnd= xpp.getText();
                                }else if(tagName.equals("prfcast")) {
                                    xpp.next();
                                    if(data!=null) data.cast= xpp.getText();
                                }else if(tagName.equals("fcltynm")) {
                                    xpp.next();
                                    if(data!=null) data.place= xpp.getText();
                                }else if(tagName.equals("mt10id")) {
                                    xpp.next();
                                    if(data!=null) data.hallID= xpp.getText();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                //ArrayList<PerformDetailData> datas --> 가져온 데이터 add
                                String tagName2= xpp.getName();
                                if(tagName2.equals("db")){
                                    if(data!=null) {
                                        if(data.genre.equals("뮤지컬")) {
                                            datas.add(data);
                                        }
                                    }
                                }
                                break;
                        }
                        eventType= xpp.next();
                    }

                    //notification 작업 : 메인스레드 runOnUiThread( new Runnable() )
                    ((MainActivity)MainActivity.mainContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
