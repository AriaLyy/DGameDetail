package com.example.arial.dgamedetail;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.AndroidVersionUtil;
import com.arialyy.frame.util.DensityUtils;
import com.arialyy.frame.util.show.L;
import com.example.arial.dgamedetail.adapter.SimpleViewPagerAdapter;
import com.example.arial.dgamedetail.databinding.ActivityMainBinding;
import com.example.arial.dgamedetail.fragment.ListViewFragment;
import com.example.arial.dgamedetail.fragment.ScreenshotFragment;
import com.example.arial.dgamedetail.fragment.ScrollViewFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @InjectView(R.id.content)
    LinearLayout mContent;
    @InjectView(R.id.bar_bg)
    View mBarBg;
    @InjectView(R.id.back)
    TextView mBack;
    @InjectView(R.id.img_vp)
    ViewPager mImgVP;
    @InjectView(R.id.content_vp)
    ViewPager mContentVp;
    @InjectView(R.id.indicator)
    CircleIndicator mIndicator;
    @InjectView(R.id.tab)
    TabLayout mTab;
    @InjectView(R.id.state_bar_temp)
    View mTemp;
    @InjectView(R.id.tool_bar)
    RelativeLayout mToolBar;
    private int STATE_TOP = 0x01;   //顶部状态
    private int STATE_CENTER = 0x02;    //中间状态
    private int STATE_BOTTOM = 0x03;    //底部状态
    private int STATE_OTHER = 0x04;    //未知状态

    private int mCurrentState = STATE_TOP;  //当前状态
    private int mRawY;
    private int mStateBarH, mBarH, mHeadH, mScreenH, mNBarH;
    private int mTopL, mCenterL, mBottomL;
    private int mShotVpPosition = 0;
    private float a;
    private boolean rotated = false;
    private GestureDetectorCompat mDetector;
    private View mRootView;
    private float mOldY = 0, mOldX;
    private boolean isScrollTop = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // 设置状态栏可用
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        init();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    private void init() {
        initParams();
        initWidget();
        //增加内容页面高度
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mRootView.post(new Runnable() {
                    @Override
                    public void run() {
                        int sh = AndroidVersionUtil.hasKitKat() ? mStateBarH : 0;
                        mRootView.getLayoutParams().height = mRootView.getHeight() + mHeadH + sh;
                    }
                });
            }
        });
    }

    /**
     * 初始化参数
     */
    private void initParams() {
        mDetector = new GestureDetectorCompat(this, new SimpleGestureAction());
        mStateBarH = AndroidUtils.getStatusBarHeight(this);
        mHeadH = (int) getResources().getDimension(R.dimen.game_detail_head_height);
        mBarH = (int) getResources().getDimension(R.dimen.tool_bar_height);
        mScreenH = AndroidUtils.getScreenParams(this)[1];
        mNBarH = AndroidUtils.getNavigationBarHeight(this);
        mTopL = -mHeadH + mBarH;
        mCenterL = DensityUtils.dp2px(150);
        mBottomL = mScreenH - mStateBarH - mNBarH - mHeadH + mBarH;
        mRootView = findViewById(android.R.id.content);
        //处理4.4之后状态栏
        if (AndroidVersionUtil.hasKitKat()) {
            mTemp.setVisibility(View.VISIBLE);
            mToolBar.setTranslationY(mStateBarH);
            mTemp.getLayoutParams().height = mStateBarH;
        } else {
            mTemp.setVisibility(View.GONE);
        }
    }

    private void initWidget() {
        mBack.setCompoundDrawablePadding(-DensityUtils.dp2px(5));
        mBack.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_bar_transparent));
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        int size = DensityUtils.dp2px(40);
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_left_back);
        assert drawable != null;
        drawable.setBounds(0, 0, size, size);
        mBack.setCompoundDrawables(drawable, null, null, null);
        setupGameShotVp(mImgVP);
        setContentVp(mContentVp);
        toCenter();
    }

    /**
     * 子滑动控件是否滑动到顶部，子Fragment的滑动控件必须调用这个方法！！
     *
     * @param view
     * @param top
     */
    public void setTopState(View view, boolean top) {
        isScrollTop = top;
//        L.d(TAG, top + "");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mRawY <= mTopL) {
            mCurrentState = STATE_TOP;
        } else if (mTopL < mRawY && mRawY <= mCenterL + (mBarH >> 1)) {
            mCurrentState = STATE_CENTER;
        } else if (mCenterL + (mBarH >> 1) <= mRawY && mRawY < mBottomL + mBarH) {
            mCurrentState = STATE_BOTTOM;
        } else {
            mCurrentState = STATE_OTHER;
        }
        boolean isTop = mRawY == mTopL;

        if (Math.abs(ev.getX() - mOldX) >= 0 && Math.abs(ev.getY() - mOldY) < 300 && isTop) {
            mOldX = ev.getX();
            return super.dispatchTouchEvent(ev);
        }

        float t = Math.abs(ev.getY() - mOldY);
        //处于顶部时的事件过滤区域
        if (isTop && (ev.getY() < mTopL || t < 10)) {
            return super.dispatchTouchEvent(ev);
        }

        //处于中间时的事件过滤区域
        if (mCurrentState == STATE_CENTER && ev.getY() < (mCenterL + mBarH) && mRawY >= mCenterL) {
            return super.dispatchTouchEvent(ev);
        }

        //处于底部时的事件过滤区域
        if (mCurrentState == STATE_BOTTOM && ev.getY() < (mBottomL + mBarH) && mRawY >= mBottomL) {
            return super.dispatchTouchEvent(ev);
        }

        boolean isUp = ev.getY() - mOldY < 0;

        if (isTop && mCurrentState == STATE_TOP) {
            mOldY = (int) ev.getY();
            if (isScrollTop && !isUp) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    return super.dispatchTouchEvent(ev);
                }
                return onTouchEvent(ev);
            } else {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    return super.dispatchTouchEvent(ev);
                }
                return super.dispatchTouchEvent(ev);
            }
        }
        return onTouchEvent(ev);
    }

    public void onClick(View view) {
        Toast.makeText(this, "按钮点击", Toast.LENGTH_LONG).show();
    }

    /**
     * 对Banner图片进行旋转
     */
    private void rotationBanner(boolean toBottom) {
        if (toBottom && !rotated) {
            rotated = true;
        } else if (!toBottom && rotated) {
            rotated = false;
        } else {
            return;
        }
        SimpleViewPagerAdapter adapter = (SimpleViewPagerAdapter) mImgVP.getAdapter();
        if (adapter != null) {
            for (int i = 0, count = adapter.getCount(); i < count; i++) {
                ScreenshotFragment fragment = (ScreenshotFragment) adapter.getItem(i);
                if (fragment != null) {
                    fragment.setRotation(rotated, mShotVpPosition == i);
                }
            }
        }
        System.gc();
    }

    /**
     * 设置不同的分页内容
     */
    private void setContentVp(ViewPager vp) {
        SimpleViewPagerAdapter adapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(ScrollViewFragment.getInstance(), "ScrollView");
        adapter.addFrag(ListViewFragment.getInstance(), "ListView");
        vp.setAdapter(adapter);
        mTab.setupWithViewPager(vp);
        vp.setOffscreenPageLimit(2);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isScrollTop = true;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化游戏截图ViewPager
     *
     * @return
     */
    private void setupGameShotVp(final ViewPager viewPager) {
        SimpleViewPagerAdapter adapter = new SimpleViewPagerAdapter(getSupportFragmentManager());
        List<BannerEntity> data = getBannerData();
        for (BannerEntity entity : data) {
            adapter.addFrag(ScreenshotFragment.newInstance(entity), "");
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(data.size());
        mIndicator.setViewPager(viewPager);
//        mIndicator.onPageSelected(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mShotVpPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //设置Banner图片高度
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        SimpleViewPagerAdapter adapter = (SimpleViewPagerAdapter) mImgVP.getAdapter();
                        int h = (int) getResources().getDimension(R.dimen.game_detail_head_img_vp_height);
                        for (int i = 0, count = adapter.getCount(); i < count; i++) {
                            ScreenshotFragment fragment = (ScreenshotFragment) adapter.getItem(i);
                            if (fragment != null) {
                                fragment.setBannerHeight(h);
                            }
                        }
                    }
                });
            }
        });
    }

    private List<BannerEntity> getBannerData() {
        String[] imgs = getResources().getStringArray(R.array.img_shot);
        List<BannerEntity> list = new ArrayList<>();
        for (String img : imgs) {
            BannerEntity entity = new BannerEntity();
            entity.setImgUrl(img);
            list.add(entity);
        }
        return list;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mRawY <= -mStateBarH) {
                    toTop();
                } else if ((-mStateBarH < mRawY && mRawY <= mCenterL + (mBarH << 1))) {
                    toCenter();
                } else if (mCenterL + (mBarH << 1) <= mRawY) {
                    toBottom();
                }
                return true;
            default:
                if (0 <= a && a <= 1.0f) {
                    mBarBg.setAlpha(a);
                    mTemp.setAlpha(a);
                }
                mDetector.onTouchEvent(event);
                return super.onTouchEvent(event);
        }
    }

    /**
     * 回到顶部
     */
    private void toTop() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mContent, "translationY", mRawY, mTopL);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBarBg, "alpha", a, 1.0f);
        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(mTemp, "alpha", a, 1.0f);
        set.setDuration(500);
        set.play(animator).with(alpha).with(alpha1);
        set.start();
        mRawY = mTopL;
        a = 1.0f;
        mBarBg.setAlpha(a);
        mTemp.setAlpha(a);
    }

    /**
     * 回到中间
     */
    private void toCenter() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mContent, "translationY", mRawY, mCenterL);
        animator.setDuration(500);
        animator.start();
        mRawY = mCenterL;
        a = 0.0f;
        mBarBg.setAlpha(a);
        mTemp.setAlpha(a);
        mCurrentState = STATE_CENTER;
        rotationBanner(false);
    }

    /**
     * 到底部
     */
    private void toBottom() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mContent, "translationY", mRawY, mBottomL);
        animator.setDuration(500);
        animator.start();
        mRawY = mBottomL;
        a = 0.0f;
        mBarBg.setAlpha(a);
        mTemp.setAlpha(a);
        mCurrentState = STATE_BOTTOM;
        rotationBanner(true);
    }

    class SimpleGestureAction extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mRawY <= mTopL && distanceY > 0) {
                mRawY = mTopL;
                return true;
            }

            if (mRawY >= mBottomL && distanceY < 0) {
                mRawY = mBottomL;
                return true;
            }
            mRawY -= distanceY;
            if (mRawY < mCenterL) {
                a += distanceY < 0 ? -0.03 : 0.03;
                if (a < 0.0f) {
                    a = 0.0f;
                } else if (a > 1.0f) {
                    a = 1.0f;
                }
            } else {
                a = 0.0f;
            }
            if (mRawY <= mTopL) {
                mRawY = mTopL;
                a = 1.0f;
                mBarBg.setAlpha(a);
                mTemp.setAlpha(a);
            }
            mContent.setTranslationY(mRawY);
            if (mRawY >= mCenterL + mBarH) {
                rotationBanner(true);
            }
            return true;
        }
    }
}
