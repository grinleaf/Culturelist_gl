package com.grinleaf.tp14librarysearch;

public class PerformDetailData {
        String iv, title, genre, author,product,state,dateStart,dateEnd,cast,place,hallID,ivScrap;

    public PerformDetailData(String iv, String title, String genre, String author, String product, String state, String dateStart, String dateEnd, String cast, String place, String hallID, String ivScrap) {
        this.iv = iv;
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.product = product;
        this.state = state;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.cast = cast;
        this.place = place;
        this.hallID = hallID;
        this.ivScrap = ivScrap;
    }

    public PerformDetailData() {}
}
