package com.tencent.qqlogincountfxxker.preferences;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

import com.tencent.qqlogincountfxxker.config.Constant;

/**
 * Copyright (C), 2015-2020
 *
 * @author Cooper
 * @date 2021/3/29 11:35
 * History:
 * <author> <time> <version> <desc>
 * Cooper 2021/3/29 11:35 1  配置
 */
public class MyPreferenceProvider extends RemotePreferenceProvider {
    public MyPreferenceProvider() {
        super(Constant.MY_CONTENT_PROVIDER_AUTHORITY, new String[]{Constant.MY_SHARED_PREFERENCE});
    }
}