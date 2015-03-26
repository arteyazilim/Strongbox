package poly.darkdepths.strongbox;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import java.io.File;

/**
 * Entry point for app
 */

public class MainActivity extends Activity{
    DataStore dataStore = new DataStore();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Globals   appState    = (Globals) getApplicationContext();
        Security  securestore = appState.getSecurestore();

        File file = new File("/data/data/poly.darkdepths.strongbox/databases/store.db");

        if (!((file.exists() && securestore.getSalt(getApplicationContext())  != null))) {
            // first time running app
            Intent intent = new Intent(MainActivity.this, Welcome.class);
            startActivity(intent);
        } else if (securestore.getKey() == null) {
            // password not in memory, prompt user
            Intent intent = new Intent(MainActivity.this, Unlock.class);
            startActivity(intent);
        } else {
            // all good, start recording
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        }


        // TODO remove this later
        //setContentView(R.layout.activity_main);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    public void openWelcome(View view) {
        Intent intent = new Intent(MainActivity.this, Welcome.class);
        startActivity(intent);
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

}

