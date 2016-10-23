package org.de.jmg.jmgphotouploader;


import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
//import android.runtime.*;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;
import android.provider.*;
import org.de.jmg.jmgphotouploader.Controls.*;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveStatus;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [Activity(Label = "JMGPhotoPrinter", MainLauncher = true, Icon = "@drawable/edit")] public class MainActivity : Activity
public class _MainActivity extends Activity
{
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 112;
	public ExpandableListView lv = null;
	
	public JMPPPApplication app;
    protected Context Context = this;
	//private Intent LoginLiveIntent;
	//private GestureDetector _gestureDetector;
	//private ScaleGestureDetector _ScaleGestureDetector;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		try
		{
			app = (JMPPPApplication) getApplication();
			app.MainContext = this.Context;
			initDB(app);
			//requestWindowFeature(Window.FEATURE_PROGRESS);
			//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			// Set our view from the "main" layout resource
					//_gestureDetector = new GestureDetector(this);
			//_ScaleGestureDetector = new ScaleGestureDetector (this,this);
			//lv.Touch += lv_touch;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
					&& ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
			}
			else
			{
				loadmedia();
			}


		}
		catch (Exception ex)
		{
			lib.ShowToast(this, ex.getMessage());
		}

		
		
	}


	private void initDB(JMPPPApplication app)
	{
		if (app.dbpp != null)
		{
			lib.dbpp = app.dbpp;
			lib.dbpp = new dbpp(this);
			lib.dbpp.createDataBase();
			app.dbpp = lib.dbpp;
		}
		else
		{
			lib.dbpp = new dbpp(this);
			lib.dbpp.createDataBase();
			app.dbpp = lib.dbpp;
		}
		String SQL = "ALTER TABLE Services ADD COLUMN visible BOOL NOT NULL DEFAULT true";
		try
		{
			Cursor c = lib.dbpp.query("Select * FROM Services");
			if (c.getColumnCount() < 5)	lib.dbpp.DataBase.execSQL(SQL);
			boolean first = true;
			lib.setgstatus("enumerate Services");
			while ((first) ? (c.moveToFirst()) : (c.moveToNext()))
			{
				first = false;
				String service = c.getString(c.getColumnIndex("Name"));
				String Package = c.getString(c.getColumnIndex("package"));
				if (service.contains("Facebook")){
					if ( Package == null || Package.equals(""))
					{
						String p = "com.facebook.katana";
						String sql = "Update Services SET package = \"" + p + "\" WHERE _id = " + c.getInt(0);
						lib.dbpp.DataBase.execSQL(sql);
						p = "https://www.facebook.com/mobile/";
						sql = "Update Services SET URL = \"" + p + "\" WHERE _id = " + c.getInt(0);
						lib.dbpp.DataBase.execSQL(sql);
					}
				}
				else if (service.contains("Twitter")){
					if (Package == null|| Package.equals(""))
					{
						String p = "com.twitter." +
								"android,com." +
								"twidroid,com.handmark." +
								"tweetcaster,com.thedeck.android";
						String sql = "Update Services SET package = \"" + p + "\" WHERE _id = " + c.getInt(0);
						lib.dbpp.DataBase.execSQL(sql);

						p = "https://about.twitter.com/de/products/list";
						sql = "Update Services SET URL = \"" + p + "\" WHERE _id = " + c.getInt(0);
						lib.dbpp.DataBase.execSQL(sql);

					}
				}
				else if (service.contains("Instagram")){
					if (Package == null || Package.equals("") )
					{
						String p = "com.instagram.android";
						String sql = "Update Services SET package = \"" + p + "\" WHERE _id = " + c.getInt(0);
						lib.dbpp.DataBase.execSQL(sql);
						p = "http://instagram.de.uptodown.com/android";;
						sql = "Update Services SET URL = \"" + p + "\" WHERE _id = " + c.getInt(0);
						lib.dbpp.DataBase.execSQL(sql);
					}
				}
				else if (service.contains("Pinterest")){
				}
				//c = lib.dbpp.query("Select * FROM Services");

			}
			if (c.getCount() < 6)
			{
				String sql = "INSERT INTO Services ('Name','URL','package') VALUES('Flickr','http://flickr.com','com.yahoo.mobile.client.android.flickr')";
				lib.dbpp.DataBase.execSQL(sql);
				sql = "INSERT INTO Services ('Name','URL','package') VALUES('Tumblr','http://tumblr.com','com.tumblr')";
				lib.dbpp.DataBase.execSQL(sql);
			}
			if (c.getCount() < 7)
			{
				String sql = "INSERT INTO Services ('Name','URL','package') VALUES('Photobucket','http://photobucket.com','com.photobucket.android')";
				lib.dbpp.DataBase.execSQL(sql);
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}

	}

	private void loadmedia() throws Exception
	{
		System.out.println(lib.getExternalPicturesDir());

		String selection = "";
		String[] selectionArgs = new String[]{};
		String[] projection = new String[] {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
		if (app.ppa == null)
		{
			Cursor mediaCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,selectionArgs, "");
			if (mediaCursor != null) lib.GetThumbnails(this, false, mediaCursor, app.BMList);

			mediaCursor = getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI,projection,selection,selectionArgs, "");
			if (mediaCursor != null) lib.GetThumbnails(this, true, mediaCursor, app.BMList);

			if (app.ppa == null) {
				app.BMList.add(new ImgFolder("One Drive",ImgFolder.Type.OneDriveAlbum));
				app.BMList.add(new ImgFolder("Google Drive",ImgFolder.Type.Google));
			}
		}
		setContentView(R.layout.activity_main);

		lv = new ZoomExpandableListview(this); //FindViewById<ExpandableListView> (Resource.Id.lvItems);
		this.addContentView(lv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));

		lv.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
		lv.setClickable(true);
		lv.setFocusable(true);
		lv.setFocusableInTouchMode(true);
		SetPPA();

		lv.setOnChildClickListener(lv_ChildClick);
		lv.setOnScrollListener(app.ppa.onScrollListener);

		//lv.setOverScrollMode(View.OVER_SCROLL_NEVER);

	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission granted.
				try {
					loadmedia();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// User refused to grant permission.
			}
		}
	}

	private void SetPPA()
	{
		if (app.ppa == null)  	app.ppa = new PhotoFolderAdapter(this, app.BMList);
		app.ppa.context = this;
		app.ppa.rows = app.BMList;
		app.ppa.ServiceCursor = null;
		lv.setAdapter(app.ppa);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		lib.dbpp.close();
		/*
		LiveAuthClient client = app.getAuthClient();
		if (client != null) client.logout(new LiveAuthListener() {
			
			@Override
			public void onAuthError(LiveAuthException arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAuthComplete(LiveStatus arg0, LiveConnectSession arg1,
					Object arg2) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		
	}
	
	@Override
	public void onBackPressed()
	{
		/*
		lib.dbpp.close();
		LiveAuthClient client = app.getAuthClient();
		if (client != null && app.listener != null) client.logout(app.listener);
		*/
		super.onBackPressed();
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        //case R.id.action_search:
	        //    openSearch();
	         //   return true;
	        case R.id.action_settings:
	            openSettings();
	            return true;
	        case R.id.action_resetdatabase:
	            resetdatabase();
	            return true;
	        case R.id.action_refresh:
	        	refreshPhotos();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void resetdatabase()
	{
		try
		{
			Cursor c = lib.dbpp.query("Select * FROM Services");
			boolean first = true;
			lib.setgstatus("enumerate Services");
			int i = 0;
			while ((first) ? (c.moveToFirst()) : (c.moveToNext()))
			{
				i++;
				first = false;
				String service = c.getString(c.getColumnIndex("Name"));
				String Package = c.getString(c.getColumnIndex("package"));
				if (service.contains("Facebook")|| i == 1)
				{
					String p = "com.facebook.katana";
					String sql = "Update Services SET package = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					p = "https://www.facebook.com/mobile/";
					sql = "Update Services SET URL = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					p = "Facebook";
					sql = "Update Services SET Name = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "true";
					sql = "Update Services SET visible = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
				}
				else if (service.contains("Twitter") || i == 2){
					String p = "com.twitter." +
							"android,com." +
							"twidroid,com.handmark." +
							"tweetcaster,com.thedeck.android";
					String sql = "Update Services SET package = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "https://about.twitter.com/de/products/list";
					sql = "Update Services SET URL = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "Twitter";
					sql = "Update Services SET Name = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "true";
					sql = "Update Services SET visible = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
				}
				else if (service.contains("Instagram") || i == 3)
				{
					String p = "com.instagram.android";
					String sql = "Update Services SET package = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "http://instagram.de.uptodown.com/android";;
					sql = "Update Services SET URL = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "Instagram";
					sql = "Update Services SET Name = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "true";
					sql = "Update Services SET visible = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
				}
				else if (service.contains("Pinterest") || i == 4)
				{
					String p = "com.pinterest";
					String sql = "Update Services SET package = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "http://pinterest.com";;
					sql = "Update Services SET URL = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "Pinterest";;
					sql = "Update Services SET Name = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
					
					p = "true";
					sql = "Update Services SET visible = \"" + p + "\" WHERE _id = " + c.getInt(0);
					lib.dbpp.DataBase.execSQL(sql);
				}
				//c = lib.dbpp.query("Select * FROM Services");
			
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
	private void openSearch()
	{
		lib.ShowMessage(this, getString(R.string.search));
	}
	
	public void StartLoginLive(ImgFolder OneDrive)
	{
		app.LoginClosed=false;
		app.OneDriveFolder = OneDrive;
		Intent LoginLiveIntent = new Intent(this, LoginLiveActivity.class);
		LoginLiveIntent.putExtra("GroupPosition", lib.LastgroupPosition);
		this.startActivityForResult(LoginLiveIntent, LoginLiveActivity.requestCode);
	}

	public void StartLoginGoogle(ImgFolder Google)
	{
		app.LoginClosed=false;
		app.GoogleFolder = Google;
		Intent LoginIntent = new Intent(this, LoginGoogleActivity.class);
		LoginIntent.putExtra("GroupPosition", lib.LastgroupPosition);
		this.startActivityForResult(LoginIntent, LoginGoogleActivity.requestCode);
	}
	
	private void openSettings()
	{
		try
		{
			Intent SettingsIntent = new Intent(this, SettingsActivity.class);
			SettingsIntent.putExtra("GroupPosition", lib.LastgroupPosition);
			this.startActivityForResult(SettingsIntent, SettingsActivity.requestCode); //, LoginLiveActivity.requestCode);
			//this.finish();
		}
		catch (Exception ex)
		{
			lib.ShowException(this, ex);
		}
			//context.finish();
	}
	
	private OnChildClickListener lv_ChildClick = new OnChildClickListener() {
		
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			if (v.getTag() != null)
			{
				ViewHolder holder = (ViewHolder)(v.getTag());
				ImgListItem ImgListItem = holder.item;
				boolean isOneDrive = ImgListItem.type == ImgFolder.Type.OneDriveAlbum 
						|| ImgListItem.type == ImgFolder.Type.OneDriveFolder;
				if (isOneDrive)
				{
					lib.StartViewer(Context , ImgListItem.Uri);
				}
				else
				{
					lib.StartViewer(Context, Uri.parse("file://" + ImgListItem.folder));
				}
			}
			return false;
		}
	};
    @Override
    protected void onStart() {
        super.onStart();
        
        
    }

        	
    
    private LiveOperation LiveOp;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	Toast.makeText(this, getString(R.string.onActivityResultcalled), Toast.LENGTH_LONG).show();
        lib.setClient(app.getConnectClient());
    	lib.setClientGoogle(app.getGoogleDriveClient());
		if (requestCode == LoginLiveActivity.requestCode && resultCode == Activity.RESULT_OK && lib.getClient(this) != null){
			final int GroupPosition = data.getExtras().getInt("GroupPosition");
			try {
				lib.GetThumbnailsOneDrive(this, "/", app.OneDriveFolder, GroupPosition, _MainActivity.this.lv);
				if (app.OneDriveFolder!=null)app.OneDriveFolder.fetched=true;
			} catch (LiveOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (requestCode == LoginGoogleActivity.requestCode && resultCode == Activity.RESULT_OK && lib.getClientGoogle(this) != null){
			final int GroupPosition = data.getExtras().getInt("GroupPosition");
			/*
			try {
				lib.GetThumbnailsOneDrive(this, "/", app.OneDriveFolder, GroupPosition, _MainActivity.this.lv);
				if (app.OneDriveFolder!=null)app.OneDriveFolder.fetched=true;
			} catch (LiveOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}

		else if (requestCode == SettingsActivity.requestCode && resultCode == Activity.RESULT_OK)
        {
        	boolean changed = data.getExtras().getBoolean("changed");
        	final int GroupPosition = data.getExtras().getInt("GroupPosition");
        	if (changed)
        		{
        			refreshPhotos();
        		}
        	
        }    
    }
    private void refreshPhotos()
    {
    	app.ppa = null;
		SetPPA();
    }
	
}