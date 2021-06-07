package com.example.instagramclone.model;

public class Comment {
    private String comment;
    private String publihser;

    public Comment(String comment, String publihser) {
        this.comment = comment;
        this.publihser = publihser;
    }
    Comment(){}

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublihser() {
        return publihser;
    }

    public void setPublihser(String publihser) {
        this.publihser = publihser;
    }
}
