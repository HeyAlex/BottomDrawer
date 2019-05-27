package com.github.heyalex.bottomdrawer

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BottomDrawerFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(getContainer(), container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomDrawerDialog(context!!)
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

    @LayoutRes
    abstract fun getContainer(): Int
}