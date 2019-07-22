package com.github.heyalex.handle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.github.heyalex.bottomdrawer.R
import com.github.heyalex.bottomdrawer.TranslationUpdater

class PlainHandleView : View, TranslationUpdater {

    @FloatRange(from = 0.0, to = 1.0)
    private var currentOffset = 1f
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
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(tempRect, thickness, thickness, paint)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    override fun updateTranslation(@FloatRange(from = 0.0, to = 1.0) value: Float) {
        if (value != currentOffset) {
            currentOffset = value
            val offset = (width.toFloat() * currentOffset) / 2
            tempRect.set(0 + offset, 0f, width - offset, height.toFloat())
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        superState?.let {
            val customViewSavedState = PullHandleViewSavedState(superState)
            customViewSavedState.offset = currentOffset
            return customViewSavedState
        }
        return superState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val customViewSavedState = state as PullHandleViewSavedState
        currentOffset = customViewSavedState.offset
        super.onRestoreInstanceState(customViewSavedState.superState)
    }

    private class PullHandleViewSavedState : BaseSavedState {

        internal var offset: Float = 0f

        constructor(superState: Parcelable) : super(superState)

        private constructor(source: Parcel) : super(source) {
            offset = source.readFloat()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(offset)
        }

        companion object CREATOR : Parcelable.Creator<PullHandleViewSavedState> {
            override fun createFromParcel(source: Parcel): PullHandleViewSavedState {
                return PullHandleViewSavedState(source)
            }

            override fun newArray(size: Int): Array<PullHandleViewSavedState?> {
                return arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int {
            return 0
        }
    }
}