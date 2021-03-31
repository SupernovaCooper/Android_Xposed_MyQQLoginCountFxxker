package com.tencent.qqlogincountfxxker.manager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.view.View;

import com.tencent.qqlogincountfxxker.config.Constant;
import com.tencent.qqlogincountfxxker.utils.MyXposedUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Copyright (C), 2015-2020
 *
 * @author Cooper
 * @date 2021/3/29 11:37
 * History:
 * <author> <time> <version> <desc>
 * Cooper 2021/3/29 11:37 1  主要代码
 */
public class MyHookManager {
    private static final boolean d = true;

    //==============================初始化================================
    private static class LazyHolder {
        private static final MyHookManager INSTANCE = new MyHookManager();
    }

    private MyHookManager() {
    }


    public static MyHookManager getInstance() {
        return MyHookManager.LazyHolder.INSTANCE;
    }

    public void destroy() {
        try {
            allHooked.clear();
        } catch (Exception e) {
            if (d) e.printStackTrace();
        }
    }

    //==============================内部变量======================================
    private XC_LoadPackage.LoadPackageParam mLoadPackageParam = null;
    private final List<XC_MethodHook.Unhook> allHooked = new ArrayList<>();
    private Context mContext = null;
    private BroadcastReceiver mReceiver = null; //接收外部修改通知的广播

    //==============================私有方法======================================

    //因为没有什么要配置的，所以不用了。广播是为了让配置即时生效（不是在同一进程进行配置，用广播来通信）。
    private void initReceiverForConfigChange() {
//        if (mReceiver == null) {
//            mReceiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    if (intent != null) {
//                        if (Constant.BROADCAST_CONFIG_CHANGED.equals(intent.getAction())) {
//                            if (AppConfigManager.getInstance().isOn()) {
//                                hook();
//                            } else {
//                                unhook();
//                            }
//                        }
//                    }
//                }
//            };
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(Constant.BROADCAST_CONFIG_CHANGED);
//            mContext.registerReceiver(mReceiver, filter);
//        }
    }

    //==============================对外提供的方法================================
    public void initHook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (MyXposedUtils.checkSelfActivated(Constant.MY_PACKAGE_NAME, loadPackageParam) != null) {
            return;
        }
        if (Constant.MY_TARGET_PACKAGE_NAME.equals(loadPackageParam.packageName)) {
            //为了拿到 mContext
            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Context context = (Context) param.args[0];
                    if (context != null) {
                        XposedBridge.log("Found target package, install all hooks, packageName:" + loadPackageParam.packageName);
                        mLoadPackageParam = loadPackageParam;
                        mContext = context;
                        try {
                            hook();//start all the hooks.
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void hook() {
        initReceiverForConfigChange();

        if (allHooked.size() == 0) {
            if (mLoadPackageParam != null) {
                //逆向思路：
                //1.添加账号时根据提示“你的预设帐号数量已达上限”搜索调用者；
                //2.总共有4个位置，实际上是3个要处理的点
                //3.第一个是AccountManageActivity$19的按钮事件，如下处理
                //4.第二个和第四个是工具类返回值，如下处理
                //5.第三个是代码中的逻辑，不太好处理，可以用下面处理AccountManageActivity$19的方式尝试，比较麻烦，得好好分析一下
                //总结一下，不管处理哪里，尽量用更加通用的方式，避免QQ更新之后就失效。
                //hook点不可靠的原因：1.匿名类，每次增加匿名类都会导致顺序变动；2.内部方法，增加新方法就导致顺序变动；
                //所以尽量减少不可靠的hook点，动态地搜索匹配。
                //要是能像windows随意读写内存改写代码逻辑就更强大了。
                //还有种思路就是：hook按钮事件，不管三七二十一直接打开添加账号的页面。不知道有啥影响。

                //region hook账号管理点击添加新账号
                try {
                    //<editor-fold desc="原始代码过程">
//                    /* renamed from: a  reason: collision with other field name */
//                    List<SimpleAccount> f28869a;
//                    /* renamed from: b  reason: collision with other field name */
//                    View.OnClickListener f28874b = new View.OnClickListener() {
//                        /* class com.tencent.mobileqq.activity.AccountManageActivity.AnonymousClass19 */
//
//                        public void onClick(View view) {
//                            if (QLog.isColorLevel()) {
//                                QLog.d("Switch_Account", 2, "add account");
//                            }
//                            if (AccountManageActivity.this.m9148a()) {
//                                if (AccountManageActivity.this.f28869a == null || AccountManageActivity.this.f28869a.size() - 1 < 8) {
//                                    AccountManageActivity.this.f28891f = ((ISubAccountControllUtil) QRoute.api(ISubAccountControllUtil.class)).isAnyAccountBind(AccountManageActivity.this.app);
//                                    Intent intent = new Intent();
//                                    intent.setPackage(AccountManageActivity.this.getPackageName());
//                                    intent.setClass(AccountManageActivity.this, AddAccountActivity.class);
//                                    AccountManageActivity.this.startActivityForResult(intent, 1000);
//                                    AccountManageActivity.this.overridePendingTransition(R.anim.a3, R.anim.w);
//                                    ((ISubAccountAssistantForward) QRoute.api(ISubAccountAssistantForward.class)).doSomethingBeforeAddAccount(AccountManageActivity.this.app, AccountManageActivity.this);
//                                    ReportController.b(AccountManageActivity.this.app, "CliOper", "", "", "Setting_tab", "Clk_acc_add", 0, 0, "", "", "", "");
//                                    ReportController.a(AccountManageActivity.this.app, "0X800B837");
//                                } else {
//                                    QQToast.a(AccountManageActivity.this, (int) R.string.c6a, 0).m23756a();
//                                }
//                            }
//                            EventCollector.getInstance().onViewClicked(view);
//                        }
//                    };
                    //</editor-fold>

                    // AccountManageActivity$19 这个名并不可靠，但是没有其他办法，只能如此。
                    // (貌似想到一个办法，比较复杂，列出全部AccountManageActivity，然后都hook，成功的才算，不成功的还是原逻辑)
                    // 后面之所以写这么复杂，就是为了减少这种不可靠！不必每个版本升级，做到通杀。
                    //实现原理，调用原方法之前，将list清空，以通过判断。调用完成后恢复原list，防止破坏原逻辑引发崩溃等。
                    allHooked.add(findAndHookMethod("com.tencent.mobileqq.activity.AccountManageActivity$19",
                            mLoadPackageParam.classLoader, "onClick", View.class, new XC_MethodHook() {

                                //我的list，用来缓存原始数据的
                                final List<Object> mySimpleAccountList = new ArrayList();
                                //真实的数据，清空之后才能过后面的判断（为了减少影响，方法执行完毕之后要恢复）
                                List<Object> listSimpleAccount = null;

                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    //注意：param.thisObject 并不是目标activity = com.tencent.mobileqq.activity.AccountManageActivity
                                    //param.thisObject = "com.tencent.mobileqq.activity.AccountManageActivity$19"

                                    //不知道为什么 getSurroundingThis 不好使反而 getObjectField 会返回目标activity，费解
                                    //Object surroundingThis = XposedHelpers.getSurroundingThis(param);
                                    //之所以传个a，是因为页面99%会有a这个变量吧，所以按照上面的试验，也一定会返回activity吧？
                                    Object activity = XposedHelpers.getObjectField(param.thisObject, "a");
                                    if (activity != null) {
                                        //目标activity的class，用于寻找List<SimpleAccount>
                                        Class<?> clazz = XposedHelpers.findClassIfExists(activity.getClass().getName(), mLoadPackageParam.classLoader);
                                        if (clazz != null) {
                                            //遍历寻找List<SimpleAccount>
                                            Field[] declaredFields = clazz.getDeclaredFields();
                                            for (Field f : declaredFields) {
                                                //判断是否为list
                                                if (List.class.isAssignableFrom(f.getType())) {
                                                    //type t:java.util.List<com.tencent.qphone.base.remote.SimpleAccount>
                                                    Type genericType = f.getGenericType();
                                                    if (genericType == null) continue;
                                                    // 如果是泛型参数的类型
                                                    if (genericType instanceof ParameterizedType) {
                                                        ParameterizedType pt = (ParameterizedType) genericType;
                                                        //得到泛型里的class类型对象 com.tencent.qphone.base.remote.SimpleAccount
                                                        Class<?> simpleAccountClazz = (Class<?>) pt.getActualTypeArguments()[0];
                                                        //通过类名确保是目标对象
                                                        if ("SimpleAccount".equals(simpleAccountClazz.getSimpleName())) {
                                                            //拿到activity实例

                                                            //f.getName() = a 名字是a，并不可靠

                                                            try {
                                                                f.setAccessible(true);
                                                                Object objectField = f.get(activity);
                                                                listSimpleAccount = ((List<Object>) objectField);
                                                                //备份原对象
                                                                mySimpleAccountList.addAll(listSimpleAccount);
                                                                //清空原对象
                                                                listSimpleAccount.clear();
                                                                // emmmm，成功完成任务
                                                            } catch (Exception e) {
                                                                MyXposedUtils.logD("从Activity实例读取数据失败：" + e.toString());
                                                                e.printStackTrace();
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        MyXposedUtils.logD("获取Activity实例失败！无法继续！");
                                    }
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws
                                        Throwable {
                                    if (mySimpleAccountList != null && listSimpleAccount != null) {
                                        try {
                                            listSimpleAccount.addAll(mySimpleAccountList);
                                            mySimpleAccountList.clear();
                                        } catch (Exception e) {
                                            MyXposedUtils.logD("恢复作案现场出错：" + e.toString());
                                        }
                                    }
                                }
                            }));
                } catch (Exception e) {
                    MyXposedUtils.logD("尝试Hook AccountManageActivity$19 失败！" + e.toString());
                }
                //endregion

                //region hook工具类（没验证）。这个其实没啥影响。
                //<editor-fold desc="工具类里判断数量的方法">
                //if (Utils.c()) {
                //                QQToast.a(this.f41040a, (int) R.string.c6a, 0).m23756a();
                //
//
//                public static boolean c() {
//                    List<SimpleAccount> allAccounts = MobileQQ.sMobileQQ.getAllAccounts();
//                    boolean z = allAccounts != null && allAccounts.size() >= 8;
//                    if (QLog.isColorLevel()) {
//                        QLog.d("Utils", 2, "isAccountNumExceedMax, isExceed=" + z);
//                    }
//                    return z;
//                }
                //</editor-fold>
                //原理直接返回false。但是.c()这个非常不可靠，当更新了之后（在这个方法前增加新方法）可能就失效了。
                try {
                    allHooked.add(findAndHookMethod("com.tencent.mobileqq.util.Utils",
                            mLoadPackageParam.classLoader, "c", new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    MyXposedUtils.logD("Utils.c() 调用了，直接返回false");
                                    param.setResult(false);
                                }
                            }));
                } catch (Exception e) {
                    MyXposedUtils.logD("尝试Hook Utils.c() 失败！" + e.toString());
                }
                //endregion

                //region 还有个要处理的点，太麻烦，算了。
                //还有个位置：com.tencent.mobileqq.activity.registerGuideLogin 里面也有个判断，但是不好hook处理
                //代码如下：
//                private void h() {
//                    String str;
//                    boolean z;
//                    if (this.f41129i) {
//                        str = this.f41040a.getIntent().getStringExtra("uin");
//                        if (!this.f41040a.getIntent().getBooleanExtra("hasPwd", true)) {
//                            a(null, this.f41040a.getString(R.string.fzt), this.f41040a.getString(R.string.xzz), this);
//                            return;
//                        }
//                    } else {
//                        str = this.f41077a.getText().toString();
//                    }
//                    if (str == null || str.length() == 0 || str.trim().length() == 0) {
//                        QQToast.a(this.f41040a, (int) R.string.clh, 0).m23756a();
//                        this.f41077a.requestFocus();
//                        this.f41076a.showSoftInput(this.f41077a, 2);
//                    } else if (str.startsWith("0")) {
//                        QQToast.a(this.f41040a, (int) R.string.bzh, 0).m23756a();
//                        this.f41077a.requestFocus();
//                        this.f41076a.showSoftInput(this.f41077a, 2);
//                    } else if (this.f41090a.getText().toString().length() < 1) {
//                        QQToast.a(this.f41040a, (int) R.string.cqq, 0).m23756a();
//                        this.f41090a.requestFocus();
//                        this.f41076a.showSoftInput(this.f41090a, 2);
//                    } else {
//                        int size = this.f41093a.size();
//                        int i2 = 0;
//                        while (true) {
//                            if (i2 < size) {
//                                SimpleAccount simpleAccount = this.f41093a.get(i2);
//                                if (simpleAccount != null && str.equals(simpleAccount.getUin())) {
//                                    z = false;
//                                    break;
//                                }
//                                i2++;
//                            } else {
//                                z = true;
//                                break;
//                            }
//                        }
//                        if (z) {
//                            size++;
//                        }
//                        if (size > 8) { //这里是比较点，处理这个就太麻烦了，也不知道行不行，还是算了。android上hook后貌似不像windows可以任意修改内存数据。
//                            QQToast.a(this.f41040a, (int) R.string.c6a, 0).m23756a();
//                            return;
//                        }
//                        if (this.f41091a == null && libsafeedit.checkPassLegal(LoginActivity.FAKE_PASSWORD)) {
//                            String obj = this.f41077a.getText().toString();
//                            int i3 = 0;
//                            while (true) {
//                                if (i3 < this.f41093a.size()) {
//                                    if (this.f41093a.get(i3) != null && this.f41093a.get(i3).getUin() != null && this.f41093a.get(i3).getUin().equals(obj)) {
//                                        this.f41091a = this.f41093a.get(i3);
//                                        break;
//                                    }
//                                    i3++;
//                                } else {
//                                    break;
//                                }
//                            }
//                        }
//                        if (QLog.isColorLevel()) {
//                            QLog.d("userguide", 2, "login");
//                        }
//                        LoginStaticField.a(1);
//                        if (this.f41091a != null) {
//                            LoginSetting.f136130a = false;
//                            this.f41040a.showDialog(0);
//                            if (this.f41042a != null) {
//                                this.f41042a.login(this.f41091a);
//                            }
//                        } else {
//                            LoginSetting.f136130a = true;
//                            try {
//                                this.f41040a.showDialog(0);
//                            } catch (Exception e2) {
//                                e2.printStackTrace();
//                            }
//                            byte[] byteSafeEditTextToMD5 = libsafeedit.byteSafeEditTextToMD5(true);
//                            AppRuntime appRuntime = this.f41040a.getAppRuntime();
//                            if (appRuntime == null) {
//                                QLog.e("LoginActivity.LoginView", 1, "login() appRuntime is null");
//                            } else if (RegisterLimitHelperImpl.a().a(this.s, str, this.f41117d)) {
//                                appRuntime.login(RegisterLimitHelperImpl.a().a(this.f41117d), str, byteSafeEditTextToMD5, (AccountObserver) null);
//                            } else {
//                                appRuntime.login(str, byteSafeEditTextToMD5, null);
//                            }
//                        }
//                        this.f41084a.a(this.f41040a);
//                    }
//                }
                //endregion

            }
        }
    }

    public void unhook() {
        for (XC_MethodHook.Unhook single : allHooked) {
            single.unhook();
        }
        allHooked.clear();
    }

    ////////////////////////////////////////////////////
}
