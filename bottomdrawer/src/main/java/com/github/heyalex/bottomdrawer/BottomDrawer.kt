package com.github.heyalex.bottomdrawer

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class BottomDrawer : FrameLayout {
    private var container: FrameLayout
    private val rect: Rect = Rect()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {

        container = FrameLayout(context).apply {
            val params =
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.topMargin =
                //TODO make it configurable through attributes
                resources.getDimensionPixelSize(R.dimen.default_bottom_sheet_top_container_margin)

            layoutParams = params
        }
        super.addView(container)
    }

    override fun addView(child: View?) {
        container.addView(child)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rect.set(left, top, right - left, bottom - top)
    }

}