package com.bureau.nocomment.exporizon.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bureau.nocomment.exporizon.R;
import com.bureau.nocomment.exporizon.view.KeyboardImageButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.item_image) ImageView itemImage;
    @Bind(R.id.item_title) TextView itemTitle;
    @Bind(R.id.item_subtitle) TextView itemSubtitle;
    @Bind(R.id.item_description) TextView itemDescription;
    @Bind(R.id.play_button) ImageButton playButton;
    @Bind(R.id.pause_button) ImageButton  pauseButton;
    MediaPlayer player;

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

        itemImage.setImageResource(R.drawable.weininger);
        itemTitle.setText("Théâtre sphérique");
        itemSubtitle.setText("Andor Weininger, 1926");
        itemDescription.setText(R.string.lorem_ipsum);
        itemDescription.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        player.start();
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
    }

    private void pauseSoundtrack() {
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
}
