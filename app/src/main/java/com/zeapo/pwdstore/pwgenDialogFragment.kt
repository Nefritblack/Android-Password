package com.zeapo.pwdstore

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.zeapo.pwdstore.pwgen.pwgen

import java.util.ArrayList


/**
 * A placeholder fragment containing a simple view.
 */
class pwgenDialogFragment : DialogFragment() {


    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val callingActivity = activity
        val inflater = callingActivity!!.layoutInflater
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.fragment_pwgen, null)
        val monoTypeface = Typeface.createFromAsset(callingActivity.assets, "fonts/sourcecodepro.ttf")

        builder.setView(view)

        val prefs = activity!!.applicationContext.getSharedPreferences("pwgen", Context.MODE_PRIVATE)

        var checkBox = view.findViewById<CheckBox>(R.id.numerals)
        checkBox.isChecked = !prefs.getBoolean("0", false)

        checkBox = view.findViewById(R.id.symbols)
        checkBox.isChecked = prefs.getBoolean("y", false)

        checkBox = view.findViewById(R.id.uppercase)
        checkBox.isChecked = !prefs.getBoolean("A", false)

        checkBox = view.findViewById(R.id.ambiguous)
        checkBox.isChecked = !prefs.getBoolean("B", false)

        checkBox = view.findViewById(R.id.pronounceable)
        checkBox.isChecked = !prefs.getBoolean("s", true)

        val textView = view.findViewById<TextView>(R.id.lengthNumber)
        textView.text = Integer.toString(prefs.getInt("length", 20))

        (view.findViewById<View>(R.id.passwordText) as TextView).typeface = monoTypeface

        builder.setPositiveButton(resources.getString(R.string.dialog_ok)) { dialog, which ->
            val edit = callingActivity.findViewById<EditText>(R.id.crypto_password_edit)
            val generate = view.findViewById<TextView>(R.id.passwordText)
            edit.setText(generate.text)
        }

        builder.setNegativeButton(resources.getString(R.string.dialog_cancel)) { dialog, which -> }

        builder.setNeutralButton(resources.getString(R.string.pwgen_generate), null)

        val ad = builder.setTitle("Generate Password").create()
        ad.setOnShowListener {
            setPreferences()
            val textView = view.findViewById<TextView>(R.id.passwordText)
            textView.text = pwgen.generate(activity!!.applicationContext)[0]

            val b = ad.getButton(AlertDialog.BUTTON_NEUTRAL)
            b.setOnClickListener {
                setPreferences()
                val textView = view.findViewById<TextView>(R.id.passwordText)
                textView.text = pwgen.generate(callingActivity.applicationContext)[0]
            }
        }
        return ad
    }

    private fun setPreferences() {
        val preferences = ArrayList<String>()
        if (!(dialog.findViewById<View>(R.id.numerals) as CheckBox).isChecked) {
            preferences.add("0")
        }
        if ((dialog.findViewById<View>(R.id.symbols) as CheckBox).isChecked) {
            preferences.add("y")
        }
        if (!(dialog.findViewById<View>(R.id.uppercase) as CheckBox).isChecked) {
            preferences.add("A")
        }
        if (!(dialog.findViewById<View>(R.id.ambiguous) as CheckBox).isChecked) {
            preferences.add("B")
        }
        if (!(dialog.findViewById<View>(R.id.pronounceable) as CheckBox).isChecked) {
            preferences.add("s")
        }
        val editText = dialog.findViewById<EditText>(R.id.lengthNumber)
        try {
            val length = Integer.valueOf(editText.text.toString())
            pwgen.setPrefs(activity!!.applicationContext, preferences, length)
        } catch (e: NumberFormatException) {
            pwgen.setPrefs(activity!!.applicationContext, preferences)
        }

    }
}

