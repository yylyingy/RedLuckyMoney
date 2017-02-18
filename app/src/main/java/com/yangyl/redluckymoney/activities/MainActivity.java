package com.yangyl.redluckymoney.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import com.yangyl.redluckymoney.R;

import java.util.List;

public class MainActivity extends Activity implements AccessibilityManager.AccessibilityStateChangeListener {

    //开关切换按钮
    private Button switchPlugin;
    //AccessibilityService 管理
    private AccessibilityManager accessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchPlugin = (Button) findViewById(R.id.button_accessible);

        handleMaterialStatusBar();

        explicitlyLoadPreferences();

        //监听AccessibilityService 变化
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        updateServiceStatus();
    }

    private void explicitlyLoadPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, false);
    }

    /**
     * 适配沉浸状态栏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleMaterialStatusBar() {
        // Not supported in APK level lower than 21
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(0xffd84e43);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();

        // Check for update when WIFI is connected or on first time.
//        if (ConnectivityUtil.isWifi(this) || UpdateTask.count == 0)
//            new UpdateTask(this, false).update();
    }

    @Override
    protected void onDestroy() {
        //移除监听服务
        accessibilityManager.removeAccessibilityStateChangeListener(this);
        super.onDestroy();
    }

    public void onButtonClicked(View view) {
        Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(accessibleIntent);
    }

    public void openGithub(View view) {
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra("title", "Github项目主页");
        webViewIntent.putExtra("url", "https://github.com/yylyingy/RedLuckyMoney");
        startActivity(webViewIntent);
    }

    public void openGithubReleaseNotes(View view) {
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra("title", "发布日志");
        webViewIntent.putExtra("url", "https://github.com/yylyingy/RedLuckyMoney");
        startActivity(webViewIntent);
    }

    public void openSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra("title", "偏好设置");
        settingsIntent.putExtra("frag_id", "GeneralSettingsFragment");
        startActivity(settingsIntent);
    }

    public void openNews(View view) {
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra("title", "红包攻略");
        webViewIntent.putExtra("url", "http://sec-cdn.static.xiaomi.net/secStatic/proj/luckyNewsInfo/0127/index.html?v=1&");
        startActivity(webViewIntent);
    }

    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }

    /**
     * 更新当前 HongbaoService 显示状态
     */
    private void updateServiceStatus() {
        if (isServiceEnabled()) {
            switchPlugin.setText(R.string.service_off);
        } else {
            switchPlugin.setText(R.string.service_on);
        }
    }

    /**
     * 获取 RedLuckyMoney 是否启用状态
     *
     * @return
     */
    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.services.RedLuckyMoneyService")) {
                return true;
            }
        }
        return false;
    }
}