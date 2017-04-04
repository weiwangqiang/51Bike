package com.joshua.a51bike.customview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.joshua.a51bike.R;

/**
 * class description here
 *
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-17
 */
public class SchoolProgress extends ViewGroup {
    private String TAG = "SchoolProgress";
    private Context context;
    public SchoolProgress(Context context) {
        super(context);
        this.context = context;
    }

    public SchoolProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(0,0,0,0);
        this.context = context;

    }

    public SchoolProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    private int okImageRadius;
    private static final float RADIUS_DEFAULT = 2/5f;
    private int childWith,paragraphWith;
    private int parentWith,parentHeight;
    private int childPadding;
    private int position = 1;
    /*测量view*/
    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        builMenu();
        MeaSureParent(widthMeasureSpec, heightMeasureSpec);
        MeaSureChild();
        Log.i(TAG, "onMeasure: with "+parentWith+" height "+parentHeight);
    }
    public void setPoistion(int p){
        this.position = p;
        Log.i(TAG, "setPoistion: ");
        RebuilMenu();
        drawBackGround(new Canvas());
    }
    public void builMenu(){
        for(int i = 1; i <=position;i++){
            TextView tv = new TextView(context);
            tv.setText(i+"");
            tv.setTextColor(getResources().getColor(R.color.baseColor));
            tv.setBackgroundResource(R.drawable.progress_ok);
            tv.setGravity(Gravity.CENTER);
            addView(tv);
        }
        for(int i = position+1; i <=4;i++){
            TextView tv = new TextView(context);
            tv.setText(i+"");
            tv.setTextColor(getResources().getColor(R.color.baseColor));
//            tv.setBackgroundResource(R.drawable.progress_ok);
            tv.setGravity(Gravity.CENTER);
            addView(tv);
        }
    }
    public void RebuilMenu(){
        for(int i = 0; i <getChildCount();i++){
            final View view = getChildAt(i);
            if(view != null)
                ((TextView)view).setBackgroundResource(R.drawable.circular_white);
        }
        for(int i = 0; i <=position-1;i++){
            final View view = getChildAt(i);
            if(view != null)
              ((TextView)view).setBackgroundResource(R.drawable.progress_ok);
        }
    }
    private void MeaSureChild(){

        okImageRadius =(int)( parentHeight * RADIUS_DEFAULT);
        Log.i(TAG, "MeaSureChild: okImageRadius " + okImageRadius);
        childPadding = (parentHeight>>1) -okImageRadius;
        Log.i(TAG, "MeaSureChild: childPadding "+childPadding);
        childWith = parentWith -(childPadding + okImageRadius )* 2 ;
        paragraphWith = childWith /6;

        final int child = getChildCount();
        Log.i(TAG, "MeaSureChild: childSize "+child);
        int childMode = MeasureSpec.EXACTLY;
        for(int i = 0;i<child;i++){
            final View view = getChildAt(i);
            if(view.getVisibility() == View.GONE)
                continue;
           int makeMeasureSpec = MeasureSpec.makeMeasureSpec(okImageRadius *2,childMode);
            view.measure(makeMeasureSpec,makeMeasureSpec);
        }
    }
    private void MeaSureParent(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        Log.i(TAG, "MeaSureParent: width "+width);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.i(TAG, "MeaSureParent: height "+height);
        int heighMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode != MeasureSpec.EXACTLY
                || heighMode != MeasureSpec.EXACTLY){
            parentWith = getSuggestedMinimumWidth();
            parentWith = (parentWith == 0) ? getDefaultWidth() : parentWith;
            parentHeight = getSuggestedMinimumHeight();
            parentHeight = (parentHeight == 0) ? getDefaultWidth() : parentHeight;

        }else
        {
            parentWith = width;
            parentHeight = height;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),R.drawable.progress_bg, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        parentHeight = parentWith * imageHeight / imageWidth;
        setMeasuredDimension(parentWith,parentHeight);

    }
    /*获取屏幕宽度*/
    public int getDefaultWidth(){
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout: ");
//        drawBackGround();
        setBackgroundResource(R.drawable.progress_bg);
        final int childCount = getChildCount();
        Log.i(TAG, "onLayout: chilSize "+childCount);
        for(int i = 0;i <childCount;i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() == View.GONE){
                continue;
            }
            if(i==0){
                child.layout(childPadding  ,childPadding,
                        childPadding + okImageRadius * 2,childPadding + okImageRadius * 2);
            }
            else
            {
                child.layout(childPadding+paragraphWith *i*2  ,childPadding,
                        childPadding+paragraphWith *i*2+ 2*okImageRadius,
                        childPadding + okImageRadius * 2);
            }
            Log.i(TAG, "onLayout: chil "+i+" layout is "+child.getLeft()+" top "+child.getTop()+" right "+
                    child.getRight() +" bottom "+ child.getBottom());
        }
    }

    /**
     * Draw any foreground content for this view.
     * <p>
     * <p>Foreground content may consist of scroll bars, a {@link #setForeground foreground}
     * drawable or other view-specific decorations. The foreground is drawn on top of the
     * primary view content.</p>
     *
     * @param canvas canvas to draw into
     */
    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);

    }

    private static final float RECT_HEIGHT = 2/9f;
    public void drawBackGround(Canvas canvas){
        Log.i(TAG, "drawBackGround: ");
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.baseColor));
        int i = position -1;

        int height  = (int)(parentHeight * RECT_HEIGHT);
        int left = childPadding;
        int top = (parentHeight -height) /2;
        int right  = left+ okImageRadius +  paragraphWith *i*2+paragraphWith;
        if(i == 3){
            Log.i(TAG, "drawBackGround: parent width is "+parentWith );
            Log.i(TAG, "drawBackGround: i == 3 "+right+" childPadding "+childPadding);
            right = parentWith - 2 * childPadding;
            Log.i(TAG, "drawBackGround: "+right);
        }
        int bottom = top + height;
        canvas.drawRect(left,top,right,bottom,paint);
//        requestLayout();//
    }
    /**
     * Manually render this view (and all of its children) to the given Canvas.
     * The view must have already done a full layout before this function is
     * called.  When implementing a view, implement
     * {@link #onDraw(Canvas)} instead of overriding this method.
     * If you do need to override this method, call the superclass version.
     *
     * @param canvas The Canvas to which the View is rendered.
     */

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.i(TAG, "draw: ");
    }
    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);

    }
}
