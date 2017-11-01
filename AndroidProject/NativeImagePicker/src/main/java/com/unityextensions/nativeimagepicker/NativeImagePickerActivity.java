package com.unityextensions.nativeimagepicker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NativeImagePickerActivity extends Activity {

    final String GameObjectName = "NIP_599349_GO";
    final int SELECT_IMAGE = 9000;
    final int SELECT_CAMERA = 8000;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        boolean camera = bundle.getBoolean("fromCamera", false);

        if (camera)
            fromCamera();
        else
            fromLibrary();
    }

    void fromLibrary() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        try
        {
            startActivityForResult(intent, SELECT_IMAGE);
        } catch (ActivityNotFoundException e) {
            onActivityResult(SELECT_IMAGE, Activity.RESULT_CANCELED, intent);
        }
    }

    void fromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException e) {
                //...
            }

            if (file == null) {
                Fail();
                return;
            }

            //Uri uri = Uri.fromFile(file);
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, SELECT_CAMERA);
        } else {
            Fail();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Fail();
            return;
        }

        if (requestCode == SELECT_IMAGE) {
            if (data == null) {
                Fail();
                return;
            }

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor == null) {
                Fail();
                return;
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (picturePath == null || picturePath.equals("")) {
                Fail();
                return;
            }

            mCurrentPhotoPath = picturePath;

            boolean result = RotateAndSave();

            if (result)
                Success(mCurrentPhotoPath);
            else
                Fail();

            return;
        }

        if (requestCode == SELECT_CAMERA) {
            boolean result = RotateAndSave();

            if (result)
                Success(mCurrentPhotoPath);
            else
                Fail();
        }

        Fail();
    }

    void Fail() {
        UnityPlayer.UnitySendMessage(GameObjectName, "CallbackSelectedImage", "");

        finish();
    }

    void Success(final String picturePath) {
        String path = "file://" + picturePath;

        UnityPlayer.UnitySendMessage(GameObjectName, "CallbackSelectedImage", path);

        finish();
    }

    private Boolean RotateAndSave() {
        Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath);

        if (bmp == null)
            return false;

        Matrix matrix = new Matrix();
        matrix.postRotate(Orientation());
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(new File(mCurrentPhotoPath));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private int Orientation() {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate += 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate += 90;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate += 90;
        }
        return rotate;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "temp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        if (image.exists())
            image.delete();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
