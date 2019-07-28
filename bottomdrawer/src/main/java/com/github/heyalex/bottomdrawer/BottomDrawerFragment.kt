package com.github.heyalex.bottomdrawer

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BottomDrawerFragment : androidx.fragment.app.DialogFragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private var bottomDrawerDialog: BottomDrawerDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(getContainer(), container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomDrawerDialog(context!!, getStyle())
        bottomDrawerDialog = dialog
        return dialog
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

    fun <T> addHandleView(
        view: T,
        shouldDrawUnderStatus: Boolean = false,
        shouldDrawContentUnderHandle: Boolean = false
    ) where T : View, T : TranslationUpdater {
        bottomDrawerDialog?.drawer?.addHandleView(
            view,
            shouldDrawUnderStatus,
            shouldDrawContentUnderHandle
        )
    }

    fun dismissWithBehavior() {
        bottomDrawerDialog?.behavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun getCurrentState(): Int? {
        return bottomDrawerDialog?.behavior?.state
    }

    @LayoutRes
    abstract fun getContainer(): Int

    @StyleRes
    open fun getStyle(): Int {
        return R.style.BottomDialogTheme
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