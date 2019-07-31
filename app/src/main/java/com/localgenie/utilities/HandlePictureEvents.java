package com.localgenie.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.localgenie.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import eu.janmuller.android.simplecropimage.CropImage;

import static android.os.Build.VERSION_CODES.N;

//import com.notary90210.interfaceMgr.ImageUploadedAmazon;

/**
 * <h>HandlePictureEvents</h>
 * this class open the popup for the option to take the image
 * after it takes the the, it crops the image
 * and then upload it to amazon
 * Created by ${Ali} on 8/17/2017.
 */

public class HandlePictureEvents
{
    public File newFile;
    UploadAmazonS3 upload;
    private Activity mcontext = null;
    private String takenNewImage;
    private Fragment fragment = null;
    private  String amazonFileName="";

    public HandlePictureEvents(Activity mcontext , Fragment fragment)
    {
        this.fragment = fragment;
        this.mcontext = mcontext;
        initializeAmazon();
    }
    public HandlePictureEvents(Activity mcontext)
    {
        this.mcontext = mcontext;
        initializeAmazon();
    }

    private void initializeAmazon()
    {
        upload = UploadAmazonS3.getInstance(mcontext, Constants.Amazoncognitoid);

    }

    /**
     * <h>openDialog</h>
     * this dialog have the option to choose whether to take picture
     * or open gallery or cancel the dialog
     */

    public void openDialog()
    {
        takenNewImage = "DayRunner"+String.valueOf(System.nanoTime())+".png";
        CreateOrClearDirectory directory = CreateOrClearDirectory.getInstance();
        newFile = directory.getAlbumStorageDir(mcontext, Constants.PARENT_FOLDER+"/Profile_Pictures",false);
        final Resources resources = mcontext.getResources();
        final CharSequence[] options = {resources.getString(R.string.takephoto), resources.getString(R.string.choose_from_gallery), resources.getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(resources.getString(R.string.takephoto)))
                {
                    takePicFromCamera();
                }
                else if (options[item].equals(resources.getString(R.string.choose_from_gallery))) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    if(fragment!=null)
                        fragment.startActivityForResult(photoPickerIntent, Constants.GALLERY_PIC);
                    else
                        mcontext.startActivityForResult(photoPickerIntent, Constants.GALLERY_PIC);
                }
                else if (options[item].equals(resources.getString(R.string.cancel))){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    /**
     * <h1>takePicFromCamera</h1>
     * This method is got called, when user chooses to take photos from camera.
     */
    private void takePicFromCamera()
    {
        String state;
        try
        {
            Uri newProfileImageUri;
            takenNewImage = "";
            state = Environment.getExternalStorageState();
            takenNewImage = "takenNewImage"+String.valueOf(System.nanoTime())+".png";
            if (Environment.MEDIA_MOUNTED.equals(state))
                newFile = new File(Environment.getExternalStorageDirectory()+"/"+ Constants.PARENT_FOLDER+"/Profile_Pictures/",takenNewImage);
            else
                newFile = new File(mcontext.getFilesDir() + "/" + Constants.PARENT_FOLDER + "/Profile_Pictures/", takenNewImage);
            if (Build.VERSION.SDK_INT >= N)
                newProfileImageUri = FileProvider.getUriForFile(mcontext, com.localgenie.BuildConfig.APPLICATION_ID + ".provider", newFile);
            else
                newProfileImageUri = Uri.fromFile(newFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, newProfileImageUri);
            intent.putExtra("return-data", true);
            if(fragment!=null)
                fragment.startActivityForResult(intent, Constants.CAMERA_PIC);
            else
                mcontext.startActivityForResult(intent, Constants.CAMERA_PIC);
        }
        catch (ActivityNotFoundException e)
        {
            Log.d("TAG", "takePicFromCamera: "+e);
        }
    }
    /**
     * This method got called when cropping starts done.
     * @param newFile image file to be cropped
     */
    public File startCropImage(File newFile)
    {

        Log.d("TAG", "startCropImage: " + newFile + " CROP " + this.newFile.getPath());
        Intent intent = new Intent(mcontext,CropImage.class );
        intent.putExtra(CropImage.IMAGE_PATH, this.newFile.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 4);
        intent.putExtra(CropImage.ASPECT_Y, 4);
        if(fragment!=null)
            fragment.startActivityForResult(intent, Constants.CROP_IMAGE);
        else
            mcontext.startActivityForResult(intent, Constants.CROP_IMAGE);
        return newFile;
    }

    /**
     * <h1>gallery</h1>
     * This method is got called, when user chooses to take photos from camera.
     * @param data uri data given by gallery
     */
    public File gallery(Uri data)
    {

        try {

            String state = Environment.getExternalStorageState();
            takenNewImage = "takenNewImage" + String.valueOf(System.nanoTime()) + ".png";
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                newFile = new File(Environment.getExternalStorageDirectory() + "/" + Constants.PARENT_FOLDER+ "/Profile_Pictures/", takenNewImage);
            } else {
                newFile = new File(mcontext.getFilesDir() + "/" + Constants.PARENT_FOLDER+ "/Profile_Pictures/", takenNewImage);
            }
            InputStream inputStream = mcontext.getContentResolver().openInputStream(data);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            Utility.copyStream(inputStream, fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
            // newProfileImageUri = Uri.fromFile(newFile);
            startCropImage(newFile);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return newFile;
    }
    /**
     *<h1>uploadToAmazon</h1>
     * This method is used to upload the image on AMAZON bucket.
     * @param image image file to be uploaded
     * @param imageupload interface call back for the update of profile on the server
     */
    public void uploadToAmazon(String bucketName,File image, final ImageUploadedAmazon imageupload)
    {
        upload.Upload_data(bucketName, image, new UploadAmazonS3.Upload_CallBack() {
            @Override
            public void sucess(String sucess) {
                imageupload.onSuccessAdded(sucess);
            }
            @Override
            public void error(String errormsg) {
                imageupload.onerror(errormsg);
            }
        });
    }
}
