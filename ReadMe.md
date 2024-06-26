

# ScaffoldAndroid

[![](https://jitpack.io/v/Krosxx/ScaffoldAndroid.svg)](https://jitpack.io/#Krosxx/ScaffoldAndroid)

> 支持 ViewBinding & DataBinding 摆脱 layoutId

```kotlin
class MVVMDemoActivity : ScaffoldActivity<ActivityMvvmDemoBinding>()
```

### 引入

```groovy
//project
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
//app module
dependencies {
    implementation 'com.github.Vove7:ScaffoldAndroid:1.0.1'
}
```

### MVVM

![]( https://camo.githubusercontent.com/2b3ff9b3a5f99c5480b612aa8f4f678dc696987a/68747470733a2f2f757365722d676f6c642d63646e2e786974752e696f2f323031392f342f31352f313661323130313664663963373663353f773d39363026683d37323026663d7765627026733d3135333832 )

### [Jetpack](https://developer.android.google.cn/jetpack)


-  [Lifecycles](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)

   管理 Activity 和 Fragment 生命周期

-  [ViewModel](https://developer.android.google.cn/topic/libraries/architecture/viewmodel)

   网络请求： Retrofit -> LiveData
   数据库数据：Room -> LiveData
   
-  [DataBinding](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)

   数据绑定视图
   
-  [Room](https://developer.android.google.cn/topic/libraries/architecture/room)

   持久化储存：SQLite数据库

-  [SmartKey](https://github.com/Vove7/SmartKey)

   SharedPreference操作



### 其他

-  Retrofit  网络

-  Glide  图片加载


### Demo

包含开发模式

[>>> Demo](demo/ReadMe.md)
