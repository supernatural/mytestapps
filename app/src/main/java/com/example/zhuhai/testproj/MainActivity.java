package com.example.zhuhai.testproj;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        mText = (TextView)findViewById(R.id.text);
        mText.setText(queryLauncherInfo());
        mText.append(".........................................\n");
        //mText.append(queryPkgs());
        mText.append(android.os.Environment.getExternalStorageDirectory().getAbsolutePath());
        mText.append("\n");
        mText.append(android.os.Environment.getDataDirectory().getAbsolutePath());

        SearchManager searchManager =
            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = (ComponentName) ReflectHelper.invokeMethod(
                searchManager, "getWebSearchActivity", null, null);
        SearchableInfo searchable = searchManager.getSearchableInfo(name);
        mText.append("\nWebSearchActivity name:" + name + "\nsearchable: " + searchable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mPkgs, new IntentFilter(Intent.ACTION_PACKAGE_CHANGED));

        /*
        final Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true)
                .setContentText("Test Notification")
                .setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1001, builder.build()); */
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mPkgs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String queryLauncherInfo() {
        final Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> infos = getPackageManager().queryIntentActivities(main, 0);
        StringBuilder sb = new StringBuilder();
        for (ResolveInfo info : infos) {
            sb.append(info.activityInfo.name).append("\n");
        }

        return sb.toString();
    }

    BroadcastReceiver mPkgs = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
                 String buff = TextUtils.join(",", intent.getStringArrayExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST));
                 buff += ", uid=";
                 buff += intent.getIntExtra(Intent.EXTRA_UID, -1);

                 mText.append("\n");
                 mText.append(buff);
             }
        }
    };

    private String queryPkgs() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        StringBuilder sb = new StringBuilder();

        for (PackageInfo info : list) {
            sb.append(info.packageName).append("\n");
        }

        return sb.toString();
    }
}
