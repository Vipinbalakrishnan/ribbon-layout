package com.vcb.viewgroup.ribbonlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * A ViewGroup with the shape of a horizontal ribbon.
 * Extending from LinearLayout and set the default orientation as vertical.
 */
public class RibbonLayout extends LinearLayout {
    /** The depth of the inward curve. */
    private static final int DEFAULT_DEPTH = 6;
    /** The right padding to apply based on the depth.
     * General calculation is 6 dp depth for 10 dp right padding.
     * Now height is not taking in to consideration. */
    private static final float DEPTH_TO_PADDING_RATIO = 1.66f;//10/6 = 1.6666
    /** The default ribbon color hex code. */
    private static final String DEFAULT_RIBBON_COLOR = "#3963c2f9";
    /** The orientation of the children in the ribbon view */
    private int childOrientation;
    /** The resolved integer color value. */
    private int ribbonColor;
    /** The depth by which the end of the ribbon should curve inward.
     * 6 dp depth for 10 dp right padding and 17 dp height of the view. */
    private int curveDepth;
    /** Whether the padding right need to be automatically calculate and set. */
    private boolean automatePadding;
    /** The paint object to draw the ribbon background. */
    private Paint paint;

    /**
     * Constructor
     * @param context - the context
     */
    public RibbonLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     * @param context - the context
     * @param attrs - the attribute set
     */
    public RibbonLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     * @param context - the context
     * @param attrs - the attribute set
     * @param defStyleAttr - the style attribute
     */
    public RibbonLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RibbonLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr);
    }

    /**
     * Returns the current ribbon color
     * @return the current ribbon color
     */
    public int getRibbonColor() {
        return ribbonColor;
    }

    /**
     * Sets the ribbon color. It should be the resolved color value
     * @param ribbonColor the resolved color value to be set
     */
    private void setRibbonColor(int ribbonColor) {
        this.ribbonColor = ribbonColor;
    }

    /**
     * Returns the depth of the curve
     * @return the depth of the curve
     */
    public int getCurveDepth() {
        return curveDepth;
    }

    /**
     * Sets the depth of the curve
     * @param curveDepth the depth of the curve to set
     */
    private void setCurveDepth(int curveDepth) {
        this.curveDepth = curveDepth;
    }

    /**
     * Returns the automate state of the padding
     * @return the automate state of the padding
     */
    public boolean isAutomatePadding() {
        return automatePadding;
    }

    /**
     * Sets the automate state of teh padding.
     * If set to true, it will calculate the right padding depending up on the depth of the curve.
     * @param automatePadding the automate state of padding to set
     */
    private void setAutomatePadding(boolean automatePadding) {
        this.automatePadding = automatePadding;
    }

    /**
     * Returns the child orientation
     * @return the child orientation
     */
    public int getChildOrientation() {
        return childOrientation;
    }

    /**
     * Sets the child orientation
     * @param childOrientation the child orientation
     */
    private void setChildOrientation(int childOrientation) {
        this.childOrientation = childOrientation;
    }

    /**
     * Sets the layout orientation of the LinearLayout
     */
    private void setLayoutOrientation() {
        setOrientation(getChildOrientation() > 0 ? LinearLayout.VERTICAL : HORIZONTAL);
    }

    /**
     * Returns default depth of the curve in pixel
     * @return the depth of the curve in pixel
     */
    private int getDefaultDepthInPixel() {
        return dpToPixel(DEFAULT_DEPTH);
    }

    /**
     * Returns default ribbon color that is resolved
     * @return the resolved color value
     */
    private int getDefaultRibbonColor() {
        return Color.parseColor(DEFAULT_RIBBON_COLOR);
    }

    /**
     * Returns default automate padding state
     * @return the default automate padding state
     */
    private boolean getDefaultAutomatePaddingState() {
        return true;
    }

    /**
     * Returns default orientation of the layout
     * @return default orientation of the layout
     */
    private int getDefaultOrientation() {
        return LinearLayout.VERTICAL;
    }

    /**
     * Construct or refresh the paint object with the current parameters
     */
    private void constructPaintObject() {
        paint = getPaintObject(getRibbonColor());
    }

    /**
     * Initializes the variable
     */
    private void initializeVariables() {
        ribbonColor = getDefaultRibbonColor();
        curveDepth = getDefaultDepthInPixel();
        automatePadding = getDefaultAutomatePaddingState();
        childOrientation = getDefaultOrientation();
    }

    /**
     * Initializes the view
     * @param context the context
     * @param attrs the attribute set
     * @param defStyleAttr the style attribute
     */
    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        initializeVariables();
        readAttributes(context, attrs, defStyleAttr);
        setLayoutOrientation();
        setPaddingIfPaddingAutomated();
        constructPaintObject();
        // inorder to call onDraw by default, we need to set this as false
        setWillNotDraw(false);
    }

    /**
     * If 'automatePaddingEnd' property set to true (default = true), will apply the right padding.
     * Or else padding need to handle by the developer.
     *
     * Sets the right/ end padding based on the depth of the curve not to overflow the content.
     * We will add the current provided right/ end padding with the calculated padding from code.
     */
    private void setPaddingIfPaddingAutomated() {
        if(isAutomatePadding()) {
            int paddingRight = (int) (getCurveDepth() * DEPTH_TO_PADDING_RATIO);
            // Adding the current padding set from xml and offset padding needed for the curve shape
            paddingRight += getPaddingEnd();
            setPadding(getPaddingStart(), getPaddingTop(), paddingRight, getBottom());
        }
    }

    /**
     * Method for reading the custom attributes
     * @param context - the context
     * @param attrs - the attribute set
     * @param defStyleAttr - the style attribute
     */
    private void readAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if(null == attrs || null == context) {
            return;
        }

        TypedArray attrArray = null;
        try {
            attrArray = context.obtainStyledAttributes(attrs, R.styleable.RibbonLayout, defStyleAttr, 0);
            int colorValue = attrArray.getColor(R.styleable.RibbonLayout_ribbonColor, getDefaultRibbonColor());
            int curveDepth = attrArray.getDimensionPixelOffset(R.styleable.RibbonLayout_curveDepth, getDefaultDepthInPixel());
            boolean automatePadding = attrArray.getBoolean(R.styleable.RibbonLayout_automatePaddingEnd, getDefaultAutomatePaddingState());
            int childOrientation = attrArray.getInt(R.styleable.RibbonLayout_childOrientation, getDefaultOrientation());
            setRibbonColor(colorValue);
            setCurveDepth(curveDepth);
            setAutomatePadding(automatePadding);
            setChildOrientation(childOrientation);
//            log(" colorValue = " + colorValue + " curveDepth = " + curveDepth + " automatePadding = " + automatePadding);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(null != attrArray) {
                attrArray.recycle();
            }
        }
    }

    /**
     * Treats the given value in dp and converts to corresponding pixel value.
     * @param value the value in dp
     * @return the value in pixel
     */
    private int dpToPixel(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    /**
     * Initializes the paint variable with the color
     * @param color the color integer
     * @return the paint object
     */
    private Paint getPaintObject(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(5);
        paint.setStrokeJoin(Paint.Join.BEVEL);// Line
//        paint.setStrokeJoin(Paint.Join.MITER);// Sharp edge
        paint.setStrokeCap(Paint.Cap.BUTT);
//        paint.setStrokeCap(Paint.Cap.SQUARE);
        return paint;
    }

    /**
     * Initializes the paint variable with the color
     * @param color the color hex string
     * @return the paint object
     */
    private Paint getPaintObject(String color) {
        return getPaintObject(Color.parseColor(color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawConcavePentagon(canvas);
    }

    /**
     * Drawing the ribbon shaped pentagon with the concave projection
     * @param canvas the canvas to draw
     */
    private void drawConcavePentagon(Canvas canvas) {
        /** As we are drawing shape as the background, we don't need to consider the padding.
         * With self relative location. So the starting point will be (0,0). */
        int leftTopX = 0;
        int leftTopY = 0;
        int rightTopX = leftTopX + getWidth();
        int rightTopY = leftTopY;

        int leftBottomX = leftTopX;
        int leftBottomY = leftTopY + getHeight();
        int rightBottomX = rightTopX;
        int rightBottomY = leftBottomY;

        int concavePointX = rightTopX - getCurveDepth();
        int concavePointY = (leftTopY + leftBottomY)/ 2;

        Path path = new Path();
        path.moveTo(leftTopX, leftTopY);// move to top left
        path.lineTo(rightTopX, rightTopY);// line to top right
        path.lineTo(concavePointX, concavePointY);// line to center concave point
        path.lineTo(rightBottomX, rightBottomY);// line to bottom right
        path.lineTo(leftBottomX, leftBottomY);// line to bottom left
        path.lineTo(leftTopX, leftTopY);// line to top left
        path.close();
        canvas.drawPath(path, paint);
    }
}
