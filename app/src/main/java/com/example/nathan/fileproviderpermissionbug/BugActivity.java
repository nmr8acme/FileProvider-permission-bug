package com.example.nathan.fileproviderpermissionbug;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import java.io.File;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class BugActivity extends Activity {
    private static final int PHOTO_REQUEST_CODE = "PHOTO_REQUEST_CODE".hashCode();
    private static final String PHOTO_FILENAME = "photos/photo.jpg";
    private static final int READ_WRITE_FLAGS =
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION;

    private File photoFile;
    private ImageView photoRequestResult;
    private Uri contentUri;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug);
        photoRequestResult = (ImageView) findViewById(R.id.image_view);

        photoFile = new File(getFilesDir(), PHOTO_FILENAME);
        photoFile.getParentFile().mkdir();
    }

    public void withSetFlags(View view) {
        Intent photoIntent = createPhotoIntent(this, photoFile);
        photoIntent.setFlags(READ_WRITE_FLAGS);
        startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
    }

    public void withGrantUriPermission(View view) {
        Intent photoIntent = createPhotoIntent(this, photoFile);
        contentUri = photoIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        grantUriPermission("com.google.android.GoogleCamera", contentUri, READ_WRITE_FLAGS);
        startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE) {
            Bitmap bm = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            photoRequestResult.setImageBitmap(bm);
            photoFile.delete();
            revokeUriPermission(contentUri, READ_WRITE_FLAGS);
        }
    }

    public static Intent createPhotoIntent(Activity activity, File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String authority = "com.example.nathan.fileproviderpermissionbug.fileprovider";
        Uri contentUri = FileProvider.getUriForFile(activity, authority, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri); // set the image file name
        return intent;
    }
}
