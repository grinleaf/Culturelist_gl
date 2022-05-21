package com.grinleaf.tp14librarysearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //MainActivity 메소드를 다른 클래스에서 끌어다쓰기 위한 전역변수
    public static Context mainContext;

    NavigationView nav;

    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;

    TabLayout tabLayout;
    ViewPager2 pager;
    FragmentAdapter fragmentAdapter;

    String[] tabTitle= new String[]{"연극","뮤지컬","클래식","무용"};
    ArrayList<PerformID> ids= new ArrayList<PerformID>();

    String apiKey= "c95870c8bfe2437880b4aed38acb6efe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext= this;
        
        //공연ID 데이터 불러오기 --> loadIdData()
        loadIdData();

        //탭 레이아웃 설정
        tabLayout= findViewById(R.id.layout_tab);
        //뷰페이저 설정 + 각 프래그먼트 객체화
        pager= findViewById(R.id.pager);
        fragmentAdapter= new FragmentAdapter(this);
        pager.setAdapter(fragmentAdapter);

        //pager 수 만큼 tab 생성 --> tab : 해당 번째 tab / position : 해당 번째 tab 의 인덱스번호
        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabTitle[position]);
            }
        }).attach();

        //툴바설정
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //좌측 네비게이션 설정
        drawerLayout= findViewById(R.id.layout_drawer);
        drawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);

        nav= findViewById(R.id.nav);
        CircleImageView civ= nav.getHeaderView(0).findViewById(R.id.circle_iv);
        Glide.with(this).load(G.profileImage).into(civ);
        TextView userId= nav.getHeaderView(0).findViewById(R.id.tv_userid);
        userId.setText(G.nickname);

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.option01:     //마이페이지로 화면 전환
                        break;
                    case R.id.option02:     //최근 방문한 페이지로 화면 전환
                        break;
                    case R.id.option03:     //즐겨찾기 목록으로 화면 전환
                        break;
                }
                drawerLayout.closeDrawer(nav,true);
                return false;
            }
        });
        nav.setItemIconTintList(null);
        nav.setItemVerticalPaddingResource(R.dimen.option_padding);
    }

    //네트워크 서버에서 데이터 읽어오기 --> 순서 주의 상세정보를 가져올 API 의 load 보다 앞에서 쓰일 것
    void loadIdData(){
        Thread t= new Thread(){
            @Override
            public void run() {

                //오늘 날짜에서 3 개월 후 설정
                Date date= new Date();
                date.setTime(date.getTime()+(1000*60*60*24*90));
                SimpleDateFormat sdf= new SimpleDateFormat("yyyMMdd");  //날짜정보 포맷
                String dateToday= sdf.format(date);

                //최근 3 개월 전 날짜 설정
                date.setTime(date.getTime()-(1000*60*60*24*90));
                String datePrevAYear= sdf.format(date);

                //총 6 개월 간의 데이터 파싱
                
                int rows= 100;

                //테스트 URL 주소
//                String address= "http://www.kopis.or.kr/openApi/restful/pblprfr?service=c95870c8bfe2437880b4aed38acb6efe&stdate=20220217&eddate=20220317&rows=10&cpage=1";

                String address= "http://www.kopis.or.kr/openApi/restful/pblprfr"
                                +"?service="+apiKey
                                +"&stdate="+datePrevAYear
                                +"&eddate="+dateToday
                                +"&rows="+rows
                                +"&cpage=1";

                try {
                    //스트림 만드는 객체 URL~
                    URL url= new URL(address);
                    InputStream is= url.openStream();
                    InputStreamReader isr= new InputStreamReader(is);
                    //읽어온 xml 문서 분석해주는 XmlPullParser 공장 + xpp 객체 생성
                    XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                    XmlPullParser xpp= factory.newPullParser();
                    xpp.setInput(isr);
                    
                    //xpp 의 xml 문서 분석 작업 : eventType - xml 문서의 <> 상태에 따라 처리
                    int eventType= xpp.getEventType();
                    PerformID id= null;
                    while(eventType!=XmlPullParser.END_DOCUMENT){       // while 조건문 처리 덕분에 END_DOCUMENT 스킵
                        switch (eventType){
                            case XmlPullParser.START_DOCUMENT:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //xml 문서 시작 <> 을 만났을 때 실행되는 영역
                                    }
                                });
                                break;
                                
                            case XmlPullParser.START_TAG:
                                String tagName= xpp.getName();    //가져와 저장할 데이터 변수
                                if(tagName.equals("db")){
                                    id= new PerformID();
                                }else if(tagName.equals("mt20id")){
                                    xpp.next();
                                    id.performId= xpp.getText();
                                }
                                break;
                                
                            case XmlPullParser.END_TAG:
                                String tagName2= xpp.getName();
                                if(tagName2.equals("db")){
                                    if(id!=null) ids.add(id);
                                }
                                break;
                        }
                        eventType= xpp.next();
                    }
                    //loadIdData() 함수 안. 파싱 -> 첫번째 프래그먼트 배열값을 꺼내옴 -> 해당 프래그먼트의 loadData() 메소드에 ids 보내기
                    Tab1Fragment tab1Fragment= (Tab1Fragment) fragmentAdapter.fragments.get(0);   //첫번째 프래그먼트
                    tab1Fragment.loadData(ids);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar pb= findViewById(R.id.pb);
                            pb.setVisibility(View.GONE);
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
        t.start();  //위의 생성된 스레드 객체 시작
    }

}

//다 구현 성공하묜 하단에 광고도 넣어보기~! ^__^