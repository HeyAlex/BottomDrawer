package com.github.heyalex.handle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.github.heyalex.bottomdrawer.R
import com.github.heyalex.bottomdrawer.TranslationUpdater

class PullHandleView : View, TranslationUpdater {

    @FloatRange(from = 0.0, to = 1.0)
    private var currentOffset = 0f

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
        val radius = thickness / 2

        val halfWidth = width / 2
        val halfHeight = height / 2

        val leftX = radius
        val rightX = width - radius
        val leftRightY = halfHeight - ((halfHeight - radius) * currentOffset)

        val centerX = halfWidth.toFloat()
        val centerY = halfHeight + ((halfHeight - radius) * currentOffset)

        canvas.drawCircle(leftX, leftRightY, radius, paint)
        canvas.drawLine(leftX, leftRightY, centerX, centerY, paint)

        canvas.drawCircle(centerX, centerY, radius, paint)

        canvas.drawLine(centerX, centerY, rightX, leftRightY, paint)
        canvas.drawCircle(rightX, leftRightY, radius, paint)
    }


    override fun updateTranslation(value: Float) {
        if (value != currentOffset) {
            currentOffset = value
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        superState?.let {
            val customViewSavedState = HandleViewSavedState(superState)
            customViewSavedState.offset = currentOffset
            return customViewSavedState
        }
        return superState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val customViewSavedState = state as HandleViewSavedState
        currentOffset = customViewSavedState.offset
        super.onRestoreInstanceState(customViewSavedState.superState)
    }

    private class HandleViewSavedState : BaseSavedState {

        internal var offset: Float = 0f

        constructor(superState: Parcelable) : super(superState)

        private constructor(source: Parcel) : super(source) {
            offset = source.readFloat()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(offset)
        }

        companion object CREATOR : Parcelable.Creator<HandleViewSavedState> {
            override fun createFromParcel(source: Parcel): HandleViewSavedState {
                return HandleViewSavedState(source)
            }

            override fun newArray(size: Int): Array<HandleViewSavedState?> {
                return arrayOfNulls(size)
            }
        }

        override fun describeContents(): Int {
            return 0
        }
    }
}