package ng.org.faithtabernacle.android.MedivatorBeta;


import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dyanamitechetan.vusikview.VusikView;


public class home_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    final Handler handler = new Handler ();
    private ImageButton btn_play_pause;
    private ImageButton btn_skip_next;
    private SeekBar seekBar;
    private TextView textView;
    private TextView trackList;
    private VusikView musicView;
    private MediaPlayer mediaPlayer;
    private int mediaFileLength;
    private int realTimeLength;
    private List<modelAudioTrack> mTrackList;

    //  private String modelAudioTrack;

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_home_activity );

        musicView = findViewById ( R.id.musicView );


        seekBar = findViewById ( R.id.seekBar );
        seekBar.setMax ( 99 ); // 100% (0~99)
        seekBar.setOnTouchListener ( new View.OnTouchListener () {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mediaPlayer.isPlaying ()) {
                    SeekBar seekBar = (SeekBar) v;
                    int playPosition = (mediaFileLength / 100) * seekBar.getProgress ();
                    mediaPlayer.seekTo ( playPosition );
                }
                return false;
            }
        } );

        // Nicks Codes
        textView = findViewById ( R.id.textTimer );
        btn_play_pause = findViewById ( R.id.btn_play_pause );
        btn_play_pause.setOnClickListener ( new View.OnClickListener () {

            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog ( home_activity.this );

                AsyncTask <String, String, String> mp3Play = new AsyncTask <String, String, String> () {
                    @Override
                    protected void onPreExecute() {
                        mDialog.setMessage ( "Please wait ..." );
                        mDialog.show ();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            mediaPlayer.setDataSource ( params[0] );
                            mediaPlayer.prepare ();
                        } catch (Exception ex) {

                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mediaFileLength = mediaPlayer.getDuration ();
                        realTimeLength = mediaFileLength;

                        if (!mediaPlayer.isPlaying ()) {
                            mediaPlayer.start ();
                            btn_play_pause.setImageResource ( R.drawable.ic_pause );
                        } else {
                            mediaPlayer.pause ();
                            btn_play_pause.setImageResource ( R.drawable.ic_play );
                        }


                        updateSeekBar ();
                        mDialog.dismiss ();

                    }
                };
                mp3Play.execute ( "http://41.216.164.90:9089/wd106.mp3" ); // direct link mp3 files on Media Server
                musicView.start ();

            }

        });

        mediaPlayer = new MediaPlayer ();
        mediaPlayer.setOnBufferingUpdateListener ( this );
        mediaPlayer.setOnCompletionListener ( this );


        // OnClick Button PlayNext
        trackList = findViewById ( R.id.track_list );
        btn_skip_next = findViewById ( R.id.btn_skip_next );
        btn_skip_next.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getTrackUrl();
            }
        } );

    }
    private void getTrackUrl(){
        final   List<modelAudioTrack>  mTrackList = new ArrayList<>();
        new Thread ( new Runnable () {
            @Override
            public void run() {

                try {
                    Document document = Jsoup.connect ( "http://41.216.164.90:9088/medivatorlibrary.php" ).get ();
                    String title = document.title ();
                    Elements links = document.select ( "a[href]" );
                    // modelAudioTrack.add ( links );

                    for (Element link : links) {
                        mTrackList.add(new  modelAudioTrack(link.attr("href"),link.text()));
//                        links.append ( String.valueOf ( modelAudioTrack ) ).append("Link : ").append(link.attr("href"))
//                                .append("\n").append("Text : ").append(link.text());
                        Log.e("LINK",link.text());
                        Log.e("SIZE",links.size()+" ");
                    }
                }catch (IOException e) {

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        for (int i=0; i<=mTrackList.size(); i++){
 //                          trackList.append(mTrackList.get(i). getTrackTitle()+"\n");
 //                       }

                    }
                });
            }
        } ).start ();
    }



    private void updateSeekBar() {
        seekBar.setProgress ( (int) (((float) mediaPlayer.getCurrentPosition () / mediaFileLength) * 100) );
        if (mediaPlayer.isPlaying ()) {

            Runnable updater = new Runnable () {
                @Override
                public void run() {
                    updateSeekBar ();
                    realTimeLength -= 1000; // declare 1 second
                    textView.setText ( String.format ( "%d:%d", TimeUnit.MILLISECONDS.toMinutes ( realTimeLength ),
                            TimeUnit.MILLISECONDS.toSeconds ( realTimeLength ) -
                                    TimeUnit.MILLISECONDS.toSeconds ( TimeUnit.MILLISECONDS.toMinutes ( realTimeLength ) ) ) );

                }

            };
            handler.postDelayed ( updater, 1000 ); // 1 second
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress ( percent );

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btn_play_pause.setImageResource ( R.drawable.ic_play );
        musicView.stopNotesFall ();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater ();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate ( R.menu.right_top_nav, menu );
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId ();

        if (id == R.id.lfcww) {

            Intent intent = new Intent ( home_activity.this, MapsActivity.class );

            startActivity ( intent );
        } else if (id == R.id.contact) {
            Intent intentToStartContact = new Intent ( home_activity.this, ContactActivity.class );

            startActivity ( intentToStartContact );
        }


        return super.onOptionsItemSelected ( item );
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}