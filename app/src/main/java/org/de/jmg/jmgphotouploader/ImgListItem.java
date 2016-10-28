package org.de.jmg.jmgphotouploader;

import android.graphics.*;
import android.content.*;
import android.net.Uri;
import android.provider.*;
import android.provider.MediaStore.Images.Thumbnails;

public class ImgListItem extends Object
{

	public ImgListItem(Context context, String id, int ImageID, String FileName, android.net.Uri Uri, String folder, ImgFolder.Type type, String Size)
	{
		this.id = id;
		this.ImageID = ImageID;
		this.FileName = FileName;
		this._size = Size;
		this.Uri = Uri;
		this.folder = folder;
		this.context = context;
		this.type = type;
	}
	private Bitmap _Img;
	private Bitmap _DownImg;
	private android.net.Uri _DownUri = null;
	public Context context;
	public String id;
	public int ImageID;
	public String FileName;
	public String Name;
	public String folder;
	public android.net.Uri Uri;
	public ImgFolder.Type type;
	public String ThumbNailLink;

	public boolean ThumbnailLoaded;
	private String _size;


	public Uri getDownUri()
	{
		return _DownUri;
	}
	public void setDownUri(Uri uri)
	{
		_DownUri = uri;
	}
	public final Bitmap getDownImg()
	{
		return _DownImg;
	}
	public void setDownImg(Bitmap value)
	{
		_DownImg = value;
	}
	public final Bitmap getImg()
	{
		if (_Img == null)
		{
			Bitmap bitmap = null;
			try
			{
				BitmapFactory.Options tempVar = new BitmapFactory.Options();
				tempVar.inSampleSize = 1;
				bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), ImageID, Thumbnails.MICRO_KIND, tempVar);
				if (bitmap == null)
				{
					try
					{
						bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri);
						_size = bitmap.getWidth() + "*" + bitmap.getHeight();
						double rel = bitmap.getWidth() / bitmap.getHeight();
						if (rel > 1)
						{
							bitmap = Bitmap.createScaledBitmap(bitmap, 96, (int)(96 / rel), false);
						}
						else
						{
							bitmap = Bitmap.createScaledBitmap(bitmap, (int)(96 * rel), 96, false);
						}
					}
					catch (java.lang.Exception e)
					{
					}
				}
				else
				{
					BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
					sizeOptions.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(folder, sizeOptions);
					_size = sizeOptions.outWidth + "*" + sizeOptions.outHeight;
				}
					//return bitmap;
			}
			catch (RuntimeException ex)
			{
				lib.ShowException(context, ex);
			}
			finally
			{
				_Img = bitmap;
			}

		}
		return _Img;
	}
	public final void setImg(Bitmap value)
	{
		_Img = value;
	}
	public final String getsize()
	{
		if (_size == null && _size == "")
		{
			try
			{
			BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
			sizeOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(folder, sizeOptions);
			_size = sizeOptions.outWidth + "*" + sizeOptions.outHeight;
			if ((_size == null))
			{
				_size = lib.getSizeFromURI(context, Uri); //img.Width + "*" + img.Height;
			}
			}
			catch (RuntimeException ex)
			{
				lib.ShowException(context, ex);
			}
		}
		return _size;
	}
	public final void setsize(String value)
	{
		_size = value;
	}
}