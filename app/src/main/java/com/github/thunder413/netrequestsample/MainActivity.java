package com.github.thunder413.netrequestsample;

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
import com.github.thunder413.netrequest.RequestDataType;
import com.github.thunder413.netrequest.RequestMethod;

import java.util.HashMap;
import java.util.Map;

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
        Map<String,Object> map = new HashMap<>();
        map.put("id",1);
        map.put("username","john");
        map.put("password","*****");
        netRequest.addParameterSet(map);
        // Set Request method #NetRequest.METHOD_POST | #NetRequest.METHOD_GET (Default)
        netRequest.setRequestMethod(RequestMethod.POST);
        netRequest.setRequestDataType(RequestDataType.XML);
        // Bind Listener
        netRequest.setOnResponseListener(new OnNetResponse() {
            @Override
            public void onNetResponseCompleted(NetResponse response) {
                // Get response as string
                Log.d("TAG",response.toString());
            }

            @Override
            public void onNetResponseError(NetError error) {
                Log.d("TAG",error.toString());
                // Handle error
                switch (error.getStatus()) {
                    case CONNECTION_ERROR:
                        break;
                    case PARSE_ERROR:
                        break;
                    case ERROR:
                        break;
                    case INVALID_URI_ERROR:
                        break;
                    case SERVER_ERROR:
                        break;
                    case NOT_FOUND:
                        break;
                    case BAD_GATEWAY:
                        break;
                    case REQUEST_ERROR:
                        break;
                    case CANCELED:
                        break;
                }
            }
        });
        // Set Request
        netRequest.setRequestUri("http://192.168.1.11/string_radom/xml.php");
        // Trigger request
        netRequest.load();
        // You can also pass the uri directly to load method


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
