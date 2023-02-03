package com.example.myapplication.DialogFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.example.myapplication.Data.BroccoliApi
import com.example.myapplication.Data.RetrofitHelper
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InviteFormDialog : DialogFragment() {
    private lateinit var btnSend: Button
    private lateinit var progressCircleSending: ProgressBar
    private lateinit var txtViewRequestError: TextView
    private lateinit var edtTxtFullName: EditText
    private lateinit var txtViewFullNameError: TextView
    private lateinit var edtTxtEmail: EditText
    private lateinit var txtViewEmailError: TextView
    private lateinit var edtTxtConfirmEmail: EditText
    private lateinit var txtViewConfirmEmailError: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.request_invite_form_dialog, container, false)
        initViews(view)
        registerEditTextListeners()
        handleSendButton()
        return view
    }

    private fun initViews(view: View) {
        btnSend = view.findViewById(R.id.btnSend)
        progressCircleSending = view.findViewById(R.id.progressCircleSending)
        txtViewRequestError = view.findViewById(R.id.txtViewRequestError)
        edtTxtFullName = view.findViewById(R.id.edtTxtFullName)
        txtViewFullNameError = view.findViewById(R.id.txtViewFullNameError)
        edtTxtEmail = view.findViewById(R.id.edtTxtEmail)
        txtViewEmailError = view.findViewById(R.id.txtViewEmailError)
        edtTxtConfirmEmail = view.findViewById(R.id.edtTxtConfirmEmail)
        txtViewConfirmEmailError = view.findViewById(R.id.txtViewConfirmEmailError)
    }

    private fun registerEditTextListeners() {
        edtTxtFullName.doOnTextChanged { _, _, _, _ ->
            txtViewFullNameError.visibility =
                if (edtTxtFullName.text.length >= 3)
                    View.GONE
                else
                    View.VISIBLE

            txtViewRequestError.visibility = View.GONE
        }

        edtTxtEmail.doOnTextChanged { _, _, _, _ ->
            edtTxtConfirmEmail.text = edtTxtConfirmEmail.text
            txtViewEmailError.visibility =
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(edtTxtEmail.text).matches())
                    View.GONE
                else
                    View.VISIBLE

            txtViewRequestError.visibility = View.GONE
        }

        edtTxtConfirmEmail.doOnTextChanged { _, _, _, _ ->
            txtViewConfirmEmailError.visibility =
                if (edtTxtEmail.text.contentEquals(edtTxtConfirmEmail.text))
                    View.GONE
                else
                    View.VISIBLE

            txtViewRequestError.visibility = View.GONE
        }
    }

    private fun handleSendButton() {
        btnSend.setOnClickListener {
            val fields = mutableListOf(edtTxtFullName, edtTxtEmail, edtTxtConfirmEmail)
            val errorFields =
                mutableListOf(txtViewFullNameError, txtViewEmailError, txtViewConfirmEmailError)

            if (fields.filter { it.text.isNotEmpty() }.size == 3 && errorFields.filter { it.visibility == View.GONE }.size == 3) {
                controlSendButton(true)
                sendRequest()
            } else {
                fields.forEach { it.text = it.text }
            }

        }
    }

    private fun controlSendButton(isSending: Boolean, errorText: String = "") {
        if (isSending) {
            btnSend.visibility = View.GONE
            progressCircleSending.visibility = View.VISIBLE
        } else {
            btnSend.visibility = View.VISIBLE
            progressCircleSending.visibility = View.GONE
            txtViewRequestError.visibility = View.VISIBLE
            txtViewRequestError.text = errorText
        }
    }

    private fun sendRequest() {
        val response = RetrofitHelper.buildService(BroccoliApi::class.java)

        val requestBody = JsonObject()
        requestBody.addProperty(NAME, edtTxtFullName.text.toString())
        requestBody.addProperty(EMAIL, edtTxtEmail.text.toString())

        response.postEmailAccount(requestBody.toString()).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        navigateToCongratulationsDialog()
                    } else {
                        controlSendButton(false, "This email address is already in use.")
                    }

                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, "onResponse: ${t.message}")
                    controlSendButton(false, "An unknown error has occurred.")
                }
            }
        )
    }

    private fun navigateToCongratulationsDialog() {
        dismiss()
        val mainActivity = activity as MainActivity
        mainActivity.saveToUserPreferences(true)
        mainActivity.updateActivity(true)
        mainActivity.createGenericDialog(GenericDialog.CONGRATULATIONS_DIALOG)
            .show(parentFragmentManager, GenericDialog.CONGRATULATIONS_DIALOG)
    }

    companion object {
        private const val TAG = "CustomDialog"
        private const val NAME = "name"
        private const val EMAIL = "email"
    }

}