package cn.daqinjia.android.scaffold.demo.app

import cn.daqinjia.android.scaffold.demo.activities.MStepViewModel
import cn.daqinjia.android.scaffold.demo.activities.DemoViewModel
import cn.daqinjia.android.scaffold.demo.activities.ImagesViewModel
import cn.daqinjia.android.scaffold.demo.repo.ImagesRepo
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
    viewModel{ ImagesViewModel(get()) }

}

val repositoryModule = module {
    single { Api }
    single { ImagesRepo() }
}

//TODO
val appModules = listOf(repositoryModule, viewModelModule)