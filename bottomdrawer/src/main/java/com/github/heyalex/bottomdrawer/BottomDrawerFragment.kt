package com.github.heyalex.bottomdrawer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BottomDrawerFragment : androidx.fragment.app.DialogFragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private var bottomDrawerDialog: BottomDrawerDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = prepareBottomDrawerDialog()
        bottomDrawerDialog = dialog
        return dialog
    }

    abstract fun prepareBottomDrawerDialog() : BottomDrawerDialog

    override fun onStart() {
        super.onStart()
        addHandleView()
        bottomDrawerDialog?.drawer?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        dialog.setOnDismissListener {
            if (isAdded) {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun addHandleView() {
        bottomDrawerDialog?.drawer?.addHandleView(
            getHandleView()
        )
    }

    abstract fun getHandleView(): View

    fun dismissWithBehavior() {
        bottomDrawerDialog?.behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun getCurrentState(): Int? {
        return bottomDrawerDialog?.behavior?.state
    }

    override fun onGlobalLayout() {
        bottomDrawerDialog?.drawer?.globalTranslationViews()
    }

    fun addBottomSheetCallback(func: BottomDrawerDialog.BottomSheetCallback.() -> Unit): BottomSheetBehavior.BottomSheetCallback? {
        return bottomDrawerDialog?.addBottomSheetCallback(func)
    }

    fun removeBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        bottomDrawerDialog?.removeBottomSheetCallback(callback)
    }
}