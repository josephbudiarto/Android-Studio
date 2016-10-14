package com.example.owner.gson;

public class Post {
    int idArticle;
    String ArticleTitle;
    String ArticleDateTime;
    String AuthorName;
    String ArticlePhotoPath;

    public String getArticleDateTime() {
        return ArticleDateTime;
    }

    public void setArticleDateTime(String articleDateTime) {
        ArticleDateTime = articleDateTime;
    }

    public String getArticlePhotoPath() {
        return ArticlePhotoPath;
    }

    public void setArticlePhotoPath(String articlePhotoPath) {
        ArticlePhotoPath = articlePhotoPath;
    }

    public String getArticleTitle() {
        return ArticleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        ArticleTitle = articleTitle;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

}