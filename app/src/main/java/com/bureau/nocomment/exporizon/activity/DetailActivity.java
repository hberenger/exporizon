package com.bureau.nocomment.exporizon.activity;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bureau.nocomment.exporizon.R;
import com.bureau.nocomment.exporizon.view.KeyboardImageButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.item_image) PhotoView itemImage;
    @Bind(R.id.item_title) TextView itemTitle;
    @Bind(R.id.item_subtitle) TextView itemSubtitle;
    @Bind(R.id.item_description) TextView itemDescription;
    @Bind(R.id.play_button) ImageButton playButton;
    @Bind(R.id.pause_button) ImageButton  pauseButton;
    @Bind(R.id.progress_bar) AppCompatSeekBar progressBar;
    MediaPlayer player;
    private Handler progressUpdateHandler;
    private Runnable progressUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        player = MediaPlayer.create(this, R.raw.weininger);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pauseSoundtrack();
                player.seekTo(0);
            }
        });

        itemImage.setMaximumScale(10);
        itemImage.setImageResource(R.drawable.weininger);
        actionBar.setTitle("Théâtre sphérique");
        itemTitle.setVisibility(View.GONE);
        itemSubtitle.setText(italicCharSequenceFrom("Andor Weininger, 1926"));
        String msg = this.getString(R.string.lorem_ipsum);
        itemDescription.setText(msg + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n");
        itemDescription.setMovementMethod(new ScrollingMovementMethod());

        progressUpdateHandler = new Handler();
        progressUpdater = createUpdater();
        progressBar.setMax(player.getDuration()); // in ms
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(player != null && fromUser){
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressUpdateHandler.removeCallbacks(progressUpdater);
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playSoundtrack();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseSoundtrack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void playSoundtrack() {
        progressUpdateHandler.post(progressUpdater);
        player.start();
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
    }

    private void pauseSoundtrack() {
        progressUpdateHandler.removeCallbacks(progressUpdater);
        player.pause();
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.play_button)
    void onPlayButton(ImageButton button) {
        playSoundtrack();
    }

    @OnClick(R.id.pause_button)
    void onPauseButton(ImageButton button) {
        pauseSoundtrack();
    }

    private Runnable createUpdater() {
        return new Runnable() {
            @Override
            public void run() {
                if(player != null){
                    int mCurrentPosition = player.getCurrentPosition();
                    progressBar.setProgress(mCurrentPosition);
                }
                progressUpdateHandler.postDelayed(this, 1000);
            }
        };
    }

    // To workaround a Samsung bug
    private static CharSequence italicCharSequenceFrom(CharSequence text) {
        final StyleSpan style = new StyleSpan(Typeface.ITALIC);
        final SpannableString str = new SpannableString(text);
        str.setSpan(style, 0, text.length(), 0);
        return str;
    }
}
