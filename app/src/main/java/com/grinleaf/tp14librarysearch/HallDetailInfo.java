package com.grinleaf.tp14librarysearch;

public class HallDetailInfo {
    String hallName;    //공연장이름
    int seatScale;      //객석 수
    int hallCount;      //공연장 수
    String tel;         //전화번호
    String homeUrl;     //홈페이지주소
    String hallAddress; //공연장주소
    double lat; //위도
    double lng; //경도

    public HallDetailInfo(String hallName, int seatScale, int hallCount, String tel, String homeUrl, String hallAddress, double lat, double lng) {
        this.hallName = hallName;
        this.seatScale = seatScale;
        this.hallCount = hallCount;
        this.tel = tel;
        this.homeUrl = homeUrl;
        this.hallAddress = hallAddress;
        this.lat = lat;
        this.lng = lng;
    }

    public HallDetailInfo() { }
}
