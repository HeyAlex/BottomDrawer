package com.github.heyalex.bottomdrawerexample

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.github.heyalex.handle.PlainHandleView

class GoogleTaskExampleDialog : BottomDrawerFragment() {

    private lateinit var cancelButton: ImageView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        cancelButton = view.findViewById(R.id.cancel)
        addBottomSheetCallback {
            onSlide { _, slideOffset ->
                cancelButton.alpha = slideOffset
                cancelButton.isEnabled = slideOffset > 0
            }
        }
        cancelButton.setOnClickListener { dismissWithBehavior() }
        return view
    }

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
