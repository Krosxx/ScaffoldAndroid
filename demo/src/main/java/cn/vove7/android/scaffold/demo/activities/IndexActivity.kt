package cn.vove7.android.scaffold.demo.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import cn.vove7.android.scaffold.demo.R
import cn.vove7.android.scaffold.ui.base.NoBindingActivity
import com.github.ielse.imagewatcher.PhotoViewActivity

/**
 * # IndexActivity
 * Created on 2019/12/13
 *
 * @author Vove
 */
class IndexActivity : NoBindingActivity() {
    override val layoutRes: Int = 0
    override val showReturnIcon: Boolean = false

    private val excludeActivity = arrayOf(
        PhotoViewActivity::class,
        IndexActivity::class
    ).map { it.java.name }

    val activities: List<ActivityInfo> by lazy {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).let {
            it.activities.filter { i -> i.name !in excludeActivity }
        }
    }

    override fun initView() {
        setContentView(buildContentView())
    }

    private fun buildContentView(): View = ListView(this).apply {
        adapter = object : BaseAdapter() {
            val context = this@IndexActivity
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val item = getItem(position)

                return ((convertView as TextView?) ?: TextView(context).apply {
                    layoutParams = AbsListView.LayoutParams(-1, -2).apply {
                        setPadding(20, 30, 20, 30)
                    }
                    val tv = TypedValue()
                    theme.resolveAttribute(R.attr.selectableItemBackground, tv, true)
                    ViewCompat.setBackground(
                        this,
                        ContextCompat.getDrawable(context, tv.resourceId)
                    )
                }).apply {
                    text = try {
                        val cls = Class.forName(item.name)
                        setOnClickListener {
                            context.startActivity(Intent(context, cls))
                        }
                        cls.simpleName
                    } catch (E: Throwable) {
                        E.message
                    }
                }
            }

            override fun getItem(position: Int): ActivityInfo = activities[position]

            override fun getItemId(position: Int): Long = position.toLong()

            override fun getCount(): Int = activities.size
        }
    }

}