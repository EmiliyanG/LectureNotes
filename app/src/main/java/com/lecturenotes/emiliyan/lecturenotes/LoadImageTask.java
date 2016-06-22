package com.lecturenotes.emiliyan.lecturenotes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Emiliyan on 3/13/2016.
 */
public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Activity activity;
    private int imgThumbnailRes = 0;
    public LoadImageTask(Activity activity,ImageView imageView) {
        this.activity = activity;
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }
    public LoadImageTask(Activity activity,ImageView imageView, int imgThumbnailRes) {
        this.activity = activity;
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.imgThumbnailRes = imgThumbnailRes;
    }


    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap mImageBitmap = null;
        try {
            mImageBitmap = SaveVariables.loadImage(activity,params[0]);//get image
            if(imgThumbnailRes != 0) mImageBitmap = ThumbnailUtils.extractThumbnail(mImageBitmap, imgThumbnailRes, imgThumbnailRes);//get thumbnail for image

        } catch (Exception e){
            System.out.println("Error came from async task while loading an image ");
        }

        return mImageBitmap;

    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }else{
            System.out.println("error comes from async task");
        }
    }
}