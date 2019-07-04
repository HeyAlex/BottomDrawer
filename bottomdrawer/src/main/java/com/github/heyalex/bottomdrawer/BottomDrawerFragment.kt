package com.github.heyalex.bottomdrawer

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

abstract class BottomDrawerFragment : DialogFragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private var bottomDrawerDialog: BottomDrawerDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(getContainer(), container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomDrawerDialog(context!!)
        bottomDrawerDialog = dialog
        return dialog
    }

    override fun onStart() {
        super.onStart()
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

    @LayoutRes
    abstract fun getContainer(): Int

    override fun onGlobalLayout() {
        bottomDrawerDialog?.drawer?.globalTranslationViews()
    }
}