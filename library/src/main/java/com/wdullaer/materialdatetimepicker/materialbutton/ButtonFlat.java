package com.wdullaer.materialdatetimepicker.materialbutton;

/**
 * Button that follows the Material Design guidelines
 *
 * Based on the Material Design Library from https://github.com/navasmdc/MaterialDesignLibrary/
 *
 * Created by wdullaer on 04.04.15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.Utils;

public class ButtonFlat extends Button {

    TextView textButton;

    public ButtonFlat(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    protected void setDefaultProperties(){
        minHeight = 36;
        minWidth = 88;
        rippleSize = 3;
        // Min size
        setMinimumHeight(Utils.dpToPx(minHeight, getResources()));
        setMinimumWidth(Utils.dpToPx(minWidth, getResources()));
        setBackgroundResource(android.R.color.transparent);
    }

    @Override
    protected void setAttributes(AttributeSet attrs) {
        // Get the text color
        int textColorResource = attrs.getAttributeResourceValue(ANDROIDXML, "textColor", -1);
        int textColor;
        if(textColorResource == -1) {
            String colorString = attrs.getAttributeValue(ANDROIDXML, "textColor");
            textColor = Color.parseColor(colorString!=null ? colorString : "#1E88E5");
        }
        else {
            textColor = getResources().getColor(textColorResource);
        }

        // Set text button
        String text;
        int textResource = attrs.getAttributeResourceValue(ANDROIDXML,"text",-1);
        if(textResource != -1){
            text = getResources().getString(textResource);
        }else{
            text = attrs.getAttributeValue(ANDROIDXML,"text");
        }
        if(text != null){
            textButton = new TextView(getContext());
            textButton.setText(text.toUpperCase());
            textButton.setTextColor(textColor);
            textButton.setTypeface(null, Typeface.BOLD);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            textButton.setLayoutParams(params);
            addView(textButton);
        }
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,"background",-1);
        if(bacgroundColor != -1){
            setBackgroundColor(getResources().getColor(bacgroundColor));
        }else{
            // Color by hexadecimal
            // Color by hexadecimal
            background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1)
                setBackgroundColor(background);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(makePressColor());
            canvas.drawCircle(x, y, radius, paint);
            if(radius > getHeight()/rippleSize)
                radius += rippleSpeed;
            if(radius >= getWidth()){
                x = -1;
                y = -1;
                radius = getHeight()/rippleSize;
                if(onClickListener != null&& clickAfterRipple)
                    onClickListener.onClick(this);
            }
            invalidate();
        }

    }

    /**
     * Make a dark color to ripple effect
     * @return int representation of the ripple effect color
     */
    @Override
    protected int makePressColor(){
        return Color.parseColor("#88DDDDDD");
    }

    public void setText(String text){
        textButton.setText(text.toUpperCase());
    }

    // Set color of background
    public void setBackgroundColor(int color){
        backgroundColor = color;
        if(isEnabled())
            beforeBackground = backgroundColor;
        textButton.setTextColor(color);
    }

    @Override
    public TextView getTextView() {
        return textButton;
    }

    public String getText(){
        return textButton.getText().toString();
    }

}
