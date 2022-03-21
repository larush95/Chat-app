
package com.example.chatapp.observers

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button

class ButtonObserver(private val button: Button) : TextWatcher {
    override fun onTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
        button.isEnabled = charSequence.toString().trim().isNotEmpty()
    }

    override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {}
}
