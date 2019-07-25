package com.github.heyalex.handle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import com.github.heyalex.bottomdrawer.R
import com.github.heyalex.bottomdrawer.TranslationUpdater

class PlainHandleView : View, TranslationUpdater {

    @FloatRange(from = 0.0, to = 1.0)
    private var currentOffset = 0f
    private var rect = RectF()
    private var tempRect: RectF = RectF()

    private var paint = Paint()
    private var thickness =
        resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_thickness).toFloat()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {
        paint.apply {
            color = ContextCompat.getColor(context, R.color.bottom_drawer_handle_view_color)
            strokeWidth = thickness
            flags = Paint.ANTI_ALIAS_FLAG
        }
        isSaveEnabled = true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(tempRect, thickness, thickness, paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    override fun updateTranslation(@FloatRange(from = 0.0, to = 1.0) value: Float) {
        currentOffset = value
        val offset = (width.toFloat() * currentOffset) / 2
        tempRect.set(0 + offset, 0f, width - offset, height.toFloat())
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        superState?.let {
            val customViewSavedState = PlainHandleViewSavedState(superState)
            customViewSavedState.offset = currentOffset
            return customViewSavedState
        }
        return superState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        Log.d("show_handle", "restore")
        val customViewSavedState = state as PlainHandleViewSavedState
        currentOffset = customViewSavedState.offset
        val offset = (width.toFloat() * currentOffset) / 2
        tempRect.set(0 + offset, 0f, width - offset, height.toFloat())
        super.onRestoreInstanceState(customViewSavedState.superState)
    }

    private class PlainHandleViewSavedState : BaseSavedState {

        internal var offset: Float = 0f

        constructor(superState: Parcelable) : super(superState)

        private constructor(source: Parcel) : super(source) {
            offset = source.readFloat()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(offset)
        }

        companion object CREATOR : Parcelable.Creator<PlainHandleViewSavedState> {
            override fun createFromParcel(source: Parcel): PlainHandleViewSavedState {
                return PlainHandleViewSavedState(source)
            }

            override fun newArray(size: Int): Array<PlainHandleViewSavedState?> {
                return arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int {
            return 0
        }
    }
}