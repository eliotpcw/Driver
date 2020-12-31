package kz.kazpost.driver.utils

import android.animation.Animator
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import me.dm7.barcodescanner.zxing.ZXingScannerView

class Utils {

    fun viewVisibility(
        viewList: List<View>,
        isScannerActivated: Boolean,
        startScanner:() -> Unit,
        stopScanner: () -> Unit
    ) {
        if(isScannerActivated){
            viewList.forEach {
                it.visibility = View.VISIBLE
            }
            startScanner()
        } else {
            viewList.forEach {
                it.visibility = View.GONE
            }
            stopScanner()
        }
    }
}