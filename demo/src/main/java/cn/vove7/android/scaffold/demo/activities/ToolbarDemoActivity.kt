package cn.vove7.android.scaffold.demo.activities

import cn.vove7.android.common.logi
import cn.vove7.android.scaffold.demo.databinding.ActivityToolbarDemoBinding
import cn.vove7.android.scaffold.ui.base.ScaffoldActivity

/**
 * # ToolbarDemoActivity
 *
 * Created on 2019/12/19
 * @author Vove
 */
class ToolbarDemoActivity :
    ScaffoldActivity<ActivityToolbarDemoBinding>() {

    override fun initView() {

        "${binding.root}".logi()
    }
}