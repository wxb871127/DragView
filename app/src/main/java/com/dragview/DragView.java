package com.dragview;

import android.content.Context;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *  自定义viewGroup 拖拽view
 */
public class DragView extends ViewGroup {

    private View selectViewTitle;//选择区标题
    private View unselectViewTitle;//未选择区标题

    private int itemWidth;// item 宽度
    private int itemHeight;//item 高度
    private float ratio;//item 宽高比
    private int colum;//item 列数
    private int horizontalPadding;//item 之间的横向间距
    private int vertivcalPadding;//item之间的纵向间距

    private List<View> selectViewList;
    private List<View> unselectViewList;

    private DragAdapter dragAdapter;
    private Vibrator vibrator;

    private float dragViewX;
    private float dragViewY;
    private View draggingView;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        colum = 4;
        vertivcalPadding = 10;
        horizontalPadding = 10;
        ratio = 1.2f;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(selectViewList != null && selectViewList.contains(v)) {
                int position = selectViewList.indexOf(v);
                selectViewList.remove(position);
                unselectViewList.add(v);
                requestLayout();
                String str = dragAdapter.removeSelect(position);
                dragAdapter.addUnselect(str);
                v.setOnLongClickListener(null);
            } else if(unselectViewList != null && unselectViewList.contains(v)) {
                int position = unselectViewList.indexOf(v);
                String str = dragAdapter.removeUnselect(position);
                unselectViewList.remove(position);
                dragAdapter.addSelect(str);
                selectViewList.add(v);
                v.setOnLongClickListener(onLongClickListener);
                requestLayout();
            }
        }
    };

    private OnLongClickListener onLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            vibrator.vibrate(200);
            draggingView = v;
            return  true;
        }
    };

    public void setAdapter(DragAdapter adapter){
        this.dragAdapter = adapter;
        View itemView;
        if(dragAdapter.getSelectCount() > 0){
            selectViewList = new ArrayList<>();
            for(int i=0; i<dragAdapter.getSelectCount(); i++) {
                itemView = dragAdapter.getSelectView(i, this);
                addView(itemView, new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT));
                selectViewList.add(itemView);
                itemView.setOnClickListener(listener);
                itemView.setOnLongClickListener(onLongClickListener);
            }
        }

        if(dragAdapter.getUnselectCount() > 0){
            unselectViewList = new ArrayList<>();
            for(int i=0; i<dragAdapter.getUnselectCount(); i++){
                itemView = dragAdapter.getUnselectView(i,this );
                addView(itemView, new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT));
                unselectViewList.add(itemView);
                itemView.setOnClickListener(listener);
            }
        }
        requestLayout();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount() < 2){
            throw new IllegalArgumentException("缺少标题布局");
        }
        selectViewTitle = getChildAt(0);
        unselectViewTitle = getChildAt(1);
    }

    /**
     *   测量viewGroup
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        itemWidth = (width - getPaddingLeft() - getPaddingRight() - (colum+1)*horizontalPadding) / colum;
        itemHeight = (int)(itemWidth/ratio);

        int height = 0;
        for(int i=0; i<getChildCount(); i++){
            View child = getChildAt(i);
            if(child == selectViewTitle || child == unselectViewTitle){//测量标题尺寸
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                height += child.getMeasuredHeight();
            }else{//测量item尺寸
                child.measure(MeasureSpec.makeMeasureSpec(itemWidth,MeasureSpec.EXACTLY ),
                        MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY ));
            }
        }

        if(selectViewList != null && selectViewList.size()>0){
            int selectRow = selectViewList.size() / colum;
            selectRow += (selectViewList.size() % colum == 0) ? 0 : 1;
            height += selectRow * itemHeight + (selectRow+1)*vertivcalPadding;
        }
        if(unselectViewList != null && unselectViewList.size() > 0){
            int unselectRow = unselectViewList.size() / colum;
            unselectRow += (unselectViewList.size() % colum == 0) ? 0: 1;
            height += unselectRow * itemHeight + (unselectRow +1)*vertivcalPadding;
        }

        setMeasuredDimension(width, height + getPaddingTop() + getPaddingBottom());//保存当前viewGroup测量尺寸
    }

    //确定viewGroup中的位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = l + getPaddingLeft();
        int top = t + getPaddingTop();
        int startLeft = left;
        //确定选择标题的位置
        selectViewTitle.layout(startLeft, top, startLeft+ selectViewTitle.getMeasuredWidth() , top+selectViewTitle.getMeasuredHeight());

        top += selectViewTitle.getMeasuredHeight();

        View itemView;
        //确定选择框中的item位置
        if(selectViewList != null && selectViewList.size()>0){
            int row = selectViewList.size() / colum;
            row += (selectViewList.size() % colum == 0) ? 0: 1;
            int position = 0;
            for(int i=0; i<row; i++){
                if(i == 0)
                    top += vertivcalPadding;
                else
                    top +=  itemHeight + vertivcalPadding;
                for(int j=0; j<colum; j++){
                    position = i*colum + j;
                    if(position == dragAdapter.getSelectCount()) {
                        break;
                    }
                    itemView = selectViewList.get(position);
                    left = j*itemWidth + (j+1)*horizontalPadding;
                    if(draggingView == itemView) {
                        int leftMargin = ((MarginLayoutParams)draggingView.getLayoutParams()).leftMargin;
                        int topMargin = ((MarginLayoutParams)draggingView.getLayoutParams()).topMargin;
                        itemView.layout(left + leftMargin,top+topMargin ,left+leftMargin + itemWidth , top+topMargin+itemHeight);
                    }else
                        itemView.layout(left,top ,left + itemWidth , top+itemHeight);
                }
            }
            top += itemHeight + vertivcalPadding;
        }

        unselectViewTitle.layout(startLeft, top,startLeft+unselectViewTitle.getMeasuredWidth(), top+unselectViewTitle.getMeasuredHeight() );

        top += unselectViewTitle.getMeasuredHeight();
        if(unselectViewList != null && unselectViewList.size() > 0){
            int row = unselectViewList.size() / colum;
            row += (unselectViewList.size() % colum == 0) ? 0: 1;
            int position = 0;
            for(int i=0; i<row; i++){
                if(i == 0)
                    top += vertivcalPadding;
                else
                    top += itemHeight + vertivcalPadding;
                for(int j=0; j<colum; j++){
                    position = i*colum + j;
                    if(position == dragAdapter.getUnselectCount())
                        break;
                    itemView = unselectViewList.get(position);
                    left = j*itemWidth + (j+1)*horizontalPadding;
                    itemView.layout(left,top ,left + itemWidth , top+itemHeight);
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                dragViewX = ev.getX();
                dragViewY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(draggingView != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    updateDragViewPosition((int)(ev.getX() - dragViewX), (int)(ev.getY() - dragViewY));
                }
                break;
            case MotionEvent.ACTION_UP:
                default:
                if(draggingView != null){
                    getParent().requestDisallowInterceptTouchEvent(false);
                    draggingView.requestLayout();
                    draggingView = null;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateDragViewPosition(int x, int y){
        MarginLayoutParams layoutParams = (MarginLayoutParams)draggingView.getLayoutParams();
        int left = layoutParams.leftMargin + x;
        int top = layoutParams.topMargin + y;

        //边界处理
        left = Math.max(getPaddingLeft(), left);
        left = Math.min(getWidth() - itemWidth - getPaddingRight(), left);

        Rect rect = new Rect();
        getLocalVisibleRect(rect);

        top = Math.max(rect.top + getPaddingTop(), top);
        top = Math.min(rect.bottom - itemHeight - getPaddingBottom(), top);
//        layoutParams.leftMargin = left;
//        layoutParams.topMargin = top;
//        draggingView.setLayoutParams(layoutParams);
        ((MarginLayoutParams) draggingView.getLayoutParams()).leftMargin = left;
        ((MarginLayoutParams) draggingView.getLayoutParams()).topMargin = top;
        draggingView.requestLayout();
    }
}
