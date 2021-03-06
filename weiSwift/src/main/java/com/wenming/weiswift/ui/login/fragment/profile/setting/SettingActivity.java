package com.wenming.weiswift.ui.login.fragment.profile.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wenming.weiswift.MyApplication;
import com.wenming.weiswift.R;
import com.wenming.weiswift.mvp.presenter.SettingActivityPresent;
import com.wenming.weiswift.mvp.presenter.imp.SettingActivityPresentImp;
import com.wenming.weiswift.mvp.view.SettingActivityView;
import com.wenming.weiswift.ui.common.BaseActivity;
import com.wenming.weiswift.ui.login.fragment.profile.setting.accoutlist.AccoutActivity;
import com.wenming.weiswift.utils.SharedPreferencesUtil;

/**
 * Created by wenmingvs on 2016/1/7.
 */
public class SettingActivity extends BaseActivity implements SettingActivityView {

    private Context mContext;
    private RelativeLayout mExitLayout;
    private ImageView mBackImageView;
    private RelativeLayout mClearCache;
    private SettingActivityPresent mSettingActivityPresent;
    private RelativeLayout mAccountLayout;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        mContext = this;
        mSettingActivityPresent = new SettingActivityPresentImp(this);
        mExitLayout = (RelativeLayout) findViewById(R.id.exitLayout);
        mBackImageView = (ImageView) findViewById(R.id.toolbar_back);
        mClearCache = (RelativeLayout) findViewById(R.id.clearCache_layout);
        mAccountLayout = (RelativeLayout) findViewById(R.id.accoutlayout);
        mCheckBox = (CheckBox) findViewById(R.id.nightMode_cb);
        initView();
        setUpListener();
    }

    private void initView() {
        boolean isNightMode = (boolean) SharedPreferencesUtil.get(this, "setNightMode", false);
        mCheckBox.setChecked(isNightMode);
    }

    private void setUpListener() {
        mExitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("确定要退出微博？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //mSettingActivityPresent.logout(mContext);
                                ((MyApplication) getApplication()).finishAll();

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingActivityPresent.clearCache(mContext);
            }
        });
        mAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AccoutActivity.class);
                startActivity(intent);
            }
        });
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtil.put(mContext, "setNightMode", true);
                    getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    SharedPreferencesUtil.put(mContext, "setNightMode", false);
                    getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                ((MyApplication) mContext.getApplicationContext()).recreateAll();
            }
        });
    }


}
