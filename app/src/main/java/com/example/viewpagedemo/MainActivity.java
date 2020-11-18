package com.example.viewpagedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private ImageView indicator; //表示圆点指示器
    private ImageView[] indicators;// 保存四个圆点指示器的数组
    private boolean isContinue = true;
    private ViewPager viewPager;
    private ViewGroup group;//承载四个圆圈的线性布局，它派生自ViewGroup
    private AtomicInteger index = new AtomicInteger();//表示当前显示的图片是哪一张
    private Handler viewHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(msg.what);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.vp_adv);
        group = findViewById(R.id.view_indicators);

        //四张广告图片加入到集合中进行保存
        List<View> listPics = new ArrayList<>();
        ImageView img1 = new ImageView(this);
        img1.setBackgroundResource(R.drawable.img1);
        listPics.add(img1);
        ImageView img2 = new ImageView(this);
        img2.setBackgroundResource(R.drawable.img2);
        listPics.add(img2);
        ImageView img3 = new ImageView(this);
        img3.setBackgroundResource(R.drawable.img3);
        listPics.add(img3);
        ImageView img4 = new ImageView(this);
        img4.setBackgroundResource(R.drawable.img4);
        listPics.add(img4);
        //动态设置四个圆点的属性
        indicators = new ImageView[listPics.size()];
        for (int i = 0; i < indicators.length; i++) {
            indicator = new ImageView(this);
            // 设置四张图片的指定宽高
            indicator.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
            // 每个指示器之间的距离
            indicator.setPadding(5, 5, 5, 5);
            indicators[i] = indicator;
            if (i == 0) {
                indicators[i].setBackgroundResource(R.drawable.color_focus);
            } else {
                indicators[i].setBackgroundResource(R.drawable.green);
            }
            group.addView(indicators[i]);
        }
        //设置viewPager的适配器
        viewPager.setAdapter(new MyPagerAdapter(listPics));
        //设置viewPager的监听器
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index.getAndSet(position);
                for(int i = 0;i<indicators.length;i++){
                    if(i==position){
                        indicators[i].setBackgroundResource(R.drawable.color_focus);
                    }else {
                        indicators[i].setBackgroundResource(R.drawable.green);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 当鼠标按下时停止翻页
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        //使用多线程定时自动切换page
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(isContinue){
                        viewHandler.sendEmptyMessage(index.get());
                        whatOption();
                    }
                }
            }
        }).start();
    }
    private void whatOption(){
        index.incrementAndGet();//将当前位置值加1
        if(index.get()>indicators.length-1){
            index.getAndAdd(-4);
        }
        try {
            // 每隔两秒启动
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MyPagerAdapter extends PagerAdapter {
    private List<View> viewList;

    //构造方法
    public MyPagerAdapter(List<View> viewList){
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(viewList.get(position),0);
            return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }
}