package com.jmg.jmgphotouploader;

import android.app.Activity;

import com.microsoft.live.LiveAuthListener;

public class ConnectToLive implements Runnable {
	
	private com.microsoft.live.LiveAuthClient Client;
	private Activity activity;
	private Iterable<String> scopes;
	private LiveAuthListener listener;
    public void run() {
        Login();
    }
    private synchronized void Login(){
    	lib.LoginLive(Client, activity, scopes, listener);
    }
    public ConnectToLive(com.microsoft.live.LiveAuthClient Client,
    					Activity activity,
    					Iterable<String> scopes,
    					LiveAuthListener listener)
    {
    	this.Client = Client;
    	this.activity = activity;
    	this.scopes = scopes;
    	this.listener = listener;
    }
}