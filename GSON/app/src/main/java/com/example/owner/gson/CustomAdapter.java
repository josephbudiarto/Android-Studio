package com.example.owner.gson;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Post> mPosts;
    private ViewHolder mViewHolder;

    private Bitmap mBitmap;
    private Post mPost;
    private Activity mActivity;

    public CustomAdapter(Activity activity, List<Post> posts) {
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mPosts = posts;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.post, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.ArticlePhotoPath = (ImageView) convertView.findViewById(R.id.post_thumbnail);
            mViewHolder.AuthorName = (TextView) convertView.findViewById(R.id.post_author);
            mViewHolder.ArticleTitle = (TextView) convertView.findViewById(R.id.post_title);
            mViewHolder.ArticleDateTime = (TextView) convertView.findViewById(R.id.post_date);
            mViewHolder.id = (TextView) convertView.findViewById(R.id.post_id);

            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }


        mPost = mPosts.get(position);

        if (mPost.getArticlePhotoPath() != null) {



            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        URL url = new URL(mPost.getArticlePhotoPath());
                        mBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } catch (MalformedURLException e) {

                    } catch (IOException e) {

                    }
                    return null;
                }
            }.execute();


            mViewHolder.ArticlePhotoPath.setImageBitmap(mBitmap);
        }

        // ถ้าใช้ Picasso ก็ uncomment ข้างล้างนี้ แล้วลบ AsyncTask ออก
         Picasso.with(mActivity).load("https://opensource.petra.ac.id/~rina/" + mPost.getArticlePhotoPath()).into(mViewHolder.ArticlePhotoPath);

        mViewHolder.AuthorName.setText(mPost.getAuthorName());
        mViewHolder.ArticleTitle.setText(mPost.ArticleTitle);
        mViewHolder.ArticleDateTime.setText(mPost.ArticleDateTime);
        mViewHolder.id.setText(Integer.toString(mPost.getIdArticle()));

        return convertView;
    }

    private static class ViewHolder {
        ImageView ArticlePhotoPath;
        TextView ArticleTitle;
        TextView AuthorName;
        TextView ArticleDateTime;
        TextView id;
    }
}