package ir.tildaweb.tildaslider;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ir.tildaweb.tilda_slider.posters.Poster;
import ir.tildaweb.tilda_slider.posters.RemoteImage;
import ir.tildaweb.tilda_slider.posters.RemoteVideo;
import ir.tildaweb.tilda_slider.views.TildaSlider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TildaSlider tildaSlider = findViewById(R.id.slider);
        List<Poster> posterList = new ArrayList<>();
        posterList.add(new RemoteImage("https://fujifilm-x.com/wp-content/uploads/2019/08/xc16-50mmf35-56-ois-2_sample-images01.jpg"));
//        posterList.add(new RemoteVideo(Uri.parse("https://hajifirouz2.cdn.asset.aparat.com/aparat-video/15b109648ac78fa2e1257df650032d1546555781-144p.mp4?wmsAuthSign=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6IjBiY2E5YjljNDY0ZDcxMTU5YzVkNjEwZTQ3ZWMwMTE1IiwiZXhwIjoxNjU5MTM1NDU0LCJpc3MiOiJTYWJhIElkZWEgR1NJRyJ9.-23WpgeXmrO3v_quUg3x2-PTQuSTMCWO_qOG54jhVY4")));
        tildaSlider.setPosters(posterList);

    }
}