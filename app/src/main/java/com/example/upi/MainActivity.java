package com.example.upi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    DrawerLayout mDrawerlayout;
    ActionBarDrawerToggle mToggle;
    GridView gridView;
    Toolbar toolbar;
    NavigationView nav_view;
    static int count=0;
    Switch button;
    SharedPreferences sharedPreferences = null;
    boolean doubleBackToExitPressedOnce = false;
    static final int REQUEST_CODE = 123;

    String letterList[] = {"QR","Permissions","About","History"};
    int letterIcon[] = {R.drawable.qr, R.drawable.access, R.drawable.about,R.drawable.history};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)+
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)+
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)+
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CAMERA)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.SEND_SMS)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.INTERNET)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
                builder.setTitle("This APP requires the following Permissions");
                builder.setMessage("Camera, SMS, Read Contacts, Internet, Read External Storage");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.SEND_SMS,
                                        Manifest.permission.INTERNET,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                },REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else
            {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },REQUEST_CODE
                );
            }
        }
        else
        {
        }


        nav_view=findViewById(R.id.nav_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.activity_switch);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);


        sharedPreferences = getSharedPreferences("night",0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode",true);

        button = (Switch) findViewById(R.id.switch1);
        if (booleanValue)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            button.setChecked(true);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            button.setChecked(false);
        }
        button.setOnCheckedChangeListener(this);

        nav_view.getMenu().getItem(0).setChecked(true);

        mDrawerlayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, toolbar, R.string.Open, R.string.Close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();

        gridView = findViewById(R.id.gridview1);
        GridAdapter adapter = new GridAdapter(MainActivity.this, letterIcon, letterList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(letterList[position] == letterList[0])//QR
                {
                    Intent i = new Intent(MainActivity.this, qr.class);
                    startActivity(i);
                }
                if(letterList[position] == letterList[1])//Permissions
                {
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",
                            getPackageName(),null);
                    i.setData(uri);
                    startActivity(i);
                }
                if(letterList[position] == letterList[2])//About
                {
                    Intent i = new Intent(MainActivity.this, About.class);
                    startActivity(i);
                }
                if(letterList[position] == letterList[3])//History
                {
                    Intent i = new Intent(MainActivity.this, History.class);
                    startActivity(i);
                }

            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {

        }
        else if (id == R.id.nav_qr) {
            Intent i = new Intent(MainActivity.this, qr.class);
            startActivity(i);

        }
        else if (id == R.id.nav_share)
        {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            StringBuilder sb = new StringBuilder();
            sb.append("Hi, I am using Stock Predictor. I like this and I want you to check it out.");
            sb.append("https://play.google.com/store/apps/details?id=" + this.getBaseContext().getPackageName());
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Test");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(sharingIntent, "Share with"));
        }
        else if (id == R.id.nav_about) {
            Intent i = new Intent(MainActivity.this, About.class);
            startActivity(i);
        }
        else if (id == R.id.nav_history) {
            Intent i = new Intent(MainActivity.this, History.class);
            startActivity(i);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
    {
        if (b)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            button.setChecked(true);
            editor.putBoolean("night_mode",true);
            editor.commit();
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);
            overridePendingTransition(0, 0);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            button.setChecked(false);
            editor.putBoolean("night_mode",false);
            editor.commit();
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);
            overridePendingTransition(0, 0);

        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}