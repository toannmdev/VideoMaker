package com.hiep.video.maker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.exoplayer.ExoPlayer;
import com.hiep.video.maker.entity.VideoEntity;
import com.hiep.video.maker.player.DemoPlayer;
import com.hiep.video.maker.player.ExtractorRendererBuilder;
import com.hiep.video.maker.system.AppConfig;
import com.hiep.video.maker.system.Config;
import com.hiep.video.maker.util.FFmpegCmdUtil;
import com.hiep.video.maker.util.FileUtil;
import com.hiep.video.maker.util.Logger;
import com.hiep.video.maker.widget.GifImageView;

import java.io.File;

/**
 * Created by anh on 8/7/2016.
 */
public class AddGifActivity extends BaseActivity implements SurfaceHolder.Callback,DemoPlayer.Listener {

    private static final java.lang.String TAG = AddGifActivity.class.getSimpleName();
    public static final int HANDLE_SHOW_VIDEO_DURATION = 1;

    private LinearLayout mLlBack;
    private LinearLayout mLlDone;

    private SurfaceView mSvVideo;
    private ProgressBar mPgDuration;
    private ImageView mIvStatus;
    private GifImageView mGifImg;

    private FFmpeg ffmpeg;
    private DemoPlayer player;
    private Uri contentUri;

    VideoEntity videoEntity;

    private String videoInput;
    private String gitFacePath;
    private String videoOutput;
    private int left,top;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gif);
        gitFacePath= FileUtil.getVideoEffect()+"/gif_money.gif";
        if (getIntent()!=null){
            videoEntity = (VideoEntity) getIntent().getSerializableExtra(AppConfig.EXTRA_VIDEO_ENTITY);
            if (videoEntity == null) {
                finish();
                return;
            }

            videoInput=videoEntity.getFilePath();
            if (videoInput==null || TextUtils.isEmpty(videoInput)){
                onBackPressed();
            }
        }else {
            onBackPressed();
        }
        ffmpeg= FFmpeg.getInstance(getApplicationContext());
        loadFFMpegBinary();
        iniUI();
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }

    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(AddGifActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddGifActivity.this.finish();
                    }
                })
                .create()
                .show();
    }
    @Override
    public void iniUI() {
        super.iniUI();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(null);

        mLlBack=(LinearLayout)findViewById(R.id.ll_video_gif_back);
        mLlDone=(LinearLayout)findViewById(R.id.ll_video_gif_done);
        mSvVideo=(SurfaceView)findViewById(R.id.sv_play_gif_video);
        mPgDuration=(ProgressBar)findViewById(R.id.pb_preview_video_duration);
        mIvStatus=(ImageView)findViewById(R.id.iv_play_gif_status);
        mGifImg=(GifImageView)findViewById(R.id.gif_image);

        Glide.with(getApplicationContext())
                .load(gitFacePath)
                .asGif()
                .placeholder(R.drawable.icon_app_mirror)
                .crossFade()
                .into(mGifImg);

        mLlBack.setOnClickListener(this);
        mLlDone.setOnClickListener(this);
        mIvStatus.setOnClickListener(this);

        mGifImg.setOnMoveDoneListener(new GifImageView.OnMoveDoneListener() {
            @Override
            public void onDone(int left, int top) {
                Logger.d("left: "+left+", top: "+top);
                // Max size video 480
                float distance=480F / Config.SCREENWIDTH;
                Logger.d("distance: "+distance);
                AddGifActivity.this.left=(int)(left*distance);
                AddGifActivity.this.top=(int)(top*distance);
            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.setSurface(surfaceHolder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        contentUri = Uri.parse(videoInput);
        preparePlayer();
    }

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = com.google.android.exoplayer.util.Util.getUserAgent(this, "ExoPlayerDemo");
        return new ExtractorRendererBuilder(this, userAgent, contentUri);
    }
    private void preparePlayer() {
        if (player != null)
            player.release();

        player = new DemoPlayer(getRendererBuilder());
        player.addListener(this);
        player.seekTo(0);
        if (player != null)
            player.prepare();

        player.setSurface(mSvVideo.getHolder().getSurface());
        player.setPlayWhenReady(true);
        mIvStatus.setVisibility(View.INVISIBLE);
    }

    private void releasePlayer() {
        handler.removeMessages(HANDLE_SHOW_VIDEO_DURATION);
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_SHOW_VIDEO_DURATION:
                    if (player != null) {
                        mPgDuration.setProgress((int) ((player.getCurrentPosition() * 1000) / player.getDuration()));
                        handler.sendEmptyMessageDelayed(HANDLE_SHOW_VIDEO_DURATION, 100);
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sv_play_video:
                pauseResumeVideo();
                break;
            case R.id.ll_video_gif_done:
                addFrameToVideo();
                break;
            case R.id.ll_video_gif_back:
                onBackPressed();
                finish();
                break;
        }
    }



    public  void addFrameToVideo(){
        videoOutput =  FileUtil.getMyVideo() + "/video_maker_"+System.currentTimeMillis()+".mp4";
        execFFmpegBinary(FFmpegCmdUtil.cmdAddIMageToVideo(videoInput,videoOutput,left,top));
    }

    private void execFFmpegBinary(final String[] command) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    Logger.d(TAG, "Started onFailure : ffmpeg "+s);
                }

                @Override
                public void onSuccess(String s) {
                    Logger.d(TAG, "Started onSuccess : ffmpeg "+s);
                }

                @Override
                public void onProgress(String s) {
                    Logger.d(TAG, "Started onProgress : ffmpeg ");
                    progressDialog.setMessage("Processing\n"+s);
                }

                @Override
                public void onStart() {
                    Logger.d(TAG, "Started onStart : ffmpeg " + command);
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Logger.d(TAG, "Finished onFinish : ffmpeg "+command);
                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void pauseResumeVideo() {
        if (player != null && player.getPlayerControl() != null) {
            if (player.getPlayerControl().isPlaying()) {
                player.getPlayerControl().pause();
                mIvStatus.setVisibility(View.VISIBLE);
            } else {
                player.getPlayerControl().start();
                mIvStatus.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStateChanged(final boolean playWhenReady, final int playbackState) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "playWhenReady=" + playWhenReady + ", playbackState=";
                switch (playbackState) {
                    case ExoPlayer.STATE_BUFFERING:
                        text += "buffering";
                        break;
                    case ExoPlayer.STATE_ENDED:
                        releasePlayer();
                        if (!isFinishing()) {
                            contentUri = Uri.parse(videoInput);
                            preparePlayer();
                        }
                        text += "ended";
                        break;
                    case ExoPlayer.STATE_IDLE:
                        text += "idle";
                        break;
                    case ExoPlayer.STATE_PREPARING:
                        text += "preparing";
                        break;
                    case ExoPlayer.STATE_READY:
                        text += "ready";
                        handler.sendEmptyMessage(HANDLE_SHOW_VIDEO_DURATION);
                        break;
                    default:
                        text += "unknown";
                        break;
                }
                Logger.d(TAG, "exoplayer " + text);
            }
        });
    }

    @Override
    public void onError(Exception e) {
        onBackPressed();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        releasePlayer();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        releasePlayer();
        super.onStop();
    }

}
