package com.wsc.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wsc on 2016/3/24.
 */
public class BannerView extends LinearLayout {
    //推荐页banner组件
    private LinearLayout bannerContainer;
    private LinearLayout indicatorGroup;
    private ViewPager bannerPager;
    private BannerAdapter viewPagerAdapter;
    private ImageView[] indicator;
    private ImageView point;
    private int indicatorNum = 0;
    private boolean isBannerNumChange = true;
    private String bannerEvent = "Banner";
    private Context mContext;

    public BannerView(Context context) {
        super(context);
        mContext = context;
        initView();
        update(getImageSource());
    }

    void initView() {
        //初始化banner组件
        LayoutInflater.from(mContext).inflate(R.layout.banner, this);
        bannerContainer = (LinearLayout) findViewById(R.id.banner_container);
        indicatorGroup = (LinearLayout) findViewById(R.id.indicator_group);
        indicatorGroup.setOrientation(LinearLayout.HORIZONTAL);
        indicatorGroup.setGravity(Gravity.CENTER);
        viewPagerAdapter = new BannerAdapter();
        //创建一个viewpager
        bannerPager = new ViewPager(mContext);
        bannerPager.setId(R.id.viewpager);
        bannerPager.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        bannerPager.setAdapter(viewPagerAdapter);
        bannerPager.setOnPageChangeListener(new PageChangeListener());
        bannerContainer.addView(bannerPager);
    }

    public void update(List<Ad> ads) {
        initCirclePoint(ads);
        if (isBannerNumChange) {
            loopPlay();
        }
        viewPagerAdapter.setData(ads);
        viewPagerAdapter.notifyDataSetChanged();
    }

    /**
     * 获取本地广告图片
     * @return
     */
    List<Ad> getImageSource(){
        List<Ad> picList = new ArrayList<Ad>();
        Ad a1 = new Ad(R.drawable.ad1,"随手拍萌娃","840张图片");
        Ad a2 = new Ad(R.drawable.ad2,"随手拍风景","999张图片");
        Ad a3 = new Ad(R.drawable.ad3,"一人食小时光","788张图片");
        picList.add(a1);
        picList.add(a2);
        picList.add(a3);
        return picList;
    }

    /**
     * BannerAdapter
     **/
    public class BannerAdapter extends PagerAdapter {
        private List<Ad> mDataList = new ArrayList<>();

        public BannerAdapter() {
            super();
        }

        public void setData(List<Ad> data) {
            mDataList.clear();
            mDataList.addAll(data);
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(mContext, R.layout.banner_item_view, null);
            ImageView topicImage = (ImageView) view.findViewById(R.id.banner_image);
            TextView topicName = (TextView) view.findViewById(R.id.topic_name);
            TextView topicSize = (TextView) view.findViewById(R.id.topic_size);

            final Ad ad = mDataList.get(position);
            topicSize.setText(ad.getSubtitle());
            topicName.setText(ad.getTitle());
            topicImage.setImageResource(ad.getImg());
            container.addView(view);
            return view;
        }

    }

    /**
     * 设置间隔和循环播放
     **/
    private void loopPlay() {
        isBannerNumChange = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                viewHandler.sendEmptyMessage(indicatorNum);
                indicatorNum = indicatorNum + 1;
                if (indicatorNum >= indicator.length) {
                    indicatorNum = 0;
                }
            }
        }, 200, 5000);

    }

    /**
     * 每隔固定时间切换banner图片
     **/
    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            bannerPager.setCurrentItem(msg.what);
            //行为分析
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(bannerEvent, msg.what + "");

            //重新设置原点布局集合
            for (int i = 0; i < indicator.length; i++) {
                if (msg.what < indicator.length) {
                    indicator[msg.what]
                            .setBackgroundResource(R.drawable.banner_selected);
                    if (msg.what != i) {
                        indicator[i]
                                .setBackgroundResource(R.drawable.banner_unselected);
                    }
                }
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 初始化point
     **/
    private void initCirclePoint(List<Ad> picList) {
        int bannerNum = picList.size();
        indicator = new ImageView[bannerNum];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(convertDipToPixel(4), 0, convertDipToPixel(4), 0);//设置point的间隔
        params.height = (int) convertDipToPixel(7);
        params.width = (int) convertDipToPixel(7);
        //广告栏的小圆点图标
        if (bannerNum > 1) {
            for (int i = 0; i < bannerNum; i++) {
                point = new ImageView(mContext);
                point.setLayoutParams(params);
                indicator[i] = point;
                //将小圆点放入到布局中
                indicatorGroup.setPadding(0, 0, 0, 0);
                indicatorGroup.addView(indicator[i]);
                //初始值, 默认第0个选中
                if (i == 0) {
                    indicator[i].setBackgroundResource(R.drawable.banner_selected);
                } else {
                    indicator[i]
                            .setBackgroundResource(R.drawable.banner_unselected);
                }
            }
        } else {
            isBannerNumChange = false;// 只有一张图无需循环
        }
    }

    /**
     * ViewPager 页面改变监听器
     */
    private final class PageChangeListener implements ViewPager.OnPageChangeListener {

        /**
         * 页面滚动状态发生改变的时候触发
         */
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        /**
         * 页面滚动的时候触发
         */
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        /**
         * 页面选中的时候触发
         */
        @Override
        public void onPageSelected(int arg0) {
            //重新设置原点布局集合
            for (int i = 0; i < indicator.length; i++) {
                indicator[arg0]
                        .setBackgroundResource(R.drawable.banner_selected);
                if (arg0 != i) {
                    indicator[i]
                            .setBackgroundResource(R.drawable.banner_unselected);
                }
            }
        }
    }


    /**
     * @param dipValue dip value
     * @return The distance in pixel
     */
    public int convertDipToPixel(float dipValue) {
        // Get the screen's density scale
        final float scale = mContext.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dipValue * scale + 0.5f);
    }
}
