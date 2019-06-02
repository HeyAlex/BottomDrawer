package com.github.heyalex.bottomdrawerexample

import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.github.heyalex.bottomdrawer.BottomDrawerFragment

class GoogleTaskExampleDialog : BottomDrawerFragment() {

    override fun onStart() {
        super.onStart()
        addHandleView(PlainHandleView(context!!).apply {
            val widthHandle =
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_width)
            val heightHandle =
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_height)
            val params =
                FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.CENTER_HORIZONTAL)

            params.topMargin =
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_handle_height)

            layoutParams = params
        })
    }

    override fun getContainer(): Int {
        return R.layout.google_task_example_layout
    }
}
