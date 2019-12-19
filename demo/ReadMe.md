
### parentActivityName

在某些界面直接启动后，通过返回想进入主页


可指定 `parentActivityName` 并继承 `ScaffoldActivity` 实现

```kotlin
class XXXActivity : ScaffoldActivity
```
```xml
<activity
    android:name=".XXXActivity"
    android:parentActivityName=".activities.MainActivity" />
```

**经测试指定 android:parentActivityName, 直接进入XXXActivity 按返回键无法跳回主页**



### [glimpse-android](https://github.com/the-super-toys/glimpse-android)

保证图片主体部分在显示区

未开启与开启后：

![](img/d1.png)
![](img/d0.png)

```xml
<ImageView
    android:id="@+id/img"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:scaleType="centerCrop"
    app:imageUrl="@{imgUrl}"
    app:glimpse="@{true}"
    tools:src="@drawable/ic_launcher_background" />
```



### OkHttp在4.4及以下不支持TLS协议

影响：
- 无法请求 https 资源
- Glide 无法加载 https 图片资源

解决方法: 


```
api 'com.github.bumptech.glide:glide:4.10.0'
kapt 'com.github.bumptech.glide:compiler:4.10.0'
//不可高于3.10.0
implementation 'com.squareup.okhttp3:okhttp:3.10.0'
```

自定义 OkHttpClient 

`cn.daqinjia.android.scaffold.compat.okhttp` 包下


**但是 又使用了 retrofit:2.6 已经依赖 okhttp 4.2.2  优先不考虑加载https资源**

### 配置打包文件路径

```grvooy
android {
    //配置打包apk路径及文件名
    android.applicationVariants.all { variant ->
        variant.outputs.all { output ->
            def type = "-" + buildType.name
            if (type != "-release") {
                type = ""
            }
            def filename = "../../apks//应用名-signed-v${versionName}${type}.apk"
            outputFileName = filename
        }
    }
}

```