package com.github.heyalex.bottomdrawerexample

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.github.heyalex.handle.PlainHandleView
import com.github.heyalex.handle.PullHandleView

class CustomExampleDialog : BottomDrawerFragment() {

    private var alphaCancelButton = 0f
    private lateinit var cancelButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        cancelButton = view.findViewById(R.id.cancel)
        val percent = 0.65f
        addBottomSheetCallback {
            onSlide { _, slideOffset ->
                alphaCancelButton = (slideOffset - percent) * (1f / (1f - percent))
                cancelButton.alpha = alphaCancelButton
                cancelButton.isEnabled = alphaCancelButton > 0
            }
        }
        cancelButton.setOnClickListener { dismissWithBehavior() }
        return view
    }

    override fun getHandleView(): View {
        return PullHandleView(context!!).apply {
            val widthHandle =
                resources.getDimensionPixelSize(R.dimen.sample_bottom_sheet_handle_width)
            val heightHandle =
                resources.getDimensionPixelSize(R.dimen.sample_bottom_sheet_handle_height)
            val params =
                FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.CENTER_HORIZONTAL)

            params.topMargin =
                resources.getDimensionPixelSize(R.dimen.sample_bottom_sheet_handle_top_margin)

            layoutParams = params
        }
    }

    override fun getContainer(): Int {
        return R.layout.google_task_example_layout
    }

    override fun getStyle(): Int {
        return R.style.Pull
    }
}
