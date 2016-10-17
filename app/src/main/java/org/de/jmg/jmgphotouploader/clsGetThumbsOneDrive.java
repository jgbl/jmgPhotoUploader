package org.de.jmg.jmgphotouploader;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;

public class clsGetThumbsOneDrive implements Runnable {
	public void run() {
		try {
			GetThumbnailsOneDrive(context, BMList);
		} catch (LiveOperationException e) {
			// TODO Auto-generated catch block
			lib.ShowException(context, e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			lib.ShowException(context, e);
		} catch (Exception e) {
			lib.ShowException(context, e);
		}
	}

	public  LiveConnectClient client;
	public  LiveOperation LiveOp;
	public  CountDownLatch Latch;
	private final Activity context;
	private final java.util.ArrayList<ImgFolder> BMList;
	private void GetThumbnailsOneDrive(final Activity context, final java.util.ArrayList<ImgFolder> BMList) throws LiveOperationException, InterruptedException
	{
		//com.microsoft.live.LiveAuthClient mlAuth = new com.microsoft.live.LiveAuthClient(context, clientId);
		/*
		if (client == null)
		{
			 Intent intent = new Intent(context, LoginLiveActivity.class);
			 int requestCode = 1;
			context.startActivityForResult(intent, requestCode);
			 
		}
		*/
		Date dtDate = new Date();
		while (client == null){
			if(new Date().getSeconds()-dtDate.getSeconds()>200) break;
			Thread.sleep(1000, 0);
		}
		if (client != null){
			try 
			{
				String queryString = "me/skydrive/files";//?filter=folders,albums";
	            //Latch = new CountDownLatch(1);
	            client.getAsync(queryString, new LiveOperationListener() 
	            {
					@Override
					public void onError(LiveOperationException exception,
							LiveOperation operation) {
						// TODO Auto-generated method stub
						lib.ShowException(context, exception);
						LiveOp = operation;
						Latch.countDown();
					}
					
					@Override
					public void onComplete(LiveOperation operation) {
						// TODO Auto-generated method stub
						LiveOp = operation;
						if (LiveOp != null)
			            {
				    		JSONObject folders = LiveOp.getResult();
				    		if (folders != null)
				    		{
					            final JSONArray data = folders.optJSONArray("data");
					            for (int i = 0; i < data.length(); i++) { 
					                final JSONObject oneDriveItem = data.optJSONObject(i);
					                if (oneDriveItem != null) {
					                	System.out.println(oneDriveItem.toString());
					                    final String itemName = oneDriveItem.optString("name");
					                    //final String itemType = oneDriveItem.optString("type");
					                    ImgFolder Folder = new ImgFolder(itemName,ImgFolder.Type.OneDriveAlbum);
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
										                    final String itemType = oneDriveItem.optString("type");
										                    final String id = oneDriveItem.optString("id");
										                    final String uri = oneDriveItem.optString("link");
										                    final String size = oneDriveItem.optString("size");
										                    Folder.items.add(new ImgListItem(context,id, i2, itemName2, null, uri,ImgFolder.Type.OneDriveAlbum,size));
										                } 
										            }
									    		}
								            }
										} catch (LiveOperationException e) {
											// TODO Auto-generated catch block
											lib.ShowException(context, e);
										}
					    	            
					                    //Folder.items.add(new ImgListItem(context, imageId, (new java.io.File(folder)).getName(), Uri, folder));
					                    BMList.add(Folder);
					                } 
					            }
				    		}
			            }
						//  get all folders
						Latch.countDown();
					}
				} );
			}
			catch(Exception e)
			{
				lib.ShowException(context, e);
			}
			finally{
				//Latch.countDown();
			}
		}
	}

	public clsGetThumbsOneDrive(CountDownLatch Latch,LiveConnectClient client,final Activity context, final java.util.ArrayList<ImgFolder> BMList) {
		this.Latch = Latch;
		this.client = client;
		this.context = context;
		this.BMList = BMList;
	}

}
