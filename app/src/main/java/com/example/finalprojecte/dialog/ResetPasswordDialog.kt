package com.example.finalprojecte.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.finalprojecte.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomsheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_pass, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.EdResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSendResetPassword)

    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        if (email.isEmpty()) Unit
        else {
            onSendClick(email)
            dialog.dismiss()
        }
    }

    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}