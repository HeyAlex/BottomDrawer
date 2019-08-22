package com.github.heyalex.bottomdrawerexample

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.github.heyalex.handle.PullHandleView

class CustomExampleDialog : BottomDrawerFragment() {

    private var alphaCancelButton = 0f
    private lateinit var cancelButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.google_task_example_layout, container, false)
        cancelButton = view.findViewById(R.id.cancel)
        addBottomSheetCallback {
            onSlide { _, slideOffset ->
                alphaCancelButton = (slideOffset - PERCENT) * (1f / (1f - PERCENT))
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

    override fun prepareBottomDrawerDialog(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            theme = R.style.Pull
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("alphaCancelButton", alphaCancelButton)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        alphaCancelButton = savedInstanceState?.getFloat("alphaCancelButton") ?: 0f
    }

    companion object {
        const val PERCENT = 0.65f
    }
}
