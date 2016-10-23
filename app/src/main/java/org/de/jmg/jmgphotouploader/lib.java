package org.de.jmg.jmgphotouploader;

//import android.support.v7.app.ActionBarActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Locale;

//import android.runtime.*;

public class lib {

    public lib() {
    }

    private static String _status = "";
    private static final String ONEDRIVE_APP_ID = "48122D4E";
    private static Drive mClientGoogle;
    public static java.util.ArrayList<ImgListItem> BMList;
    public static dbpp dbpp;
    public static int LastgroupPosition;
    public static int LastChildPosition;
    public static Boolean LastisLastChild;

    public static String getgstatus() {
        return _status;
    }

    public static void setgstatus(String value) {
        _status = value;
        System.out.println(value);
    }

    public static String getRealPathFromURI(Activity context, android.net.Uri contentURI) {
        android.database.Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentURI, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception ex) {
            return contentURI.getPath();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getSizeFromURI(Context context, android.net.Uri contentURI) {
        android.database.Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.SIZE};
            cursor = context.getContentResolver().query(contentURI, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String getExternalPicturesDir() {
        String res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            res = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES).getPath();
        } else {
            res = android.os.Environment.getExternalStorageDirectory().getPath();
        }
        return res;
    }


    public static void GetThumbnails(Activity context, boolean Internal, android.database.Cursor mediaCursor, java.util.ArrayList<ImgFolder> BMList) {
        if (mediaCursor.getCount() > 0) {
            //await System.Threading.Tasks.Task.Run (() => {
            mediaCursor.moveToFirst();
            int ColumnIndexID = mediaCursor.getColumnIndex(MediaStore.Images.Media._ID);
            int ColumnIndexData = mediaCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int ColumnIndexBucket = mediaCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            // int ColumnIndexSize = mediaCursor.GetColumnIndex (MediaStore.Images.Media.InterfaceConsts.Size);
            try {
                context.setProgressBarVisibility(true);
                for (int i = 0; i <= (mediaCursor.getCount() - 1); i++) {
                    mediaCursor.moveToPosition(i);
                    context.setProgress(i);
                    int imageId = mediaCursor.getInt(ColumnIndexID);

                    if (true) {
                        android.net.Uri Uri;
                        //String size = "";
                        if (!Internal) {
                            Uri = Images.Media.INTERNAL_CONTENT_URI;//android.support.v4.media. MediaStore.Files.getContentUri("external", imageId);
                        } else {
                            Uri = Images.Media.EXTERNAL_CONTENT_URI;//MediaStore.Files.getContentUri("internal", imageId);
                        }

                        //if (bitmap != null) {
                        String folder = mediaCursor.getString(ColumnIndexData);
                        //img.Dispose();
                        //System.Diagnostics.Debug.Print (lib.getRealPathFromURI (context, Uri));
                        String Bucket = mediaCursor.getString(ColumnIndexBucket);

                        ImgFolder Folder = FindFolder(BMList, Bucket);
                        if (Folder == null) {
                            Folder = new ImgFolder(Bucket, ImgFolder.Type.Local);
                            BMList.add(Folder);
                        }
                        Folder.items.add(new ImgListItem(context, "", imageId, (new java.io.File(folder)).getName(), Uri.parse("file://" + folder), folder, ImgFolder.Type.Local, null));
                        //}
                    }
                }
            } finally {
                context.setProgressBarVisibility(false);
            }
            //});
        } else {

			/*
            foreach (System.IO.FileInfo F in new System.IO.DirectoryInfo(Android.OS.Environment.GetExternalStoragePublicDirectory(Android.OS.Environment.DirectoryPictures).Path).GetFiles("*.*",SearchOption.AllDirectories))
			{
				try{
					Bitmap B = BitmapFactory.DecodeFile(F.FullName);
					if (B != null) {
						BMList.Add(new ImgListItem(B,F.Name));
					}
				}
				catch {
				}

			}*/
            if (BMList.isEmpty()) {
                //this.Resources.GetDrawable(Resource.Drawable.P1040598)
                ImgFolder Folder1 = new ImgFolder("Test1", ImgFolder.Type.Local);
                ImgFolder Folder2 = new ImgFolder("Test2", ImgFolder.Type.Local);
                BMList.add(Folder1);
                BMList.add(Folder2);
                for (int i = 1; i <= 10; i++) {
                    ImgListItem newItem1 = new ImgListItem(context, "", -1, "RES", null, null, ImgFolder.Type.unknown, "0");
                    newItem1.setImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.ressmall));
                    Folder1.items.add(newItem1);
                    ImgListItem newItem2 = new ImgListItem(context, "", -1, "RES2", null, null, ImgFolder.Type.unknown, "0");
                    newItem2.setImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.res2small));
                    Folder2.items.add(newItem2);
                }
            }
        }
    }

    private static LiveConnectClient mClient;
    public static LiveOperation LiveOp;

    //public static CountDownLatch Latch;
    //public static CountDownLatch LatchClient;
    //private static LoginLiveActivity Login;
    //private static AutoResetEvent AR = new AutoResetEvent(false);
    //private static boolean Finished;
    public static LiveConnectClient getClient(Activity context) {
        JMPPPApplication myApp = (JMPPPApplication) context.getApplication();
        mClient = myApp.getConnectClient();
        return mClient;
    }

    public static void setClient(LiveConnectClient client) {
        mClient = client;
    }

    public static Drive getClientGoogle(Activity context) {
        JMPPPApplication myApp = (JMPPPApplication) context.getApplication();
        mClientGoogle = myApp.getGoogleDriveClient();
        return mClientGoogle;
    }

    public static void setClientGoogle(Drive client) {
        mClientGoogle = client;
    }

    public static void GetThumbnailsOneDrive(final Activity context, final String folder, final ImgFolder imgFolder, final int GroupPosition, final ExpandableListView lv) throws LiveOperationException, InterruptedException {

        try {
            String queryString = "me/skydrive/files" + folder;//?filter=folders,albums";
            if (imgFolder != null && imgFolder.id != null) queryString = imgFolder.id + "/files";
            //Latch = new CountDownLatch(1);
            lib.getClient(context).getAsync(queryString, new LiveOperationListener() {
                @Override
                public void onError(LiveOperationException exception,
                                    LiveOperation operation) {
                    // TODO Auto-generated method stub
                    lib.ShowException(context, exception);
                    if (imgFolder != null) imgFolder.fetched = false;
                    LiveOp = operation;
                    //lib.Latch.countDown();
                }

                @Override
                public void onComplete(LiveOperation operation) {
                    // TODO Auto-generated method stub
                    LiveOp = operation;
                    if (LiveOp != null) {
                        JSONObject folders = LiveOp.getResult();
                        if (folders != null) {
                            final JSONArray data = folders.optJSONArray("data");
                            if (data != null) {
                                lib.BMList.clear();
                                int countFolders = 0;
                                for (int i = 0; i < data.length(); i++) {
                                    final JSONObject oneDriveItem = data.optJSONObject(i);
                                    if (oneDriveItem != null) {
                                        System.out.println(oneDriveItem.toString());
                                        final String itemName = oneDriveItem.optString("name");
                                        //final String itemType = oneDriveItem.optString("type");
                                        final String itemType = oneDriveItem.optString("type");
                                        final String id = oneDriveItem.optString("id");
                                        final String uri = oneDriveItem.optString("link");
                                        final String size = oneDriveItem.optString("size");

                                        //lib.ShowMessage(context,itemType);
                                        _MainActivity Main = (_MainActivity) context;
                                        JMPPPApplication app = (JMPPPApplication) Main.getApplication();
                                        PhotoFolderAdapter ppa = app.ppa;
                                        if (itemType.equals("photo")) {
                                            final int width = oneDriveItem.optInt("width");
                                            final int height = oneDriveItem.optInt("height");
                                            final android.net.Uri auri = android.net.Uri.parse(oneDriveItem.optString("source"));
                                            ImgListItem Item = (new ImgListItem(context, id, 0, itemName, auri, uri, ImgFolder.Type.OneDriveAlbum, width + "x" + height));
                                            lib.BMList.add(Item);
                                            ppa.notifyDataSetChanged();
                                        } else if (itemType.equals("album") || itemType.equals("folder")) {
                                            ImgFolder.Type type;
                                            if (itemType.equals("album")) {
                                                type = ImgFolder.Type.OneDriveAlbum;
                                            } else {
                                                type = ImgFolder.Type.OneDriveFolder;
                                            }
                                            int position = ppa.rows.indexOf(imgFolder);
                                            countFolders++;
                                            ppa.rows.add(position + countFolders, new ImgFolder(folder + itemName + "/", type, id));
                                            ppa.notifyDataSetChanged();
                                        }

                                    }
                                }
                            }
                        }
                    }
                    //  get all folders
                    //lib.Latch.countDown();
                    //context.finishActivity(LoginLiveActivity.requestCode);
                    //lv.collapseGroup(GroupPosition);
                    //lv.expandGroup(GroupPosition);
                    //lv.expandGroup(lv.getSelectedItemPosition());
                    //Adapter.getChildView(lib.LastgroupPosition, lib.LastChildPosition, lib.LastisLastChild, null, null);
                }
            });
        } catch (Exception e) {
            lib.ShowException(context, e);
            if (e instanceof LiveOperationException) {
                throw (LiveOperationException) (e);
            } else if (e instanceof InterruptedException) {
                throw (InterruptedException) (e);
            }
        } finally {
            //Latch.countDown();
        }
    }

    public static void GetThumbnailsOneDrive(final Activity context, final java.util.ArrayList<ImgListItem> BMList) throws LiveOperationException, InterruptedException {
        //com.microsoft.live.LiveAuthClient mlAuth = new com.microsoft.live.LiveAuthClient(context, clientId);
        /*
		if (client == null)
		{
			 Intent intent = new Intent(context, LoginLiveActivity.class);
			 int requestCode = 1;
			context.startActivityForResult(intent, requestCode);
			 
		}
		
		Date dtDate = new Date();
		while (client == null){
			if(new Date().getSeconds()-dtDate.getSeconds()>200) break;
			Thread.sleep(1000, 0);
		}
		*/
        if (lib.getClient(context) != null) {
            try {
                String queryString = "me/skydrive/files";//?filter=folders,albums";
                //Latch = new CountDownLatch(1);
                lib.getClient(context).getAsync(queryString, new LiveOperationListener() {
                    @Override
                    public void onError(LiveOperationException exception,
                                        LiveOperation operation) {
                        // TODO Auto-generated method stub
                        lib.ShowException(context, exception);
                        LiveOp = operation;
                        //Latch.countDown();
                    }

                    @Override
                    public void onComplete(LiveOperation operation) {
                        // TODO Auto-generated method stub
                        LiveOp = operation;
                        if (LiveOp != null) {
                            JSONObject folders = LiveOp.getResult();
                            if (folders != null) {
                                final JSONArray data = folders.optJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    final JSONObject oneDriveItem = data.optJSONObject(i);
                                    if (oneDriveItem != null) {
                                        System.out.println(oneDriveItem.toString());
                                        final String itemName = oneDriveItem.optString("name");
                                        //final String itemType = oneDriveItem.optString("type");
                                        final String itemType = oneDriveItem.optString("type");
                                        final String id = oneDriveItem.optString("id");
                                        final String uri = oneDriveItem.optString("link");
                                        final String size = oneDriveItem.optString("size");
                                        final android.net.Uri auri = android.net.Uri.parse(uri);
                                        BMList.add(new ImgListItem(context, id, 0, itemName, auri, uri, ImgFolder.Type.OneDriveAlbum, size));
					                    /*
					                    String queryString = "me/skydrive/" + itemName + "/files";//?filter=folders,albums";
					    	            //Latch = new CountDownLatch(1);
					    	            try {
											LiveOperation LiveOp2 = client.get(queryString);
											if (LiveOp2 != null)
								            {
									    		JSONObject folders2 = LiveOp2.getResult();
									    		if (folders2 != null)
									    		{
										            final JSONArray data2 = folders2.optJSONArray("data");
										            for (int i2 = 0; i < data2.length(); i2++) { 
										                final JSONObject oneDriveItem2 = data.optJSONObject(i);
										                if (oneDriveItem2 != null) {
										                	System.out.println(oneDriveItem2.toString());
										                    final String itemName2 = oneDriveItem2.optString("name");
										                    final String itemType = oneDriveItem2.optString("type");
										                    final String id = oneDriveItem2.optString("id");
										                    final String uri = oneDriveItem2.optString("uri");
										                    Folder.items.add(new ImgListItem(context, i2, uri, null, uri));
										                } 
										            }
									    		}
								            }
										} catch (LiveOperationException e) {
											// TODO Auto-generated catch block
											lib.ShowException(context, e);
										}
					    	            
					                    //Folder.items.add(new ImgListItem(context, imageId, (new java.io.File(folder)).getName(), Uri, folder));
					                    */
                                        // BMList.add(Folder);
                                    }
                                }
                            }
                        }
                        //  get all folders
                        //Latch.countDown();
                    }
                });
            } catch (Exception e) {
                lib.ShowException(context, e);
            } finally {
                //Latch.countDown();
            }
        }
    }

    public static void GetThumbnailsGoogle(final Activity context, final String folder, final ImgFolder imgFolder, final int GroupPosition, final ExpandableListView lv) throws LiveOperationException, InterruptedException, IOException {

        try {
            if (lib.getClientGoogle(context) != null)
            {
                String queryString = "me/skydrive/files" + folder;//?filter=folders,albums";
                if (imgFolder != null && imgFolder.id != null)
                    queryString = imgFolder.id + "/files";
                //Latch = new CountDownLatch(1);
                AsyncTask<Void,Void,FileList> task = new AsyncTask<Void, Void, FileList>() {
                    @Override
                    protected FileList doInBackground(Void... params) {
                        Drive client = lib.getClientGoogle(context);
                        FileList result = null;
                        try {
                            result = client.files().list()
                                    .setPageSize(10)
                                    .setFields("nextPageToken, files(id, name)")
                                    .execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return result;

                    }
                };
                FileList result = task.execute().get();
                if (result != null) {
                    List<com.google.api.services.drive.model.File> files = result.getFiles();
                    if (files != null) {
                        lib.BMList.clear();
                        int countFolders = 0;
                        for (int i = 0; i < files.size(); i++) {
                            final com.google.api.services.drive.model.File GoogleDriveItem = files.get(i);
                            if (GoogleDriveItem != null) {
                                System.out.println(GoogleDriveItem.toString());
                                final String itemName = GoogleDriveItem.getName();
                                //final String itemType = oneDriveItem.optString("type");
                                final String itemType = GoogleDriveItem.getKind();
                                final String id = GoogleDriveItem.getId();
                                final String uri = GoogleDriveItem.getWebViewLink();
                                final String size = GoogleDriveItem.getSize().toString();

                                //lib.ShowMessage(context,itemType);
                                _MainActivity Main = (_MainActivity) context;
                                JMPPPApplication app = (JMPPPApplication) Main.getApplication();
                                PhotoFolderAdapter ppa = app.ppa;
                                final android.net.Uri auri = android.net.Uri.parse(GoogleDriveItem.getWebContentLink());

                                ImgListItem Item = (new ImgListItem(context, id, 0, itemName, auri, uri, ImgFolder.Type.OneDriveAlbum, 0 + "x" + 0));

                                lib.BMList.add(Item);
                                ppa.notifyDataSetChanged();
                                            /*
                                            if (itemType.equals("photo"))
                                            {

                                                final int width = GoogleDriveItem.optInt("width");
                                                final int height = oneDriveItem.optInt("height");
                                                final android.net.Uri auri = android.net.Uri.parse(oneDriveItem.optString("source"));
                                                ImgListItem Item = (new ImgListItem(context, id, 0, itemName, auri, uri,ImgFolder.Type.OneDriveAlbum,width + "x" + height));
                                                lib.BMList.add(Item);
                                                ppa.notifyDataSetChanged();
                                            }
                                            else if(itemType.equals("album") || itemType.equals ("folder"))
                                            {
                                                ImgFolder.Type type;
                                                if (itemType.equals("album")){
                                                    type = ImgFolder.Type.OneDriveAlbum;
                                                }
                                                else
                                                {
                                                    type = ImgFolder.Type.OneDriveFolder;
                                                }
                                                int position = ppa.rows.indexOf(imgFolder);
                                                countFolders ++;
                                                ppa.rows.add(position + countFolders,new ImgFolder(folder + itemName + "/",type,id));
                                                ppa.notifyDataSetChanged();
                                            }*/

                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            lib.ShowException(context, ex);
        }
    }


    private static ImgFolder FindFolder(java.util.ArrayList<ImgFolder> BMList, String Bucket) {
        for (ImgFolder f : BMList) {
            if (Bucket.equals(f.Name)) return f;
        }
        return null;
    }

    public static void StartViewer(Context context, android.net.Uri uri) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, "image/*");
            context.startActivity(i);
        } catch (Exception ex) {
            lib.ShowException(context, ex);
        }
    }

    public static void SharePictureOnFacebook(Context context, android.net.Uri uri) {

        String urlToShare = getRealPathFromURI((Activity) context, uri);
        File F = new File(urlToShare);
        Uri URI = uri;
        if (F.exists()) {
            URI = Uri.fromFile(F);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setDataAndType(Uri.parse(urlToShare), "image/*");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, F.getName());
        intent.putExtra(Intent.EXTRA_SUBJECT, F.getName());
        intent.putExtra(Intent.EXTRA_TITLE, F.getName());

        intent.putExtra(Intent.EXTRA_STREAM, URI);  //optional//use this when you want to send an image
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        java.util.List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase(Locale.US).startsWith("com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/mobile/"; //"https://www.facebook.com/sharer/sharer.php?u=" + URI.getPath() + "&t=" + F.getName();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        context.startActivity(intent);
    }

    public static void SharePictureOnTwitter(Context context, android.net.Uri uri) {

        final String[] twitterApps = {
                // package // name - nb installs (thousands)
                "com.twitter.android", // official - 10 000
                "com.twidroid", // twidroyd - 5 000
                "com.handmark.tweetcaster", // Tweecaster - 5 000
                "com.thedeck.android"// TweetDeck - 5 000 };
        };
        String urlToShare = getRealPathFromURI((Activity) context, uri);
        File F = new File(urlToShare);
        Uri URI = uri;
        if (F.exists()) {
            URI = Uri.fromFile(F);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setDataAndType(Uri.parse(urlToShare), "image/*");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, F.getName());
        intent.putExtra(Intent.EXTRA_SUBJECT, F.getName());
        intent.putExtra(Intent.EXTRA_TITLE, F.getName());

        intent.putExtra(Intent.EXTRA_STREAM, URI);  //optional//use this when you want to send an image
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // See if official Facebook app is found
        boolean twitterAppFound = false;
        java.util.List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            String s = info.activityInfo.packageName;
            System.out.print(s);
            for (String ss : twitterApps) {
                if (s.toLowerCase(Locale.US).startsWith(ss)) {
                    intent.setPackage(s);
                    twitterAppFound = true;
                    break;
                }
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!twitterAppFound) {
            String sharerUrl = "https://about.twitter.com/de/products/list"; //"http://twitter.com/share?text=com.jmg.photoprinter&url=" + URI.getPath();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        context.startActivity(intent);
    }

    public static void SharePicture(Context context, android.net.Uri uri, Cursor c, int id) {

        c.moveToPosition(id);
        final String[] Apps = c.getString(c.getColumnIndex("package")).split(",");
        String urlToShare = getRealPathFromURI((Activity) context, uri);
        File F = new File(urlToShare);
        Uri URI = uri;
        if (F.exists()) {
            URI = Uri.fromFile(F);
            //URI = Uri.parse("file://" + F.getAbsolutePath());
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setDataAndType(Uri.parse(urlToShare), "image/*");
        if (F.getPath().toLowerCase().endsWith(".jpg") || F.getPath().toLowerCase().endsWith(".jpeg")) {
            intent.setType("image/jpeg");
        } else {
            intent.setType("image/*");
        }
        intent.putExtra(Intent.EXTRA_TEXT, F.getName());
        intent.putExtra(Intent.EXTRA_SUBJECT, F.getName());
        intent.putExtra(Intent.EXTRA_TITLE, F.getName());

        intent.putExtra(Intent.EXTRA_STREAM, URI);  //optional//use this when you want to send an image
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // See if official app is found
        boolean AppFound = false;
        java.util.List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            String s = info.activityInfo.packageName;
            System.out.print(s);
            for (String ss : Apps) {
                if (s.toLowerCase(Locale.US).startsWith(ss)) {
                    intent.setPackage(s);
                    context.grantUriPermission(s, URI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    AppFound = true;
                    break;
                }
            }
        }
		
		/*
		if(c.getString(c.getColumnIndex("Name")).equals("Photobucket"))
	    {
			intent.setPackage("");
	    }
	    */
        // As fallback, launch sharer.php in a browser
        if (!AppFound) {
            if (c.getString(c.getColumnIndex("Name")).equals("Pinterest")) {
                String sharerUrl = "http://pinterest.com/pin/create/link/?url=" + URI.getPath();
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            } else {
                String sharerUrl = c.getString(c.getColumnIndex("URL")); //"https://about.twitter.com/de/products/list"; //"http://twitter.com/share?text=com.jmg.photoprinter&url=" + URI.getPath();
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            }
        }

        //context.startActivity(intent);
        context.startActivity(Intent.createChooser(intent, "Share With"));
    }

    public static void SharePictureOnInstagram(Context context, android.net.Uri uri) {

        String urlToShare = getRealPathFromURI((Activity) context, uri);
        File F = new File(urlToShare);
        Uri URI = uri;
        if (F.exists()) {
            URI = Uri.fromFile(F);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setDataAndType(Uri.parse(urlToShare), "image/*");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, F.getName());
        intent.putExtra(Intent.EXTRA_SUBJECT, F.getName());
        intent.putExtra(Intent.EXTRA_TITLE, F.getName());

        intent.putExtra(Intent.EXTRA_STREAM, URI);  //optional//use this when you want to send an image
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // See if official Facebook app is found
        boolean instaAppFound = false;
        java.util.List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            String s = info.activityInfo.packageName;
            System.out.print(s);
            if (s.toLowerCase(Locale.US).startsWith("com.instagram.android")) {
                intent.setPackage(s);
                instaAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!instaAppFound) {
            String sharerUrl = "http://instagram.de.uptodown.com/android"; // "https://www.instagram.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        context.startActivity(intent);
    }

    public static void SharePictureOnPinterest(Context context, android.net.Uri uri) {

        String urlToShare = getRealPathFromURI((Activity) context, uri);
        File F = new File(urlToShare);
        Uri URI = uri;
        if (F.exists()) {
            URI = Uri.fromFile(F);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setDataAndType(Uri.parse(urlToShare), "image/*");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, F.getName());
        intent.putExtra(Intent.EXTRA_SUBJECT, F.getName());
        intent.putExtra(Intent.EXTRA_TITLE, F.getName());

        intent.putExtra(Intent.EXTRA_STREAM, URI);  //optional//use this when you want to send an image
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // See if official Facebook app is found
        boolean pinAppFound = false;
        java.util.List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            String s = info.activityInfo.packageName;
            System.out.print(s);
            if (s.toLowerCase(Locale.US).startsWith("com.pinterest")) {
                intent.setPackage(s);
                pinAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!pinAppFound) {
            String sharerUrl = "http://pinterest.com/pin/create/link/?url=" + URI.getPath();
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        context.startActivity(intent);
    }

    public static Point ScreenSize;

    public static Point getScreenSize(Activity context) {
        if (ScreenSize != null) return ScreenSize;
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        final int width;
        final int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);

        } else {
            //noinspection deprecation
            width = display.getWidth();  // deprecated
            //noinspection deprecation
            height = display.getHeight();  // deprecated
            size = new Point(width, height);
        }
        ScreenSize = size;
        return ScreenSize;
    }

    public static PointF ScreenInches;

    public static PointF getScreenInches(Activity context) {
        if (ScreenInches != null) return ScreenInches;
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = context.getWindowManager().getDefaultDisplay();

        float widthDpi = metrics.xdpi;
        float heightDpi = metrics.ydpi;


        float widthInches = getScreenSize(context).x / widthDpi;
        float heightInches = getScreenSize(context).y / heightDpi;
        ScreenInches = new PointF(widthInches, heightInches);
        return ScreenInches;
    }

    private static class ExStateInfo {
        public Context context;
        public RuntimeException ex;

        public ExStateInfo(Context context, RuntimeException ex) {
            this.context = context;
            this.ex = ex;
        }

    }

    public static synchronized void ShowException(Context context, Exception ex) {
        //System.Threading.SynchronizationContext.Current.Post(new System.Threading.SendOrPostCallback(DelShowException),new ExStateInfo(context, ex));
        String msg;
        AlertDialog.Builder A = new AlertDialog.Builder(context);
        A.setPositiveButton("OK", listener);
        msg = ex.getMessage();
        msg += getCauses(ex);
        A.setMessage(msg);
        A.setTitle("Error " + ex.getClass().getName());
        A.show();
    }

    public static String getCauses(Throwable ex) {
        String res = "";
        while (ex.getCause() != null) {
            res += "\n" + ex.getCause().getMessage();
            ex = ex.getCause();
        }
        return res;
    }

    public static synchronized void ShowMessage(Context context, String msg) {
        //System.Threading.SynchronizationContext.Current.Post(new System.Threading.SendOrPostCallback(DelShowException),new ExStateInfo(context, ex));
        AlertDialog.Builder A = new AlertDialog.Builder(context);
        A.setPositiveButton("OK", listener);
        A.setMessage(msg);
        A.setTitle("Message");
        A.show();
    }

    public static synchronized boolean ShowMessageYesNo(Context context, String msg) {
        //System.Threading.SynchronizationContext.Current.Post(new System.Threading.SendOrPostCallback(DelShowException),new ExStateInfo(context, ex));
        try {
            AlertDialog.Builder A = new AlertDialog.Builder(context);
            A.setPositiveButton("Yes", listener);
            A.setNegativeButton("No", listener);
            A.setMessage(msg);
            A.setTitle("Question");
            A.show();
        } catch (Exception ex) {
            ShowException(context, ex);
        }
        return DialogResultYes;
    }

    public static void ShowToast(Context context, String msg) {
		/*Looper.prepare();*/
        Toast T = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        T.show();
    }

    private static boolean DialogResultYes = false;
    private static DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    DialogResultYes = true;
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    DialogResultYes = false;
                    break;
            }
        }
    };

    public static void copyFile(String Source, String Dest) throws IOException {
        File source = new File(Source);
        File dest = new File(Dest);
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

    public static float convertFromDp(Context context, float input) {
        int minWidth = context.getResources().getDisplayMetrics().widthPixels;
        int minHeight = context.getResources().getDisplayMetrics().heightPixels;
        if (minHeight < minWidth) minWidth = minHeight;
        final float scale = 768.0f / (float) minWidth;
        return ((input - 0.5f) / scale);
    }

    public static synchronized void LoginLive
            (com.microsoft.live.LiveAuthClient Client,
             Activity activity,
             Iterable<String> scopes,
             LiveAuthListener listener) {
        Client.login(activity, scopes, listener);
    }
}
