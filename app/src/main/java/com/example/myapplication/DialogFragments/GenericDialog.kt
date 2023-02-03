package com.example.myapplication.DialogFragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class GenericDialog : DialogFragment() {
    private lateinit var dialogType: String
    private lateinit var txtViewGenericTitle: TextView
    private lateinit var txtViewGenericDesc: TextView
    private lateinit var btnOk: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.generic_dialog_layout, container, false)
        initViews(view)

        dialogType = arguments?.getString(DIALOG_TYPE).toString()
        when (dialogType) {
            CONGRATULATIONS_DIALOG -> {
                txtViewGenericTitle.setText(R.string.congratulations_title_dialog)
                txtViewGenericDesc.setText(R.string.congratulations_desc_dialog)
                (activity as MainActivity).startConfetti()

            }
            EMAIL_REGISTERED_DIALOG -> {
                txtViewGenericTitle.setText(R.string.already_done_title_dialog)
                txtViewGenericDesc.setText(R.string.email_already_registered_desc_dialog)
            }
            CANCELLED_INVITE_DIALOG -> {
                txtViewGenericTitle.setText(R.string.cancelled_invite_title_dialog)
                txtViewGenericDesc.setText(R.string.cancelled_invite_desc_dialog)
            }
        }

        btnOk.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity = (activity as MainActivity)

        if (dialogType.contentEquals(CONGRATULATIONS_DIALOG)) {
            activity.createGenericDialog(EMAIL_REGISTERED_DIALOG)
                .show(parentFragmentManager, EMAIL_REGISTERED_DIALOG)
        }
        else if (dialogType.contentEquals(EMAIL_REGISTERED_DIALOG)) {
            activity.stopConfetti()
        }
    }

    private fun initViews(view: View) {
        txtViewGenericTitle = view.findViewById(R.id.txtViewGenericTitle)
        txtViewGenericDesc = view.findViewById(R.id.txtViewGenericDesc)
        btnOk = view.findViewById(R.id.btnOk)
    }

    companion object {
        const val DIALOG_TYPE = "dialog_type"
        const val CONGRATULATIONS_DIALOG = "congratulations_dialog"
        const val EMAIL_REGISTERED_DIALOG = "email_registered_dialog"
        const val CANCELLED_INVITE_DIALOG = "cancelled_invite_dialog"
    }
}