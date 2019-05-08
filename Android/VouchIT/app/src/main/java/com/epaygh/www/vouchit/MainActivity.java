package com.epaygh.www.vouchit;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private TextView result;
    private Button try_button, ussd_button;

    File captured_file;
    Uri captured_uri;

    File photoFile = null;
    private String imageFilePath = "";
    OCRManager manager = new OCRManager();

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.CAMERA
    };

    public static final int REQUEST_PERMISSION = 200;

//    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.welcome);
        try_button = (Button) findViewById(R.id.try_button);
        ussd_button = (Button) findViewById(R.id.ussdbtn);
        imageView = (ImageView) findViewById(R.id.my_avatar);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_PERMISSION);
//        }
//        selectImage(this);

        //handle ussd button onclick
        ussd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("USSD BUTTON", "USSD BUTTON CLICKED!");

                String extracted_code = "283748372617264";

                String ussd_code = "*134*" + extracted_code + Uri.encode("#");

                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                Manifest.permission.CALL_PHONE)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed; request the permission
                            int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        // Permission has already been granted
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd_code)));
                    }



//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode("*3282#")));
//                startActivity(intent);

//                String encodedHash = Uri.encode("#");
//                String ussd = "*" + encodedHash + "123" + encodedHash;
//                startActivityForResult(new Intent("android.intent.action.CALL",
//                        Uri.parse("tel:" + ussd)), 1);

//                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // Permission is not granted
//                }

            }
        });

        //Handle button click here
        try_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Vouch", "TRY BUTTON CLICKED");
                        selectImage(MainActivity.this);

            }
        });

//        kj

    }

    @Override
    public void onClick(View view) {

    }

    private class OCRManager {
        TessBaseAPI baseAPI = null;
        public void initAPI(){
            baseAPI = new TessBaseAPI();
            String dataPath = MainApplication.instance.getTessDataParentDirectory();

            baseAPI.init(dataPath, "eng");
        }

        public String startRecognizer(Bitmap bitmap){
            if (baseAPI == null)
                initAPI();
            baseAPI.setImage(bitmap);
            return baseAPI.getUTF8Text();
        }
    }

    //start
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
//                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                        imageView.setImageBitmap(selectedImage);

                        ///////
//                        Uri selectedImg = data.getData();
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImg);
//                            imageView.setImageBitmap(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        //////

//                        imageView.setImageURI(Uri.parse(imageFilePath));

                        Log.d("ImageFilePath", imageFilePath);

                        Log.d("PhotoFile", photoFile.toString());

                        Bitmap selectedImage2 = BitmapFactory.decodeFile(imageFilePath);

//                        File sd = Environment.getExternalStorageDirectory();
                        File image = new File(imageFilePath);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.toString(),bmOptions);
                        imageView.setImageBitmap(bitmap);


//                        manager.initAPI();
                        String extracted_text = manager.startRecognizer(bitmap);
                        result.setText(extracted_text);


                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                Bitmap selectedImage2 = BitmapFactory.decodeFile(picturePath);
                                Log.d("PicturePath", picturePath);


                                //Extract text here
                                String extracted_text = manager.startRecognizer(selectedImage2);
                                result.setText(extracted_text);



                                cursor.close();


                            }
                        }

                    }
                    break;
            }
        }
    }

//start
    private void selectImage(View.OnClickListener context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Camera View", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder((Context) context);
        builder.setTitle("Choose Voucher Photo");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);



//                    if(takePicture.resolveActivity(getPackageManager()) != null){
//                        //Create a file to store the image
//                        File photoFile = null;
//                        try {
//                            photoFile = createImageFile();
//                        } catch (IOException ex) {
//                            // Error occurred while creating the File
//                        }
//                        if (photoFile != null) {
//                            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,                                                                                                    "com.example.android.provider", photoFile);
//                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
//                                    photoURI);
//                            startActivityForResult(takePicture, 0);
//                        }
//                    }
                    openCameraIntent();



                } else if (options[item].equals("Choose from Gallery")) {

                    if(!hasPermissions(MainActivity.this, PERMISSIONS)){
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }


//                    if (EasyPermissions.hasPermissions(MainActivity.this, galleryPermissions)) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);
//                    } else {
//                        EasyPermissions.requestPermissions(this, "Access for storage",
//                                101, galleryPermissions);
//                    }



                } else if(options[item].equals("Camera View")){
                        //start live camera here
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

//            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, 0);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

}