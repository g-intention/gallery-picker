package com.gallery_picker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.g_intention.gallery_picker.R;
import com.gallery_picker.model.MediaFile;
import com.gallery_picker.utils.FilePickerProvider;
import com.gallery_picker.utils.TimeUtils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class FileGalleryAdapter extends MultiSelectionAdapter<FileGalleryAdapter.ViewHolder> implements MultiSelectionAdapter.OnSelectionListener<FileGalleryAdapter.ViewHolder> {
    public static final int CAPTURE_IMAGE_VIDEO = 1;
    private ArrayList<MediaFile> mediaFiles;
    private Activity activity;
    private RequestManager glideRequest;
    private OnSelectionListener<ViewHolder> onSelectionListener;
    private OnCameraClickListener onCameraClickListener;
    private boolean showCamera;
    private boolean showVideoCamera;
    private File lastCapturedFile;
    private Uri lastCapturedUri;
    private SimpleDateFormat TimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    private String TAG = "To testing";
    private int getCardHeight;

    public FileGalleryAdapter(Activity activity, ArrayList<MediaFile> mediaFiles, int imageSize, boolean showCamera, boolean showVideoCamera, int getCardHeight) {
        super(mediaFiles);
        this.mediaFiles = mediaFiles;
        this.activity = activity;
        this.showCamera = showCamera;
        this.showVideoCamera = showVideoCamera;
        this.getCardHeight = getCardHeight;
        glideRequest = Glide.with(this.activity)
                .applyDefaultRequestOptions(RequestOptions
                        .sizeMultiplierOf(0.70f)
                        .optionalCenterCrop()
                        .override(imageSize));
        super.setOnSelectionListener(this);
        if (showCamera && showVideoCamera)
            setItemStartPosition(2);
        else if (showCamera || showVideoCamera)
            setItemStartPosition(1);
    }

    public File getLastCapturedFile() {
        return lastCapturedFile;
    }

    public void setLastCapturedFile(File file) {
        lastCapturedFile = file;
    }

    public Uri getLastCapturedUri() {
        return lastCapturedUri;
    }

    public void setLastCapturedUri(Uri uri) {
        lastCapturedUri = uri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity)
                .inflate(R.layout.filegallery_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ViewGroup.LayoutParams params = holder.gallery_item_card.getLayoutParams();
        params.height = getCardHeight;

        if (showCamera) {
            if (position == 0) {
                handleCamera(holder.openCamera, false);
                return;
            }
            if (showVideoCamera) {
                if (position == 1) {
                    handleCamera(holder.openVideoCamera, true);
                    return;
                }
                holder.openVideoCamera.setVisibility(View.GONE);
                position--;
            }
            holder.openCamera.setVisibility(View.GONE);
            position--;
        }
        else if (showVideoCamera) {
            if (position == 0) {
                handleCamera(holder.openVideoCamera, true);
                return;
            }
            holder.openVideoCamera.setVisibility(View.GONE);
            position--;
        }

        super.onBindViewHolder(holder, position);
        MediaFile mediaFile = mediaFiles.get(position);
        if (mediaFile.getMediaType() == MediaFile.TYPE_IMAGE) {

            // When we're getting media type image
            // 1-:) First, gone all media types excepted image thumbnail
            holder.image_item_thumbnail.setVisibility(View.VISIBLE);
            holder.video_item_thumbnail.setVisibility(View.GONE);
            holder.container_audio_item_thumbnail.setVisibility(View.GONE);
            holder.container_file_item_thumbnail.setVisibility(View.GONE);
            glideRequest.load(mediaFile.getUri())
                    .into(holder.image_item_thumbnail);

        }
        else if (mediaFile.getMediaType() == MediaFile.TYPE_VIDEO) {
            // When we're getting media type video
            // 1-:) First, gone all media types excepted image thumbnail
            holder.image_item_thumbnail.setVisibility(View.GONE);
            holder.video_item_thumbnail.setVisibility(View.VISIBLE);
            holder.container_audio_item_thumbnail.setVisibility(View.GONE);
            holder.container_file_item_thumbnail.setVisibility(View.GONE);

            // Set Run Time Video Thumbnail
            SetVideoThumbnail(holder.video_item_thumbnail, holder.simpleExoPlayer, holder.view, mediaFile.getUri());

        }
        else if (mediaFile.getMediaType() == MediaFile.TYPE_AUDIO) {

            // When we're getting media type audio
            // 1-:) First, gone all media types excepted image thumbnail
            holder.image_item_thumbnail.setVisibility(View.GONE);
            holder.video_item_thumbnail.setVisibility(View.GONE);
            holder.container_audio_item_thumbnail.setVisibility(View.VISIBLE);
            holder.container_file_item_thumbnail.setVisibility(View.GONE);

            glideRequest.load(mediaFile.getThumbnail())
                    .apply(RequestOptions.placeholderOf(R.drawable.music_thumbnail))
                    .into(holder.audio_item_thumbnail);

        }
        else {

            holder.image_item_thumbnail.setVisibility(View.GONE);
            holder.video_item_thumbnail.setVisibility(View.GONE);
            holder.container_audio_item_thumbnail.setVisibility(View.GONE);
            holder.container_file_item_thumbnail.setVisibility(View.VISIBLE);

            glideRequest.load(mediaFile.getThumbnail())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_pdf_thumbnail))
                    .into(holder.file_item_thumbnail);


        }

        if (mediaFile.getMediaType() == MediaFile.TYPE_VIDEO ||
                mediaFile.getMediaType() == MediaFile.TYPE_AUDIO) {
            holder.fileDuration.setVisibility(View.VISIBLE);
            holder.fileDuration.setText(TimeUtils.getDuration(mediaFile.getDuration()));
        }
        else {
            holder.fileDuration.setVisibility(View.GONE);
        }

        if (mediaFile.getMediaType() == MediaFile.TYPE_FILE
                || mediaFile.getMediaType() == MediaFile.TYPE_AUDIO) {
            holder.audio_file_name.setVisibility(View.VISIBLE);
            holder.pdf_file_name.setVisibility(View.VISIBLE);
            holder.audio_file_name.setText(mediaFile.getName());
            holder.pdf_file_name.setText(mediaFile.getName());
        }
        else {
            holder.audio_file_name.setVisibility(View.GONE);
            holder.pdf_file_name.setVisibility(View.GONE);
        }

        holder.fileSelected.setVisibility(isSelected(mediaFile) ? View.VISIBLE : View.GONE);

    }

    private void SetVideoThumbnail(PlayerView ExoPlayerView, ExoPlayer ExoPlayer, View view, Uri uri) {

        try {

            ExoPlayer = new SimpleExoPlayer.Builder(view.getContext()).build();

            DataSource.Factory factory = new DefaultDataSourceFactory(view.getContext(),
                    Util.getUserAgent(view.getContext(),
                            "GOGOMET"));

            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri);

            // ExoPlayerView
            ExoPlayerView.setPlayer(ExoPlayer);
            ExoPlayerView.setKeepScreenOn(true);
            ExoPlayerView.setUseController(false);

            // ExoPlayer
            ExoPlayer.setPlayWhenReady(true);
            ExoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            ExoPlayer.prepare(mediaSource);
            ExoPlayer.getAudioComponent().setVolume(0f);


        } catch (Exception e) {
            // below line is used for
            // handling our errors.
            Log.e("TAG", "Error : " + e.toString());
        }


    }

    private void handleCamera(final RelativeLayout openCamera, final boolean forVideo) {
        openCamera.setVisibility(View.VISIBLE);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCameraClickListener != null && !onCameraClickListener.onCameraClick(forVideo))
                    return;
                openCamera(forVideo);
            }
        });
    }

    public void openCamera(boolean forVideo) {
        Intent intent;
        String fileName;
        File dir;
        Uri externalContentUri;
        if (forVideo) {
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileName = "/VID_" + getTimeStamp() + ".mp4";
            dir = getExternalStoragePublicDirectory(DIRECTORY_MOVIES);
            externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            dir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
            fileName = "/IMG_" + getTimeStamp() + ".jpeg";
            externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        if (!dir.exists() && !dir.mkdir()) {
            Log.d(TAG, "onClick: " +
                    (forVideo ? "MOVIES" : "PICTURES") + " Directory not exists");
            return;
        }
        lastCapturedFile = new File(dir.getAbsolutePath() + fileName);

        Uri fileUri = FilePickerProvider.getUriForFile(activity, lastCapturedFile);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, lastCapturedFile.getAbsolutePath());
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, System.currentTimeMillis());
        lastCapturedUri = activity.getContentResolver().insert(externalContentUri, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        activity.startActivityForResult(intent, CAPTURE_IMAGE_VIDEO);
    }

    private String getTimeStamp() {
        return TimeStamp.format(new Date());
    }

    @Override
    public void setOnSelectionListener(OnSelectionListener<ViewHolder> onSelectionListener) {
        this.onSelectionListener = onSelectionListener;
    }

    public void setOnCameraClickListener(OnCameraClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    @Override
    public int getItemCount() {
        if (showCamera) {
            if (showVideoCamera)
                return mediaFiles.size() + 2;
            return mediaFiles.size() + 1;
        }
        else if (showVideoCamera) {
            return mediaFiles.size() + 1;
        }
        return mediaFiles.size();
    }

    @Override
    public void onSelectionBegin() {
        if (onSelectionListener != null) {
            onSelectionListener.onSelectionBegin();
        }
    }

    @Override
    public void onSelected(ViewHolder view, int position) {
        if (onSelectionListener != null) {
            onSelectionListener.onSelected(view, position);
        }
        view.fileSelected.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUnSelected(ViewHolder view, int position) {
        if (onSelectionListener != null) {
            onSelectionListener.onUnSelected(view, position);
        }
        view.fileSelected.setVisibility(View.GONE);
    }

    @Override
    public void onSelectAll() {
        if (onSelectionListener != null) {
            onSelectionListener.onSelectAll();
        }
    }

    @Override
    public void onUnSelectAll() {
        if (onSelectionListener != null) {
            onSelectionListener.onUnSelectAll();
        }
    }

    @Override
    public void onSelectionEnd() {
        if (onSelectionListener != null) {
            onSelectionListener.onSelectionEnd();
        }
    }

    @Override
    public void onMaxReached() {
        if (onSelectionListener != null) {
            onSelectionListener.onMaxReached();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView gallery_item_card;
        private View view;
        private LinearLayout fileSelected;
        private RelativeLayout openCamera, openVideoCamera;
        private ShapeableImageView image_item_thumbnail;
        private PlayerView video_item_thumbnail;
        private SimpleExoPlayer simpleExoPlayer;
        private LinearLayout container_audio_item_thumbnail, container_file_item_thumbnail;
        private ShapeableImageView audio_item_thumbnail;
        private ShapeableImageView file_item_thumbnail;
        private TextView fileDuration, audio_file_name, pdf_file_name;

        ViewHolder(View v) {
            super(v);
            view = v;
            gallery_item_card = v.findViewById(R.id.gallery_item_card);
            openCamera = v.findViewById(R.id.file_open_camera);
            openVideoCamera = v.findViewById(R.id.file_open_video_camera);
            image_item_thumbnail = v.findViewById(R.id.image_item_thumbnail);
            video_item_thumbnail = v.findViewById(R.id.video_item_thumbnail);
            container_audio_item_thumbnail = v.findViewById(R.id.container_audio_item_thumbnail);
            container_file_item_thumbnail = v.findViewById(R.id.container_file_item_thumbnail);
            audio_item_thumbnail = v.findViewById(R.id.audio_item_thumbnail);
            file_item_thumbnail = v.findViewById(R.id.file_item_thumbnail);
            fileDuration = v.findViewById(R.id.file_duration);
            audio_file_name = v.findViewById(R.id.audio_file_name);
            pdf_file_name = v.findViewById(R.id.pdf_file_name);
            fileSelected = v.findViewById(R.id.file_selected);

        }
    }

    public interface OnCameraClickListener {
        boolean onCameraClick(boolean forVideo);
    }
}
