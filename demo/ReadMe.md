
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

### 适配安卓Q DarkMode

在 `Application` 配置

```kotlin
ScaffoldActivity.apply {
    enableThamable = true
    darkTheme = R.style.DarkTheme
}
```

style 参考 [demo/styles.xml](src/main/res/values/styles.xml)


### [ShimmerRecyclerView](https://github.com/sharish/ShimmerRecyclerView)

列表加载效果

```xml
<com.cooltechworks.views.shimmer.ShimmerRecyclerView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:shimmer_demo_angle="20"
    app:shimmer_demo_child_count="8"
    app:shimmer_demo_grid_child_count="2"
    app:shimmer_demo_layout="@layout/shimmer_item_image"
    app:shimmer_demo_layout_manager_type="grid"
    tools:listitem="@layout/shimmer_item_image">

</com.cooltechworks.views.shimmer.ShimmerRecyclerView>
```

![](https://raw.githubusercontent.com/sharish/ShimmerRecyclerView/master/screenshots/list_demo.gif)