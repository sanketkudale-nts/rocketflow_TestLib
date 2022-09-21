package com.tracki.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.tracki.data.local.prefs.PreferencesHelper;

public class AutoStartHelper {
    /**
     * Xiaomi
     */
    private static final String BRAND_XIAOMI = "xiaomi";
    private static final String PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter";
    private static final String PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity";

    /**
     * Letv
     */
    private static final String BRAND_LETV = "letv";
    private static final String PACKAGE_LETV_MAIN = "com.letv.android.letvsafe";
    private static final String PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManageActivity";

    /**
     * ASUS ROG
     */
    private static final String BRAND_ASUS = "asus";
    private static final String PACKAGE_ASUS_MAIN = "com.asus.mobilemanager";
    private static final String PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings";

    /**
     * Honor
     */
    private static final String BRAND_HONOR = "honor";
    private static final String PACKAGE_HONOR_MAIN = "com.huawei.systemmanager";
    private static final String PACKAGE_HONOR_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity";

    /**
     * Oppo
     */
    private static final String BRAND_OPPO = "oppo";
    private static final String PACKAGE_OPPO_MAIN = "com.coloros.safecenter";
    private static final String PACKAGE_OPPO_FALLBACK = "com.oppo.safe";
    private static final String PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity";
    // more addition
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK_B = "com.coloros.safe.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK_C = "com.coloros.safe.permission.startupapp.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK_D = "com.coloros.safe.permission.startupmanager.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_1 = "com.coloros.safecenter.permission.startup.FakeActivity";
    private static final String PACKAGE_OPPO_COMPONENT_2 = "com.coloros.safecenter.permission.startupapp.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_3 = "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_4 = "com.coloros.safecenter.permission.startsettings";
    private static final String PACKAGE_OPPO_COMPONENT_5 = "com.coloros.safecenter.permission.startupapp.startupmanager";
    private static final String PACKAGE_OPPO_COMPONENT_6 = "com.coloros.safecenter.permission.startupmanager.startupActivity";
    private static final String PACKAGE_OPPO_COMPONENT_7 = "com.coloros.safecenter.permission.startup.startupapp.startupmanager";
    private static final String PACKAGE_OPPO_COMPONENT_8 = "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager";
    private static final String PACKAGE_OPPO_COMPONENT_9 = "com.coloros.privacypermissionsentry.PermissionTopActivity";
    private static final String PACKAGE_OPPO_COMPONENT_10 = "com.coloros.safecenter.FakeActivity";
    private static final String PACKAGE_OPPO_COMPONENT_11 = "com.oppo.safe.permission.floatwindow.FloatWindowListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_12 = "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_13 = "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity";

    /**
     * Vivo
     */
    private static final String BRAND_VIVO = "vivo";
    private static final String PACKAGE_VIVO_MAIN = "com.iqoo.secure";
    private static final String PACKAGE_VIVO_FALLBACK = "com.vivo.permissionmanager";
    private static final String PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager";

    /**
     * Nokia
     */
    private static final String BRAND_NOKIA = "nokia";
    private static final String PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3";
    private static final String PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity";
    private PreferencesHelper preferencesHelper;

    private AutoStartHelper() {
    }

    public static AutoStartHelper getInstance() {
        return new AutoStartHelper();
    }

    public void getAutoStartPermission(Context context, PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
        String build_info = Build.BRAND.toLowerCase();
        switch (build_info) {
            case BRAND_ASUS:
                autoStartAsus(context);
                break;
            case BRAND_XIAOMI:
                autoStartXiaomi(context);
                break;
            case BRAND_LETV:
                autoStartLetv(context);
                break;
            case BRAND_HONOR:
                autoStartHonor(context);
                break;
            case BRAND_OPPO:
                autoStartOppo(context);
                break;
            case BRAND_VIVO:
                autoStartVivo(context);
                break;
            case BRAND_NOKIA:
                autoStartNokia(context);
                break;
        }
    }

    private void autoStartAsus(final Context context) {
//        if (isPackageExists(context, PACKAGE_ASUS_MAIN)) {
//            showAlert(context, (dialog, which) -> {
        try {
            preferencesHelper.saveAppAutoStartFlag(true);
            startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
//                dialog.dismiss();
//            });
//    }
    }

//    private void showAlert(Context context, DialogInterface.OnClickListener onClickListener) {
//
//        new AlertDialog.Builder(context).setTitle("Allow AutoStart")
//                .setMessage("Please enable auto start in settings.")
//                .setPositiveButton("Allow", onClickListener).show().setCancelable(false);
//    }

    private void autoStartXiaomi(final Context context) {
//        if (isPackageExists(context, PACKAGE_XIAOMI_MAIN)) {
//            showAlert(context, (dialog, which) -> {
        try {
            preferencesHelper.saveAppAutoStartFlag(true);
            startIntent(context, PACKAGE_XIAOMI_MAIN, PACKAGE_XIAOMI_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
//            });
//
//
//        }
    }

    private void autoStartLetv(final Context context) {
//        if (isPackageExists(context, PACKAGE_LETV_MAIN)) {
//            showAlert(context, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {

        try {
            preferencesHelper.saveAppAutoStartFlag(true);
            startIntent(context, PACKAGE_LETV_MAIN, PACKAGE_LETV_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
//                }
//            });
//
//
//        }
    }

    private void autoStartHonor(final Context context) {
//        if (isPackageExists(context, PACKAGE_HONOR_MAIN)) {
//            showAlert(context, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {

        try {
            preferencesHelper.saveAppAutoStartFlag(true);
            startIntent(context, PACKAGE_HONOR_MAIN, PACKAGE_HONOR_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
//                }
//            });
//
//
//        }
    }

    private void autoStartOppo(final Context context) {
        //com.coloros.safecenter.permission.singlepage.PermissionSinglePageActivity     listpermissions
        //com.coloros.privacypermissionsentry.PermissionTopActivity                     privacypermissions
        // getPackageManager().getLaunchIntentForPackage("com.coloros.safecenter");

//        if (isPackageExists(context, PACKAGE_OPPO_MAIN) || isPackageExists(context, PACKAGE_OPPO_FALLBACK)) {
//        showAlert(context, (dialog, which) -> {
        try {
            preferencesHelper.saveAppAutoStartFlag(true);
            startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                preferencesHelper.saveAppAutoStartFlag(true);
                startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    preferencesHelper.saveAppAutoStartFlag(true);
                    startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_FALLBACK_A);
                } catch (Exception exx) {
                    exx.printStackTrace();
                    try {
                        preferencesHelper.saveAppAutoStartFlag(true);
                        startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK_B);
                    } catch (Exception e1) {
                        try {
                            preferencesHelper.saveAppAutoStartFlag(true);
                            startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK_C);
                        } catch (Exception e2) {
                            try {
                                preferencesHelper.saveAppAutoStartFlag(true);
                                startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK_D);
                            } catch (Exception e3) {
                                try {
                                    preferencesHelper.saveAppAutoStartFlag(true);
                                    startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_1);
                                } catch (Exception e4) {
                                    try {
                                        preferencesHelper.saveAppAutoStartFlag(true);
                                        startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_2);
                                    } catch (Exception e5) {
                                        try {
                                            preferencesHelper.saveAppAutoStartFlag(true);
                                            startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_3);
                                        } catch (Exception e6) {
                                            try {
                                                preferencesHelper.saveAppAutoStartFlag(true);
                                                startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_4);
                                            } catch (Exception e7) {
                                                try {
                                                    preferencesHelper.saveAppAutoStartFlag(true);
                                                    startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_5);
                                                } catch (Exception e8) {
                                                    try {
                                                        preferencesHelper.saveAppAutoStartFlag(true);
                                                        startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_6);
                                                    } catch (Exception e9) {
                                                        try {
                                                            preferencesHelper.saveAppAutoStartFlag(true);
                                                            startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_7);
                                                        } catch (Exception e10) {
                                                            try {
                                                                preferencesHelper.saveAppAutoStartFlag(true);
                                                                startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_8);
                                                            } catch (Exception e11) {
                                                                e11.printStackTrace();
                                                                try {
                                                                    preferencesHelper.saveAppAutoStartFlag(true);
                                                                    startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_9);
                                                                } catch (Exception e12) {
                                                                    e12.printStackTrace();
                                                                    try {
                                                                        preferencesHelper.saveAppAutoStartFlag(true);
                                                                        startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_10);
                                                                    } catch (Exception e13) {
                                                                        e13.printStackTrace();
                                                                        try {
                                                                            preferencesHelper.saveAppAutoStartFlag(true);
                                                                            startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_11);
                                                                        } catch (Exception e14) {
                                                                            e14.printStackTrace();
                                                                            try {
                                                                                preferencesHelper.saveAppAutoStartFlag(true);
                                                                                startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_12);
                                                                            } catch (Exception e15) {
                                                                                e15.printStackTrace();
                                                                                try {
                                                                                    preferencesHelper.saveAppAutoStartFlag(true);
                                                                                    startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_13);
                                                                                } catch (Exception e16) {
                                                                                    e16.printStackTrace();
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        });
    }
//    }

    private void autoStartVivo(final Context context) {
//        if (isPackageExists(context, PACKAGE_VIVO_MAIN) || isPackageExists(context, PACKAGE_VIVO_FALLBACK)) {
//            showAlert(context, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {

        try {
            preferencesHelper.saveAppAutoStartFlag(true);
            startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                preferencesHelper.saveAppAutoStartFlag(true);
                startIntent(context, PACKAGE_VIVO_FALLBACK, PACKAGE_VIVO_COMPONENT_FALLBACK);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    preferencesHelper.saveAppAutoStartFlag(true);
                    startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT_FALLBACK_A);
                } catch (Exception exx) {
                    exx.printStackTrace();
                }

            }

        }

//                }
//            });
//        }
    }

    private void autoStartNokia(final Context context) {
//        if (isPackageExists(context, PACKAGE_NOKIA_MAIN)) {
//            showAlert(context, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {

        try {
            preferencesHelper.saveAppAutoStartFlag(true);
            startIntent(context, PACKAGE_NOKIA_MAIN, PACKAGE_NOKIA_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//            });
//        }
//}

    private void startIntent(Context context, String packageName, String componentName) throws Exception {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, componentName));
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            throw var5;
        }
    }

//    private Boolean isPackageExists(Context context, String targetPackage) {
//        List<ApplicationInfo> packages;
//        PackageManager pm = context.getPackageManager();
//        packages = pm.getInstalledApplications(0);
//        for (ApplicationInfo packageInfo :
//                packages) {
//            if (packageInfo.packageName.equals(targetPackage)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
}
