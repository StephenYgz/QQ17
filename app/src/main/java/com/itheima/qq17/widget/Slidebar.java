package com.itheima.qq17.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.util.DensityUtil;
import com.itheima.qq17.R;
import com.itheima.qq17.adapter.SlideBarAdapter;
import com.itheima.qq17.utils.StringUtils;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-02 14:37
 * 网址：http://www.itheima.com
 */

public class Slidebar extends View {

    private static final String[] SECTIONS = {"搜","A","B","C","D","E","F","G","H","I","J","K","L","M",
            "N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private Paint mPaint;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private float mX;
    private float mAvgHeight;
    private TextView mFloatView;
    private RecyclerView mRecyclerView;

    public Slidebar(Context context) {
        this(context,null);
    }

    public Slidebar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }


    public Slidebar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //设置背景
                setBackgroundResource(R.drawable.slidebar_bg);
                //显示FloatView，并且获取到点击到的Section，然后根据Section定位RecyclerView
                showFloatViewAndScrollRecyclerView(event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                //不断更新FloatView，并且获取到Move到的Section，然后根据Section定位RecyclerView
                showFloatViewAndScrollRecyclerView(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                //隐藏FloatView
                mFloatView.setVisibility(INVISIBLE);
                //背景设成透明的
                setBackgroundColor(Color.TRANSPARENT);
                break;
        }
        return true;
    }

    private void showFloatViewAndScrollRecyclerView(float height) {
        if (mFloatView==null){
            ViewGroup parent = (ViewGroup) getParent();
            mFloatView = (TextView) parent.findViewById(R.id.tv_float);
            mRecyclerView = (RecyclerView) parent.findViewById(R.id.recyclerView);
        }
        mFloatView.setVisibility(VISIBLE);
        int index = (int) (height/mAvgHeight);
        if (index<0){
            index = 0;
        }else if (index>SECTIONS.length-1){
            index = SECTIONS.length - 1;
        }
        String section = SECTIONS[index];
        mFloatView.setText(section);
        //获取RecyclerView中的所有数据
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof SlideBarAdapter){
            SlideBarAdapter slideBarAdapter = (SlideBarAdapter) adapter;
            List<String> dataList = slideBarAdapter.getData();
            for (int i = 0; i < dataList.size(); i++) {
                String contact = dataList.get(i);
                //判断当前遍历到的联系人的首字母是否等于当前FloatView上的字母，如果等于了则将RecyclerView定位到脚标i
                String initial = StringUtils.getInitial(contact);
                if (initial.equals(section)){
                    mRecyclerView.smoothScrollToPosition(i);
                    return;
                }
            }
        }

    }

    public Slidebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //单位是像素的，但是需求是10sp，因此需要将10sp转换为像素
        int sp2px = DensityUtil.sp2px(context, 10);
        mPaint.setTextSize(sp2px);
        //将文本绘制到指定位置的中心位置
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.parseColor("#9c9c9c"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mX = mMeasuredWidth/2;
        mMeasuredHeight = getMeasuredHeight();
        mAvgHeight = mMeasuredHeight/SECTIONS.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i=0;i<SECTIONS.length;i++){
            canvas.drawText(SECTIONS[i],mX,mAvgHeight*(i+1),mPaint);
        }
    }
}
