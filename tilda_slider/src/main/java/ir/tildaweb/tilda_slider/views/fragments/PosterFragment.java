package ir.tildaweb.tilda_slider.views.fragments;


import static androidx.media3.common.Player.STATE_ENDED;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.RawResourceDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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

@UnstableApi
public class PosterFragment extends Fragment implements Player.Listener {

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
        poster = getArguments() != null ? getArguments().getParcelable("poster") : null;
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (poster != null && getActivity() != null) {
            if (poster instanceof ImagePoster imagePoster) {
                final AdjustableImageView imageView = new AdjustableImageView(getActivity());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(imagePoster.getScaleType());
                if (imagePoster instanceof DrawableImage image) {
                    Glide.with(getActivity())
                            .load(image.getDrawable())
                            .into(imageView);
                } else if (imagePoster instanceof BitmapImage image) {
                    Glide.with(getActivity())
                            .load(image.getBitmap())
                            .into(imageView);
                } else {
                    final RemoteImage image = (RemoteImage) imagePoster;
                    if (image.getErrorDrawable() == null && image.getPlaceHolder() == null) {
                        Glide.with(getActivity()).load(image.getUrl()).into(imageView);
//                                .into(new CustomTarget<Drawable>() {
//                                    @Override
//                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                        resource.getIntrinsicWidth();
//                                        Log.d(TAG, "onResourceReady:WWW " + resource.getIntrinsicWidth());
//                                        Log.d(TAG, "onResourceReady:WWW " + resource.getIntrinsicHeight());
//
//                                        int imageW = resource.getIntrinsicWidth();
//                                        int imageH = resource.getIntrinsicHeight();
//                                        float imageRatio = imageH / (float) imageW;
//                                        int w = MathUtils.getScreenWidth(getActivity());
//                                        int y = MathUtils.convertDipToPixels(getActivity(), w * imageRatio);
//                                        Log.d(TAG, "onResourceReady: " + w);
//                                        Log.d(TAG, "onResourceReady: " + y);
//                                        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
//                                        layoutParams.height = y;
//                                        layoutParams.width = w;
//                                        imageView.setLayoutParams(layoutParams);
//                                        imageView.setAdjustViewBounds(true);
//                                        imageView.setImageDrawable(resource);
//
//                                    }
//
//                                    @Override
//                                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                                    }
//                                });
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
//                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
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

                if (poster instanceof RawVideo video) {
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
                } else if (poster instanceof RemoteVideo video) {
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
    public void onPositionDiscontinuity(int reason) {

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
