package ng.org.faithtabernacle.android.MedivatorBeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewPlace extends AppCompatActivity {

    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_view_place );

        photo = (ImageView)findViewById ( R.id.photo );
          if(Common.currentResult.getPhotos () != null && Common.currentResult.getPhotos ().length > 0){
              Picasso.with ( this )
                      .load(getPhotoOfPlace(Common.currentResult.getPhotos ()[0].getPhoto_reference (), 1000))
                      .into ( photo );

          }
    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder ( "https://maps.googleapis.com/maps/api/place/photo" );
        url.append ("?maxwidth="+maxWidth);
        url.append("&photoreference="+photo_reference);
        url.append("&key="+getResources ().getString ( R.string.browser_key ));
        return url.toString ();
    }
}
