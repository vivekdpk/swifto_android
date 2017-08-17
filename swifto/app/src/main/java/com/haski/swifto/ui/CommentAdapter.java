package com.haski.swifto.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.haski.swifto.R;
import com.haski.swifto.model.vo.dog.Comment;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mindminews1 on 18/7/17.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    private LayoutInflater mInflater;
    private Context mContex;
    private ArrayList<Comment> mCommentList;

    public CommentAdapter(Context context, int textViewResourceId, ArrayList<Comment> commentList) {
        super(context, textViewResourceId, commentList);

        this.mInflater = LayoutInflater.from(context);
        this.mContex = context;
        this.mCommentList = commentList;
    }

    @Nullable
    @Override
    public Comment getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        int count= 0;
        if(mCommentList != null)
            count = mCommentList.size();

        return count;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.comment_list_row, null);

            holder = new ViewHolder();

            holder.imgView = (CircleImageView) convertView.findViewById(R.id.author_picture);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.comment_title);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.comment_description);
            holder.tvCommentedBy = (TextView) convertView.findViewById(R.id.commented_by);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = getItem(position);

        if(comment.author_picture != null && comment.author_picture != "")
        {
            new AQuery(convertView)
                    .id(holder.imgView)
                    .image(comment.author_picture, true, true, 0,
                            R.drawable.dummy_image).width(100, true)
                    .height(100, true);
        }

        if (holder.tvTitle != null){
            holder.tvTitle.setText(comment.title);
        }

        if (holder.tvDescription != null){
            holder.tvDescription.setText(comment.body);
        }

        if (holder.tvCommentedBy != null){
            long millisecond = Long.parseLong(comment.created) * 1000L;
            String dateString = DateFormat.format("MM/dd/yy", new Date(millisecond)).toString();

            holder.tvCommentedBy.setText("Submitted by " + comment.author_name + " on " + dateString);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView tvTitle;
        TextView tvDescription;
        TextView tvCommentedBy;
        CircleImageView imgView;
    }
}

