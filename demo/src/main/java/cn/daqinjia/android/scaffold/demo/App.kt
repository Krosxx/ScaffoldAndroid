package cn.daqinjia.android.scaffold.demo

import cn.daqinjia.android.scaffold.app.ScaffoldApp
import cn.daqinjia.android.scaffold.demo.app.AppDatabase
import cn.daqinjia.android.scaffold.demo.app.appModules
import cn.daqinjia.android.scaffold.demo.data.Config
import cn.daqinjia.android.scaffold.demo.data.set
import glimpse.core.Glimpse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * # App
 * Created on 2019/11/25
 *
 * @author Vove
 */
class App : ScaffoldApp() {
    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch {
            for (i in 0..9)
                AppDatabase.configdDao.insert(Config("key_$i", "value_%i"))
        }

        startKoin {
            androidContext(this@App)
            modules(appModules)
        }

        Glimpse.init(this)
    }

}
