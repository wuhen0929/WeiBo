package com.wenming.weiswift.ui.login.fragment.home;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wenming.weiswift.R;
import com.wenming.weiswift.entity.Status;
import com.wenming.weiswift.mvp.presenter.HomeFragmentPresent;
import com.wenming.weiswift.mvp.presenter.WeiBoArrowPresent2;
import com.wenming.weiswift.mvp.presenter.imp.HomeFragmentPresentImp;
import com.wenming.weiswift.mvp.view.HomeFragmentView;
import com.wenming.weiswift.ui.common.BarManager;
import com.wenming.weiswift.ui.common.dialog.ArrowDialog;
import com.wenming.weiswift.ui.common.login.Constants;
import com.wenming.weiswift.ui.login.fragment.home.groupwindow.GroupPopWindow;
import com.wenming.weiswift.ui.login.fragment.home.groupwindow.IGroupItemClick;
import com.wenming.weiswift.ui.login.fragment.home.weiboitem.HomeHeadView;
import com.wenming.weiswift.ui.login.fragment.home.weiboitem.TimelineArrowWindow;
import com.wenming.weiswift.ui.login.fragment.home.weiboitem.WeiboAdapter;
import com.wenming.weiswift.utils.DensityUtil;
import com.wenming.weiswift.utils.ScreenUtil;
import com.wenming.weiswift.widget.endlessrecyclerview.EndlessRecyclerOnScrollListener;
import com.wenming.weiswift.widget.endlessrecyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.wenming.weiswift.widget.endlessrecyclerview.RecyclerViewUtils;
import com.wenming.weiswift.widget.endlessrecyclerview.utils.RecyclerViewStateUtils;
import com.wenming.weiswift.widget.endlessrecyclerview.weight.LoadingFooter;

import java.util.ArrayList;

/**
 * Created by wenmingvs on 16/4/27.
 */
public class HomeFragment extends Fragment implements HomeFragmentView, ArrowDialog.onDialogButtonClick {

    private ArrayList<Status> mDatas;
    public Context mContext;
    public Activity mActivity;
    public View mView;
    private LinearLayout mGroup;
    public RecyclerView mRecyclerView;
    public TextView mUserNameTextView;
    public TextView mErrorMessage;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public WeiboAdapter mAdapter;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
    private HomeFragmentPresent mHomePresent;
    private long mCurrentGroup = Constants.GROUP_TYPE_ALL;
    private LinearLayout mEmptyLayout;
    private GroupPopWindow mPopWindow;

    private boolean mComeFromAccoutActivity;
    private String mUserName;


    /**
     * 顶部导航栏
     */
    private RelativeLayout mTopBar;

    /**
     * 手指滑动距离多少个像素点的距离，才隐藏bar
     */
    private static final int HIDE_THRESHOLD = 80;
    /**
     * 记录手指滑动的距离
     */
    private int mScrolledDistance = 0;
    /**
     * 记录bar是否显示或者隐藏
     */
    private boolean mControlsVisible = true;

    private onButtonBarListener mOnBottonBarListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = getContext();
        mHomePresent = new HomeFragmentPresentImp(this);
        mComeFromAccoutActivity = getArguments().getBoolean("comeFromAccoutActivity");

        mView = inflater.inflate(R.layout.mainfragment_layout, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.weiboRecyclerView);
        mTopBar = (RelativeLayout) mView.findViewById(R.id.toolbar_home);
        mGroup = (LinearLayout) mView.findViewById(R.id.group);
        mUserNameTextView = (TextView) mView.findViewById(R.id.name);
        mEmptyLayout = (LinearLayout) mView.findViewById(R.id.emptydeault_layout);
        mErrorMessage = (TextView) mView.findViewById(R.id.errorMessage);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh_widget);
        initRecyclerView();
        initRefreshLayout();
        initGroupWindows();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mHomePresent.refreshUserName(mContext);
                if (mComeFromAccoutActivity) {
                    mHomePresent.firstLoadData(mContext, true);
                } else {
                    mHomePresent.firstLoadData(mContext, mActivity.getIntent().getBooleanExtra("fisrtstart", false));
                }
            }
        });
        return mView;
    }

    @Override
    public void onDestroyView() {
        mHomePresent.cancelTimer();
        if (mPopWindow != null) {
            mPopWindow.onDestory();
        }
        super.onDestroyView();
    }

    public HomeFragment() {
    }

    /**
     * 静态工厂方法需要一个int型的值来初始化fragment的参数，
     * 然后返回新的fragment到调用者
     */
    public static HomeFragment newInstance(boolean comeFromAccoutActivity) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putBoolean("comeFromAccoutActivity", comeFromAccoutActivity);
        homeFragment.setArguments(args);
        return homeFragment;
    }


    public void initRecyclerView() {
        mDatas = new ArrayList<>();
        mAdapter = new WeiboAdapter(mDatas, mContext) {
            @Override
            public void arrowClick(Status status, int position) {
//                TimelineArrowWindow popupWindow = new TimelineArrowWindow(mContext, mDatas.get(position), mAdapter, position, mUserNameTextView.getText().toString());
//                popupWindow.showAtLocation(mRecyclerView, Gravity.CENTER, 0, 0);

                ArrowDialog arrowDialog = new TimelineArrowWindow.Builder(mContext, mDatas.get(position), mAdapter, position, mUserNameTextView.getText().toString())
                        .setCanceledOnTouchOutside(true)
                        .setCancelable(true)
                        .create();
                int width = ScreenUtil.getScreenWidth(mContext) - DensityUtil.dp2px(mContext, 80);
                arrowDialog.setOnDialogButtonClick(HomeFragment.this);
                arrowDialog.show();
                arrowDialog.getWindow().setLayout(width, (ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        };
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
        RecyclerViewUtils.setHeaderView(mRecyclerView, new HomeHeadView(mContext));
    }


    private void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHomePresent.pullToRefreshData(mCurrentGroup, mContext);
            }
        });
        mSwipeRefreshLayout.setProgressViewOffset(false, DensityUtil.dp2px(mContext, 10), DensityUtil.dp2px(mContext, 10 + 65));
    }

    private void initGroupWindows() {
        mGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect rect = new Rect();
                getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                int statusBarHeight = rect.top;
                mPopWindow = GroupPopWindow.getInstance(mContext, ScreenUtil.getScreenWidth(mContext) * 3 / 5, ScreenUtil.getScreenHeight(mContext) * 2 / 3);
                mPopWindow.showAtLocation(mUserNameTextView, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, mUserNameTextView.getHeight() + statusBarHeight + DensityUtil.dp2px(mContext, 8));
                mPopWindow.setOnGroupItemClickListener(new IGroupItemClick() {
                    @Override
                    public void onGroupItemClick(int position, long groupId, String groupName) {
                        mCurrentGroup = groupId;
                        if (groupId != Constants.GROUP_TYPE_ALL) {
                            setGroupName(groupName);
                        } else {
                            setGroupName(mUserName);
                        }
                        mPopWindow.dismiss();
                        mHomePresent.pullToRefreshData(groupId, mContext);
                    }
                });
                if (mPopWindow.isShowing()) {
                    mPopWindow.scrollToSelectIndex();
                }
            }
        });
    }

    /**
     * 把列表滑动到顶部，refreshDrata为true的话，会同时获取更新的数据
     *
     * @param refreshData
     */
    @Override
    public void scrollToTop(boolean refreshData) {
        mRecyclerView.scrollToPosition(0);
        if (refreshData) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mHomePresent.pullToRefreshData(mCurrentGroup, mContext);
                }
            });
        }
    }

    @Override
    public void showRecyclerView() {
        if (mSwipeRefreshLayout.getVisibility() != View.VISIBLE) {
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideRecyclerView() {
        if (mSwipeRefreshLayout.getVisibility() != View.GONE) {
            mSwipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyBackground(String text) {
        if (mEmptyLayout.getVisibility() != View.VISIBLE) {
            mEmptyLayout.setVisibility(View.VISIBLE);
            mErrorMessage.setText(text);
        }
    }

    @Override
    public void hideEmptyBackground() {
        if (mEmptyLayout.getVisibility() != View.GONE) {
            mEmptyLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void popWindowsDestory() {
        if (mPopWindow != null) {
            mPopWindow.onDestory();
        }
    }


    @Override
    public void updateListView(ArrayList<Status> statuselist) {
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mDatas = statuselist;
        mAdapter.setData(statuselist);
        mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingIcon() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideLoadingIcon() {

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void showLoadFooterView() {
        RecyclerViewStateUtils.setFooterViewState(mActivity, mRecyclerView, mDatas.size(), LoadingFooter.State.Loading, null);
    }

    @Override
    public void hideFooterView() {
        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
    }

    @Override
    public void showEndFooterView() {
        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.TheEnd);
    }

    @Override
    public void showErrorFooterView() {
        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.NetWorkError);
    }


    @Override
    public void setGroupName(String userName) {
        mUserNameTextView.setText(userName);
        if (mGroup.getVisibility() != View.VISIBLE) {
            mGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置顶部导航栏的用户名
     *
     * @param userName
     */
    @Override
    public void setUserName(String userName) {
        mUserName = userName;
    }


    public EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);
            if (mDatas != null && mDatas.size() > 0) {
                showLoadFooterView();
                mHomePresent.requestMoreData(mCurrentGroup, mContext);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //手指向上滑动
            if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
                if (mOnBottonBarListener != null) {
                    hideTopBar();
                    mOnBottonBarListener.hideButtonBar();
                }
                mControlsVisible = false;
                mScrolledDistance = 0;
            }
            //手指向下滑动
            else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
                if (mOnBottonBarListener != null) {
                    showTopBar();
                    mOnBottonBarListener.showButtonBar();
                }
                mControlsVisible = true;
                mScrolledDistance = 0;
            }
            if ((mControlsVisible && dy > 0) || (!mControlsVisible && dy < 0)) {
                mScrolledDistance += dy;
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showTopBar();
            mOnBottonBarListener.showButtonBar();
            mControlsVisible = true;
        }
    }

    /**
     * 隐藏底部导航栏
     */
    public void hideTopBar() {
        BarManager barManager = new BarManager(mContext);
        barManager.hideTopBar(mTopBar);
    }


    /**
     * 显示顶部导航栏
     */
    public void showTopBar() {
        BarManager barManager = new BarManager(mContext);
        barManager.showTopBar(mTopBar);
    }

    /**
     * 设置实现
     *
     * @param onBarListener
     */
    public void setOnBarListener(onButtonBarListener onBarListener) {
        this.mOnBottonBarListener = onBarListener;
    }

    @Override
    public void onDeleteButtonClick(Status status, int position, WeiBoArrowPresent2 weiBoArrowPresent2, TextView deleteTextView) {

    }

    @Override
    public void onFriendShipButtonClick(Status status, int position, WeiBoArrowPresent2 weiBoArrowPresent2, TextView friendShipTextView) {

    }

    /**
     * 设置收藏按钮要执行的事件
     *
     * @param status
     * @param position
     * @param weiBoArrowPresent2
     * @param favoriteTextView
     */
    @Override
    public void onFavoriteButtonClick(final Status status, final int position, final WeiBoArrowPresent2 weiBoArrowPresent2, TextView favoriteTextView) {
        if (status.favorited) {
            favoriteTextView.setText("取消收藏");
            weiBoArrowPresent2.cancalFavorite(position, status, mContext, false);
        } else {
            favoriteTextView.setText("收藏");
            weiBoArrowPresent2.createFavorite(status, mContext);
        }
    }


    /**
     * 因为ButotnBar的布局并不在fragment中，而是在MainActivity中，所有隐藏和显示底部导航栏的工作要交给MainActivity去做
     */
    public interface onButtonBarListener {
        void showButtonBar();

        void hideButtonBar();
    }


}
