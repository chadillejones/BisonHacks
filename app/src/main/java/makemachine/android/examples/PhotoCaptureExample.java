package makemachine.android.examples;

import java.io.File;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.nio.charset.StandardCharsets;


public class PhotoCaptureExample extends Activity {
    protected Button _button;
    protected ImageView _image;
    protected TextView _field;
    protected String _path;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN = "photo_taken";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        _image = (ImageView) findViewById(R.id.image);
        _field = (TextView) findViewById(R.id.field);
        _button = (Button) findViewById(R.id.button);
        _button.setOnClickListener(new ButtonClickHandler());

        _path = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";

    }



    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.i("MakeMachine", "ButtonClickHandler.onClick()");
            startCameraActivity();
        }
    }

    protected void startCameraActivity() {
        Log.i("MakeMachine", "startCameraActivity()");
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:
                onPhotoTaken();
                break;
        }
        try {
            mainuh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onPhotoTaken() {
        Log.i("MakeMachine", "onPhotoTaken");

        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        _image.setImageBitmap(bitmap);

        _field.setVisibility(View.GONE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(PhotoCaptureExample.PHOTO_TAKEN)) {
            onPhotoTaken();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PhotoCaptureExample.PHOTO_TAKEN, _taken);




    }



        public static void mainuh() throws IOException {

            Log.v("MyActivity", "hiya");

            String credentialsToEncode = "acc_cba1bd39fd559e0" + ":" + "4917a5e8068206aae5d820fbfaa35f96";
            String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

            // Change the file path here
            String filepath = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";
            File fileToUpload = new File(filepath);

            String endpoint = "/tags";

            String crlf = "\r\n";
            String twoHyphens = "--";
            String boundary =  "Image Upload";

            URL urlObject = new URL("https://api.imagga.com/v2" + endpoint);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(connection.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + fileToUpload.getName() + "\"" + crlf);
            request.writeBytes(crlf);


            InputStream inputStream = new FileInputStream(fileToUpload);
            int bytesRead;
            byte[] dataBuffer = new byte[1024];
            while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
                request.write(dataBuffer, 0, bytesRead);
            }

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            request.flush();
            request.close();

            InputStream responseStream = new BufferedInputStream(connection.getInputStream());

            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();
            System.out.println(response);
            Log.v("MyActivity", response);

            System.out.println("protocol = " + urlObject.getProtocol());

            responseStream.close();
            connection.disconnect();
        }
    }



