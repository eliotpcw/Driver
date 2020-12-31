package kz.kazpost.driver.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import kz.kazpost.driver.R

inline fun Context.alert(
    titleResource: Int = 0,
    messageResource: Int = 0,
    func: AlertDialogHelper.() -> Unit
) {
    val title = if (titleResource == 0) null else getString(titleResource)
    val message = if (messageResource == 0) null else getString(messageResource)
    AlertDialogHelper(this, title, message).apply {
        func()
    }
}

@SuppressLint("InflateParams")
class AlertDialogHelper(
    context: Context,
    title: CharSequence?,
    message: CharSequence?
) {

    private val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.progress, null)
    }

    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(dialogView)

//    private val title: TextView by lazyFast {
//        dialogView.findViewById<TextView>(R.id.dialogInfoTitleTextView)
//    }

    private val message: TextView by lazy {
        dialogView.findViewById<TextView>(R.id.progress_text)
    }

//    private val positiveButton: Button by lazyFast {
//        dialogView.findViewById<Button>(R.id.dialogInfoPositiveButton)
//    }
//
//    private val negativeButton: Button by lazyFast {
//        dialogView.findViewById<Button>(R.id.dialogInfoNegativeButton)
//    }

    var cancelable: Boolean = false

    private var dialog: AlertDialog? = null


    init {
        this.message.text = message
        dialog = builder
            .setCancelable(cancelable)
            .create()
    }

    fun onCancel(func: () -> Unit) {
        builder.setOnCancelListener { func() }
    }

    fun create(): AlertDialog {
        message.goneIfTextEmpty()
        return dialog!!
    }

    fun closeDialog(){
        dialog?.dismiss()
    }

    private fun TextView.goneIfTextEmpty() {
        visibility = if (text.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun Button.setClickListenerToDialogButton(func: (() -> Unit)?) {
        setOnClickListener {
            func?.invoke()
            dialog?.dismiss()
        }
    }

}