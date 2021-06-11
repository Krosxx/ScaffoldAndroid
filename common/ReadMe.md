# Common

> 公共模块，提供基础功能


- 日志 Logger

初始化配置：

```kotlin
Logger {
    outputLevel = if(BuildConfig.DEBUG) Log.VERBOSE else 100
    callstackDepth = 3
}
```

## 扩展

- TextSpan

