package com.haski.swifto.model.vo.dog;

/**
 * Created by mindminews1 on 15/7/17.
 */

public class Comment {
    public String title = "";
    public String body = "";
    public String created = "";
    public String cid = "";
    public String _id = "";
    public String author_name = "";
    public String author_picture = "";
    public String nid = "";

    @Override
    protected Comment clone() throws CloneNotSupportedException {
        Comment toRet = new Comment();

        toRet.nid = this.nid;
        toRet.title = this.title;
        toRet.body = this.body;
        toRet.created = this.created;
        toRet.cid = this.cid;
        toRet._id = this._id;
        toRet.author_name = this.author_name;
        toRet.author_picture = this.author_picture;

        return toRet;
    }

    public Comment getClone() {
        try {
            return clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
