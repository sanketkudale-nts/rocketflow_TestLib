package com.tracki.ui.custom;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tracki.R;
import com.tracki.data.local.prefs.AppPreferencesHelper;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.utils.AppConstants;
import com.tracki.utils.Log;
import com.trackthat.lib.TrackThatUtils;

import static com.trackthat.lib.internal.util.EventType.GEOFENCE_IN;
import static com.trackthat.lib.internal.util.EventType.GEOFENCE_OUT;
import static com.trackthat.lib.internal.util.EventType.HARSH_ACCELERATION;
import static com.trackthat.lib.internal.util.EventType.HARSH_BREAKING;
import static com.trackthat.lib.internal.util.EventType.HARSH_CORNERING;
import static com.trackthat.lib.internal.util.EventType.OVER_SPEEDING;
import static com.trackthat.lib.internal.util.EventType.PHONE_USAGE;

/**
 * Created by rahul on 6/5/19
 */
public class EventDialogFragment extends DialogFragment {

    private static final String TAG = EventDialogFragment.class.getSimpleName();
    private String type;
    private MediaPlayer mediaPlayer = null;
    private PreferencesHelper preferencesHelper = null;
    private Context context;

    public static EventDialogFragment getInstance(String type) {
        EventDialogFragment eventDialog = new EventDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        eventDialog.setArguments(bundle);
        return eventDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            type = bundle.getString("type");
        }
    }

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.fragment_event_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvEventNam = view.findViewById(R.id.tvEventNam);
        ImageView ivEventPics = view.findViewById(R.id.ivEventPics);
        StringBuilder builder = new StringBuilder();
        builder.append("Avoid");
        builder.append(" ");
        switch (TrackThatUtils.getEventType(type)) {
            case OVER_SPEEDING:
                builder.append(OVER_SPEEDING);
                ivEventPics.setImageResource(R.drawable.ic_over_speeding);
                break;
            case HARSH_CORNERING:
                builder.append(HARSH_CORNERING);
                ivEventPics.setImageResource(R.drawable.ic_harsh_cornering);
                break;
            case HARSH_ACCELERATION:
                builder.append(HARSH_ACCELERATION);
                ivEventPics.setImageResource(R.drawable.ic_harsh_acceleration);
                break;
            case HARSH_BREAKING:
                builder.append(HARSH_BREAKING);
                ivEventPics.setImageResource(R.drawable.ic_harsh_braking);
                break;
            case GEOFENCE_IN:
                builder.append(GEOFENCE_IN);
                ivEventPics.setImageResource(R.drawable.ic_geo_in);
                break;
            case GEOFENCE_OUT:
                builder.append(GEOFENCE_OUT);
                ivEventPics.setImageResource(R.drawable.ic_geo_in);
                break;
            case PHONE_USAGE:
                builder.append(PHONE_USAGE);
                ivEventPics.setImageResource(R.drawable.ic_phone_usage);
                break;
            default:
                builder.append(type);
                ivEventPics.setImageResource(R.drawable.ic_warning);
                break;
        }
        if (preferencesHelper.getVoiceAlertsTracking()) {
            playSound(type);
        }
        tvEventNam.setText(builder);
        new Handler().postDelayed(() -> {
            dismiss();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }, 2000);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        preferencesHelper = new AppPreferencesHelper(context, AppConstants.PREF_NAME);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        mediaPlayer = null;
    }

    private void playSound(String type) {
        switch (TrackThatUtils.getEventType(type)) {
            case OVER_SPEEDING:

                mediaPlayer = MediaPlayer.create(context, R.raw.avoid_over_speed);
                mediaPlayer.start();

                break;
            case HARSH_CORNERING:
                mediaPlayer = MediaPlayer.create(context, R.raw.avoid_harsh_corning);
                mediaPlayer.start();

                break;
            case HARSH_ACCELERATION:
                mediaPlayer = MediaPlayer.create(context, R.raw.avoid_harsh_accleration);
                mediaPlayer.start();

                break;
            case HARSH_BREAKING:
                mediaPlayer = MediaPlayer.create(context, R.raw.avoid_harsh_breaking);
                mediaPlayer.start();

                break;
            case GEOFENCE_IN:
                mediaPlayer = MediaPlayer.create(context, R.raw.geofence_in);
                mediaPlayer.start();
                break;
            case GEOFENCE_OUT:
                mediaPlayer = MediaPlayer.create(context, R.raw.geofence_out);
                mediaPlayer.start();

                break;
            case PHONE_USAGE:
                mediaPlayer = MediaPlayer.create(context, R.raw.phone_usage);
                mediaPlayer.start();
                break;
            default:
                break;
        }


    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d(TAG, "Exception" + e);
        }
    }

}
