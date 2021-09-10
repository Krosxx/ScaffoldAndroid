# databinding
#-keep public class * extends androidx.databinding.ViewDataBinding
-keepclassmembers class * extends androidx.databinding.ViewDataBinding {
    public static <methods>;
}

# viewbinding
-keepclassmembers class * extends androidx.viewbinding.ViewBinding {
    public static <methods>;
}
