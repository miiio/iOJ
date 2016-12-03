package com.ioj.wax.ioj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProblemsView extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Pbv_Theme);
        setContentView(R.layout.problemsview);
        Intent getIntent = getIntent();
        TextView tx_title = (TextView)findViewById(R.id.problemsview_title);
        String title = getIntent.getStringExtra("title");
        tx_title.setText(title);
    }
}
