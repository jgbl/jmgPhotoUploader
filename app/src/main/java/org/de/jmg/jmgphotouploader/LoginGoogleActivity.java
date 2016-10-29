package org.de.jmg.jmgphotouploader;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginGoogleActivity extends Activity
        implements EasyPermissions.PermissionCallbacks {
    public static int requestCode = 9998;
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Drive API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {DriveScopes.DRIVE_READONLY,DriveScopes.DRIVE_METADATA_READONLY};
    private JMPPPApplication mApp;
    private int GroupPosition;

    /**
     *  * Create the main activity.
     *  * @param savedInstanceState previously saved instance data.
     *  
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GroupPosition = getIntent().getExtras().getInt("GroupPosition");
        mApp = (JMPPPApplication) getApplication();
        if (mApp.LoginGoogleClosed)
        {
            mApp.LoginGoogleClosed = false;
            this.finish();
        }
        else {
            LinearLayout activityLayout = new LinearLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            activityLayout.setLayoutParams(lp);
            activityLayout.setOrientation(LinearLayout.VERTICAL);
            activityLayout.setPadding(16, 16, 16, 16);

            ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
             /*
          mCallApiButton = new Button(this);
          mCallApiButton.setText(BUTTON_TEXT);
          mCallApiButton.setOnClickListener(new View.OnClickListener() {
           @Override
   public void onClick(View v) {
            mCallApiButton.setEnabled(false);
            mOutputText.setText("");
            getResultsFromApi();
            mCallApiButton.setEnabled(true);
           }
          });
          activityLayout.addView(mCallApiButton);
*/
            mOutputText = new TextView(this);
            mOutputText.setLayoutParams(tlp);
            mOutputText.setPadding(16, 16, 16, 16);
            mOutputText.setVerticalScrollBarEnabled(true);
            mOutputText.setMovementMethod(new ScrollingMovementMethod());
            //mOutputText.setText(
            //       "Click the \'" + BUTTON_TEXT + "\' button to test the API.");
            activityLayout.addView(mOutputText);

            mProgress = new ProgressDialog(this);
            mProgress.setMessage(getString(R.string.CallingDriveApi));

            setContentView(activityLayout);

            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            mOutputText.setText("");

        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        getResultsFromApi();
    }


    /**
     *  * Attempt to call the API, after verifying that all the preconditions are
     *  * satisfied. The preconditions are: Google Play Services installed, an
     *  * account was selected and the device currently has online access. If any
     *  * of the preconditions are not satisfied, the app will prompt the user as
     *  * appropriate.
     *  
     */
    private void getResultsFromApi() {
        try {
            if (!isGooglePlayServicesAvailable()) {
                acquireGooglePlayServices();
            } else if (mCredential.getSelectedAccountName() == null) {
                chooseAccount();
            } else if (!isDeviceOnline()) {
                mOutputText.setText("No network connection available.");
            } else {
                new MakeRequestTask(mCredential).execute();
            }
        }
        catch(Exception ex)
        {
            lib.ShowException(this,ex);
        }
    }

    /**
     *  * Attempts to set the account used with the API credentials. If an account
     *  * name was previously saved it will use that one; otherwise an account
     *  * picker dialog will be shown to the user. Note that the setting the
     *  * account to use with the credentials object requires the app to have the
     *  * GET_ACCOUNTS permission, which is requested here if it is not already
     *  * present. The AfterPermissionGranted annotation indicates that this
     *  * function will be rerun automatically whenever the GET_ACCOUNTS permission
     *  * is granted.
     *  
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     *  * Called when an activity launched here (specifically, AccountPicker
     *  * and authorization) exits, giving you the requestCode you started it with,
     *  * the resultCode it returned, and any additional data from it.
     *  * @param requestCode code indicating which activity result is incoming.
     *  * @param resultCode code indicating the result of the incoming
     *  *  activity result.
     *  * @param data Intent (containing result data) returned by incoming
     *  *  activity result.
     *  
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                    //CloseActivity();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                        //CloseActivity();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                    //CloseActivity();
                }
                break;
        }
    }

    /**
     *  * Respond to requests for permissions at runtime for API 23 and above.
     *  * @param requestCode The request code passed in
     *  *  requestPermissions(android.app.Activity, String, int, String[])
     *  * @param permissions The requested permissions. Never null.
     *  * @param grantResults The grant results for the corresponding permissions
     *  *  which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     *  
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     *  * Callback for when a permission is granted using the EasyPermissions
     *  * library.
     *  * @param requestCode The request code associated with the requested
     *  *   permission
     *  * @param list The requested permission list. Never null.
     *  
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     *  * Callback for when a permission is denied using the EasyPermissions
     *  * library.
     *  * @param requestCode The request code associated with the requested
     *  *   permission
     *  * @param list The requested permission list. Never null.
     *  
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     *  * Checks whether the device currently has a network connection.
     *  * @return true if the device has a network connection, false otherwise.
     *  
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     *  * Check that Google Play services APK is installed and up to date.
     *  * @return true if Google Play Services is available and up to
     *  *  date on this device; false otherwise.
     *  
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     *  * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     *  * Play Services installation via a user dialog, if possible.
     *  
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     *  * Display an error dialog showing that Google Play Services is missing
     *  * or out of date.
     *  * @param connectionStatusCode code describing the presence (or lack of)
     *  *  Google Play Services on this device.
     *  
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                LoginGoogleActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     *  * An asynchronous task that handles the Drive API call.
     *  * Placing the API calls in their own task ensures the UI stays responsive.
     *  
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        public com.google.api.services.drive.Drive getServiceDrive() {
            return mService;
        }

        public MakeRequestTask(GoogleAccountCredential credential) {
            lib.setgstatus("create Start");
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("jmgphotouploader")
                    .build();

        }

        /**
         *  * Background task to call Drive API.
         *  * @param params no parameters needed for this task.
         *  
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                lib.setgstatus("getDataFromApi Error " + e.getMessage());
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         *  * Fetch a list of up to 10 file names and IDs.
         *  * @return List of Strings describing files, or an empty list if no files
         *  *   found.
         *  * @throws IOException
         *  
         */
        private List<String> getDataFromApi() throws Exception {
            // Get a list of up to 10 files.
            lib.setgstatus("getDataFromApi Start");
            List<String> fileInfo = new ArrayList<String>();
            FileList result = mService.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            lib.setgstatus(result.toString());
            List<File> files = result.getFiles();

            if (files != null) {
                lib.setgstatus("getDataFromApi files.size:" + files.size());
                for (File file : files) {
                    fileInfo.add(String.format("%s (%s)\n",
                            file.getName(), file.getId()));
                }
            }
            else
            {
                lib.setgstatus("getDataFromApi files is null");
            }

            lib.setgstatus("getDataFromApi Finish");
            return fileInfo;
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            lib.setgstatus("onPostExecute");
            mProgress.hide();
            mProgress.dismiss();;
            mOutputText.setText("PostExecute");
            if (output == null || output.size() == 0) {
                lib.setgstatus("No results returned");
                mOutputText.setText(getString(R.string.NoResultsReturned));
            } else {
                lib.setgstatus("Output size " + output.size());
                output.add(0, getString(R.string.DataReturned));
                mOutputText.setText(TextUtils.join("\n", output));

            }
            mApp.setGoobleDriveClient(mService);
            Intent i = new Intent();
            i.putExtra("GroupPosition", GroupPosition);
            //i.putExtra("client", client);
            LoginGoogleActivity.this.setResult(Activity.RESULT_OK, i);
            CloseActivity();
        }


        @Override
        protected void onCancelled() {
            lib.setgstatus("onCancelled");
            mProgress.hide();
            mProgress.dismiss();
            if (mLastError != null) {
                lib.setgstatus("The following error occurred:\n"
                        + mLastError.getMessage());
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            LoginGoogleActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
                lib.setgstatus("Request cancelled");
            }
            Intent i = new Intent();
            i.putExtra("GroupPosition", GroupPosition);
            LoginGoogleActivity.this.setResult(Activity.RESULT_CANCELED, i);
            //CloseActivity();
        }
    }
    public void CloseActivity()
    {
        lib.setgstatus("CloseActivity");
        //finishActivity(requestCode);
        mApp.LoginGoogleClosed = true;
        finish();
    }

}