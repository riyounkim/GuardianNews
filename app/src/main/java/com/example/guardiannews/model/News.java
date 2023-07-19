package com.example.guardiannews.model;

public class News {
    private String news_id;

    private String webPublicationDate;
    private String webTitle;

    private String apiUrl;

    private long id;



    public News(){

    }


    public News(String n_id, String webTitle,  String apiUrl, String publicationDate) {
        this.news_id = n_id;
        this.webTitle = webTitle;
        this.apiUrl = apiUrl;
        this.webPublicationDate = publicationDate;
    }

    public News(long id, String n_id, String webTitle,  String apiUrl, String publicationDate) {
        this( n_id,  webTitle,   apiUrl,  publicationDate);
        this.id=id;

    }



    public long getId() { return id;}

    public void setId(long id) {this.id = id;}
    public String getNews_id() {
        return news_id;
    }

    public void setNews_id(String news_id) {
        this.news_id = news_id;
    }


    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public void setWebPublicationDate(String webPublicationDate) {
        this.webPublicationDate = webPublicationDate;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }



    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }


}
