package com.example.myapplication

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.myapplication.DialogFragments.GenericDialog
import com.example.myapplication.DialogFragments.InviteFormDialog
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var konfettiView: KonfettiView
    private lateinit var txtViewHeading: TextView
    private lateinit var txtViewSmallHeading: TextView
    private lateinit var btnRequestInvite: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        initViews()
        updateActivity(getFromUserPreferences())
        startSlideAnimation()
    }

    private fun initViews() {
        konfettiView = findViewById(R.id.konfettiView)
        txtViewHeading = findViewById(R.id.txtViewHeading)
        txtViewSmallHeading = findViewById(R.id.txtViewSmallHeading)
        btnRequestInvite = findViewById(R.id.btnRequestInvite)
    }

    private fun startSlideAnimation() {
        val views = mutableListOf<View>(txtViewHeading, txtViewSmallHeading, btnRequestInvite)
        views.forEach {
            val animSlide = AnimationUtils.loadAnimation(applicationContext, R.anim.slide)
            it.startAnimation(animSlide)
        }
    }

    private fun getFromUserPreferences(): Boolean {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return false
        return sharedPreferences.getBoolean(SENT_INVITE, false)
    }

    fun saveToUserPreferences(isInviteSent: Boolean) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()) {
            putBoolean(SENT_INVITE, isInviteSent)
            apply()
        }
    }

    fun createGenericDialog(dialogType: String): GenericDialog {
        val dialog = GenericDialog()
        val sentBundle = Bundle()

        sentBundle.putString(GenericDialog.DIALOG_TYPE, dialogType)
        dialog.arguments = sentBundle

        return dialog
    }

    fun updateActivity(isInviteSent: Boolean) {
        if (isInviteSent) {
            btnRequestInvite.text = getText(R.string.cancel_invite_button)
            btnRequestInvite.setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(getText(R.string.are_you_sure_cancel))
                    .setCancelable(false)
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes") { dialog, _ ->
                        saveToUserPreferences(false)
                        updateActivity(false)
                        createGenericDialog(GenericDialog.CANCELLED_INVITE_DIALOG).show(
                            supportFragmentManager,
                            GenericDialog.CANCELLED_INVITE_DIALOG
                        )
                    }
                    .create()
                    .show()
            }
        } else {
            btnRequestInvite.text = getText(R.string.request_an_invite_button)
            btnRequestInvite.setOnClickListener {
                val dialog = InviteFormDialog()
                dialog.show(supportFragmentManager, "InviteFormDialog")
            }
        }
    }

    fun startConfetti() {
        val party = Party(
            emitter = Emitter(duration = 60, TimeUnit.SECONDS).perSecond(1250),
            timeToLive = 800
        )
        konfettiView.start(party)
    }

    fun stopConfetti() {
        konfettiView.stopGracefully()
    }

    companion object {
        private const val TAG = "MainActivity"
        const val SENT_INVITE = "sent_invite"
    }
}