package com.couchbase.cblite.cbliteconsole;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class CBLiteConsoleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cblite_console);
/*
        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(".......");

        // Set the text view as the activity layout
        setContentView(textView);
*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cblite_console, menu);
        return true;
    }
    
}
