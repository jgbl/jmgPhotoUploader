/*package org.de.jmg.jmgphotouploader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.GphotoFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Picasa
{
    private static final String API_PREFIX
            = "https://picasaweb.google.com/data/feed/api/user/";
    PicasawebService picasaService;
    String userId;

    public Picasa(String authToken, String userID)
    {
        initPicasa(authToken);
        this.userId = userID;
    }

    public <T extends GphotoFeed> T getFeed(String feedHref,
                                            Class<T> feedClass) throws IOException, ServiceException
    {
        lib.setgstatus("Get Feed URL: " + feedHref);
        return picasaService.getFeed(new URL(feedHref), feedClass);
    }

    public List<AlbumEntry> getAlbums() throws IOException,
            ServiceException
    {

        String albumUrl = API_PREFIX + userId;
        UserFeed userFeed = getFeed(albumUrl, UserFeed.class);

        List<GphotoEntry> entries = userFeed.getEntries();
        List<AlbumEntry> albums = new ArrayList<AlbumEntry>();
        for (GphotoEntry entry : entries)
        {
            AlbumEntry ae = new AlbumEntry(entry);
            lib.setgstatus(String.format("Album name {}", ae.getName()));
            albums.add(ae);
        }

        return albums;
    }

    public List<PhotoEntry> getPhotos(AlbumEntry album) throws IOException,
            ServiceException
    {
        AlbumFeed feed = album.getFeed();
        List<PhotoEntry> photos = new ArrayList<PhotoEntry>();
        for (GphotoEntry entry : feed.getEntries())
        {
            PhotoEntry pe = new PhotoEntry(entry);
            photos.add(pe);
        }
        lib.setgstatus(String.format("Album {} has {} photos", album.getName(), photos.size()));
        return photos;
    }

    public void initPicasa(String authToken)
    {
        picasaService = new PicasawebService("pictureframe");

        picasaService.setUserToken(authToken);
    }

    public Bitmap getBitmap(PhotoEntry photo) throws Throwable
    {
        URL photoUrl = new URL(photo.getMediaContents().get(0).getUrl());
        Bitmap bmp = BitmapFactory.decodeStream(photoUrl.openConnection().getInputStream());
        return bmp;
    }
}

*/