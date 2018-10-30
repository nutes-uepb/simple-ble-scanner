package br.edu.uepb.nutes.simplebleconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import br.edu.uepb.nutes.simplebleconnect.examples.ExampleBLEScanner;

public class Home extends AppCompatActivity {

    private ImageView btnScan;
    private ImageView btnManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ExampleBLEScanner.class));
            }
        });

        btnManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(Home.this, ExampleBLEManager.class));
            }
        });
    }

    private void initViews() {
        btnScan = findViewById(R.id.btn_scan);
        btnManager = findViewById(R.id.btn_manager);
    }


}
