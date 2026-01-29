package com.tty.lib.annotations;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandMeta {
    //显示名称
    String displayName();
    //需要的权限
    String permission() default "";
    //是否允许控制台执行
    boolean allowConsole() default false;
    //限制的token长度
    int tokenLength() default -1;
}
