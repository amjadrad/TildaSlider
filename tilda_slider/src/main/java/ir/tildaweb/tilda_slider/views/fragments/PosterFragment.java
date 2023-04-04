package ir.tildaweb.tilda_slider.views.fragments;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import static com.google.android.exoplayer2.Player.STATE_ENDED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import ir.tildaweb.tilda_slider.MathUtils;
import ir.tildaweb.tilda_slider.events.IVideoPlayListener;
import ir.tildaweb.tilda_slider.events.OnPosterClickListener;
import ir.tildaweb.tilda_slider.posters.BitmapImage;
import ir.tildaweb.tilda_slider.posters.DrawableImage;
import ir.tildaweb.tilda_slider.posters.ImagePoster;
import ir.tildaweb.tilda_slider.posters.Poster;
import ir.tildaweb.tilda_slider.posters.RawVideo;
import ir.tildaweb.tilda_slider.posters.RemoteImage;
import ir.tildaweb.tilda_slider.posters.RemoteVideo;
import ir.tildaweb.tilda_slider.posters.VideoPoster;
import ir.tildaweb.tilda_slider.views.AdjustableImageView;
import ir.tildaweb.tilda_slider.views.TildaSlider;

public class PosterFragment extends Fragment implements Player.Listener {

    private final String TAG = this.getClass().getName();
    private Poster poster;

    private IVideoPlayListener videoPlayListener;

    private ExoPlayer player;
    private boolean isLooping;

    public PosterFragment() {
        // Required empty public constructor
    }

    public static PosterFragment newInstance(@NonNull Poster poster, IVideoPlayListener videoPlayListener) {
        PosterFragment fragment = new PosterFragment();
        fragment.setVideoPlayListener(videoPlayListener);
        Bundle args = new Bundle();
        args.putParcelable("poster", poster);
        fragment.setArguments(args);
        return fragment;
    }

    public void setVideoPlayListener(IVideoPlayListener videoPlayListener) {
        this.videoPlayListener = videoPlayListener;
        isLooping = ((TildaSlider) videoPlayListener).getMustLoopSlides();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poster = getArguments().getParcelable("poster");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (poster != null) {
            if (poster instanceof ImagePoster) {
                final AdjustableImageView imageView = new AdjustableImageView(getActivity());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setAdjustViewBounds(true);
                ImagePoster imagePoster = (ImagePoster) poster;
                imageView.setScaleType(imagePoster.getScaleType());
                if (imagePoster instanceof DrawableImage) {
                    DrawableImage image = (DrawableImage) imagePoster;
                    Glide.with(getActivity())
                            .load(image.getDrawable())
                            .into(imageView);
                } else if (imagePoster instanceof BitmapImage) {
                    BitmapImage image = (BitmapImage) imagePoster;
                    Glide.with(getActivity())
                            .load(image.getBitmap())
                            .into(imageView);
                } else {
                    final RemoteImage image = (RemoteImage) imagePoster;
                    if (image.getErrorDrawable() == null && image.getPlaceHolder() == null) {
                        Glide.with(getActivity()).load(image.getUrl())
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        resource.getIntrinsicWidth();
                                        Log.d(TAG, "onResourceReady:WWW " + resource.getIntrinsicWidth());
                                        Log.d(TAG, "onResourceReady:WWW " + resource.getIntrinsicHeight());

                                        int imageW = resource.getIntrinsicWidth();
                                        int imageH = resource.getIntrinsicHeight();
                                        float imageRatio = imageH / (float) imageW;
                                        int w = MathUtils.getScreenWidth(getActivity());
                                        int y = MathUtils.convertDipToPixels(getActivity(), w * imageRatio);
                                        Log.d(TAG, "onResourceReady: " + w);
                                        Log.d(TAG, "onResourceReady: " + y);
                                        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
                                        layoutParams.height = y;
                                        layoutParams.width = w;
                                        imageView.setLayoutParams(layoutParams);
                                        imageView.setAdjustViewBounds(true);
                                        imageView.setImageDrawable(resource);

                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
//                                .into(new CustomTarget<Bitmap>() {
//                                    @Override
//                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
////                                        int width = resource.getWidth();
////                                        int height = resource.getHeight();
////                                        imageView.setImageBitmap(resource);
//                                    }
//
//                                    @Override
//                                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                                    }
//                                });

                    } else {
                        if (image.getPlaceHolder() != null && image.getErrorDrawable() != null) {
                            Glide.with(getActivity())
                                    .load(image.getUrl())
                                    .apply(new RequestOptions()
                                            .placeholder(image.getPlaceHolder()))
                                    .into(imageView);
                        } else if (image.getErrorDrawable() != null) {
                            Glide.with(getActivity())
                                    .load(image.getUrl())
                                    .apply(new RequestOptions()
                                            .error(image.getErrorDrawable()))
                                    .into(imageView);
                        } else if (image.getPlaceHolder() != null) {
                            Glide.with(getActivity())
                                    .load(image.getUrl())
                                    .apply(new RequestOptions()
                                            .placeholder(image.getPlaceHolder()))
                                    .into(imageView);
                        }
                    }
                }
                imageView.setOnTouchListener(poster.getOnTouchListener());
                imageView.setOnClickListener((View.OnClickListener) view -> {
                    OnPosterClickListener onPosterClickListener = poster.getOnPosterClickListener();
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onClick(poster.getPosition());
                    }
                });
                return imageView;
            } else if (poster instanceof VideoPoster) {
                final PlayerView playerView = new PlayerView(getActivity());
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//                TrackSelection.Factory videoTrackSelectionFactory =
//                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(getContext());
//                trackSelector.setParameters();
                player = new ExoPlayer.Builder(getContext()).setTrackSelector(trackSelector).build();
                //

                playerView.setPlayer(player);
                if (isLooping) {
                    playerView.setUseController(false);
                }

                if (poster instanceof RawVideo) {
                    RawVideo video = (RawVideo) poster;
                    DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(video.getRawResource()));
                    final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(getActivity());
                    try {
                        rawResourceDataSource.open(dataSpec);
                    } catch (RawResourceDataSource.RawResourceDataSourceException e) {
                        e.printStackTrace();
                    }

                    DataSource.Factory factory = () -> rawResourceDataSource;
                    ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(rawResourceDataSource.getUri()));
                    player.prepare(mediaSource);
                } else if (poster instanceof RemoteVideo) {
                    RemoteVideo video = (RemoteVideo) poster;
//                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(
//                            new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), "TildaSlider"))).
//                            createMediaSource(video.getUri());
                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory()).createMediaSource(MediaItem.fromUri(video.getUri()));
                    player.prepare(mediaSource, true, false);
                }


                return playerView;
            } else {
                throw new RuntimeException("Unknown Poster kind");
            }
        } else {
            throw new RuntimeException("Poster cannot be null");
        }
    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (isLooping && playbackState == STATE_ENDED) {
            videoPlayListener.onVideoStopped();
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isLooping && player != null) {
            videoPlayListener.onVideoStarted();
            if (player.getPlaybackState() == STATE_ENDED) {
                player.seekTo(0);
            }
            player.setPlayWhenReady(true);
            player.addListener(this);
        }
    }

}
