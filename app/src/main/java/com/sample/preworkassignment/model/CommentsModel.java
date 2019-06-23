package com.sample.preworkassignment.model;

/**
 * Created by admin on 6/23/2019.
 */

public class CommentsModel {

    long id;//: 1669027095,
    String image_id;//": "wy4pCR0",
    String comment;//": "I'm windows",
    String author;//":"roxanacrainic",
    //    "author_id": 106298405,
    //  "on_album": false,
    //"album_cover": null,
    //"ups": 1,
    //"downs": 0,
    //"points": 1,
    //  "datetime": 1561058901,
    //"parent_id": 0,
    //"deleted": false,

    public CommentsModel(long id, String image_id, String comment, String author) {
        this.id = id;
        this.image_id = image_id;
        this.comment = comment;
        this.author = author;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    //"vote": null,
    //"platform": "android",
    //"children": []
}

