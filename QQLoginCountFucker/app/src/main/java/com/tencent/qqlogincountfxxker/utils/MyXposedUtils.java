package com.tencent.qqlogincountfxxker.utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Copyright (C), 2015-2020
 *
 * @author Cooper
 * @date 2021/3/29 11:28
 * History:
 * <author> <time> <version> <desc>
 * Cooper 2021/3/29 11:28 1  是否启用了
 */
public class MyXposedUtils {
    /**
     * 判断自身是否启动、是否激活
     * 注意：如果插件生效范围不含自身，则该方法不准确！
     *
     * @return 是否启动激活
     */
    public static boolean isActivated() {
        //增加一下代码长度，不知道hook的字节够不够。
        int t = 0;
        if (System.currentTimeMillis() != -123456789) {
            t = 1;
        }
        return t != 0;
    }

    /**
     * 检查是否为自身，是否需要hook
     * 如果返回null，则不是自身，继续执行其他即可。不为null则说明是自身！
     *
     * @param selfPackageName  自身包名，用常量的方式传入，其他方式传入的不对！
     * @param loadPackageParam 加载的包参数
     * @return unhook对象，如果不是自身则为null
     */
    public static XC_MethodHook.Unhook checkSelfActivated(
            String selfPackageName,
            final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (loadPackageParam.packageName.equals(selfPackageName)) {
            return findAndHookMethod(MyXposedUtils.class.getName(),
                    loadPackageParam.classLoader, "isActivated", XC_MethodReplacement.returnConstant(true));
        }
        return null;
    }

    //默认的调试tag，用于过滤的，因为lsposed和xposed的tag不一样
    private static String TAG = "==== MyXposed debug log ====";

    /**
     * 设置tag，用于过滤日志的
     *
     * @param tag tag
     */
    public static void setLogTag(String tag) {
        TAG = tag;
    }

    /**
     * 输出日志，用于调试的
     *
     * @param content 内容
     */
    public static void logD(String content) {
        XposedBridge.log(TAG + content);
    }
}
