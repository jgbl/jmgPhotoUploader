package org.de.jmg.jmgphotouploader.DropBox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;

import org.de.jmg.jmgphotouploader.LoginGoogleActivity;
import org.de.jmg.jmgphotouploader.R;
import org.de.jmg.jmgphotouploader.secrets;


/**
 * Activity that shows information about the currently logged in user
 */
public class DropBoxUserActivity extends DropboxActivity {

    public static int requestCode = 9991;
    private int GroupPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GroupPosition = getIntent().getExtras().getInt("GroupPosition");
        setContentView(R.layout.activity_user);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        //setSupportActionBar(toolbar);

        Button loginButton = (Button)findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.startOAuth2Authentication(DropBoxUserActivity.this, secrets.DropBAppkey);
            }
        });

        Button filesButton = (Button)findViewById(R.id.files_button);
        filesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(FilesActivity.getIntent(DropBoxUserActivity.this, ""));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasToken()) {
            findViewById(R.id.login_button).setVisibility(View.GONE);
            findViewById(R.id.email_text).setVisibility(View.VISIBLE);
            findViewById(R.id.name_text).setVisibility(View.VISIBLE);
            findViewById(R.id.type_text).setVisibility(View.VISIBLE);
            findViewById(R.id.files_button).setEnabled(true);
        } else {
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_text).setVisibility(View.GONE);
            findViewById(R.id.name_text).setVisibility(View.GONE);
            findViewById(R.id.type_text).setVisibility(View.GONE);
            findViewById(R.id.files_button).setEnabled(false);
        }
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                ((TextView) findViewById(R.id.email_text)).setText(result.getEmail());
                ((TextView) findViewById(R.id.name_text)).setText(result.getName().getDisplayName());
                ((TextView) findViewById(R.id.type_text)).setText(result.getAccountType().name());
                Intent i = new Intent();
                i.putExtra("GroupPosition", GroupPosition);
                DropBoxUserActivity.this.setResult(Activity.RESULT_OK, i);
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
                Intent i = new Intent();
                i.putExtra("GroupPosition", GroupPosition);
                DropBoxUserActivity.this.setResult(Activity.RESULT_CANCELED, i);
            }
        }).execute();
    }

}
