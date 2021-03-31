package com.tencent.qqlogincountfxxker.hook;

import com.tencent.qqlogincountfxxker.manager.MyHookManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Copyright (C), 2015-2020
 *
 * @author Cooper
 * @date 2021/3/29 11:31
 * History:
 * <author> <time> <version> <desc>
 * Cooper 2021/3/29 11:31 1  入口
 */
public class MainEntrance implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        MyHookManager.getInstance().initHook(loadPackageParam);
    }
}
