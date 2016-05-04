package com.example.arial.dgamedetail.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arialyy.frame.util.AndroidUtils;
import com.arialyy.frame.util.AndroidVersionUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.arial.dgamedetail.BannerEntity;
import com.example.arial.dgamedetail.R;
import com.example.arial.dgamedetail.databinding.FragmentBannerBinding;

import butterknife.InjectView;

/**
 * Created by lyy on 2015/11/18.
 * Banner
 */
@SuppressLint("ValidFragment")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScreenshotFragment extends BaseFragment<FragmentBannerBinding> {
    @InjectView(R.id.img_banner)
    ImageView mBannerImg;
    private BannerEntity mEntity;
    private boolean isCanClick = false;
    private HandlerThread mHt;
    private RotationHandler mHandler;

    private ScreenshotFragment() {

    }

    public static ScreenshotFragment newInstance(BannerEntity entity) {
        ScreenshotFragment fragment = new ScreenshotFragment();
        Bundle b = new Bundle();
        b.putParcelable("entity", entity);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mEntity = getArguments().getParcelable("entity");
        mBannerImg.setScaleType(ImageView.ScaleType.FIT_XY);
        setUpData(mEntity);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCanClick) {
                    return;
                }
            }
        });
        mHt = new HandlerThread("rotation_ht", Process.THREAD_PRIORITY_DEFAULT);
        mHt.start();
        mHandler = new RotationHandler(mHt.getLooper());
    }

    /**
     * 获取ImageView
     */
    public ImageView getBannerImg() {
        return mBannerImg;
    }

    /**
     * 设置能否点击
     */
    public void setCanClick(boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    /**
     * 设置数据
     *
     * @param entity
     */
    private void setUpData(BannerEntity entity) {
        mBannerImg.setTag(null);
        Glide.with(getContext()).load(entity.getImgUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBannerImg);
    }

    /**
     * 设置图片
     */

    public void setDrawable(@DrawableRes int drawable) {
        if (mBannerImg != null) {
            mBannerImg.setImageResource(drawable);
        }
    }

    /**
     * 更新数据
     */
    public void update(BannerEntity entity) {
        mEntity = entity;
        setUpData(entity);
    }

    /**
     * 设置Banner高度
     *
     * @param height
     */
    public void setBannerHeight(int height) {
        if (mBannerImg == null) {
            return;
        }
        ViewGroup.LayoutParams lp = mBannerImg.getLayoutParams();
        lp.height = height;
        mBannerImg.setLayoutParams(lp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHt != null) {
            mHt.quit();
        }
    }

    /**
     * 对图片进行旋转
     *
     * @param rotation 是否旋转
     */
    public void setRotation(boolean rotation) {
        setRotation(rotation, false);
    }

    /**
     * 对图片进行旋转
     *
     * @param useAnim 使用动画
     */
    public void setRotation(boolean rotation, boolean useAnim) {
        mHandler.obtainMessage(rotation ? 0 : 1, useAnim).sendToTarget();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_banner;
    }

    private class RotationHandler extends Handler {
        public RotationHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mBannerImg == null) {
                return;
            }
            final int what = msg.what;
            final boolean useAnim = (boolean) msg.obj;
            mBannerImg.post(new Runnable() {
                @Override
                public void run() {
                    if (what == 0) {
                        rotation(mBannerImg, useAnim);
                    } else if (what == 1) {
                        resumeRotation(mBannerImg, useAnim);
                    }
                }
            });
        }

        /**
         * 旋转
         *
         * @param img
         */
        private void rotation(ImageView img, boolean useAnim) {
            int w = AndroidUtils.getWindowWidth(getActivity()), h = AndroidUtils.getWindowHeight(getActivity());
            int iw = img.getMeasuredWidth(), ih = img.getMeasuredHeight();
            if (useAnim) {
                ObjectAnimator move = ObjectAnimator.ofFloat(img, "translationY", 0, (h - ih) / 2f);
                move.setDuration(400);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(img, "scaleX", 1.0f, (float) h / iw);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(img, "scaleY", 1.0f, (float) w / ih);
                ObjectAnimator rotation = ObjectAnimator.ofFloat(img, "rotation", 0f, 90f);
                AnimatorSet set = new AnimatorSet();
                set.play(scaleX).with(scaleY).with(rotation).with(move);
                set.setDuration(600);
                set.start();
            } else {
                img.setTranslationY((h - ih) / 2f);
                img.setScaleX((float) h / iw);
                img.setScaleY((float) w / ih);
                img.setRotation(90f);
            }
        }

        /**
         * 恢复
         *
         * @param img
         */
        private void resumeRotation(ImageView img, boolean useAnim) {
            int w = AndroidUtils.getWindowWidth(getActivity()), h = AndroidUtils.getWindowHeight(getActivity());
            int iw = img.getMeasuredWidth(), ih = img.getMeasuredHeight();
            if (useAnim) {
                ObjectAnimator move = ObjectAnimator.ofFloat(img, "translationY", (h - ih) / 2f, 0);
                move.setDuration(400);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(img, "scaleX", (float) h / iw, 1.0f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(img, "scaleY", (float) w / ih, 1.0f);
                ObjectAnimator rotation = ObjectAnimator.ofFloat(img, "rotation", 90f, 0f);
                AnimatorSet set = new AnimatorSet();
                set.play(scaleX).with(scaleY).with(rotation).with(move);
                set.setDuration(600);
                set.start();
            } else {
                img.setTranslationY(0f);
                img.setScaleX(1.0f);
                img.setScaleY(1.0f);
                img.setRotation(0f);
            }
        }

        /**
         * 旋转操作
         *
         * @param angle 旋转角度
         */
        @Deprecated
        private void rotation(int angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            Drawable drawable = mBannerImg.getDrawable();
            if (drawable != null) {
                Bitmap bm;
                if (drawable instanceof BitmapDrawable) {
                    bm = ((BitmapDrawable) drawable).getBitmap();
                } else if (drawable instanceof GlideBitmapDrawable) {
                    bm = ((GlideBitmapDrawable) drawable).getBitmap();
                } else {
                    Drawable d = ((TransitionDrawable) drawable).getDrawable(1);
                    bm = ((BitmapDrawable) d).getBitmap();
                }
                int bW = bm.getWidth();
                int bH = bm.getHeight();
                Bitmap resizeBm = Bitmap.createBitmap(bm, 0, 0, bW, bH, matrix, true);
                mBannerImg.setScaleType(ImageView.ScaleType.FIT_XY);
                mBannerImg.setImageBitmap(resizeBm);
                mBannerImg.requestLayout();
            }
        }
    }

}
