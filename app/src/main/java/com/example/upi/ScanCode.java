package com.example.upi;


import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

public class ScanCode extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sv = new ZXingScannerView(this);
        setContentView(sv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void handleResult(Result result)
    {
        qr.result.setText(result.getText());
        Intent i = new Intent(ScanCode.this,FirebaseValues.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sv.setResultHandler(this);
        sv.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sv.stopCamera();
    }
}
