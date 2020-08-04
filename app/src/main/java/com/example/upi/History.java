package com.example.upi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class History extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase db;
    EditText deltext;
    StringBuffer buffer;
    Button viewall,delete,deleteall,share;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = openOrCreateDatabase
                ("ResultDB", Context.MODE_PRIVATE, null);

        db.execSQL
                ("CREATE TABLE IF NOT EXISTS " +
                        "result(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "date VARCHAR,time VARCHAR,place VARCHAR,quality VARCHAR,quantity VARCHAR);");


        deltext = findViewById(R.id.deltext);

        viewall = findViewById(R.id.viewall);
        viewall.setOnClickListener(this);

        delete = findViewById(R.id.delete); // DELETE
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   String table = "result";
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";

                Cursor mCursor = db.rawQuery(sql, null);

                if (mCursor.getCount() > 0)
                {
                    if (deltext.getText().toString().trim().length() == 0) {
                        errorMessage("Error", "Please Enter Id Number");
                        return;
                    }
                    // Searching roll number 
                    Cursor c = db.rawQuery("SELECT *" +
                            " FROM result WHERE id='" + deltext.getText().toString() + "'", null);

                    if (c.moveToFirst()) {

                        showMessage("Success", "Record Deleted");
                        db.execSQL("DELETE FROM result WHERE id='" +
                                deltext.getText() + "'");
                    }
                    else {
                        errorMessage("Error", "Invalid Id Number");
                    }
                }
                else if (mCursor.getCount() <= 0)
                {
                    errorMessage("Error", "No record has been Saved Yet!");
                }
                clearText();
                mCursor.close();
            }
        });

        deleteall = findViewById(R.id.deleteall);//Delete ALL
        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   String table = "result";
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";

                Cursor mCursor = db.rawQuery(sql, null);

                if (mCursor.getCount() > 0)
                {
                    Cursor c = db.rawQuery
                            ("SELECT * FROM result",
                                    null);

                    if (c.getCount() == 0) {
                        errorMessage("Error", "Table is Already Empty!!");
                        return;
                    } else {
                        db.execSQL("DROP TABLE result");
                        showMessage("Success", "All Records Deleted");
                    }
                }
                else if (mCursor.getCount() <= 0)
                {
                    errorMessage("Error", "No record has been Saved Yet!");
                }
                clearText();
                mCursor.close();
            }
        });

        share = findViewById(R.id.share); // SHARE
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String table = "result";
                String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";

                Cursor mCursor = db.rawQuery(sql, null);

                if (mCursor.getCount() > 0)
                {
                    if (deltext.getText().toString().trim().length() == 0) {
                        errorMessage("Error", "Please Enter Id Number");
                        return;
                    }
                    // Searching roll number 
                    Cursor c = db.rawQuery
                            ("SELECT * FROM result" +
                                    " WHERE id='" +
                                    deltext.getText()
                                    + "'", null);

                    if (c.getCount() == 0) {
                        errorMessage("Error", "Invalid Id Number");
                        return;
                    }

                    buffer = new StringBuffer();
                    while (c.moveToNext())
                    {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        StringBuilder sb = new StringBuilder();
                        sb.append("Sent from Ultra Pure Indicator app" + "\n");
                        sb.append("Id: " + c.getString(0) + "\n");
                        sb.append("Date: " + c.getString(1) + "\n");
                        sb.append("Time: " + c.getString(2) + "\n");
                        sb.append("Place: " + c.getString(3) + "\n");
                        sb.append(c.getString(4) + "\n");
                        sb.append(c.getString(5) + "\n\n");
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Test");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                        startActivity(Intent.createChooser(sharingIntent, "Share with"));
                    }
                }
                else if (mCursor.getCount() <= 0)
                {
                    errorMessage("Error", "No record has been Saved Yet!");
                }
                clearText();
                mCursor.close();
            }
        });

    }

    @Override
    public void onClick(View view)// VIEW ALL
    {
        String table = "result";
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'";

        Cursor mCursor = db.rawQuery(sql, null);

        if (mCursor.getCount() > 0)
        {
            Cursor c = db.rawQuery
                    ("SELECT * FROM result",
                            null);

            if (c.getCount() == 0) {
                errorMessage("Error", "No record has been Saved Yet!");
                return;
            }

            StringBuffer buffer = new StringBuffer();
            while (c.moveToNext()) {
                buffer.append("Id: " + c.getString(0) + "\n");
                buffer.append("Date: " + c.getString(1) + "\n");
                buffer.append("Time: " + c.getString(2) + "\n");
                buffer.append("Place: " + c.getString(3) + "\n");
                buffer.append(c.getString(4) + "\n");
                buffer.append(c.getString(5) + "\n\n");
            }
            generalMessage("Customer History", buffer.toString());
        }
        else if (mCursor.getCount() <= 0)
        {
            errorMessage("Error", "No record has been Saved Yet!");
        }
        clearText();
        mCursor.close();
    }
    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialog);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void errorMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialog3);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void generalMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialog4);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onBackPressed()
    {
        Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
    }
    public void clearText(){

        deltext.setText("");
    }
}
