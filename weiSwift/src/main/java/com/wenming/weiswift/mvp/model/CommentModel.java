package com.wenming.weiswift.mvp.model;

import android.content.Context;

import com.wenming.weiswift.entity.Comment;

import java.util.ArrayList;

/**
 * Created by wenmingvs on 16/5/15.
 */
public interface CommentModel {

    interface OnDataFinishedListener {
        void noMoreDate();

        void onDataFinish(ArrayList<Comment> commentlist);

        void onError(String error);

    }


    public void toMe(int sourceType, Context context, OnDataFinishedListener onDataFinishedListener);

    public void toMeNextPage(int sourceType, Context context, OnDataFinishedListener onDataFinishedListener);

    public void toMeCacheSave(Context context, String response);

    public void ToMeCacheLoad(Context context, OnDataFinishedListener onDataFinishedListener);

    public void byMe(Context context, OnDataFinishedListener onDataFinishedListener);

    public void byMeNextPage(Context context, OnDataFinishedListener onDataFinishedListener);

}