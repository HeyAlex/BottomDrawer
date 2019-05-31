package com.github.heyalex.bottomdrawerexample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.github.heyalex.bottomdrawer.TranslationUpdater

class PlainHandleView : View, TranslationUpdater {

    @FloatRange(from = 0.0, to = 1.0)
    private var currentOffset = 0f
    private var rect = RectF()

    private var paint = Paint()
    private var thickness =
        resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_thickness).toFloat()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.colorAccent)
            strokeWidth = thickness
            flags = Paint.ANTI_ALIAS_FLAG
        }
    }

    override fun onDraw(canvas: Canvas) {
        Log.d("rect", rect.toShortString())
        canvas.drawRoundRect(rect, thickness, thickness, paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    override fun updateTranslation(@FloatRange(from = 0.0, to = 1.0) value: Float) {
        Log.d("PlainHandleView", value.toString())
        if (value != currentOffset) {
            currentOffset = value
            invalidate()
        }
    }
}