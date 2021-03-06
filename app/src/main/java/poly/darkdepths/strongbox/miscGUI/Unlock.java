package poly.darkdepths.strongbox.miscGUI;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

import info.guardianproject.iocipher.VirtualFileSystem;
import poly.darkdepths.strongbox.Globals;
import poly.darkdepths.strongbox.R;
import poly.darkdepths.strongbox.Security;

/**
 * Activity for unlocking Strongbox. Loads keys and salt, sets up IOCipher
 * and SQLCipher
 */
public class Unlock extends ActionBarActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlock);
        watch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void watch() {
        EditText passwordField = (EditText) findViewById(R.id.password_prompt);
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView warning = (TextView) findViewById(R.id.pass_warn);
                warning.setText("");

                Button createButton = (Button) findViewById(R.id.unlock_button);
                createButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
    });

}

    public void checkPass(View view){
        EditText passwordField = (EditText) findViewById(R.id.password_prompt);
        String password = passwordField.getText().toString();
        try {
            // get Globals
            Globals appState = (Globals) getApplicationContext();
            Security securestore = appState.getSecurestore();
            securestore.generateKey(password.toCharArray(),
                    Security.getSalt(this.getBaseContext()));

            // load encrypted SQLlite database
            // TODO get rid of sqlcipher, use SQLITE within VFS
            SQLiteDatabase.loadLibs(this);
            File databaseFile = getDatabasePath(appState.getDatabaseName());

            // attempt to open database, this will fail if password is wrong
            // or if database is otherwise corrupted
            SQLiteDatabase database = SQLiteDatabase.openDatabase(
                    databaseFile.getPath(),
                    new String(securestore.getKey().getEncoded()), null, 0);

            // error returned if database isn't closed correctly
            database.close();

            // attempt to open virtual file system
            VirtualFileSystem vfs = appState.getVFS();
            vfs.mount(appState.getDbFile(), securestore.getKey().getEncoded());

            finish();
        } catch (Exception e) {
            e.printStackTrace();
            TextView warning = (TextView) findViewById(R.id.pass_warn);
            warning.setText("Wrong password or corrupted database");
            Button createButton = (Button) findViewById(R.id.unlock_button);
            createButton.setEnabled(false);
        }
    }
}
