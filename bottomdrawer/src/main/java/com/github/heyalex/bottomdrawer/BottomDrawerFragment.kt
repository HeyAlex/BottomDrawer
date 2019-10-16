package com.github.heyalex.bottomdrawer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.LayoutRes
import com.github.heyalex.utils.BottomDrawerDelegate
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BottomDrawerFragment : androidx.fragment.app.DialogFragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private var bottomDrawerDialog: BottomDrawerDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = prepareBottomDrawerDialog()
        bottomDrawerDialog = dialog
        return dialog
    }

    open fun prepareBottomDrawerDialog() : BottomDrawerDialog {
        return BottomDrawerDialog(context!!)
    }

    override fun onStart() {
        super.onStart()
        bottomDrawerDialog?.drawer?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        dialog.setOnDismissListener {
            if (isAdded) {
                dismissAllowingStateLoss()
            }
        }
    }

    fun dismissWithBehavior() {
        bottomDrawerDialog?.behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun expandWithBehaivor() {
        bottomDrawerDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun getCurrentState(): Int? {
        return bottomDrawerDialog?.behavior?.state
    }

    override fun onGlobalLayout() {
        bottomDrawerDialog?.drawer?.globalTranslationViews()
    }

    fun addBottomSheetCallback(func: BottomDrawerDelegate.BottomSheetCallback.() -> Unit): BottomSheetBehavior.BottomSheetCallback? {
        return bottomDrawerDialog?.bottomDrawerDelegate?.addBottomSheetCallback(func)
    }

    fun removeBottomSheetCallback(callback: BottomSheetBehavior.BottomSheetCallback) {
        bottomDrawerDialog?.bottomDrawerDelegate?.removeBottomSheetCallback(callback)
    }

    fun changeCornerRadius(radius: Float) {
        bottomDrawerDialog?.drawer?.changeCornerRadius(radius)
    }

    fun changeBackgroundColor(color: Int) {
        bottomDrawerDialog?.drawer?.changeBackgroundColor(color)
    }

    fun changeExtraPadding(extraPadding: Int) {
        bottomDrawerDialog?.drawer?.changeExtraPadding(extraPadding)
    }

    fun changeHandleView(handleView: View) {
        bottomDrawerDialog?.drawer?.addHandleView(handleView)
    }

    fun shouldDrawUnderStatusBar(shouldDrawUnderStatusBar: Boolean) {
        bottomDrawerDialog?.drawer?.shouldDrawUnderStatusBar(shouldDrawUnderStatusBar)
    }

    fun shouldDrawUnderHandleView(shouldDrawUnderHandleView: Boolean) {
        bottomDrawerDialog?.drawer?.shouldDrawUnderHandleView(shouldDrawUnderHandleView)
    }
}