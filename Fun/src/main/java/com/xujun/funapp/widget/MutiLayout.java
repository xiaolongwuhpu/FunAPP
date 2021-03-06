package com.xujun.funapp.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xujun.funapp.R;

import static com.xujun.funapp.R.id.page_bt;

/**
 * @ explain:
 * @ author：xujun on 2016/10/27 20:07
 * @ email：gdutxiaoxu@163.com
 */
public class MutiLayout extends FrameLayout {

    public static final int STATE_NOONE = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_EMPTY = 3;

    private ImageView mIvEmpty;
    private TextView mTvEmpty;

    private Button mBtnError;
    private TextView mTvError;
    private ImageView mIvError;

    private int mEmptyIconId;
    private String mEmptyText;
    private Context mContext;
    private String mErrorText;
    private int mErrorIconId;

    private View loadingView;// 加载中的界面
    private View errorView;// 错误界面
    private View emptyView;// 空界面
    private View successView;// 加载成功的界面

    int state = STATE_NOONE;

    OnClickListener mRetryListener;

    public MutiLayout(Context context) {
        this(context, null, 0);
    }

    public MutiLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MutiLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttr(attrs);
        initView();

    }

    private void initView() {
      /*  emptyView = createEmptyView();
        errorView = createErrorView();
        loadingView = createLoadingView();
        removeParent(emptyView);
        removeParent(errorView);
        removeParent(loadingView);
        add(emptyView);
        add(errorView);
        add(loadingView);*/
        show(LoadResult.loading);

    }

    private void add(View view) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(view, layoutParams);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.empty);
        mEmptyText = typedArray.getString(R.styleable.empty_emptyText);
        mEmptyIconId = typedArray.getResourceId(R.styleable.empty_emptyIcon, -1);
        typedArray.recycle();
        //  错误界面的
        TypedArray b = mContext.obtainStyledAttributes(attrs, R.styleable.error);
        mErrorText = b.getString(R.styleable.error_errorText);
        mErrorIconId = b.getResourceId(R.styleable.error_errorIcon, -1);
        b.recycle();


    }

    @TargetApi(21)
    public MutiLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public enum LoadResult {
        noone(0), loading(1), error(2), empty(3);

        int value;

        LoadResult(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public void setOnRetryListener(OnClickListener onClickListener) {
        mRetryListener = onClickListener;
    }

    public void hide() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.setVisibility(View.GONE);
        }
    }

    // 根据不同的状态显示不同的界面
    public void show(LoadResult loadResult) {
        state = loadResult.getValue();
        switch (state) {
            case STATE_LOADING:
                loadingView = createLoadingView();
                break;
            case STATE_ERROR:
                errorView = createErrorView();
                break;
            case STATE_EMPTY:
                emptyView = createEmptyView();
                break;
            case STATE_NOONE:
                hide();
                break;
            default:
                break;

        }
        if (loadingView != null) {
            loadingView.setVisibility(state == STATE_LOADING ? View.VISIBLE : View.GONE);
        }

        if (errorView != null) {
            errorView.setVisibility(state == STATE_ERROR ? View.VISIBLE : View.GONE);
        }


        if (emptyView != null) {
            emptyView.setVisibility(state == STATE_EMPTY ? View.VISIBLE : View.GONE);
        }


    }

    /* 创建了空的界面 */
    private View createEmptyView() {
        if (emptyView == null) {
            emptyView = View.inflate(mContext, R.layout.loadpage_empty, null);
            mTvEmpty = (TextView) emptyView.findViewById(R.id.tv_empty);
            mIvEmpty = (ImageView) emptyView.findViewById(R.id.iv_empty);
            if (mEmptyText!=null) {
                mTvEmpty.setText(mEmptyText);
            }


            if (mEmptyIconId != -1) {
                mIvEmpty.setImageResource(mEmptyIconId);
            }
            this.addView(emptyView);
        }

        return emptyView;
    }

    /* 创建了错误界面 */
    private View createErrorView() {
        if (errorView == null) {
            errorView = View.inflate(mContext, R.layout.loadpage_error, null);
            mBtnError = (Button) errorView.findViewById(page_bt);
            mIvError = (ImageView) errorView.findViewById(R.id.page_iv);
            if (mEmptyText!=null) {
                mBtnError.setText(mErrorText);
            }


            if (mErrorIconId != -1) {
                mIvError.setImageResource(mErrorIconId);
            }

            mBtnError.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mRetryListener != null) {
                        mRetryListener.onClick(v);
                    }
                    //设置两秒之后才能重新点击
                    mBtnError.setEnabled(false);
                    mBtnError.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBtnError.setEnabled(true);
                        }
                    }, 2000);
                }
            });
            addView(errorView);
        }

        return errorView;
    }

    /* 创建加载中的界面 */
    private View createLoadingView() {
        if (loadingView == null) {
            loadingView = View.inflate(mContext, R.layout.loadpage_loading, null);
            this.addView(loadingView);
        }


        return loadingView;
    }

    public void removeParent(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

    }


}
