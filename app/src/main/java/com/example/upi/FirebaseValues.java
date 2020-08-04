package com.example.upi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class FirebaseValues extends AppCompatActivity implements View.OnClickListener {

    TextView tv2,place,density,viscosity,quality,quantity;
    Button save,shares,screenshot;
    String mass,volume,name,date,time;
    Firebase mref;
    private static DecimalFormat df = new DecimalFormat("0.000");
    private static DecimalFormat df2 = new DecimalFormat("0.00000");
    public static Float v1,v2,den,vis,grad;
    final static float actual_viscosity = (float) 0.00890;
    final static float actual_density = (float) 1.0;
    int count=0;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_values);
        Firebase.setAndroidContext(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv2 = findViewById(R.id.tv2);

        place = findViewById(R.id.place);
        density = findViewById(R.id.density);
        viscosity = findViewById(R.id.viscosity);
        quantity = findViewById(R.id.quantity);
        quality = findViewById(R.id.quality);

        save = findViewById(R.id.save);
        save.setOnClickListener(this);

        String n = qr.result.getText().toString();


        db = openOrCreateDatabase
                ("ResultDB", Context.MODE_PRIVATE, null);

        db.execSQL
                ("CREATE TABLE IF NOT EXISTS " +
                        "result(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "date VARCHAR,time VARCHAR,place VARCHAR,quality VARCHAR,quantity VARCHAR);");


        Log.v("URL",n);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String dateTime = simpleDateFormat.format(calender.getTime());
        tv2.setText(dateTime);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        SimpleDateFormat simpleDateFormat2= new SimpleDateFormat("dd-MMM-yyyy");
        date = simpleDateFormat2.format(calender.getTime());

        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("hh:mm a");
        time = simpleDateFormat3.format(calender.getTime());

        mref = new Firebase(n);

                mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Map<String, String> map = dataSnapshot.getValue(Map.class);

                        String s1[] = map.get("Name").split("[,]",0);

                        name = s1[0]+" "+s1[1]+" "+s1[2];
                        place.setText(name);

                        count = Integer.parseInt(s1[2]);

                        String s2[] = map.get("values").split("[,]",0);

                        mass=s2[0];
                        volume=s2[1];

                        v1 = Float.parseFloat(mass);//mass
                        v2 = Float.parseFloat(volume);//volume

                        quantity.setText(getApplicationContext().getResources().getString(R.string.quantity)+String.valueOf(df.format(v2)+" mL"));
                        Log.v("E_VAL","v1 "+ v1);
                        Log.v("E_VAL","v2 "+ v2);

                        Density(v1,v2);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError)
                    {
                    }
                });

        shares= findViewById(R.id.shares);
        shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (count > 0)
                {
                    if (tv2.getText().toString().trim().length() == 0 ||
                            date.trim().length() == 0 ||
                            time.trim().length() == 0 ||
                            quality.getText().toString().trim().length() == 0 ||
                            quantity.getText().toString().trim().length() == 0) {
                        showMessage("Error", "Please enter all values");
                        return;
                    }
                    else
                    {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        StringBuilder sb = new StringBuilder();
                        sb.append("Sent from Ultra Pure Indicator app" + "\n");
                        sb.append("Date: " + date + "\n");
                        sb.append("Time: " + time + "\n");
                        sb.append("Place: " + place.getText() + "\n");
                        sb.append(quality.getText() + "\n");
                        sb.append(quantity.getText() + "\n\n");
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Test");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                        startActivity(Intent.createChooser(sharingIntent, "Share with"));
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Click SHARE after 2 seconds!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        screenshot = findViewById(R.id.screenshot);
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                View view1 = getWindow().getDecorView().getRootView();
                view1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
                view1.setDrawingCacheEnabled(false);

                String filePath = Environment.getExternalStorageDirectory()+"/Download/"+"UPI "+Calendar.getInstance().getTime().toString()+".jpg";
                File fileScreenshot = new File(filePath);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(fileScreenshot);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(fileScreenshot);
                intent.setDataAndType(uri,"image/jpeg");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    public void Density(float m,float v)
    {
        den=v1/v2;
        density.setText(getApplicationContext().getResources().getString(R.string.density)+String.valueOf(df.format(den)+" g/cm\u00B3"));
        Viscosity(den);
        Grade(den);
    }

    public void Viscosity(float den)
    {
        vis=actual_viscosity/den;
        Log.v("VISCOSITY",String.valueOf(vis));
        viscosity.setText(getApplicationContext().getResources().getString(R.string.viscosity)+String.valueOf(df2.format(vis)+" cm\u00B2/s"));
    }

    public void Grade(float den)
    {
        grad = (Math.abs(den-actual_density)/actual_density)*100;
        Log.v("Grade",String.valueOf(grad));
        if(grad <= 10)
        {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String red = "Grade A";
            SpannableString redSpannable= new SpannableString(red);
            redSpannable.setSpan(new ForegroundColorSpan( getResources().getColor(R.color.grade)), 0, red.length(), 0);
            builder.append(getApplicationContext().getResources().getString(R.string.quality));
            builder.append(redSpannable);
            quality.setText(builder, TextView.BufferType.SPANNABLE);
        }
        else if(grad >10 && grad <= 20)
        {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String red = "Grade B";
            SpannableString redSpannable= new SpannableString(red);
            redSpannable.setSpan(new ForegroundColorSpan( getResources().getColor(R.color.grade)), 0, red.length(), 0);
            builder.append(getApplicationContext().getResources().getString(R.string.quality));
            builder.append(redSpannable);
            quality.setText(builder, TextView.BufferType.SPANNABLE);
        }
        else if(grad >20 && grad <= 30)
        {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String red = "Grade C";
            SpannableString redSpannable= new SpannableString(red);
            redSpannable.setSpan(new ForegroundColorSpan( getResources().getColor(R.color.grade)), 0, red.length(), 0);
            builder.append(getApplicationContext().getResources().getString(R.string.quality));
            builder.append(redSpannable);
            quality.setText(builder, TextView.BufferType.SPANNABLE);
        }
        else
        {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            String red = "Grade D";
            SpannableString redSpannable= new SpannableString(red);
            redSpannable.setSpan(new ForegroundColorSpan( getResources().getColor(R.color.grade)), 0, red.length(), 0);
            builder.append(getApplicationContext().getResources().getString(R.string.quality));
            builder.append(redSpannable);
            quality.setText(builder, TextView.BufferType.SPANNABLE);
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
    }

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialog);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onClick(View view)
    {
        if(count>0)
        {
            if (tv2.getText().toString().trim().length() == 0 ||
                    date.trim().length() == 0 ||
                    time.trim().length() == 0 ||
                    quality.getText().toString().trim().length() == 0 ||
                    quantity.getText().toString().trim().length() == 0)
            {
                showMessage("Error", "Please enter all values");
                return;
            }
            else {
                db.execSQL("INSERT INTO result (date,time,place,quality,quantity) VALUES('" + date + "','" + time + "','" + place.getText() + "','" + quality.getText()+
                        "','" + quantity.getText() + "');");
                showMessage("Success", "Record Added");
                startActivity(new Intent(getApplicationContext(),History.class));
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Click SAVE after 2 seconds!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.info:
                StringBuffer buffer = new StringBuffer();
                    buffer.append("  0 < Grade A "+getApplicationContext().getResources().getString(R.string.lessThanOrEqual)+" 10"+ "\n");
                    buffer.append("10 < Grade B "+getApplicationContext().getResources().getString(R.string.lessThanOrEqual)+" 20"+ "\n");
                    buffer.append("20 < Grade C "+getApplicationContext().getResources().getString(R.string.lessThanOrEqual)+" 30"+ "\n");
                    buffer.append("30 < Grade D "+ "\n");
                AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialog2);
                builder.setCancelable(true);
                builder.setTitle("Quality Grades");
                builder.setMessage(buffer.toString());
                builder.show();
            default:return super.onOptionsItemSelected(item);

        }
    }

}