package cn.vove7.android.scaffold.ui.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * # TextSizeableActivity
 * Created on 2019/12/18
 *
 * @author Vove
 */
abstract class TextSizeableActivity : AppCompatActivity(), TextSizeableDelegate {

    override var loadTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (textSizeChangeable) {
            _onCreate()
        }
    }

    override fun superAttachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun attachBaseContext(newBase: Context) {
        _attachBaseContext(newBase)
    }

    override fun getResources(): Resources? {
        return if(textSizeChangeable){
            _getResources(super.getResources())
        }else{
            super.getResources()
        }
    }

    override fun reStart() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        _onResume()
    }
}