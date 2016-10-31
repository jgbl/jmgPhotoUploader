package org.de.jmg.jmgphotouploader.DropBox;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.dropbox.core.android.Auth;

import org.de.jmg.jmgphotouploader.JMPPPApplication;


/**
 * Base class for Activities that require auth tokens
 * Will redirect to auth flow if needed
 */
public abstract class DropboxActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                prefs.edit().putString("access-token", accessToken).apply();
                initAndLoadData(accessToken);
            }
        } else {
            initAndLoadData(accessToken);
        }
    }

    private void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
        JMPPPApplication app = (JMPPPApplication)getApplication();
        app.setDropboxClient(DropboxClientFactory.getClient());
        //PicassoClient.init(getApplicationContext(), DropboxClientFactory.getClient());
        loadData();
    }

    protected abstract void loadData();

    protected boolean hasToken() {
        SharedPreferences prefs = getSharedPreferences("dropbox-sample", MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        return accessToken != null;
    }
}
