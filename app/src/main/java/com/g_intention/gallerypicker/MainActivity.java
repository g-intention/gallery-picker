package com.g_intention.gallerypicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.g_intention.gallerypicker.PickerModel.Model;
import com.g_intention.gallerypicker.adapter.MediaPickerAdapter;
import com.gallery_picker.activity.FilePickerActivity;
import com.gallery_picker.config.Configurations;
import com.gallery_picker.model.MediaFile;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Initialize Request Code
    private final int IMAGE_REQUEST_CODE = 101;
    private final int VIDEO_REQUEST_CODE = 102;
    private final int AUDIO_REQUEST_CODE = 103;
    private final int PDF_REQUEST_CODE = 104;

    // Declare variable
    Button image_picker_button, video_picker_button, audio_picker_button, pdf_picker_button;
    GridView idMediaPickerGV;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference
        idMediaPickerGV = findViewById(R.id.idMediaPickerGV);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ArrayList<Model> MediaPickerModelArrayList = new ArrayList<Model>();
        MediaPickerModelArrayList.add(new Model(getResources().getString(R.string.video), R.drawable.ic_video_thumbnail));
        MediaPickerModelArrayList.add(new Model(getResources().getString(R.string.image), R.drawable.ic_camera__thumbnail));
        MediaPickerModelArrayList.add(new Model(getResources().getString(R.string.audio), R.drawable.ic_music_thumbnail));
        MediaPickerModelArrayList.add(new Model(getResources().getString(R.string.pdf), R.drawable.ic_pdf_thumbnail));

        MediaPickerAdapter adapter = new MediaPickerAdapter(this, MediaPickerModelArrayList);
        idMediaPickerGV.setAdapter(adapter);

        idMediaPickerGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        PickVideo();
                        break;
                    case 1:
                        PickImage();
                        break;
                    case 2:
                        PickAudio();
                        break;
                    case 3:
                        PickPDF();
                        break;

                }
            }
        });

    }


    // To implementing for picking image
    private void PickImage() {
        // Check permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // When permissions are not granted
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

        } else {
            // -- When permissions granted
            // -- Create image picker method
            mImagePicker();

        }

    }

    private void mImagePicker() {

        // -- Let's pick image
        // -- Initialize intent
        Intent intent = new Intent(this, FilePickerActivity.class);
        // -- Put extra
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(false)
                .setShowImages(true)
                .setShowAudios(false)
                .setShowFiles(false)
                .enableImageCapture(true)
                .setMaxSelection(100)
                .setSkipZeroSizeFiles(true)
                .setPortraitSpanCount(3)
                .build());
        // Start activity result
        startActivityForResult(intent, IMAGE_REQUEST_CODE);


    }

    // To implementing for picking image
    private void PickVideo() {

        // Check permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // When permissions are not granted
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);

        } else {
            // When permissions granted
            // Create image picker method
            mVideoPicker();

        }

    }

    private void mVideoPicker() {

        // Let's pick video
        // Initialize intent
        Intent intent = new Intent(this, FilePickerActivity.class);
        // Put extra
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(true)
                .setShowImages(false)
                .setShowAudios(false)
                .setShowFiles(false)
                .enableVideoCapture(true)
                .setMaxSelection(100)
                .setSkipZeroSizeFiles(true)
                .setPortraitSpanCount(3)
                .build());
        // Start activity result
        startActivityForResult(intent, VIDEO_REQUEST_CODE);


    }

    // To implementing for picking audio
    private void PickAudio() {
        // -- Let's pick audio
        // -- Initialize intent
        Intent intent = new Intent(this, FilePickerActivity.class);
        // Put extra
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(false)
                .setShowImages(false)
                .setShowAudios(true)
                .setShowFiles(false)
                .enableVideoCapture(true)
                .setMaxSelection(100)
                .setSkipZeroSizeFiles(true)
                .setPortraitSpanCount(3)
                .build());
        // Start activity result
        startActivityForResult(intent, AUDIO_REQUEST_CODE);

    }

    // To implementing for picking PDF
    private void PickPDF() {
        // -- Let's pick PDF
        // -- Initialize intent
        Intent intent = new Intent(this, FilePickerActivity.class);
        // Put extra
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(false)
                .setShowImages(false)
                .setShowAudios(false)
                .setShowFiles(true)
                .enableVideoCapture(true)
                .setMaxSelection(100)
                .setSkipZeroSizeFiles(true)
                .setPortraitSpanCount(3)
                .build());
        // Start activity result
        startActivityForResult(intent, PDF_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // When permissions granted
            // Check conditions
            if (requestCode == 1) {
                // When requestCode is 1
                // Call mImagePicker Method
                mImagePicker();
            } else {
                // When requestCode is 2
                mVideoPicker();
            }
        } else {
            // When permission is denied
            // Show Toast message
            Toast.makeText(this, "Please allow permission", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check conditions
        if (requestCode == RESULT_OK && data != null) {
            // When resultCode is OM and data is not equal to null
            // Initialize Array List
            ArrayList<MediaFile> mMediaFiles = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);

            // Get Size of selected media data
            String mSize = String.valueOf(mMediaFiles.size());

            // Check conditions
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    String img_message = "You have selected " + mSize + " Images";
                    ShowToast(img_message);
                    break;

                case VIDEO_REQUEST_CODE:
                    String video_message = "You have selected " + mSize + " Video";
                    ShowToast(video_message);
                    break;

                case AUDIO_REQUEST_CODE:
                    String audio_message = "You have selected " + mSize + " Audio";
                    ShowToast(audio_message);
                    break;

                case PDF_REQUEST_CODE:
                    String pdf_message = "You have selected " + mSize + " PDF";
                    ShowToast(pdf_message);
                    break;


            }


        }
        ;

    }

    // Show Simple Toast Message
    private void ShowToast(String message) {
        Toast.makeText(this, "Message: " + message, Toast.LENGTH_SHORT).show();
    }

}
