package org.de.jmg.jmgphotouploader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.Arrays;
import java.util.prefs.Preferences;

import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveStatus;

public class LoginLiveActivity extends Activity implements LiveAuthListener
{

    private LiveAuthClient auth;
    private int GroupPosition; 
    private JMPPPApplication mApp;
    private Button _btnLogin;
    private Button _btnClose;
    public LiveConnectClient client;
    public LiveConnectSession session;
    public static int requestCode = 9997;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (JMPPPApplication)getApplication();
        if (mApp.LoginClosed)
        {
        	mApp.LoginClosed = false;
        	this.finish();
        }
        else
        {
        	setContentView(R.layout.livelogin);
            _btnLogin = (Button)findViewById(R.id.btnLogin);
            _btnClose = (Button)findViewById(R.id.btnClose);
            
            auth = new LiveAuthClient(this, secrets.LoginLive);

            mApp.setAuthClient(auth);
                    
            GroupPosition = getIntent().getExtras().getInt("GroupPosition");
            
            /*
            LinearLayout layout = (LinearLayout)this.findViewById(R.id.login); // (context);  
            Button b = new Button(this); //FindViewById<ExpandableListView> (Resource.Id.lvItems);
    		b.setText("Login");
    		ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    		b.setTextSize(30);
    		b.setGravity(Gravity.CENTER_HORIZONTAL & Gravity.CENTER_VERTICAL);
    		layout.addView(b,p);
    	    b.setLeft((layout.getWidth()-b.getWidth())/2);
    		b.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				final Iterable<String> scopes = Arrays.asList("wl.signin", "wl.basic", "wl.skydrive");
    		        //LoginLiveActivity.this.auth = new LiveAuthClient(LoginLiveActivity.this.getApplicationContext(), "0000000048135143");
    		        LoginLiveActivity.this.auth.login(LoginLiveActivity.this, scopes, new LiveAuthListener() {
    					
    					@Override
    					public void onAuthError(LiveAuthException arg0, Object arg1) {
    						// TODO Auto-generated method stub
    						Intent i = new Intent();
    			            LoginLiveActivity.this.setResult(Activity.RESULT_CANCELED, i);
    			            LoginLiveActivity.this.finish();//finishActivity(requestCode);
    					}
    					
    					@Override
    					public void onAuthComplete(LiveStatus status, LiveConnectSession session,
    							Object arg2) {
    					   	if(status == LiveStatus.CONNECTED) {
    				            //lib.ShowMessage(this,"Signed in.");
    				            //client = new LiveConnectClient(session);
    				            lib.client = client;
    				            Intent i = new Intent();
    				            i.putExtra("GroupPosition", GroupPosition);
    				            //i.putExtra("client", client);
    				            launchMainActivity(session);
    				            //LoginLiveActivity.this.setResult(Activity.RESULT_OK, i);
    				            //LoginLiveActivity.this.finishActivity(LoginLiveActivity.requestCode);
    				            LoginLiveActivity.this.finish();
    				        }
    				        else {
    				            lib.ShowMessage(LoginLiveActivity.this,"Not signed in.");
    				            client = null;
    				            Intent i = new Intent();
    				            //LoginLiveActivity.this.setResult(Activity.RESULT_CANCELED, i);
    				            LoginLiveActivity.this.finish();finishActivity(requestCode);
    				        }
    						
    					}
    				});

    			}
    		})*/;
    		_btnLogin.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				LoginLive();
    			}
    		});
    		_btnClose.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				CloseActivity();
    			}
    		});

        }
    }
    private void LoginLive()
    {
		final Iterable<String> scopes = Arrays.asList("wl.signin", "wl.basic", "wl.skydrive", "wl.offline_access" );
        //LoginLiveActivity.this.auth = new LiveAuthClient(LoginLiveActivity.this.getApplicationContext(), "0000000048135143");
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String refreshToken = prefs.getString("ONE_DRIVE_REFRESH_TOKEN_KEY", null); // save it to shared preferences on first login
        if (refreshToken!=null)
        {
            LiveAuthListener liveAuthListener = (LiveAuthListener) this;
            Object userState = new Object();
            auth.initialize(scopes, liveAuthListener, userState, refreshToken);
            /*lib.ShowToast(this,"Signed in.");
            //client = new LiveConnectClient(session);
            lib.setClient(client);
            setResultPositive(session);*/
        }
        else
        {
            LoginLiveActivity.this.auth.login(LoginLiveActivity.this, scopes, LoginLiveActivity.this);
        }

    }
    @Override
    public void onBackPressed()
    {
    	super.onBackPressed();
    	CloseActivity();
    	
    }
    
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	LoginLive();
    }
    @Override
    protected void onStop() 
    {
    	super.onStop();
    	//launchMainActivity(session);
    }
    
    private void setResultPositive(LiveConnectSession session) {
        if(session != null)
        {
        	mApp.setSession(session);
        	mApp.setConnectClient(new LiveConnectClient(session));
        }
        Intent i = new Intent();
        i.putExtra("GroupPosition", GroupPosition);
        //i.putExtra("client", client);
        LoginLiveActivity.this.session = session;
        LoginLiveActivity.this.setResult(Activity.RESULT_OK, i);
        //LoginLiveActivity.this.finishActivity(LoginLiveActivity.requestCode);
        //LoginLiveActivity.this.finish();
    }



    @Override
	public void onAuthError(LiveAuthException arg0, Object arg1) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
        LoginLiveActivity.this.setResult(Activity.RESULT_CANCELED, i);
        lib.ShowToast(this, arg0.getMessage());
        CloseActivity();
	}
	
	@Override
	public void onAuthComplete(LiveStatus status, LiveConnectSession session,
			Object arg2) {
	   	if(status == LiveStatus.CONNECTED) {
            lib.ShowToast(this,"Signed in.");
            //client = new LiveConnectClient(session);
            lib.setClient(client);
            setResultPositive(session);
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
            prefs.edit().putString("ONE_DRIVE_REFRESH_TOKEN_KEY", session.getRefreshToken()).commit(); // save it to shared preferences on first login
        }
        else {
            lib.ShowToast(LoginLiveActivity.this,"Not signed in. Status is " + status + ".");
            lib.setClient(client);
            Intent i = new Intent();
            setResult(Activity.RESULT_CANCELED, i);
            
        }
		CloseActivity();
	}
	public void CloseActivity()
	{
		//finishActivity(requestCode);
		mApp.LoginClosed = true;
		finish();
	}
	
}