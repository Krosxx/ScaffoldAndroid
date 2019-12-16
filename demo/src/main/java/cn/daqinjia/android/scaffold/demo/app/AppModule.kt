package cn.daqinjia.android.scaffold.demo.app

import cn.daqinjia.android.scaffold.demo.activities.MStepViewModel
import cn.daqinjia.android.scaffold.demo.activities.DemoViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * # AppModule
 * Created on 2019/11/26
 *
 * @author Vove
 */


val viewModelModule = module {
    viewModel{ DemoViewModel() }
    viewModel{ MStepViewModel() }

}

val repositoryModule = module {
    single { Api }
}

//TODO
val appModules = listOf(repositoryModule, viewModelModule)