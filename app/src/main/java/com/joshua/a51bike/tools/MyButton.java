package com.joshua.a51bike.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joshua.a51bike.R;

/**
 * class description here
 *  imageView 在右边，textView居于两边的relativeLayout
 * @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-03-08
 */
public class MyButton extends RelativeLayout{
    private static String TAG = "MyButton";
    private TextView mLeftText;
    private TextView mRightText;
    private ImageView mRightImage;
    
    private String leftText;
    private int leftTextColor;
    private int leftTextSize;

    private String rightText;
    private int rightTextColor;
    private int rightTextSize;

    private Drawable rightDrawable;
    private int margin = 20;
    private LayoutParams mLeftTextLP;
    private LinearLayout.LayoutParams mRightTextLP,mRightImageLP;
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyButton);
        leftText = typedArray.getText(R.styleable.MyButton_leftText).toString();

        leftTextColor = typedArray.getColor(R.styleable.MyButton_leftTextColor,
                getResources().getColor(R.color.gray));

         leftTextSize = typedArray.getDimensionPixelSize(R.styleable.MyButton_leftTextSize,15);
        rightText = typedArray.getText(R.styleable.MyButton_rightText).toString();
        rightTextColor = typedArray.getColor(R.styleable.MyButton_rightTextColor,
                getResources().getColor(R.color.gray));
        rightTextSize = typedArray.getDimensionPixelSize(R.styleable.MyButton_rightTextSize,15);
        rightDrawable = typedArray.getDrawable(R.styleable.MyButton_rightImage);
        typedArray.recycle();//回收
        mLeftText = new TextView(context);
        mRightText = new TextView(context);
        mRightImage = new ImageView(context);

        setViewContent();
        mLeftTextLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        mLeftTextLP.setMargins(margin,0,margin,0);
        initRightTextAndImage(context);

        LayoutParams mLinearLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        mLinearLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
        addView(mLeftText,mLeftTextLP);
        addView(linearLayout,mLinearLP);
        setBackgroundColor(getResources().getColor(R.color.white));
    }

    /**
     * 设置view的内容
     */
    private void setViewContent() {
        mLeftText.setText(leftText);
        mLeftText.setTextSize(15);
        mLeftText.setTextColor(leftTextColor);
        mLeftText.setGravity(Gravity.CENTER_VERTICAL);

        mRightText.setText(rightText);
        mRightText.setTextSize(15);
        mRightText.setTextColor(rightTextColor);
        mRightText.setGravity(Gravity.CENTER_VERTICAL);

        mRightImage.setImageDrawable(rightDrawable);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "MyButton: 3");
    }
   private  LinearLayout linearLayout;

    /**
     * 将右边的TextView 和 ImageView 添加到linearLayout中
     * @param context
     */
    public void initRightTextAndImage(Context context){
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(mRightText);
        linearLayout.addView(mRightImage);

        mRightTextLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        mRightImageLP = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
//        int margin = UiUtils.px2dip(20);
        mRightTextLP.setMargins(0,0,margin,0);
        mRightImageLP.setMargins(margin,0,margin,0);
        mRightImage.setLayoutParams(mRightImageLP);
        mRightText.setLayoutParams(mRightTextLP);
    }

    /**
     * 设置右边的text
     * @param con
     */
    public void setRightText(String con){
        if(mRightText != null)
            mRightText.setText(con);
    }
}
