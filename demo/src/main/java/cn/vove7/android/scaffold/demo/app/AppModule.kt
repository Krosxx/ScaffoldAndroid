package cn.vove7.android.scaffold.demo.app

import cn.vove7.android.scaffold.demo.activities.MStepViewModel
import cn.vove7.android.scaffold.demo.activities.DemoViewModel
import cn.vove7.android.scaffold.demo.activities.ImagesViewModel
import cn.vove7.android.scaffold.demo.repo.ImagesRepo
import org.koin.androidx.viewmodel.dsl.viewModel
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
    single { AppApi }
    single { ImagesRepo() }
}

//TODO
val appModules = listOf(repositoryModule, viewModelModule)