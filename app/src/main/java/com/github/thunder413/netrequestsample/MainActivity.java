package com.github.thunder413.netrequestsample;

import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.thunder413.netrequest.NetError;
import com.github.thunder413.netrequest.NetRequest;
import com.github.thunder413.netrequest.NetResponse;
import com.github.thunder413.netrequest.OnNetResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        NetRequest netRequest = new NetRequest(this);
        // Set Request method #NetRequest.METHOD_POST | #NetRequest.METHOD_GET (Default)
        netRequest.setRequestMethod(NetRequest.METHOD_POST);
        // Bind Listener
        netRequest.setOnResponseListener(new OnNetResponse() {
            @Override
            public void onNetResponseCompleted(NetResponse response) {
                // Get response as string
                Log.d("TAG",response.toString());
                // Get response as JsonObject
                response.toJson();
            }

            @Override
            public void onNetResponseError(NetError error) {
                // Handle error
                switch (error.getError()) {

                }
            }
        });
        // Set Request
        netRequest.setRequestUri("http://demo.com/json");
        // Trigger request
        netRequest.load();
        // You can also pass the uri directly to load method
        netRequest.load("http://demo.com/json");

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
}
