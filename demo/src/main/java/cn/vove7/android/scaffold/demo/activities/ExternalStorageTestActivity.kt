package cn.vove7.android.scaffold.demo.activities

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * # ExternalStorageTestActivity
 *
 * Created on 2020/3/19
 * @author Vove
 */
class ExternalStorageTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            File(Environment.getExternalStorageDirectory(),"Android/data/$packageName/a.txt").apply {
                parentFile.mkdirs()
                createNewFile()
                writeText("111")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}