package com.example.arial.dgamedetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lyy on 2015/11/18.
 * Banner实体
 */
public class BannerEntity implements Parcelable {
    /**
     * 图片链接
     */
    private String imgUrl;
    /**
     * 广告链接
     */
    private String url;
    /**
     * 跳转类型
     * 1：系统浏览器；
     * 2：内页浏览器；
     * 3：内页游戏
     * 4：内页文章
     */
    private int jump;
    /**
     * 标题
     */
    private String title;
    /**
     * 详情
     */
    private String detail;

    private int appId;

    public int getJump() {
        return jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public BannerEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imgUrl);
        dest.writeString(this.url);
        dest.writeInt(this.jump);
        dest.writeString(this.title);
        dest.writeString(this.detail);
        dest.writeInt(this.appId);
    }

    protected BannerEntity(Parcel in) {
        this.imgUrl = in.readString();
        this.url = in.readString();
        this.jump = in.readInt();
        this.title = in.readString();
        this.detail = in.readString();
        this.appId = in.readInt();
    }

    public static final Creator<BannerEntity> CREATOR = new Creator<BannerEntity>() {
        public BannerEntity createFromParcel(Parcel source) {
            return new BannerEntity(source);
        }

        public BannerEntity[] newArray(int size) {
            return new BannerEntity[size];
        }
    };
}
