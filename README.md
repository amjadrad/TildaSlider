# TildaSlider

1. Add this line in your root build.gradle at the end of repositories:

```	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. Add the dependency
```
...
implementation 'com.github.amjadrad:TildaSlider:0.0.1'
```

3. in xml file
```<ir.tildaweb.tilda_slider.views.TildaSlider
        android:id="@+id/slider"
        android:layout_width="match_parent"
        android:layout_height="280dp"
        app:defaultIndicator="square"
        app:hideIndicators="true"
        app:imageSlideInterval="5000"
        app:layout_constraintTop_toTopOf="parent"
        app:loopSlides="true" />
 ```

4. in java file
```TildaSlider tildaSlider = findViewById(R.id.slider);
        List<Poster> posterList = new ArrayList<>();
        posterList.add(new RemoteImage("https://example.com/1.jpg"));
        posterList.add(new RemoteVideo(Uri.parse("https://example.com/a.mp4")));
        tildaSlider.setPosters(posterList);
 ```
