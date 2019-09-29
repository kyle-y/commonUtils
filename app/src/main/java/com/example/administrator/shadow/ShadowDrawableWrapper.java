package com.example.administrator.shadow;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.graphics.drawable.DrawableWrapper;

@SuppressLint("RestrictedApi")
public class ShadowDrawableWrapper extends DrawableWrapper {

    final Paint cornerShadowPaint;//角阴影
    final Paint edgeShadowPaint;//边界阴影
    final RectF contentBounds;//内容边界
    float cornerRadius;//圆角

    float shadowSize;//阴影大小

    Path cornerShadowPath;//圆角阴影路径
    private boolean dirty = true;//标识是否因为变动在draw前重新进行计算

    private int[] shadowColor;

    /**
     *
     * @param content
     * @param radius
     * @param shadowSize
     * @param startColor
     * @param endColor
     */
    public ShadowDrawableWrapper(Drawable content, float radius, float shadowSize, @ColorInt int startColor, @ColorInt int endColor) {
        super(content);
        this.cornerShadowPaint = new Paint();
        this.cornerShadowPaint.setStyle(Paint.Style.FILL);
        this.cornerRadius = radius;
        this.contentBounds = new RectF();
        this.edgeShadowPaint = new Paint(this.cornerShadowPaint);
        this.edgeShadowPaint.setAntiAlias(false);
        shadowColor = new int[]{startColor, endColor};
        this.setShadowSize(shadowSize);
    }

    /**
     * 变为偶数
     */
    private static int toEven(float value) {
        int i = Math.round(value);
        return i % 2 == 1 ? i - 1 : i;
    }

    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        this.cornerShadowPaint.setAlpha(alpha);
        this.edgeShadowPaint.setAlpha(alpha);
    }

    protected void onBoundsChange(Rect bounds) {
        this.dirty = true;
    }

    private void setShadowSize(float shadowSize) {
        if (shadowSize >= 0.0F) {
            shadowSize = (float) toEven(shadowSize);
            this.shadowSize = shadowSize;
            this.dirty = true;
            this.invalidateSelf();//重新计算，并重绘
        } else {
            throw new IllegalArgumentException("invalid shadow size");
        }
    }

    public boolean getPadding(Rect padding) {
        padding.set((int) Math.ceil(shadowSize),
                (int) Math.ceil(shadowSize),
                (int) Math.ceil(shadowSize),
                (int) Math.ceil(shadowSize));
        return true;
    }

    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setCornerRadius(float radius) {
        radius = (float) Math.round(radius);
        if (this.cornerRadius != radius) {
            this.cornerRadius = radius;
            this.dirty = true;
            this.invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        if (this.dirty) {
            this.buildComponents(this.getBounds());
            this.dirty = false;
        }

        this.drawShadow(canvas);
        super.draw(canvas);
    }


    private void drawShadow(Canvas canvas) {
        int rotateSaved = canvas.save();

        //阴影偏移
        float shadowOffset = this.cornerRadius;
        //是否有必要画横向的边界（本身圆弧占用两个shadowOffset）
        boolean drawHorizontalEdges = this.contentBounds.width() - 2.0F * shadowOffset > 0.0F;
        boolean drawVerticalEdges = this.contentBounds.height() - 2.0F * shadowOffset > 0.0F;

        //画左上角
        int saved = canvas.save();
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(shadowOffset + shadowSize, 0, getBounds().right - (shadowOffset + shadowSize), shadowSize, this.edgeShadowPaint);
        }
        //画右下角
        canvas.restoreToCount(saved);
        saved = canvas.save();
        canvas.translate(getBounds().right, getBounds().bottom);
        canvas.rotate(180.0F);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(shadowOffset + shadowSize, 0, getBounds().right - (shadowOffset + shadowSize), shadowSize, this.edgeShadowPaint);
        }
        //画左下角
        canvas.restoreToCount(saved);
        saved = canvas.save();
        canvas.translate(0, getBounds().bottom);
        canvas.rotate(270.0F);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(shadowOffset + shadowSize, 0, getBounds().right - (shadowOffset + shadowSize), shadowSize, this.edgeShadowPaint);
        }
        //画右上角
        canvas.restoreToCount(saved);
        saved = canvas.save();
        canvas.translate(getBounds().right, 0);
        canvas.rotate(90.0F);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(shadowOffset + shadowSize, 0, getBounds().right - (shadowOffset + shadowSize), shadowSize, this.edgeShadowPaint);
        }

        canvas.restoreToCount(saved);
        canvas.restoreToCount(rotateSaved);
    }

    private void buildShadowCorners() {
        //圆弧外半径
        float shadowRadius = shadowSize + cornerRadius;
        //外侧圆弧区域
        RectF outerBounds = new RectF(0, 0, shadowRadius * 2, shadowRadius * 2);
        //内侧圆弧区域
        RectF innerBounds = new RectF(outerBounds);
        innerBounds.inset(shadowSize, shadowSize);

        if (this.cornerShadowPath == null) {
            this.cornerShadowPath = new Path();
        } else {
            this.cornerShadowPath.reset();
        }
        //不相交的部分（即为外面的那个环）
        this.cornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        //构建一个左上角四分之一的弧
        this.cornerShadowPath.moveTo(shadowSize, shadowRadius);
        this.cornerShadowPath.rLineTo(-this.shadowSize, 0.0F);
        this.cornerShadowPath.arcTo(outerBounds, 180.0F, 90.0F, false);
        //会自动连到下一个圆弧的起点
        this.cornerShadowPath.arcTo(innerBounds, 270.0F, -90.0F, false);
        this.cornerShadowPath.close();

        if (shadowRadius > 0.0F) {
            //环状渐变   1.圆心X坐标2.Y坐标3.半径 4.颜色数组 5.相对位置数组，可为null 6.渲染器平铺模式
            float midRatio = cornerRadius / shadowRadius;
            this.cornerShadowPaint.setShader(new RadialGradient(shadowRadius, shadowRadius, shadowRadius, shadowColor, new float[]{midRatio, 1.0F}, Shader.TileMode.CLAMP));
        }
        //边界阴影渐变
        this.edgeShadowPaint.setShader(new LinearGradient(shadowRadius, shadowSize, shadowRadius, 0f, shadowColor, new float[]{0.0F, 1.0F}, Shader.TileMode.CLAMP));
        this.edgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        //内容区域缩小：rawShadowSize
        this.contentBounds.set((float) bounds.left + this.shadowSize, (float) bounds.top + shadowSize, (float) bounds.right - this.shadowSize, (float) bounds.bottom - shadowSize);
        //被包装的drawable边界和内容区域相同
        this.getWrappedDrawable().setBounds((int) this.contentBounds.left, (int) this.contentBounds.top, (int) this.contentBounds.right, (int) this.contentBounds.bottom);

        this.buildShadowCorners();
    }

    public float getCornerRadius() {
        return this.cornerRadius;
    }
}