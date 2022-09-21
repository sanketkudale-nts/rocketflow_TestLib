package com.rocketflyer.rocketflow.ui.dynamicform.dynamicfragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.maps.model.LatLng;
import com.iceteck.silicompressorr.SiliCompressor;
import com.tracki.R;
import com.tracki.TrackiApplication;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.GetPopulationDataResponse;
import com.tracki.data.model.request.CalculateFormData;
import com.tracki.data.model.request.UpdateFileRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.DataType;
import com.tracki.data.model.response.config.DynamicFormData;
import com.tracki.data.model.response.config.Field;
import com.tracki.data.model.response.config.FormData;
import com.tracki.data.model.response.config.TaskData;
import com.tracki.data.model.response.config.Type;
import com.tracki.data.model.response.config.WidgetData;
import com.tracki.data.network.APIError;
import com.tracki.data.network.HttpManager;
import com.tracki.databinding.ItemDynamicFormAudioBinding;
import com.tracki.databinding.ItemDynamicFormButtonBinding;
import com.tracki.databinding.ItemDynamicFormCalculateBinding;
import com.tracki.databinding.ItemDynamicFormCameraBinding;
import com.tracki.databinding.ItemDynamicFormCdtnlDdownBinding;
import com.tracki.databinding.ItemDynamicFormCheckboxBinding;
import com.tracki.databinding.ItemDynamicFormDateBinding;
import com.tracki.databinding.ItemDynamicFormDateRangeBinding;
import com.tracki.databinding.ItemDynamicFormDateTimeBinding;
import com.tracki.databinding.ItemDynamicFormDescriptionBinding;
import com.tracki.databinding.ItemDynamicFormDropdownBinding;
import com.tracki.databinding.ItemDynamicFormEdittextBinding;
import com.tracki.databinding.ItemDynamicFormEmailBinding;
import com.tracki.databinding.ItemDynamicFormEmptyViewBinding;
import com.tracki.databinding.ItemDynamicFormImageBinding;
import com.tracki.databinding.ItemDynamicFormIpAddressBinding;
import com.tracki.databinding.ItemDynamicFormLabelBinding;
import com.tracki.databinding.ItemDynamicFormLocationBinding;
import com.tracki.databinding.ItemDynamicFormMultiSelectBinding;
import com.tracki.databinding.ItemDynamicFormNumberBinding;
import com.tracki.databinding.ItemDynamicFormRadioBinding;
import com.tracki.databinding.ItemDynamicFormScannerBinding;
import com.tracki.databinding.ItemDynamicFormSignatureBinding;
import com.tracki.databinding.ItemDynamicFormTimeBinding;
import com.tracki.databinding.ItemDynamicFormToggleBinding;
import com.tracki.databinding.ItemDynamicFormUnknownBinding;
import com.tracki.databinding.ItemDynamicFormUploadBinding;
import com.tracki.databinding.ItemDynamicFormVerifyOtpBinding;
import com.tracki.databinding.ItemDynamicFormVideoBinding;
import com.tracki.ui.addfleet.AddFleetActivity;
import com.tracki.ui.base.BaseViewHolder;
import com.tracki.ui.chat.PlayVideoVerticallyActivity;
import com.tracki.ui.custom.GlideApp;
import com.tracki.ui.custom.MultiSelectSpinner;
import com.tracki.ui.dynamicform.DynamicFormActivity;
import com.tracki.ui.dynamicform.FormAudioViewModel;
import com.tracki.ui.dynamicform.FormButtonViewModel;
import com.tracki.ui.dynamicform.FormCalculateViewModel;
import com.tracki.ui.dynamicform.FormCameraViewModel;
import com.tracki.ui.dynamicform.FormCheckBoxViewModel;
import com.tracki.ui.dynamicform.FormConditionalDDViewModel;
import com.tracki.ui.dynamicform.FormDateRangeViewModel;
import com.tracki.ui.dynamicform.FormDateTimeViewModel;
import com.tracki.ui.dynamicform.FormDateViewModel;
import com.tracki.ui.dynamicform.FormDescriptionViewModel;
import com.tracki.ui.dynamicform.FormDropdownViewModel;
import com.tracki.ui.dynamicform.FormEdittextViewModel;
import com.tracki.ui.dynamicform.FormEmailViewModel;
import com.tracki.ui.dynamicform.FormEmptyItemViewModel;
import com.tracki.ui.dynamicform.FormImageViewModel;
import com.tracki.ui.dynamicform.FormLabelViewModel;
import com.tracki.ui.dynamicform.FormLocationViewModel;
import com.tracki.ui.dynamicform.FormMultiSelectViewModel;
import com.tracki.ui.dynamicform.FormNumberViewModel;
import com.tracki.ui.dynamicform.FormRadioViewModel;
import com.tracki.ui.dynamicform.FormSignatureViewModel;
import com.tracki.ui.dynamicform.FormTimeViewModel;
import com.tracki.ui.dynamicform.FormToggleViewModel;
import com.tracki.ui.dynamicform.FormUploadViewModel;
import com.tracki.ui.dynamicform.FormVerifyMobileViewModel;
import com.tracki.ui.dynamicform.FormVideoViewModel;
import com.tracki.ui.dynamicform.GetDynamicFormListById;
import com.tracki.ui.dynamicform.dynamicfragment.ChildDynamicAdapter;
import com.tracki.ui.dynamicform.dynamicfragment.DynamicFragment;
import com.tracki.ui.scanqrcode.ScanQrAndBarCodeActivity;
import com.tracki.utils.ApiType;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.DateTimeUtil;
import com.tracki.utils.FileType;
import com.tracki.utils.JSONConverter;
import com.tracki.utils.Log;
import com.tracki.utils.NetworkUtils;
import com.tracki.utils.TrackiToast;
import com.tracki.utils.ZoomableImageView;
import com.tracki.utils.image_utility.Compressor;
import com.tracki.utils.toggle.interfaces.OnToggledListener;
import com.tracki.utils.toggle.model.ToggleableView;
import com.tracki.utils.toggle.widget.LabeledSwitch;
import com.trackthat.lib.TrackThat;
import com.trackthat.lib.internal.network.TrackThatCallback;
import com.trackthat.lib.models.ErrorResponse;
import com.trackthat.lib.models.SuccessResponse;
import com.trackthat.lib.models.TrackthatLocation;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



public class DynamicAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "DynamicAdapter";
    private static final int VIEW_EMPTY = 0;
    private static final int VIEW_EDIT_TEXT = 1;
    private static final int VIEW_SIGNATURE = 2;
    private static final int VIEW_UPLOAD = 3;
    private static final int VIEW_DATE_TIME = 4;
    private static final int VIEW_DATE_RANGE = 5;
    private static final int VIEW_DATE = 6;
    private static final int VIEW_DESCRIPTION = 7;
    private static final int VIEW_UNDEFINED = 8;
    private static final int VIEW_NUMBER = 9;
    private static final int VIEW_EMAIL = 10;
    private static final int VIEW_TOGGLE = 11;
    private static final int VIEW_CAMERA = 12;
    private static final int VIEW_BUTTON = 13;

    private static final int VIEW_DROPDOWN = 14;
    private static final int VIEW_CHECKBOX = 15;
    private static final int VIEW_RADIO = 16;
    private static final int VIEW_MULTI_SELECT = 17;
    private static final int VIEW_DROPDOWN_CONDITIONAL_STATIC = 18;
    private static final int VIEW_DROPDOWN_CONDITIONAL_API = 19;
    private static final int VIEW_LABEL = 20;
    private static final int VIEW_TIME = 21;
    private static final int VIEW_DROPDOWN_API = 22;
    private static final int VIEW_CALCULATE = 23;
    private static final int VIEW_AUDIO = 24;
    private static final int VIEW_VIDEO = 25;
    private static final int VIEW_VERIFY_OTP = 26;
    private static final int VIEW_GEO = 27;
    private static final int VIEW_IMAGE = 28;
    private static final int VIEW_IP_ADDRESS = 31;
    private static final int VIEW_SCANNER = 32;


    private String formId = null;
    private Boolean isEditable = false;
    private Boolean hideButton = false;
    private Map<String, String> mapData = new HashMap<>();
    private String formula = "";
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 29;
    //    public static final int MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE = 31;
    public static final int VIDEO_CAPTURE = 30;
    public ItemDynamicFormVideoBinding itemDynamicFormVideoBinding;
    private DynamicFragment dynamicFragment;
    int pos;


    public ArrayList<FormData> formDataList;
    private Context context;
    public AdapterListener adapterListener;
    private int audioViewPos, videoViewPos;
    private HttpManager httpmanager;
    public MutableLiveData<Boolean> isSubmitButtonEnable = new MutableLiveData<>();
    public MutableLiveData<Boolean> isEstimtedWidegtFilled = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSendingDataToServer = new MutableLiveData<>();
    private String taskId;
    private SignaturePad mSignaturePad;
    public ChildDynamicAdapter childDynamicAdapter;
    public static HashMap<String, List<TaskData>> embdedFormdata = null;
    public PreferencesHelper preferencesHelper;

    public void setPreferencesHelper(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public DynamicAdapter(ArrayList<FormData> formDataList) {
        this.formDataList = formDataList;
        if (embdedFormdata != null)
            embdedFormdata.clear();
    }

    public void setAdapterListener(AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    public void setFields(ArrayList<Field> fieldsList){

    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.e(TAG, "onCreateViewHolder:--->>>>");
        this.context = parent.getContext();
        switch (viewType) {
            case VIEW_EDIT_TEXT:
                ItemDynamicFormEdittextBinding edittextBinding = ItemDynamicFormEdittextBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new EditTextViewHolder(edittextBinding);
            case VIEW_SCANNER:
                ItemDynamicFormScannerBinding scannerBinding = ItemDynamicFormScannerBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ScannerViewHolder(scannerBinding);
            case VIEW_NUMBER:
                ItemDynamicFormNumberBinding numberBinding = ItemDynamicFormNumberBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new NumberViewHolder(numberBinding);
            case VIEW_EMAIL:
                ItemDynamicFormEmailBinding emailBinding = ItemDynamicFormEmailBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new EmailViewHolder(emailBinding);
            case VIEW_UPLOAD:
                ItemDynamicFormUploadBinding uploadBinding = ItemDynamicFormUploadBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new UploadViewHolder(uploadBinding);
            case VIEW_DATE_RANGE:
                ItemDynamicFormDateRangeBinding rangeBinding = ItemDynamicFormDateRangeBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DateRangeViewHolder(rangeBinding);
            case VIEW_DATE_TIME:
                ItemDynamicFormDateTimeBinding dateTimeBinding = ItemDynamicFormDateTimeBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DateTimeViewHolder(dateTimeBinding);
            case VIEW_DATE:
                ItemDynamicFormDateBinding dateBinding = ItemDynamicFormDateBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DateViewHolder(dateBinding);
            case VIEW_DESCRIPTION:
                ItemDynamicFormDescriptionBinding descriptionBinding = ItemDynamicFormDescriptionBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DescriptionViewHolder(descriptionBinding);
            case VIEW_CAMERA:
                ItemDynamicFormCameraBinding cameraBinding = ItemDynamicFormCameraBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new CameraViewHolder(cameraBinding);
            case VIEW_TOGGLE:
                ItemDynamicFormToggleBinding toggleBinding = ItemDynamicFormToggleBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ToggleViewHolder(toggleBinding);
            case VIEW_BUTTON:
                ItemDynamicFormButtonBinding buttonBinding = ItemDynamicFormButtonBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ButtonViewHolder(buttonBinding);
            case VIEW_SIGNATURE:
                ItemDynamicFormSignatureBinding signatureBinding = ItemDynamicFormSignatureBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new SignatureViewHolder(signatureBinding);
            case VIEW_DROPDOWN:
                ItemDynamicFormDropdownBinding dropdownBinding = ItemDynamicFormDropdownBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DropdownViewHolder(dropdownBinding);
            case VIEW_DROPDOWN_API:
                ItemDynamicFormDropdownBinding dropdownAPIBinding = ItemDynamicFormDropdownBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new DropdownAPIViewHolder(dropdownAPIBinding);
            case VIEW_CHECKBOX:
                ItemDynamicFormCheckboxBinding checkboxBinding = ItemDynamicFormCheckboxBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new CheckBoxViewHolder(checkboxBinding);
            case VIEW_RADIO:
                ItemDynamicFormRadioBinding radioBinding = ItemDynamicFormRadioBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new RadioViewHolder(radioBinding);
            case VIEW_MULTI_SELECT:
                ItemDynamicFormMultiSelectBinding multiSelectBinding = ItemDynamicFormMultiSelectBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new MultiSelectViewHolder(multiSelectBinding);
            case VIEW_DROPDOWN_CONDITIONAL_STATIC:
                ItemDynamicFormCdtnlDdownBinding cdtnlDdownBinding = ItemDynamicFormCdtnlDdownBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ConditionalDropdownViewHolder(cdtnlDdownBinding);
//            case VIEW_DROPDOWN_CONDITIONAL_API:
//                ItemDynamicFormCdtnlDdownBinding cdtnlDdownAPIBinding = ItemDynamicFormCdtnlDdownBinding
//                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
//                return new ConditionalDropdownAPIViewHolder(cdtnlDdownAPIBinding);
            case VIEW_LABEL:
                ItemDynamicFormLabelBinding labelBinding = ItemDynamicFormLabelBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new LabelViewHolder(labelBinding);
            case VIEW_TIME:
                ItemDynamicFormTimeBinding timeBinding = ItemDynamicFormTimeBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new TimeViewHolder(timeBinding);
            case VIEW_CALCULATE:
                ItemDynamicFormCalculateBinding calculateBinding = ItemDynamicFormCalculateBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new CalculateViewHolder(calculateBinding);

            case VIEW_UNDEFINED:
                ItemDynamicFormUnknownBinding unknownBinding = ItemDynamicFormUnknownBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new UnknownViewHolder(unknownBinding);
            case VIEW_VIDEO:
                ItemDynamicFormVideoBinding videoBinding = ItemDynamicFormVideoBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new VideoViewHolder(videoBinding);
            case VIEW_AUDIO:
                ItemDynamicFormAudioBinding audioBinding = ItemDynamicFormAudioBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new AudioViewHolder(audioBinding);
            case VIEW_VERIFY_OTP:
                ItemDynamicFormVerifyOtpBinding otpBinding = ItemDynamicFormVerifyOtpBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new OtpViewHolder(otpBinding);
            case VIEW_GEO:
                ItemDynamicFormLocationBinding locationBinding = ItemDynamicFormLocationBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new GeoViewHolder(locationBinding);
            case VIEW_IMAGE:
                ItemDynamicFormImageBinding imageBinding = ItemDynamicFormImageBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ImageViewHolder(imageBinding);
            case VIEW_IP_ADDRESS:
                ItemDynamicFormIpAddressBinding ipAddressBinding = ItemDynamicFormIpAddressBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new IpAddressViewHolder(ipAddressBinding);
            default:
                ItemDynamicFormEmptyViewBinding emptyViewBinding = ItemDynamicFormEmptyViewBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
        //Log.e(TAG, "onBindViewHolder: ---->  ");
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        //  Log.e(TAG, "getItemCount: ------>> ");
        return formDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //  Log.e(TAG, "getItemViewType: -------------> ");
        if (formDataList != null && !formDataList.isEmpty()) {
            if (formDataList.get(position) != null &&
                    formDataList.get(position).getType() != null) {
                switch (formDataList.get(position).getType()) {
                    case USER_NAME:
                    case RTSP:
                    case TEXT:
                    case PASSWORD:
                    case EVENT:
                        return VIEW_EDIT_TEXT;
                    case SCANNER:
                        return VIEW_SCANNER;
                    case CAMERA:
                        return VIEW_CAMERA;
                    case TOGGLE:
                        return VIEW_TOGGLE;
                    case PORT:
                    case AMOUNT:
                    case NUMBER:
                        return VIEW_NUMBER;
                    case EMAIL:
                        return VIEW_EMAIL;
                    case DATE_RANGE:
                        return VIEW_DATE_RANGE;
                    case DATE_TIME:
                        return VIEW_DATE_TIME;
                    case FILE:
                    case FILES:
                        return VIEW_UPLOAD;
                    case SIGNATURE:
                        return VIEW_SIGNATURE;
                    case DATE:
                        return VIEW_DATE;
                    case TEXT_AREA:
                        return VIEW_DESCRIPTION;
                    case BUTTON:
                        return VIEW_BUTTON;
                    case MULTI_SELECT:
                        return VIEW_MULTI_SELECT;
                    case DROPDOWN:
                        return VIEW_DROPDOWN;
                    case CHECKBOX:
                        return VIEW_CHECKBOX;
                    case RADIO:
                        return VIEW_RADIO;
                    case CONDITIONAL_DROPDOWN_STATIC:
                        return VIEW_DROPDOWN_CONDITIONAL_STATIC;
//                    case CONDITIONAL_DROPDOWN_API:
//                        return VIEW_DROPDOWN_CONDITIONAL_API;
                    case LABLE:
                    case LABEL:

                        return VIEW_LABEL;
                    case TIME:
                        return VIEW_TIME;
                    case SELECT_EXECUTIVE:
                    case SELECT_EXECUTIVE_BY_PLACE:
                    case SELECT_NEAR_BY_EXECUTIVE:
                    case SELECT_GROUP:
                    case ASSIGN_SUBORDINATE:
                    case DROPDOWN_API:
                        return VIEW_DROPDOWN_API;
                    case CALCULATE:
                        return VIEW_CALCULATE;
                    case VIDEO:
                        return VIEW_VIDEO;
                    case AUDIO:
                        return VIEW_AUDIO;
                    case VERIFY_OTP:
                        return VIEW_VERIFY_OTP;
                    case GEO:
                        return VIEW_GEO;
                    case IMAGE:
                        return VIEW_IMAGE;
                    case IP_ADDRESS:
                        return VIEW_IP_ADDRESS;

                    default:
                        return VIEW_UNDEFINED;
                }
            } else {
                return VIEW_UNDEFINED;
            }
        } else {
            return VIEW_EMPTY;
        }
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setFormDataList(ArrayList<FormData> formDataList) {
        this.formDataList = formDataList;
        notifyDataSetChanged();
    }

    public void setIsEditable(Boolean isEditable, HttpManager httpManager) {
        this.isEditable = isEditable;
        this.httpmanager = httpManager;
    }

    public void setIsHideButton(Boolean hideButton) {
        this.hideButton = hideButton;
    }


    public void setAudioFiles(ArrayList<String> files, HttpManager httpManager) {
        formDataList.get(audioViewPos).setEnteredValue(files.get(0));
        this.httpmanager = httpManager;
        notifyDataSetChanged();
    }

    public void setVideoFiles(ArrayList<String> files, HttpManager httpManager) {
        if (files != null) {
            if (files.size() > 0)
                formDataList.get(videoViewPos).setEnteredValue(files.get(0));

            else
                formDataList.get(videoViewPos).setEnteredValue("");


            this.httpmanager = httpManager;
            notifyDataSetChanged();
        }
    }

    public void setDynamicFragmentInstance(DynamicFragment dynamicFragment) {
        this.dynamicFragment = dynamicFragment;
    }

    /**
     * Method used to set the image name when user select the image from the device.
     *
     * @param formData updated  model with image name and file
     * @param position selected pos
     */

    public void setImage(int position, @NotNull FormData formData) {
        formDataList.set(position, formData);
        notifyItemChanged(position, formData);
    }

    /**
     * Method is used to set the image into the imageview and widgetDataList
     * of the items with urls.
     *
     * @param position position in the widgetDataList where thid file is going to be added.
     * @param file     that is going to be added.
     */
    public void setImg(int position, @NotNull File file) {
        try {

            ArrayList<File> fileList;

            if (formDataList.get(position) != null && formDataList.get(position).getFile() != null
                    && formDataList.get(position).getFile().size() > 0) {
                //REMOVE last index and add the new file here
                // formDataList.get(position).getFile().remove((formDataList.get(position).getFile().size() - 1));
                fileList = formDataList.get(position).getFile();
            } else {
                fileList = new ArrayList<>();
            }

            fileList.add(file);

            formDataList.get(position).setFile(fileList);
            formDataList.get(position).setEnteredValue(file.getPath());
            formDataList.get(position).setValue(file.getPath());
            //Toast.makeText(context, "Captured", Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
        } catch (Exception c) {
            c.printStackTrace();
        }
    }


    public void setVid(int position, File file, String filePath) {
        pos = 1;
        VideoView mVideoView = itemDynamicFormVideoBinding.videoView;
        ImageView ivVideo = itemDynamicFormVideoBinding.ivVideo;
        ImageView ivPlay = itemDynamicFormVideoBinding.ivPlay;
        mVideoView.setZOrderOnTop(true);
        MediaController controller = new MediaController(context);
        controller.setAnchorView(mVideoView);
        if (filePath != null) {
            mVideoView.setVideoPath(filePath);
            mVideoView.setMediaController(controller);
            ArrayList<File> fileArrayList = new ArrayList<>();
            File file1 = new File(filePath);
            fileArrayList.add(file1);


            Glide.with(context).
                    load(file1.getAbsolutePath()).
                    thumbnail(0.1f).
                    into(ivVideo);
            ivVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PlayVideoVerticallyActivity.class);
                    intent.putExtra("url", file1.getAbsolutePath());
                    context.startActivity(intent);
                }
            });
            formDataList.get(position).setFile(fileArrayList);
            formDataList.get(position).setEnteredValue(filePath);
            notifyItemChanged(position);

        }
    }

    public void setInnerFormData(@Nullable HashMap<String, List<TaskData>> embdedFormdata) {
        this.embdedFormdata = embdedFormdata;
    }


    /**
     * This interface is used to call the required methods
     * of @{@link DynamicFormActivity} if required.
     */
    public interface AdapterListener {

        /**
         * Method called when user wants to upload the file.
         *
         * @param position position of clicked item
         * @param formData model of clicked item
         */
        void onUploadPic(int position, FormData formData);

        void uploadCameraImage(int adapterPosition);


        void onProcessClick(FormData formData);

        void getDropdownItems(int position, String target, @Nullable String rollId);

        void openVidCamera(int pos, ItemDynamicFormVideoBinding mBinding, int maxLength);


        void onVeriFyOtpButtonClick(FormData formData, String mobile);


        void openPlacePicker(int position, FormData formData);

        void openScanner(int position, FormData formData);


        void sendButtonInstance(Button button, boolean isEditable);

    }

    /**
     * Add signature into the view
     */
    private class SignatureViewHolder extends BaseViewHolder implements SignaturePad.OnSignedListener,
            FormSignatureViewModel.OnResetClickListener {
        private ItemDynamicFormSignatureBinding mBinding;
        private SignaturePad signaturePad;

        SignatureViewHolder(ItemDynamicFormSignatureBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
            signaturePad = mBinding.signaturePad;
            signaturePad.setOnSignedListener(this);
        }

        @Override
        public void onStartSigning() {
            //Event triggered when the pad is touched
            /*if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                pos = getAdapterPosition();
                mSignaturePad = signaturePad;
                ActivityCompat.requestPermissions((Activity) context, new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE);


            }*/
        }

        @Override
        public void onSigned() {
            pos = getAdapterPosition();
            mSignaturePad = signaturePad;

            File actualImage = CommonUtils.convertBitmapToFile(context, signaturePad.getSignatureBitmap(),
                    "sign_" + Calendar.getInstance().getTimeInMillis() + ".png");

            new Compressor(context)
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(file -> {
                        ArrayList<File> fileList = new ArrayList<>();
                        fileList.add(file);
                        formDataList.get(getAdapterPosition()).setFile(fileList);
                        formDataList.get(getAdapterPosition()).setEnteredValue(file.getPath());
                    }, throwable -> {
                        throwable.printStackTrace();
                        TrackiToast.Message.showShort(context, throwable.getMessage());
                    });


      /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        || context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    pos = getAdapterPosition();
                    mSignaturePad = signaturePad;
                    ActivityCompat.requestPermissions((Activity) context, new String[]
                                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE);


                } else {
                    Log.d("Home", "Already granted access");
                    //Event triggered when the pad is signed
                    File file = CommonUtils.convertBitmapToFile(context, signaturePad.getSignatureBitmap(),
                            "sign_" + Calendar.getInstance().getTimeInMillis() + ".png");
                    ArrayList<File> fileList = new ArrayList<>();



                    //Log.e("DynamicAdapter", getAdapterPosition() + " <----------->> " + fileList.size());
                    try {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                File sdcard = Environment.getExternalStorageDirectory();
                                File dir = new File(sdcard.getAbsolutePath(), "COMPRESS_IMAGE");
                                dir.mkdir();

                                String filePath = SiliCompressor.with(context).compress(file.getPath(), dir, true);
                                // formDataList.get(getAdapterPosition()).setEnteredValue(filePath);
                                fileList.add(new File(filePath));
                                formDataList.get(getAdapterPosition()).setFile(fileList);
                                formDataList.get(getAdapterPosition()).setEnteredValue(file.getPath());
                                // data.enteredValue=file.path
                            }
                        });
                        thread.start();

                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //Event triggered when the pad is signed
                File file = CommonUtils.convertBitmapToFile(context, signaturePad.getSignatureBitmap(),
                        "sign_" + Calendar.getInstance().getTimeInMillis() + ".png");
                ArrayList<File> fileList = new ArrayList<>();

                //Log.e("DynamicAdapter", getAdapterPosition() + " <----------->> " + fileList.size());
                try {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            File sdcard = Environment.getExternalStorageDirectory();
                            File dir = new File(sdcard.getAbsolutePath(), "COMPRESS_IMAGE");
                            dir.mkdir();

                            String filePath = SiliCompressor.with(context).compress(file.getPath(), dir, true);
                            // formDataList.get(getAdapterPosition()).setEnteredValue(filePath);
                            fileList.add(new File(filePath));
                            formDataList.get(getAdapterPosition()).setFile(fileList);
                            formDataList.get(getAdapterPosition()).setEnteredValue(file.getPath());
                            // data.enteredValue=file.path
                        }
                    });
                    thread.start();

                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }*/

        }

        @Override
        public void onClear() {
            //Event triggered when the pad is cleared
            formDataList.get(getAdapterPosition()).setFile(null);
            formDataList.get(getAdapterPosition()).setEnteredValue(null);
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);

            FormSignatureViewModel signatureViewModel = new FormSignatureViewModel(formData, this);
            mBinding.setViewModel(signatureViewModel);

            if (formData.getFile() != null && formData.getFile().size() > 0) {
                Bitmap bitmap = BitmapFactory.decodeFile(formData.getFile().get(0).getPath());
                Drawable d = new BitmapDrawable(context.getResources(), bitmap);
//                signaturePad.setSignatureBitmap(bitmap);
                signaturePad.setBackground(d);
            }


            if (formData.getEnteredValue() != null && !formData.getEnteredValue().isEmpty()) {
                CommonUtils.showLogMessage("e", "cahche_dir", formData.getEnteredValue());
                Glide.with(context)
                        .asBitmap()
                        .load(formData.getEnteredValue().replace(",", "").toString().trim())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mBinding.signaturePad.setSignatureBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });

            }


            if (!isEditable) {
                mBinding.signaturePad.setEnabled(false);
                mBinding.btnClear.setEnabled(false);
            }
            mBinding.executePendingBindings();
        }

        @Override
        public void onClickButton() {
            signaturePad.clear();
        }
    }

    /**
     * Add button into the view with text
     */
    private class ButtonViewHolder extends BaseViewHolder implements
            FormButtonViewModel.OnButtonClickListener {
        private ItemDynamicFormButtonBinding mBinding;

        ButtonViewHolder(ItemDynamicFormButtonBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);

            FormButtonViewModel emptyItemViewModel = new FormButtonViewModel(formData, this);
            mBinding.setViewModel(emptyItemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            // mBinding.btnCLick.setText(formData.getValue());

            if (!isEditable) {
                mBinding.btnCLick.setEnabled(true);
            }


            if (adapterListener != null)
                adapterListener.sendButtonInstance(mBinding.btnCLick, isEditable);
            if (hideButton) {
                mBinding.btnCLick.setVisibility(View.GONE);
                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                param.height = 0;
                param.width = 0;
                param.setMargins(0, 0, 0, 0);
                itemView.setLayoutParams(param);

            }
            isSendingDataToServer.observe((LifecycleOwner) context, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        mBinding.btnCLick.setVisibility(View.GONE);
                        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                        param.height = 0;
                        param.width = 0;
                        param.setMargins(0, 0, 0, 0);
                        itemView.setLayoutParams(param);
                    } else {
                        mBinding.btnCLick.setVisibility(View.VISIBLE);
                        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                        param.height = mBinding.btnCLick.getHeight();
                        param.width = mBinding.btnCLick.getWidth();
                        Integer margin = CommonUtils.dpToPixel(context, 10);
                        param.setMargins(margin, margin, margin, margin);
                        itemView.setLayoutParams(param);
                    }
                }
            });
            isSubmitButtonEnable.observe((LifecycleOwner) context, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    mBinding.btnCLick.setEnabled(aBoolean);
                    if (adapterListener != null)
                        adapterListener.sendButtonInstance(mBinding.btnCLick, aBoolean);
                }
            });

        }

        @Override
        public void onClickButton(@NotNull FormData formData) {
            if (adapterListener != null) {
                adapterListener.onProcessClick(formData);
            }
        }
    }


    private class CalculateViewHolder extends BaseViewHolder {
        private ItemDynamicFormCalculateBinding mBinding;

        CalculateViewHolder(ItemDynamicFormCalculateBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);
//            JSONConverter jsonConverter=new JSONConverter();
//            CommonUtils.showLogMessage("e","formuladata",jsonConverter.objectToJson(formData));

            FormCalculateViewModel emptyItemViewModel = new FormCalculateViewModel(formData);
            mBinding.setViewModel(emptyItemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            // mBinding.btnCLick.setText(formData.getValue());


            isEstimtedWidegtFilled.observe((LifecycleOwner) context, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean)
                        mBinding.label.setText("");

                }
            });
            if (formData.getValue() != null && !formData.getValue().isEmpty())
                mBinding.label.setText(formData.getValue());


           /* mBinding.btnCLick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (formData.getActionConfig() != null && formData.getActionConfig().getTarget() != null)
                        formula = formData.getActionConfig().getTarget();


                    try {
                        CommonUtils.showLogMessage("e", "Formula", "Formula before: " + formula);
                        for (FormData formData1 : formDataList) {
                            if (formula.contains(formData1.getName())) {
                                if (formData1.getType() == DataType.DROPDOWN) {
                                    Integer finalWieght = 0;
                                    for (WidgetData data : formData1.getWidgetData()) {
                                        if (data.getSelected()) {
                                            finalWieght = finalWieght + data.getWeight();
                                            formula = formula.replace(formData1.getName(), finalWieght.toString());
                                        }
                                    }

                                } else if (formData1.getType() == DataType.NUMBER) {

                                    try {
                                        if (formData1.getEnteredValue() != null && !formData1.getEnteredValue().isEmpty()) {
                                            if (formData1.getWeight() <= 0)
                                                formData1.setWeight(1.0);
                                            Double calculation = Integer.parseInt(formData1.getEnteredValue()) * formData1.getWeight();
                                            formula = formula.replace(formData1.getName(), calculation.toString());
                                        } else {
                                            TrackiToast.Message.showShort(context, formData1.getErrorMessage());
                                            break;
                                        }
                                    } catch (NumberFormatException e) {

                                    }


                                } else {
                                    if (formData1.getEnteredValue() != null && !formData1.getEnteredValue().isEmpty())
                                        formula = formula.replace(formData1.getName(), formData1.getEnteredValue());
                                    else {
                                        TrackiToast.Message.showShort(context, formData1.getErrorMessage());
                                        break;
                                    }
                                }

                            }
                        }

                        CommonUtils.showLogMessage("e", "Formula", "Formula after: " + formula);
                        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
                        if (engine != null) {
                            Object result = engine.eval(formula);
                            mBinding.label.setText(String.valueOf(result));
                            formDataList.get(getAdapterPosition()).setEnteredValue(String.valueOf(result));
                        } else {
                            mBinding.label.setText(String.valueOf(0));
                            formDataList.get(getAdapterPosition()).setEnteredValue(String.valueOf(0));
                        }


                    } catch (ScriptException e) {
                        mBinding.label.setText(String.valueOf(0));
                        formDataList.get(getAdapterPosition()).setEnteredValue(String.valueOf(0));
                        CommonUtils.showLogMessage("e", "Calculator", " ScriptEngine error: " + e.getMessage());
                    }
                }
            });*/
            final boolean[] isAllPass = {true};

            mBinding.btnCLick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        CalculateFormData calculateFormData = CommonUtils.getAllWidgetData(formDataList);
                        if (calculateFormData.getTaskData() != null && !calculateFormData.getTaskData().isEmpty()) {
                            for (int i = 0; i < calculateFormData.getTaskData().size(); i++) {
                                DynamicFormData dynamicFormData = calculateFormData.getTaskData().get(i);
                                if (dynamicFormData.getIncludeForCalculation() && dynamicFormData.getType() != null) {
                                    DataType type = dynamicFormData.getType();
                                    if (type == DataType.DROPDOWN) {
                                        isAllPass[0] = true;
                                        Double finalWieght = dynamicFormData.getWeight();
                                        stringBuilder.append(dynamicFormData.getOperation());
                                        stringBuilder.append(CommonUtils.RoundTo2Decimals(finalWieght));

                                    } else if (type == DataType.NUMBER) {
                                        if (dynamicFormData.getValue() != null && !dynamicFormData.getValue().isEmpty()) {
                                            isAllPass[0] = true;
                                            if (dynamicFormData.getWeight() <= 0)
                                                dynamicFormData.setWeight(1.0);
                                            Double calculation = Double.parseDouble(dynamicFormData.getValue()) * dynamicFormData.getWeight();
                                            stringBuilder.append(dynamicFormData.getOperation());
                                            stringBuilder.append(CommonUtils.RoundTo2Decimals(calculation));


                                        } else {
                                            if (dynamicFormData.getErrorMessage() != null && !dynamicFormData.getErrorMessage().isEmpty()) {
                                                TrackiToast.Message.showShort(context, dynamicFormData.getErrorMessage());
                                            } else {
                                                mBinding.label.setText("");
                                                formDataList.get(getAdapterPosition()).setEnteredValue("");
                                                TrackiToast.Message.showShort(context, "Please Enter " + dynamicFormData.getLabel());
                                            }
                                            isAllPass[0] = false;
                                            break;
                                        }
                                    } else {
                                        if (dynamicFormData.getValue() != null && !dynamicFormData.getValue().isEmpty()) {
                                            isAllPass[0] = true;
                                            stringBuilder.append(dynamicFormData.getOperation());
                                            stringBuilder.append(dynamicFormData.getValue());
                                        } else {
                                            if (dynamicFormData.getErrorMessage() != null && !dynamicFormData.getErrorMessage().isEmpty()) {
                                                TrackiToast.Message.showShort(context, dynamicFormData.getErrorMessage());
                                            } else {
                                                mBinding.label.setText("");
                                                formDataList.get(getAdapterPosition()).setEnteredValue("");
                                                TrackiToast.Message.showShort(context, "Please Enter " + dynamicFormData.getLabel());
                                            }
                                            isAllPass[0] = false;
                                            break;
                                        }
                                    }
                                }

                            }
                            CommonUtils.showLogMessage("e", "value", "value : " + stringBuilder.toString());
                            ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
                            if (engine != null) {
                                String value = stringBuilder.toString();
                                if (!value.isEmpty()) {
                                    if (value.startsWith("*") || value.startsWith("/")) {
                                        value = value.substring(1);
                                    }
                                }
                                Object result = engine.eval(value);
                                if (result != null && isAllPass[0]) {
                                    mBinding.label.setText(String.valueOf(result));
                                    formDataList.get(getAdapterPosition()).setEnteredValue(String.valueOf(result));
                                    formDataList.get(getAdapterPosition()).setValue(String.valueOf(result));
                                }
                            } else {
                                mBinding.label.setText(String.valueOf(0));
                                formDataList.get(getAdapterPosition()).setEnteredValue(String.valueOf(0));
                                formDataList.get(getAdapterPosition()).setValue(String.valueOf(0));
                            }

                        }
                    } catch (NumberFormatException | ScriptException e) {
                        mBinding.label.setText(String.valueOf(0));
                        formDataList.get(getAdapterPosition()).setEnteredValue(String.valueOf(0));
                        formDataList.get(getAdapterPosition()).setValue(String.valueOf(0));
                        CommonUtils.showLogMessage("e", "Calculator", " ScriptEngine error: " + e.getMessage());
                    }

                }
            });

            if (!isEditable) {
                mBinding.btnCLick.setEnabled(false);
                mBinding.label.setEnabled(false);
            }

        }


    }


    /**
     * Used to add multiple images into the view
     */
    class CameraViewHolder extends BaseViewHolder {
        private final HorizontalScrollView scrollView;
        private ItemDynamicFormCameraBinding mBinding;
        private LinearLayout linearLayoutAdd;

        CameraViewHolder(ItemDynamicFormCameraBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
            linearLayoutAdd = mBinding.linearLayoutAdd;
            scrollView = mBinding.hScrollView;
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);

            FormCameraViewModel emptyItemViewModel = new FormCameraViewModel(formData);
            mBinding.setViewModel(emptyItemViewModel);


            if (formData.getFile() == null || formData.getFile().size() == 0) {
                ArrayList<File> list = new ArrayList<>();
                list.add(new File(AppConstants.ADD_MORE));
                if (formData.getValue() != null && !formData.getValue().isEmpty()) {
                    //CommonUtils.showLogMessage("e","camera file url",formData.getValue());
                    List<String> urlList = Arrays.asList(formData.getValue().split("\\s*,\\s*"));
                    if (urlList != null && !urlList.isEmpty()) {
                        for (String url : urlList)
                            list.add(new File(url.trim()));
                    }
                }
                // list.add(new File(AppConstants.ADD_MORE));
                formData.setFile(list);
            } else {

//                if (!formData.getFile().get(formData.getFile().size() - 1).getPath().equals(AppConstants.ADD_MORE)) {
//                    formData.getFile().add(new File(AppConstants.ADD_MORE));
//                }
                if (!formData.getFile().get(0).getPath().equals(AppConstants.ADD_MORE)) {
                    formData.getFile().add(0, new File(AppConstants.ADD_MORE));
                }
            }

            if (formData.getFile().size() > 0) {
                linearLayoutAdd.removeAllViews();
                for (int i = 0; i < formData.getFile().size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.view_add_photo, linearLayoutAdd, false);
                    view.setTag(i);
                    ImageButton ibAddPic = view.findViewById(R.id.ibAddPic);
                    ImageButton ibClose = view.findViewById(R.id.ibClose);
                    TextView addMore = view.findViewById(R.id.addMore);

                    ibAddPic.setOnClickListener(v -> {
                        if (isEditable)
                            adapterListener.uploadCameraImage(getAdapterPosition());
                    });
                    int finalI = i;
                    ibClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // CommonUtils.showLogMessage("e","tag",""+view.getTag());
                            if (formData.getFile().get(finalI) != null) {
                                //remove last index file "add_more_view"
                                //   formDataList.get(getAdapterPosition()).getFile().remove(formDataList.get(position).getFile().size() - 1);
                                //remove uploaded image
                                formDataList.get(getAdapterPosition()).getFile().remove(finalI);
//                                List<String> urlList = new ArrayList<>(Arrays.asList(formData.getValue().split("\\s*,\\s*")));
//                                if(urlList!=null && !urlList.isEmpty()){
//                                    //int j= finalI -1;
//                                    urlList.remove(finalI);
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        formData.setValue(String.join(", ", urlList));
//                                    }else{
//                                        StringBuilder csvBuilder = new StringBuilder();
//                                        for(String urls : urlList){
//                                            csvBuilder.append(urls);
//                                            csvBuilder.append(", ");
//                                        }
//                                        String csv = csvBuilder.toString();
//                                        formData.setValue(csv);
//                                    }
//                                }
                                //   CommonUtils.showLogMessage("e","removed url",formData.getValue());
                                notifyDataSetChanged();
                            }
                        }
                    });
                    try {
                        if (formDataList.get(position).getFile().get(i).getPath().equals(AppConstants.ADD_MORE)) {
                            ibClose.setVisibility(View.GONE);
                            addMore.setVisibility(View.GONE);
                            GlideApp.with(context).load(R.drawable.ic_add_blue).into(ibAddPic);
                        } else {
                            if (!isEditable) {
                                ibClose.setVisibility(View.GONE);
                            } else {
                                ibClose.setVisibility(View.VISIBLE);
                            }

                            File file = formDataList.get(position).getFile().get(i);
                            if (file.exists()) {
                                addMore.setVisibility(View.GONE);
                                GlideApp.with(context).load(file).into(ibAddPic);
                            } else {
                                CommonUtils.showLogMessage("e", "load Image", file.getPath());
                                GlideApp.with(context).load(file.getPath()).into(ibAddPic);
                                addMore.setVisibility(View.GONE);
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view); // <- fix
                    }
                    linearLayoutAdd.addView(view);
                    // Locate the button.
                    int x, y;
                    x = ibAddPic.getLeft();
                    y = ibAddPic.getTop();
                    scrollView.scrollTo(x, y);
                    if (!isEditable)
                        ibAddPic.setEnabled(false);
                }
            }


            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();

        }
    }

    private class VideoViewHolder extends BaseViewHolder implements
            ActivityCompat.OnRequestPermissionsResultCallback {
        private TextView tvCamera;
        private ImageView ivRecVideo, ivPlay, ivVideo, ivDelete;

        VideoViewHolder(ItemDynamicFormVideoBinding itemView) {
            super(itemView.getRoot());
            itemDynamicFormVideoBinding = itemView;
            tvCamera = itemDynamicFormVideoBinding.tvCamera;
            ivRecVideo = itemDynamicFormVideoBinding.ivRecVideo;
            ivPlay = itemDynamicFormVideoBinding.ivPlay;
            ivVideo = itemDynamicFormVideoBinding.ivVideo;
            ivDelete = itemDynamicFormVideoBinding.ivDelete;
            itemDynamicFormVideoBinding.videoView.getLayoutParams().width = 500;
            itemDynamicFormVideoBinding.videoView.getLayoutParams().height = 150;

        }

        @Override
        public void onBind(int position) {
            //  setVid(0, null, null);
            videoViewPos = position;
            final FormData formData = formDataList.get(position);
            FormVideoViewModel formVideoViewModel = new FormVideoViewModel(formData);
            itemDynamicFormVideoBinding.setViewModel(formVideoViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            itemDynamicFormVideoBinding.executePendingBindings();
            if (formData.getEnteredValue() != null && !formData.getEnteredValue().isEmpty()) {
                ivVideo.setVisibility(View.VISIBLE);
                if (formData.getEnteredValue().startsWith("https") || formData.getEnteredValue().startsWith("http")) {
                    ivDelete.setVisibility(View.GONE);
                } else {
                    ivDelete.setVisibility(View.VISIBLE);
                }

                ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ivDelete.setVisibility(View.GONE);
                        ivVideo.setVisibility(View.GONE);
                        formData.setFile(null);
                        formData.setValue(null);
                        formData.setEnteredValue(null);
                    }
                });
                Glide.with(context).
                        load(formData.getEnteredValue())
                        .into(ivVideo);
                ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (formData.getEnteredValue() != null) {
                            Intent intent = new Intent(context, PlayVideoVerticallyActivity.class);
                            intent.putExtra("url", formData.getEnteredValue());
                            context.startActivity(intent);
                        }
                    }
                });
            } else {
                ivDelete.setVisibility(View.GONE);
            }
//                GlideApp.with(context).load(formData.getValue()).into(mBinding.cameraView);

            if (!isEditable)
                itemDynamicFormVideoBinding.ivRecVideo.setEnabled(false);

            ivRecVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED
                                || context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED

                                || context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]
                                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.RECORD_AUDIO,
                                                    Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        } else {
//                            Log.d("Home", "Already granted access");
//                            Log.e("max length", "" + formDataList.get(getAdapterPosition()).getMaxLength());
                            int maxLength = formDataList.get(getAdapterPosition()).getMaxLength();
                            if (maxLength <= 0)
                                maxLength = 5;
                            adapterListener.openVidCamera(getAdapterPosition(), itemDynamicFormVideoBinding, maxLength);
                        }
                    }


                }
            });
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
            switch (requestCode) {
                case VIDEO_CAPTURE: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d("Home", "Permission Granted");
//                        Log.e("max length", "" + formDataList.get(getAdapterPosition()).getMaxLength());

                        int maxLength = formDataList.get(getAdapterPosition()).getMaxLength();
                        if (maxLength <= 0)
                            maxLength = 5;
                        adapterListener.openVidCamera(getAdapterPosition(), itemDynamicFormVideoBinding, maxLength);
                    } else {
                        Log.d("Home", "Permission Failed");
                        TrackiToast.Message.showLong(context, "You must allow permission record audio to your mobile device.");
                    }
                }
                // Add additional cases for other permissions you may have asked for
            }
        }
    }


    private class AudioViewHolder extends BaseViewHolder implements
            ActivityCompat.OnRequestPermissionsResultCallback {
        private TextView tvStartRec, tvPlayAudio, tvPlay, tvTime;
        private ImageView ivStartRec, ivStopRec, ivPlayRec, ivGif;
        ItemDynamicFormAudioBinding itemDynamicFormAudioBinding;
        private String fileName = null;
        private boolean start = true;
        private boolean playing = false;


        private MediaRecorder recorder = null;

        private MediaPlayer player = null;

        AudioViewHolder(ItemDynamicFormAudioBinding itemView) {
            super(itemView.getRoot());
            itemDynamicFormAudioBinding = itemView;
            tvStartRec = itemDynamicFormAudioBinding.tvStartRec;
            tvPlay = itemDynamicFormAudioBinding.tvPlay;
            tvTime = itemDynamicFormAudioBinding.tvTime;
            tvPlayAudio = itemDynamicFormAudioBinding.tvPlayAudio;
            ivStartRec = itemDynamicFormAudioBinding.ivStartRec;
            ivStopRec = itemDynamicFormAudioBinding.ivStopRec;
            ivGif = itemDynamicFormAudioBinding.ivGif;
            ivStopRec.setEnabled(false);
            ivPlayRec = itemDynamicFormAudioBinding.ivPlayRec;
            ivPlayRec.setEnabled(false);
            fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName += "/audioFile.mp3";
            tvStartRec.setText(AppConstants.START_REC);
            tvPlayAudio.setText(AppConstants.PLAY_AUDIO);

        }

        @Override
        public void onBind(int position) {
            audioViewPos = position;
            final FormData formData = formDataList.get(position);
            FormAudioViewModel formAudioViewModel = new FormAudioViewModel(formData);
            itemDynamicFormAudioBinding.setViewModel(formAudioViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            itemDynamicFormAudioBinding.executePendingBindings();
//            if (formData.getValue()!=null && !formData.getValue().isEmpty())
//                GlideApp.with(context).load(formData.getValue()).into(mBinding.cameraView);

            if (!isEditable) {
                itemDynamicFormAudioBinding.ivStopRec.setEnabled(false);
                itemDynamicFormAudioBinding.ivStartRec.setEnabled(false);
            }
            if (formData.getValue() != null && !formData.getValue().isEmpty()) {
                fileName = formData.getValue();
                CommonUtils.showLogMessage("e", "value", formData.getValue());
                // fileName="https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
                itemDynamicFormAudioBinding.ivPlayRec.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_audio_play));
                itemDynamicFormAudioBinding.ivPlayRec.setEnabled(true);
                itemDynamicFormAudioBinding.ivPlayRec.setVisibility(View.VISIBLE);
            }
            ivStopRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecord(start);
                }
            });
            ivStartRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED
                                || context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED

                                || context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]
                                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.RECORD_AUDIO,
                                                    Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            formData.setValue("");

                        } else {
                            Log.d("Home", "Already granted access");
                            formData.setValue("");
                            onRecord(start);
                        }
                    }


                }
            });

            ivPlayRec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemDynamicFormAudioBinding.tvPlay.setVisibility(View.VISIBLE);
                    itemDynamicFormAudioBinding.tvTime.setVisibility(View.VISIBLE);
                    itemDynamicFormAudioBinding.ivGif.setVisibility(View.GONE);
                    itemDynamicFormAudioBinding.tvTime.setText("");
                    tvTime.post(mUpdateTime);
                    onPlay(playing);
                }
            });

        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
            switch (requestCode) {
                case VIDEO_CAPTURE: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Home", "Permission Granted");

                        onRecord(start);

                    } else {
                        Log.d("Home", "Permission Failed");
                        TrackiToast.Message.showLong(context, "You must allow permission record audio to your mobile device.");
                    }
                }
/*
                case MY_PERMISSIONS_REQUEST_READ_WRITE_STORAGE: {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        File file = CommonUtils.convertBitmapToFile(context, mSignaturePad.getSignatureBitmap(),
                                "sign_" + Calendar.getInstance().getTimeInMillis() + ".png");
                        ArrayList<File> fileList = new ArrayList<>();

                        //Log.e("DynamicAdapter", getAdapterPosition() + " <----------->> " + fileList.size());
                        try {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    File sdcard = Environment.getExternalStorageDirectory();
                                    File dir = new File(sdcard.getAbsolutePath(), "COMPRESS_IMAGE");
                                    dir.mkdir();

                                    String filePath = SiliCompressor.with(context).compress(file.getPath(), dir, true);
                                    // formDataList.get(getAdapterPosition()).setEnteredValue(filePath);
                                    fileList.add(new File(filePath));
                                    formDataList.get(pos).setFile(fileList);
                                    formDataList.get(pos).setEnteredValue(file.getPath());
                                    // data.enteredValue=file.path
                                }
                            });
                            thread.start();

                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("Home", "Permission Failed");
                        TrackiToast.Message.showLong(context, "You must allow storage permission");
                    }

                }
                // Add additional cases for other permissions you may have asked for
*/
            }
        }

        private void onRecord(boolean start) {
            if (start) {
                startRecording();
                itemDynamicFormAudioBinding.ivStopRec.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_audio_stop));
                itemDynamicFormAudioBinding.ivStopRec.setImageResource(R.drawable.ic_multimedia_stop);
                itemDynamicFormAudioBinding.ivStopRec.setEnabled(true);
                itemDynamicFormAudioBinding.ivPlayRec.setBackground(ContextCompat.getDrawable(context, R.drawable.disable_circle));
                itemDynamicFormAudioBinding.ivPlayRec.setEnabled(false);
                itemDynamicFormAudioBinding.tvPlay.setVisibility(View.GONE);
                itemDynamicFormAudioBinding.tvTime.setVisibility(View.GONE);
                itemDynamicFormAudioBinding.ivPlayRec.setVisibility(View.GONE);
                Glide.with(context).asGif().load(R.drawable.source).into(ivGif);
                itemDynamicFormAudioBinding.ivGif.setVisibility(View.VISIBLE);
                this.start = false;
            } else {
                stopRecording();
                itemDynamicFormAudioBinding.ivStopRec.setBackground(ContextCompat.getDrawable(context, R.drawable.disable_circle));
                itemDynamicFormAudioBinding.ivStopRec.setImageResource(R.drawable.stop_disable);
                itemDynamicFormAudioBinding.ivStopRec.setEnabled(false);
                itemDynamicFormAudioBinding.ivPlayRec.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_audio_play));
                itemDynamicFormAudioBinding.ivPlayRec.setEnabled(true);
                itemDynamicFormAudioBinding.ivPlayRec.setVisibility(View.VISIBLE);
                itemDynamicFormAudioBinding.tvPlay.setVisibility(View.GONE);
                itemDynamicFormAudioBinding.tvTime.setVisibility(View.GONE);
                itemDynamicFormAudioBinding.ivGif.setVisibility(View.GONE);
                this.start = true;
                try {

                    ArrayList<File> fileList = new ArrayList<>();
                    File file = new File(fileName);
                    fileList.add(file);
                    formDataList.get(getAdapterPosition()).setFile(fileList);
                    formDataList.get(getAdapterPosition()).setEnteredValue(fileName);
                    formDataList.get(getAdapterPosition()).setValue(fileName);

                    //Toast.makeText(context, "Captured", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } catch (Exception c) {
                    c.printStackTrace();
                }
            }
        }

        private void onPlay(boolean playing) {
            if (!playing) {
                startPlaying();
                itemDynamicFormAudioBinding.ivPlayRec.setImageResource(R.drawable.ic_pause);
                this.playing = true;

            } else {
                stopPlaying();
                itemDynamicFormAudioBinding.ivPlayRec.setImageResource(R.drawable.ic_multimedia_play);
                this.playing = false;


            }
        }

        private void startPlaying() {
            player = new MediaPlayer();
            try {
                player.setDataSource(fileName);
                player.prepare();
                player.start();
            } catch (IOException e) {
                Log.e("LOG_TAG", "prepare() failed");
            }
        }

        private void stopPlaying() {
            if (player != null)
                player.stop();

            //player.release();

            //player = null;
        }

        private void startRecording() {
            fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName += "/audioFile.mp3";
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            Log.e("max length", "" + formDataList.get(getAdapterPosition()).getMaxLength());
            int maxLength = formDataList.get(getAdapterPosition()).getMaxLength();
            if (maxLength <= 0)
                maxLength = 5;
            recorder.setMaxDuration(maxLength * 1000); // 10 seconds
            //recorder.setMaxFileSize(maxLength * 1000);//5kb
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("LOG_TAG", "prepare() failed");
            }

            recorder.start();
        }

        private Runnable mUpdateTime = new Runnable() {
            public void run() {
                int currentDuration;
                if (player.isPlaying()) {
                    currentDuration = player.getCurrentPosition();
                    updatePlayer(currentDuration);
                    tvTime.postDelayed(this, 100);
                } else {
                    tvTime.removeCallbacks(this);
                    itemDynamicFormAudioBinding.ivPlayRec.setImageResource(R.drawable.ic_multimedia_play);
                    itemDynamicFormAudioBinding.tvPlay.setVisibility(View.GONE);
                    itemDynamicFormAudioBinding.tvTime.setVisibility(View.GONE);

                }
            }
        };

        private void updatePlayer(int currentDuration) {
            tvTime.setText("" + milliSecondsToTimer((long) currentDuration));
        }

        /**
         * Function to convert milliseconds time to Timer Format
         * Hours:Minutes:Seconds
         */
        public String milliSecondsToTimer(long milliseconds) {
            String finalTimerString = "";
            String secondsString = "";

            // Convert total duration into time
            int hours = (int) (milliseconds / (1000 * 60 * 60));
            int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours + ":";
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }

            finalTimerString = finalTimerString + minutes + ":" + secondsString;

            // return timer string
            return finalTimerString;
        }

        private void stopRecording() {
            if (recorder != null) {
                recorder.stop();
                //recorder.release();
            }
            //recorder = null;
        }

    }

    /**
     * Used to hold  toggle button
     */
    private class ToggleViewHolder extends BaseViewHolder {
        ItemDynamicFormToggleBinding mBinding;
        private LabeledSwitch mSwitch;

        ToggleViewHolder(ItemDynamicFormToggleBinding itemView) {
            super(itemView.getRoot());
            this.mBinding = itemView;
            mSwitch = mBinding.mSwitch;
            mSwitch.setLabelOff("No");
            mSwitch.setLabelOn("Yes");
            mSwitch.setOnToggledListener(new OnToggledListener() {
                @Override
                public void onSwitched(ToggleableView toggleableView, boolean isChecked) {
                    String value;
                    if (isChecked) {
                        value = "true";
                    } else {
                        value = "false";
                    }
                    formDataList.get(getAdapterPosition()).setChecked(isChecked);
                    formDataList.get(getAdapterPosition()).setEnteredValue(value);
                }
            });
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);
            // formData.setEnteredValue("false");


            FormToggleViewModel emptyItemViewModel = new FormToggleViewModel(formData);
            mBinding.setViewModel(emptyItemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();

//            if (formData.getValue() != null && !formData.getValue().isEmpty()) {
//                if (formData.getValue().equalsIgnoreCase("true"))
//                    mSwitch.setChecked(true);
//                else
//                    mSwitch.setChecked(false);
//            }
            if (formData.getEnteredValue() != null && !formData.getEnteredValue().isEmpty()) {
                if (formData.getEnteredValue().equals("true")) {
                    mSwitch.setOn(true);
                } else {
                    mSwitch.setOn(false);
                }
            }
            if (!isEditable)
                mSwitch.setEnabled(false);
        }
    }

    private class ScannerViewHolder extends BaseViewHolder {

        ItemDynamicFormScannerBinding mBinding;

        ScannerViewHolder(ItemDynamicFormScannerBinding binding) {
            super(binding.getRoot());
            Log.e(TAG, "EditTextViewHolder: ------------>>");
            this.mBinding = binding;
            mBinding.edDynamicFormText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    formDataList.get(getAdapterPosition()).setEnteredValue(charSequence.toString());
                    formDataList.get(getAdapterPosition()).setValue(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);
            FormEdittextViewModel emptyItemViewModel = new FormEdittextViewModel(formData);
            mBinding.setViewModel(emptyItemViewModel);


            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (formData.getValue() != null && !formData.getValue().isEmpty())

                mBinding.edDynamicFormText.setText(formData.getValue());

            if (!isEditable)
                mBinding.edDynamicFormText.setEnabled(false);
            mBinding.ivQrcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(adapterListener!=null)
                        adapterListener.openScanner(position,formData);

                }
            });

        }


    }


    /**
     * Class used to handle all the text fields for email,text & number.
     */
    private class EditTextViewHolder extends BaseViewHolder {

        ItemDynamicFormEdittextBinding mBinding;

        EditTextViewHolder(ItemDynamicFormEdittextBinding binding) {
            super(binding.getRoot());
            Log.e(TAG, "EditTextViewHolder: ------------>>");
            this.mBinding = binding;
            mBinding.edDynamicFormText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    formDataList.get(getAdapterPosition()).setEnteredValue(charSequence.toString());
                    formDataList.get(getAdapterPosition()).setValue(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(int position) {

            final FormData formData = formDataList.get(position);
            FormEdittextViewModel emptyItemViewModel = new FormEdittextViewModel(formData);
            mBinding.setViewModel(emptyItemViewModel);


            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (formData.getValue() != null && !formData.getValue().isEmpty())

                mBinding.edDynamicFormText.setText(formData.getValue());


            if (!isEditable)
                mBinding.edDynamicFormText.setEnabled(false);
            if (formData.getActionConfig() != null) {
//                "action": "API",
//                        "target": "FIELD_DATA_AUTO_POPULATE",
//                        "skipTarget": false,
//                        "formula": "TASK|0|jhhjh-jkj22-2k883sbbb|estimated_amount"
                CommonUtils.showLogMessage("e", "hit aata", "hit task");
                if (formData.getActionConfig().getAction() != null && formData.getActionConfig().getAction() == Type.API) {
                    if (formData.getActionConfig().getTarget() != null && formData.getActionConfig().getTarget().equals(ApiType.FIELD_DATA_AUTO_POPULATE.name())) {
                        Api api = TrackiApplication.getApiMap().get(ApiType.FIELD_DATA_AUTO_POPULATE);
                        if (api != null && formData.getActionConfig().getFormula() != null) {
                            if (NetworkUtils.isNetworkConnected(context))
                                new LoadDataAsyncTask(api, formData.getActionConfig().getFormula(), mBinding.edDynamicFormText, getAdapterPosition()).execute();

                        }
                    }
                }
            }

        }


    }

    class LoadDataAsyncTask extends AsyncTask<Void, Void, String> {
        Api api;
        String formula;
        String response = null;
        private APIError apiError;
        private WeakReference<View> weakView;
        private WeakReference<Integer> weakPosition;

        public LoadDataAsyncTask(Api api, String data, View view, Integer position) {
            this.api = api;
            this.formula = data;
            this.weakView = new WeakReference<>(view);
            this.weakPosition = new WeakReference<>(position);
        }

        ProgressDialog pg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(context);
            pg.setCancelable(false);
            pg.setCanceledOnTouchOutside(false);
            pg.show();
        }

        @Override
        protected String doInBackground(Void... strings) {
            try {
                Api lapi = new Api();
                lapi.setName(api.getName());
                lapi.setTimeOut(api.getTimeOut());
                lapi.setCacheable(api.getCacheable());
                lapi.setVersion(api.getVersion());
                lapi.setAppendWithKey(api.getAppendWithKey());
                CommonUtils.showLogMessage("e", "url", api.getUrl());
                //https://qa2.rocketflyer.in/rfapi/secure/tracki/getLinkedFieldData?formula=123&taskId=323424324
                String url = api.getUrl() + "?formula=" + formula + "&taskId=" + taskId;
                lapi.setUrl(url);
                CommonUtils.showLogMessage("e", "url", lapi.getUrl());
                response = httpmanager.getRequest(lapi);
                CommonUtils.showLogMessage("e", "response=>", response);
            } catch (SocketTimeoutException var5) {
                android.util.Log.e(TAG, "doInBackground: " + var5);
                apiError = new APIError(APIError.ErrorType.TIMEOUT);
            } catch (SocketException | UnknownHostException | UnsupportedEncodingException var6) {
                android.util.Log.e(TAG, "doInBackground: " + var6);
                apiError = new APIError(APIError.ErrorType.NETWORK_FAIL);
            } catch (ParseException e) {
                android.util.Log.e(TAG, "doInBackground: " + e);
                apiError = new APIError(APIError.ErrorType.SERVER_DOWN);
            } catch (Exception e) {
                android.util.Log.e(TAG, "doInBackground: " + e);
                apiError = new APIError(APIError.ErrorType.UNKNOWN_ERROR);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            pg.dismiss();
            JSONConverter jsonConverter = new JSONConverter();
            CommonUtils.showLogMessage("e", "response", response);

            GetPopulationDataResponse baseResponse = (GetPopulationDataResponse) jsonConverter.jsonToObject(response.toString(), GetPopulationDataResponse.class);
            if (baseResponse != null && baseResponse.getSuccessful()) {
                if (baseResponse.getData() != null) {
                    View view = weakView.get();
                    if (view instanceof EditText)
                        ((EditText) view).setText(baseResponse.getData());
                    else if (view instanceof TextView)
                        ((TextView) view).setText(baseResponse.getData());
                    else if (view instanceof ImageView) {
                        GlideApp.with(context).load(baseResponse.getData()).placeholder(R.drawable.ic_picture).into(((ImageView) view));
                        view.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                openDialougShowImage(baseResponse.getData());
                            }
                        });

                    }
                }
            }
//                if (baseResponse != null && baseResponse.getResponseMsg() != null) {
//                    TrackiToast.Message.showShort(context, APIError.ErrorType.SERVER_DOWN.name());
//                }
        }
    }

    /**
     * Class used to handle all the text fields for email,text & number.
     */
    private class NumberViewHolder extends BaseViewHolder {

        ItemDynamicFormNumberBinding mBinding;

        NumberViewHolder(ItemDynamicFormNumberBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.edDynamicFormNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    formDataList.get(getAdapterPosition()).setEnteredValue(charSequence.toString());
                    formDataList.get(getAdapterPosition()).setValue(charSequence.toString());

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);
            mBinding.setViewModel(new FormNumberViewModel(formData));
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (formData.getValue() != null && !formData.getValue().isEmpty())
                mBinding.edDynamicFormNumber.setText(formData.getValue());

            if (!isEditable)
                mBinding.edDynamicFormNumber.setEnabled(false);
            if (formData.getActionConfig() != null) {
//                "action": "API",
//                        "target": "FIELD_DATA_AUTO_POPULATE",
//                        "skipTarget": false,
//                        "formula": "TASK|0|jhhjh-jkj22-2k883sbbb|estimated_amount"
                CommonUtils.showLogMessage("e", "hit aata", "hit task");
                if (formData.getActionConfig().getAction() != null && formData.getActionConfig().getAction() == Type.API) {
                    if (formData.getActionConfig().getTarget() != null && formData.getActionConfig().getTarget().equals(ApiType.FIELD_DATA_AUTO_POPULATE.name())) {
                        Api api = TrackiApplication.getApiMap().get(ApiType.FIELD_DATA_AUTO_POPULATE);
                        if (api != null && formData.getActionConfig().getFormula() != null) {
                            if (NetworkUtils.isNetworkConnected(context))
                                new LoadDataAsyncTask(api, formData.getActionConfig().getFormula(), mBinding.edDynamicFormNumber, getAdapterPosition()).execute();
                        }
                    }
                }
            }

        }
    }

    /**
     * Class used to handle all the text fields for email,text & number.
     */
    private class IpAddressViewHolder extends BaseViewHolder {

        ItemDynamicFormIpAddressBinding mBinding;

        IpAddressViewHolder(ItemDynamicFormIpAddressBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;

            mBinding.edDynamicFormNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    formDataList.get(getAdapterPosition()).setEnteredValue(charSequence.toString());
                    formDataList.get(getAdapterPosition()).setValue(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);
            mBinding.setViewModel(new FormNumberViewModel(formData));
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (formData.getValue() != null && !formData.getValue().isEmpty())
                mBinding.edDynamicFormNumber.setText(formData.getValue());

            if (!isEditable)
                mBinding.edDynamicFormNumber.setEnabled(false);


        }
    }

    /**
     * Class used to handle all the text fields for email,text & number.
     */
    private class EmailViewHolder extends BaseViewHolder {

        ItemDynamicFormEmailBinding mBinding;

        EmailViewHolder(ItemDynamicFormEmailBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.edDynamicFormEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    formDataList.get(getAdapterPosition()).setEnteredValue(charSequence.toString());
                    formDataList.get(getAdapterPosition()).setValue(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);

            //val radioButton = mBinding.root.findViewById(R.id.radioButtton) as RadioButton
            FormEmailViewModel emptyItemViewModel = new FormEmailViewModel(formData);
            mBinding.setViewModel(emptyItemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (formData.getValue() != null && !formData.getValue().isEmpty())
                mBinding.edDynamicFormEmail.setText(formData.getValue());

            if (!isEditable)
                mBinding.edDynamicFormEmail.setEnabled(false);

        }
    }

    /**
     * Class used to handle all the text fields for description only.
     */
    private class DescriptionViewHolder extends BaseViewHolder {

        ItemDynamicFormDescriptionBinding mBinding;

        DescriptionViewHolder(ItemDynamicFormDescriptionBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
            mBinding.edDynamicFormDesc.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    formDataList.get(getAdapterPosition()).setEnteredValue(charSequence.toString());
                    // formDataList.get(getAdapterPosition()).setValue(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        @Override
        public void onBind(int position) {
            final FormData formData = formDataList.get(position);

            FormDescriptionViewModel emptyItemViewModel = new FormDescriptionViewModel(formData);
            mBinding.setViewModel(emptyItemViewModel);

            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (formData.getValue() != null && !formData.getValue().isEmpty())
                mBinding.edDynamicFormDesc.setText(formData.getValue());

            if (!isEditable)
                mBinding.edDynamicFormDesc.setEnabled(false);

        }
    }

    /**
     * Class used to select pic form device and upload to server.
     */
    private class UploadViewHolder extends BaseViewHolder
            implements FormUploadViewModel.UploadListener {
        private ItemDynamicFormUploadBinding mBinding;

        private UploadViewHolder(ItemDynamicFormUploadBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            final FormData data = formDataList.get(position);

            FormUploadViewModel uploadViewModel = new FormUploadViewModel(data, this);
            mBinding.setViewModel(uploadViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            Bitmap bitmap = null;
            try {
                if (data.getValue() != null && !data.getValue().isEmpty())
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(data.getValue()).getContent());
            } catch (Exception e) {

            }
            if (data.getEnteredValue() != null && !data.getEnteredValue().isEmpty()) {
                mBinding.btnView.setVisibility(View.VISIBLE);
                mBinding.btnRemove.setVisibility(View.VISIBLE);
                mBinding.ivDoc.setVisibility(View.VISIBLE);

                try{
                    Uri uri = Uri.parse(data.getEnteredValue());
                    String fileName = uri.getLastPathSegment();
                    mBinding.tvBrowseFile.setText(fileName);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mBinding.tvBrowseFile.setTextColor(context.getColor(R.color.selected_file_text_color));
                    }
                }catch (Exception e){

                }

                mBinding.btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("value", data.getEnteredValue());
                        openDialougShowImage(data.getEnteredValue());

                    }
                });
                mBinding.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        data.setEnteredValue(null);
                        data.setFile(null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            mBinding.tvBrowseFile.setTextColor(context.getColor(R.color.select_drop_text_color));
                        }
                        mBinding.tvBrowseFile.setText(context.getText(R.string.browse_the_file_here));
                        notifyItemChanged(getAdapterPosition());

                    }
                });
            } else {
                mBinding.btnView.setVisibility(View.GONE);
                mBinding.btnRemove.setVisibility(View.GONE);
                mBinding.ivDoc.setVisibility(View.GONE);
            }
            mBinding.imageTxt.setBackground(new BitmapDrawable(context.getResources(), bitmap));

            if (!isEditable)
                mBinding.btnUpload.setEnabled(false);

        }

        @Override
        public void onUploadClick(@NonNull FormData formData) {
            mBinding.btnView.setVisibility(View.GONE);
            mBinding.btnRemove.setVisibility(View.GONE);
            mBinding.ivDoc.setVisibility(View.GONE);
            adapterListener.onUploadPic(getAdapterPosition(), formData);
        }
    }


    private void openDialougShowImage(final String url) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(
                        Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_show_image);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.8f;
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        ZoomableImageView imageView = dialog.findViewById(R.id.ivImages);
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .error(R.drawable.ic_picture)
                    .placeholder(R.drawable.ic_picture)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } catch (Exception e) {

        }

        dialog.getWindow().setAttributes(lp);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        if (!dialog.isShowing())
            dialog.show();

    }

    /**
     * Class used to pick date and time.
     */
    private class DateTimeViewHolder extends BaseViewHolder
            implements FormDateTimeViewModel.DateTimeListener {

        ItemDynamicFormDateTimeBinding mBinding;
        int mYear;
        int mMonth;
        int mDay;
        int mHour;
        int mMinute;
        int mSecond;
        FormDateTimeViewModel dateTimeViewModel;
        FormData data;

        DateTimeViewHolder(ItemDynamicFormDateTimeBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            data = formDataList.get(position);
            dateTimeViewModel = new FormDateTimeViewModel(data, this);
            mBinding.setViewModel(dateTimeViewModel);


            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();

//            String[] dateTime = null;
//            if (data.getValue() != null && !data.getValue().isEmpty())
//                dateTime = data.getValue().split(",");
//
//
//
//            if (dateTime != null) {
//                if (dateTime.length > 0) {
//                    mBinding.tvDate.setText(dateTime[0]);
//                    if (dateTime.length > 1)
//                        mBinding.tvTime.setText(dateTime[1]);
//                }
//            }

            if (!isEditable) {
                mBinding.tvDate.setEnabled(false);
                mBinding.tvTime.setEnabled(false);

            }
        }

        @Override
        public void dateClick() {
            Calendar calendar = Calendar.getInstance();
//            mHour = calendar.get(Calendar.HOUR_OF_DAY);
//            mMinute = calendar.get(Calendar.MINUTE);
            mSecond = 0;
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            CommonUtils.openDatePicker(context, mYear, mMonth, mDay,
                    0, 0, (view, year, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.HOUR_OF_DAY, mHour);
                        cal.set(Calendar.MINUTE, mMinute);
                        cal.set(Calendar.SECOND, 0);
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        CommonUtils.showLogMessage("e", "milisecond", "" + cal.getTimeInMillis());
                        CommonUtils.showLogMessage("e", "selectedDate", "" + selectedDate);
                        dateTimeViewModel.getDate().set(DateTimeUtil.getParsedDate(cal.getTimeInMillis()));
//                        data.setEnteredValue(cal.getTimeInMillis() + "");
                        //DATE_TIME
                        // String dateTime = CommonUtils.getDate(cal.getTimeInMillis(), "dd/MM/yyyy hh:mm");
                        String dateTime = CommonUtils.getDate(cal.getTimeInMillis(), "MM/dd/yyyy HH:mm");
                        CommonUtils.showLogMessage("e", "dateTime", dateTime);
                        data.setEnteredValue(dateTime + "");
                    });
        }

        @Override
        public void timeClick() {
            Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            CommonUtils.openTimePicker(context, mHour, mMinute,
                    (view, hourOfDay, minute) -> {

                        Calendar cal = Calendar.getInstance();
//                        if(timeInmilisec!=0)
//                            cal.setTimeInMillis(timeInmilisec);
                        cal.set(Calendar.YEAR, mYear);
                        cal.set(Calendar.MONTH, mMonth);
                        cal.set(Calendar.DAY_OF_MONTH, mDay);
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, 0);
                        mHour = hourOfDay;
                        mMinute = minute;
                        dateTimeViewModel.getTime().set(DateTimeUtil.getParsedTime(cal.getTimeInMillis()));
                        // String dateTime = CommonUtils.getDate(cal.getTimeInMillis(), "dd/MM/yyyy hh:mm");
                        String dateTime = CommonUtils.getDate(cal.getTimeInMillis(), "MM/dd/yyyy HH:mm");
//                        data.setEnteredValue(cal.getTimeInMillis() + "");
                        CommonUtils.showLogMessage("e", "dateTime", dateTime);
                        data.setEnteredValue(dateTime + "");
                    });
        }
    }

    /**
     * Class used to pick date from & to .
     */
    private class DateRangeViewHolder extends BaseViewHolder
            implements FormDateRangeViewModel.DateRangeListener {
        ItemDynamicFormDateRangeBinding mBinding;
        FormDateRangeViewModel uploadViewModel;

        DateRangeViewHolder(ItemDynamicFormDateRangeBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);

            uploadViewModel = new FormDateRangeViewModel(data, this);
            mBinding.setViewModel(uploadViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            String[] dateRange = null;
            if (data.getValue() != null && !data.getValue().isEmpty())
                dateRange = data.getValue().split(",");

            if (dateRange != null) {
                if (dateRange.length > 0) {
                    mBinding.tvDateRange1.setText(dateRange[0]);
                    if (dateRange.length > 1)
                        mBinding.tvDateRange2.setText(dateRange[1]);
                }
            }

            if (!isEditable) {
                mBinding.tvDateRange1.setEnabled(false);
                mBinding.tvDateRange2.setEnabled(false);


            }
        }

        @Override
        public void dateViewClick(@NotNull View view) {
            // Get Current Date
            Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
//            int mHour = c.get(Calendar.HOUR_OF_DAY);
//            int mMin = c.get(Calendar.MINUTE);
            CommonUtils.openDatePicker(context, mYear, mMonth, mDay,
                    c.getTimeInMillis(), 0, (view1, year, monthOfYear, dayOfMonth) -> {
                        //String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        int id = view.getId();
                        if (id == R.id.tvDateRange1) {
                            uploadViewModel.getDate1().set(DateTimeUtil.getParsedDate(cal.getTimeInMillis()));
                            formDataList.get(getAdapterPosition()).setMaxRange(cal.getTimeInMillis());
                        } else if (id == R.id.tvDateRange2) {
                            uploadViewModel.getDate2().set(DateTimeUtil.getParsedDate(cal.getTimeInMillis()));
                            formDataList.get(getAdapterPosition()).setMinRange(cal.getTimeInMillis());
                        }
                    });

        }
    }

    /**
     * Class used to pick date
     */
    private class DateViewHolder extends BaseViewHolder
            implements FormDateViewModel.DateListener {
        ItemDynamicFormDateBinding mBinding;
        FormDateViewModel emptyItemViewModel;
        int mYear, mMonth, mDay;

        DateViewHolder(ItemDynamicFormDateBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);

            emptyItemViewModel = new FormDateViewModel(data, this);
            mBinding.setViewModel(emptyItemViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (data.getValue() != null && !data.getValue().isEmpty())
                mBinding.date.setText(data.getValue());

            if (!isEditable) {
                mBinding.date.setEnabled(false);

            }

        }

        @Override
        public void onDateClick() {
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            CommonUtils.openDatePicker(context, mYear, mMonth, mDay,
                    0, 0, (view, year, monthOfYear, dayOfMonth) -> {
//                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        formDataList.get(getAdapterPosition()).setMaxRange(c.getTimeInMillis());
                        emptyItemViewModel.getDate().set(DateTimeUtil.getParsedDate(c.getTimeInMillis()));

                    });
        }
    }

    /**
     * Class used to handle the dropdown view with number of items as a dropdown.
     */
    private class ConditionalDropdownViewHolder extends BaseViewHolder {
        ItemDynamicFormCdtnlDdownBinding mBinding;

        ConditionalDropdownViewHolder(ItemDynamicFormCdtnlDdownBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);

            //string list for dropdown
            List<String> stringList = new ArrayList<>();
            //get data from form data
            Map<String, FormData> map = data.getDynamicSelectLookup();
            ArrayAdapter<String> adapter = null;
            if (map != null && map.size() > 0) {
                for (Map.Entry<String, FormData> entries : map.entrySet()) {
                    String key = entries.getKey();
                    stringList.add(key);
                }

                //set adapter here
                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList) {

                    @NotNull
                    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);

                        return v;
                    }


                    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        //v.setBackgroundColor(Color.GREEN);

                        return v;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBinding.spinner.setAdapter(adapter);
                // bind listener here.
                mBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (-1 != getAdapterPosition()) {
                            //Log.e("Condit. Spinner", "Main spinner onItemSelected(): " + position + "," + getAdapterPosition());
                            FormData formData = formDataList.get(getAdapterPosition());
                            //set the selected item's position
                            formData.setFormItemPosition(position);
                            //get the title of selected item
                            String title = (String) parent.getSelectedItem();
                            //set the selected item's title as key
                            formData.setFormItemKey(title);
                            //hide the layout if item is selected to refresh the view
                            mBinding.lLayoutConditional.setVisibility(View.GONE);

                            Map<String, FormData> map = formData.getDynamicSelectLookup();
                            if (map != null && map.size() > 0) {
                                for (Map.Entry<String, FormData> entries : map.entrySet()) {
//                                    String key = entries.getKey();
                                    FormData value = entries.getValue();
                                    //clear all the saved values till last child.
                                    clearSavedKeysForChild(value);
                                }
                            }
                            //create conditional dropdown
                            createConditionalDropDown(formData, mBinding.lLayoutConditional);
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                Log.e("CDDVH", "item position: " + data.getFormItemPosition());
                //set the selected position of spinner
                if (data.getFormItemPosition() != -1) {
                    mBinding.spinner.setSelection(data.getFormItemPosition());
                } else {
                    //set the first item for first time.
                    data.setFormItemPosition(0);
                    mBinding.spinner.setSelection(0);
                    data.setFormItemKey((String) mBinding.spinner.getSelectedItem());
                }
            }
            FormConditionalDDViewModel conditionalDDViewModel = new FormConditionalDDViewModel(data);
            mBinding.setViewModel(conditionalDDViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            int pos = 0;
            try {
                if (data.getValue() != null && !data.getValue().isEmpty())
                    pos = adapter.getPosition(data.getValue());
            } catch (Exception e) {
            }

            mBinding.spinner.setSelection(pos);

            if (!isEditable) {
                mBinding.spinner.setEnabled(false);

            }
        }

        /**
         * When user select the other value of this dropdown then all
         * the selected values of their child will be reset recursively
         * till last child.
         *
         * @param formData data that is going to be clear.
         */
        private void clearSavedKeysForChild(FormData formData) {
            if (formData != null) {
                //if type is dropdown static then clear key,position
                // and again call this function recursively to clear next item.
                if (formData.getType() == DataType.CONDITIONAL_DROPDOWN_STATIC) {
                    if (formData.getDynamicSelectLookup() != null && formData.getDynamicSelectLookup().size() > 0) {
                        Map<String, FormData> map = formData.getDynamicSelectLookup();
                        if (map != null && map.size() > 0) {
                            for (Map.Entry<String, FormData> entries : map.entrySet()) {
//                                    String key = entries.getKey();
                                FormData value = entries.getValue();
                                //clear all the saved values till last child.
                                clearSavedKeysForChild(value);
                            }
                        }
                        //call again for next item to clear
                        //clearSavedKeysForChild(formData.getDynamicSelectLookup().get(formData.getFormItemKey()));
                    }
                    //make key null
                    formData.setFormItemKey(null);
                    //make position to null
                    formData.setFormItemPosition(-1);
                }/* else if (formData.getType() == DataType.DROPDOWN) {

                }*/
            }
        }

        /**
         * Method used to create the dynamic dropdown on selection
         *
         * @param data               data that needs to check
         * @param lLayoutConditional view in which we want to add other view upto level x.
         */
        private void createConditionalDropDown(FormData data, LinearLayout lLayoutConditional) {
//            if (data.getDynamicSelectLookup() != null && data.getDynamicSelectLookup().size() > 0) {
            lLayoutConditional.setVisibility(View.VISIBLE);
            lLayoutConditional.removeAllViews();
            //int selectedPosition = data.getFormItemPosition();
            String selectedItem = data.getFormItemKey();
            FormData selectedFormData = data.getDynamicSelectLookup().get(selectedItem);
            if (selectedFormData != null && selectedFormData.getType() != null) {
                if (selectedFormData.getType() == DataType.DROPDOWN &&
                        selectedFormData.getWidgetData() != null &&
                        selectedFormData.getWidgetData().size() > 0) {

                    // Creating a new LinearLayout
                    LinearLayout parent = new LinearLayout(context);
                    // change the code above into
                    LinearLayout.LayoutParams layoutParams = new LinearLayout
                            .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    //set padding to the view
                    parent.setPadding(5, 5, 5, 5);
                    // set orientation of parent view
                    parent.setOrientation(LinearLayout.VERTICAL);
                    // add height width of parent
                    parent.setLayoutParams(layoutParams);

                    //children of parent LinearLayout
                    TextView textViewTitle = new TextView(context);
                    textViewTitle.setText(selectedFormData.getTitle());
                    //set style of the text view
                    textViewTitle.setTextAppearance(context, R.style.TextViewStyle);
                    //set font family of a text view
                    Typeface typeface;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/campton_semi_bold.ttf");
                    } else {
                        typeface = ResourcesCompat.getFont(context, R.font.campton_semi_bold);
                    }
                    textViewTitle.setTypeface(typeface);
                    //set text size
                    textViewTitle.setTextSize(16);
                    //set text color
                    textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
                    //add first child of this parent
                    parent.addView(textViewTitle);

                    // Creating a new RelativeLayout (2nd Child of parent layout)
                    RelativeLayout relativeLayout = new RelativeLayout(context);
                    // create height in dp
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            40, context.getResources().getDisplayMetrics());

                    // Defining the RelativeLayout layout parameters. In this case I want
                    // to add width as MATCH parent and height 45 dp.
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, height);
                    //set margin of this layout
                    rlp.setMargins(0, 5, 0, 0);
                    //set params to relative layout
                    relativeLayout.setLayoutParams(rlp);
                    // set background
                    //  relativeLayout.setBackgroundResource(R.drawable.bg_spinner);
                    //set gravity
                    relativeLayout.setGravity(Gravity.CENTER_VERTICAL);

                    //create spinner
                    Spinner spinner = new Spinner(context);
                    // Defining the layout parameters of the Spinner
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    // Setting the parameters on the Spinner
                    spinner.setLayoutParams(lp);
                    //get the selected widget data
                    List<WidgetData> adapterItemList = selectedFormData.getWidgetData();
                    Log.e("createConditionalDropDown ", "spinner items: " + adapterItemList);
                    if (adapterItemList != null && adapterItemList.size() > 0) {

                        int selectedPosition = 0;
                        List<String> stringList = new ArrayList<>();
                        for (int i = 0; i < adapterItemList.size(); i++) {
                            stringList.add(adapterItemList.get(i).getTitle());
                            if (adapterItemList.get(i).getSelected()) {
                                selectedPosition = i;
                            }
                        }
                        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList) {

                            @NotNull
                            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);

                                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                                ((TextView) v).setTypeface(externalFont);

                                return v;
                            }


                            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                                View v = super.getDropDownView(position, convertView, parent);

                                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                                ((TextView) v).setTypeface(externalFont);
                                //v.setBackgroundColor(Color.GREEN);

                                return v;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        //add a listener
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (-1 != getAdapterPosition()) {
                                    //get the list position where we are going to change the value
//                                        FormData formData = formDataList.get(getAdapterPosition());
                                    //get the key that is stored when user select the item from main spinner
                                    if (selectedFormData.getFormItemKey() != null && selectedFormData.getDynamicSelectLookup() != null) {
                                        // get the data of selected item of main spinner
                                        FormData item = selectedFormData.getDynamicSelectLookup().get(selectedFormData.getFormItemKey());
                                        //if we have widget data associated with it then iterate and check which item is selected
                                        if (item != null && item.getWidgetData() != null && item.getWidgetData().size() > 0) {
                                            String title = (String) parent.getSelectedItem();
                                            for (int i = 0; i < item.getWidgetData().size(); i++) {
                                                boolean isTitleMatched = title.equalsIgnoreCase(item.getWidgetData().get(i).getTitle());
                                                if (isTitleMatched) {
                                                    Log.e("createCDD", " " + title + " title: " + item.getWidgetData().get(i).getTitle());
                                                    //add selected item into the data widgetDataList
                                                    item.setEnteredValue(item.getWidgetData().get(i).getValue());
                                                }
                                                //if title is matched with widgetDataList item then mark that title as true else false.
                                                item.getWidgetData().get(i).setSelected(isTitleMatched);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                        //for first item if nothing is selected mark first item as selected.
                        selectedFormData.setEnteredValue(adapterItemList.get(selectedPosition).getValue());
                        adapterItemList.get(selectedPosition).setSelected(true);
                        //set the selected position
                        spinner.setSelection(selectedPosition);
                        Log.e("CDD-----", selectedPosition + " <....Conditional DD=====" + adapterItemList.get(selectedPosition).getTitle());
                    }

                    // Adding the Spinner to the RelativeLayout as a child
                    relativeLayout.addView(spinner);
                    //add relative layout in parent.
                    parent.addView(relativeLayout);
                    // Setting the RelativeLayout as our content view
                    View v = new View(context);
                    v.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            4
                    ));
                    v.setBackgroundColor(Color.parseColor("#6B6B6B"));
                    parent.addView(v);
                    lLayoutConditional.addView(parent);
                } else if (selectedFormData.getType() == DataType.CONDITIONAL_DROPDOWN_STATIC &&
                        selectedFormData.getDynamicSelectLookup() != null &&
                        selectedFormData.getDynamicSelectLookup().size() > 0) {
                    final View view = LayoutInflater.from(context).inflate(R.layout.item_dynamic_form_cdtnl_ddown, lLayoutConditional, false);

                    TextView tvTitle = view.findViewById(R.id.tvTitle);
                    tvTitle.setText(selectedFormData.getTitle());
                    Spinner spinner = view.findViewById(R.id.spinner);
                    LinearLayout cdtlLayout = view.findViewById(R.id.lLayoutConditional);

                    //string list for dropdown
                    List<String> stringList = new ArrayList<>();
                    //get data from form data
                    Map<String, FormData> map = selectedFormData.getDynamicSelectLookup();
                    if (map != null && map.size() > 0) {
                        for (Map.Entry<String, FormData> entries : map.entrySet()) {
                            String key = entries.getKey();
                            stringList.add(key);
                        }
                        //set adapter here
                        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList) {

                            @NotNull
                            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);

                                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                                ((TextView) v).setTypeface(externalFont);

                                return v;
                            }


                            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                                View v = super.getDropDownView(position, convertView, parent);

                                Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                                ((TextView) v).setTypeface(externalFont);
                                //v.setBackgroundColor(Color.GREEN);

                                return v;
                            }
                        };
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        // bind listener here.
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (-1 != getAdapterPosition()) {
                                    //Log.e("Condit. Spinner", "Main spinner onItemSelected(): " + position + "," + getAdapterPosition() + ", " + selectedFormData);
                                    //set the selected item's position
                                    selectedFormData.setFormItemPosition(position);
                                    //get the title of selected item
                                    String title = (String) parent.getSelectedItem();
                                    //set the selected item's title as key
                                    selectedFormData.setFormItemKey(title);
                                    //hide the layout if item is selected to refresh the view
                                    cdtlLayout.setVisibility(View.GONE);

                                    Map<String, FormData> map = selectedFormData.getDynamicSelectLookup();
                                    if (map != null && map.size() > 0) {
                                        for (Map.Entry<String, FormData> entries : map.entrySet()) {
                                            FormData value = entries.getValue();
                                            //clear all the saved values till last child.
                                            clearSavedKeysForChild(value);
                                        }
                                    }

                                    //create conditional dropdown
                                    createConditionalDropDown(selectedFormData, cdtlLayout);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        Log.e("CDDVH", "item position: " + data.getFormItemPosition());
                        //set the selected position of spinner
                        if (data.getFormItemPosition() != -1) {
                            spinner.setSelection(data.getFormItemPosition());
                        } else {
                            //set the first item for first time.
                            data.setFormItemPosition(0);
                            spinner.setSelection(0);
                            data.setFormItemKey((String) spinner.getSelectedItem());
                        }
                    }
                    // Setting the view as our content view
                    lLayoutConditional.addView(view);
                }
            }
        }
//        }
    }

    /**
     * Class used to handle the dropdown view with get items from the API and
     * set them as a dropdown.
     */
    private class ConditionalDropdownAPIViewHolder extends BaseViewHolder {
        ItemDynamicFormCdtnlDdownBinding mBinding;

        ConditionalDropdownAPIViewHolder(ItemDynamicFormCdtnlDdownBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);

            FormConditionalDDViewModel conditionalDDViewModel = new FormConditionalDDViewModel(data);
            mBinding.setViewModel(conditionalDDViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
        }
    }

    /**
     * Class used to handle the dropdown view with number of items as a dropdown.
     */
    private class DropdownViewHolder extends BaseViewHolder
            implements AdapterView.OnItemSelectedListener {
        ItemDynamicFormDropdownBinding mBinding;
        int selectedPosition = 0;
        boolean isEmbded = true;

        DropdownViewHolder(ItemDynamicFormDropdownBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            // bind listener here.
            // mBinding.spinner.setOnItemSelectedListener(this);
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);
            List<WidgetData> adapterItemList = data.getWidgetData();

            ArrayAdapter<String> adapter = null;
            List<String> stringList = new ArrayList<>();
            if (adapterItemList != null && adapterItemList.size() > 0) {

                for (int i = 0; i < adapterItemList.size(); i++) {
                    stringList.add(adapterItemList.get(i).getTitle());
                    if (adapterItemList.get(i).getSelected()) {
                        selectedPosition = i;
                    }
                }
                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList) {

                    @NotNull
                    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);

                        return v;
                    }


                    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView, parent);
                        Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        //v.setBackgroundColor(Color.GREEN);

                        return v;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mBinding.spinner.setAdapter(adapter);
                //add a listener

                mBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (-1 != getAdapterPosition()) {
                            // get the data of selected item of main spinner
                            FormData item = formDataList.get(getAdapterPosition());

                            //if we have widget data associated with it then iterate and check which item is selected
                            if (item != null && item.getWidgetData() != null && item.getWidgetData().size() > 0) {
                                String title = (String) parent.getSelectedItem();
                                for (int i = 0; i < item.getWidgetData().size(); i++) {
                                    boolean isTitleMatched = title.equalsIgnoreCase(item.getWidgetData().get(i).getTitle());
                                    if (isTitleMatched) {
                                        //add selected item into the data widgetDataList
                                        item.setEnteredValue(item.getWidgetData().get(i).getValue());
                                        item.setValue(item.getWidgetData().get(i).getValue());
                                        if (item.getWidgetData().get(i).getTarget() != null && !item.getWidgetData().get(i).getTarget().isEmpty()) {
                                            String targetId = item.getWidgetData().get(i).getTarget();

                                            GetDynamicFormListById getDyna = new GetDynamicFormListById();
                                            ArrayList<FormData> mlist = getDyna.getDynamicFormListById(targetId);
                                            childDynamicAdapter = new ChildDynamicAdapter(mlist);
                                            childDynamicAdapter.setFormId(targetId);
                                            childDynamicAdapter.setPreferencesHelper(preferencesHelper);
                                            if (embdedFormdata != null && !embdedFormdata.isEmpty()) {
                                                List<TaskData> listData = embdedFormdata.get(targetId);
                                                if (listData != null && !listData.isEmpty()) {
                                                    for (int index = 0; index < listData.size(); index++) {

                                                        String key = listData.get(index).getKey();
                                                        String value = listData.get(index).getValue();
                                                        for (int j = 0; j < mlist.size(); j++) {
                                                            if (key.equals(mlist.get(j).getName())) {
                                                                mlist.get(j).setValue(value);
                                                                mlist.get(j).setEnteredValue(value);
                                                            }

                                                        }


                                                    }

                                                }
                                            }

                                            mBinding.rvInnerDynamicForms.setVisibility(View.VISIBLE);

                                            mBinding.rvInnerDynamicForms.setAdapter(childDynamicAdapter);
                                            childDynamicAdapter.notifyDataSetChanged();
                                            childDynamicAdapter.setAdapterListener(adapterListener);
                                            childDynamicAdapter.setIsEditable(isEditable, httpmanager);
                                            childDynamicAdapter.setInnerFormData(embdedFormdata);

                                            formDataList.get(getAdapterPosition()).getWidgetData().get(i).setFormDataList(childDynamicAdapter.formDataList);
//                                            mBinding.spinner.setVisibility(View.GONE);
//                                            mBinding.tvTitle.setVisibility(View.GONE);
                                            // notifyItemInserted(getAdapterPosition());
                                        } else {
                                            mBinding.rvInnerDynamicForms.setVisibility(View.GONE);
                                            if (formDataList.get(getAdapterPosition()).getWidgetData().get(i) != null)
                                                formDataList.get(getAdapterPosition()).getWidgetData().get(i).setFormDataList(null);
                                        }
                                    } else {
                                        if (formDataList.get(getAdapterPosition()).getWidgetData().get(i) != null)
                                            formDataList.get(getAdapterPosition()).getWidgetData().get(i).setFormDataList(null);

                                    }
                                    //if title is matched with widgetDataList item then mark that title as true else false.
                                    item.getWidgetData().get(i).setSelected(isTitleMatched);
                                    formDataList.get(getAdapterPosition()).getWidgetData().get(i).setSelected(isTitleMatched);
                                    // isEstimtedWidegtFilled.setValue(true);
                                }

                                mapData.put(item.getName(), String.valueOf(item.getWidgetData().get(position).getWeight()));

                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //for first item if nothing is selected mark first item as selected.
                data.setEnteredValue(adapterItemList.get(selectedPosition).getValue());
                adapterItemList.get(selectedPosition).setSelected(true);
                //set the selected position
                // mBinding.spinner.setSelection(selectedPosition);
            }
            FormDropdownViewModel dropdownViewModel = new FormDropdownViewModel(data);
            mBinding.setViewModel(dropdownViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            if (adapter != null) {
                if (data.getValue() != null && !data.getValue().isEmpty()) {
                    mBinding.spinner.setSelection(adapter.getPosition(data.getValue()));


                }

            }

            if (!isEditable) {
                mBinding.spinner.setEnabled(false);
            }
            mBinding.executePendingBindings();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (-1 != getAdapterPosition()) {
                FormData formData = formDataList.get(getAdapterPosition());
                String title = (String) parent.getSelectedItem();
//                Log.e("DropDown: ", widgetData.getValue() + " <------> " + widgetData.getTitle());

                List<WidgetData> itemList = formData.getWidgetData();
                if (itemList != null) {
                    for (int i = 0; i < itemList.size(); i++) {
                        try {
                            boolean isTitleMatched = title.equalsIgnoreCase(itemList.get(i).getTitle());
                            if (isTitleMatched) {
                                //add selected item into the data widgetDataList
                                formData.setEnteredValue(itemList.get(i).getValue());
                            }
                            //if title is matched with widgetDataList item then mark that title as true else false.
                            itemList.get(i).setSelected(isTitleMatched);
                        } catch (NullPointerException e) {
                            Log.e("DropDown", "Title is null");
                        }
                    }
                }
//                notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    /**
     * Class used to handle the dropdown view with number of items as a dropdown.
     */
    private class DropdownAPIViewHolder extends BaseViewHolder
            implements AdapterView.OnItemSelectedListener {
        ItemDynamicFormDropdownBinding mBinding;

        DropdownAPIViewHolder(ItemDynamicFormDropdownBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            // bind listener here.
            mBinding.spinner.setOnItemSelectedListener(this);
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);

            ArrayAdapter<String> adapter = null;
            if (data.getApiMap() == null) {
                String rollId = null;
                if (data.getRoles() != null && !data.getRoles().isEmpty()) {
                    rollId = CommonUtils.getCommaSeparatedList(data.getRoles());
                }
                //hit API
                adapterListener.getDropdownItems(position, data.getActionConfig().getTarget(), rollId);
            } else {
                //get data from form data
                Map<String, String> map = data.getApiMap();
                if (map != null && map.size() > 0) {
                    //string list for dropdown
                    List<String> stringList = new ArrayList<>();
                    for (Map.Entry<String, String> entries : map.entrySet()) {
                        String value = entries.getValue();
                        stringList.add(value);
                    }

                    //set adapter here
                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList) {

                        @NotNull
                        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);

                            Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                            ((TextView) v).setTypeface(externalFont);

                            return v;
                        }


                        public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);

                            Typeface externalFont = Typeface.createFromAsset(parent.getContext().getAssets(), "fonts/campton_book.ttf");
                            ((TextView) v).setTypeface(externalFont);
                            //v.setBackgroundColor(Color.GREEN);

                            return v;
                        }
                    };
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mBinding.spinner.setAdapter(adapter);
                    // bind listener here.
                    mBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (-1 != getAdapterPosition()) {
                                //Log.e("Condit. Spinner", "Main spinner onItemSelected(): " + position + "," + getAdapterPosition());
                                FormData formData = formDataList.get(getAdapterPosition());
                                //set the selected item's position
                                formData.setFormItemPosition(position);
                                //get the title of selected item
                                String title = (String) parent.getSelectedItem();
                                for (Map.Entry<String, String> entries : map.entrySet()) {
                                    if (entries.getValue().equalsIgnoreCase(title)) {
                                        //set the selected item's title as key
                                        formData.setFormItemKey(entries.getKey());
                                        formData.setEnteredValue(entries.getKey());
                                        formData.setValue(entries.getValue());
                                        //once we get the value exit from the loop.
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    Log.e("DropdownAPIViewHolder", "item position: " + data.getFormItemPosition());
                    //set the selected position of spinner
                    if (data.getFormItemPosition() != -1) {
                        mBinding.spinner.setSelection(data.getFormItemPosition());
                    } else {
                        //set the first item for first time.
                        data.setFormItemPosition(0);
                        mBinding.spinner.setSelection(0);
                        data.setFormItemKey((String) mBinding.spinner.getSelectedItem());
                    }
                }
            }

            FormDropdownViewModel dropdownViewModel = new FormDropdownViewModel(data);
            mBinding.setViewModel(dropdownViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();

            if (data.getValue() != null && !data.getValue().isEmpty()) {
                if (adapter != null && adapter.getPosition(data.getValue()) != -1)
                    mBinding.spinner.setSelection(adapter.getPosition(data.getValue()));
            }


            if (!isEditable) {
                mBinding.spinner.setEnabled(false);
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (-1 != getAdapterPosition()) {
                FormData formData = formDataList.get(getAdapterPosition());
                String title = (String) parent.getSelectedItem();
//                Log.e("DropDown: ", widgetData.getValue() + " <------> " + widgetData.getTitle());

                List<WidgetData> itemList = formData.getWidgetData();
                if (itemList != null) {
                    for (int i = 0; i < itemList.size(); i++) {
                        try {
                            boolean isTitleMatched = title.equalsIgnoreCase(itemList.get(i).getTitle());
                            if (isTitleMatched) {
                                //add selected item into the data widgetDataList
                                formData.setEnteredValue(itemList.get(i).getValue());
                            }
                            //if title is matched with widgetDataList item then mark that title as true else false.
                            itemList.get(i).setSelected(isTitleMatched);
                        } catch (NullPointerException e) {
                            Log.e("DropDown", "Title is null");
                        }
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    /**
     * Class is used to show the radio buttons by adding into the radio group.
     */
    private class RadioViewHolder extends BaseViewHolder {
        ItemDynamicFormRadioBinding mBinding;
        FormData data;
        FormRadioViewModel radioViewModel;

        RadioViewHolder(ItemDynamicFormRadioBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            data = formDataList.get(position);
            //get the data from the widgetDataList.
            List<WidgetData> adapterItemList = data.getWidgetData();

            //check if item contains any item
            if (adapterItemList != null && adapterItemList.size() > 0) {
                //iterate through the widgetDataList and create radio buttons and add
                // into the radio group.
                mBinding.radioGroup.removeAllViews();
                for (int i = 0; i < adapterItemList.size(); i++) {
                    WidgetData item = adapterItemList.get(i);
                    RadioButton radioButton = new RadioButton(context);
                    //set font family of a text view
                    Typeface typeface;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/campton_book.ttf");
                    } else {
                        typeface = ResourcesCompat.getFont(context, R.font.campton_book);
                    }
                    radioButton.setTypeface(typeface);
                    radioButton.setTag(i);
                    radioButton.setButtonDrawable(R.drawable.custom_radio_button);
                    radioButton.setId(View.generateViewId());

                    radioButton.setPadding(radioButton.getPaddingLeft() + 20,
                            radioButton.getPaddingTop(),
                            radioButton.getPaddingRight(),
                            radioButton.getPaddingBottom());
                    radioButton.setText(item.getTitle());
                    radioButton.setChecked(item.getSelected());
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow_punch_btn)));
                    }
                    radioButton.setHighlightColor(context.getResources().getColor(R.color.yellow_punch_btn));*/
                    if (item.getSelected()) {
                        //add this value as entered value
                        data.setEnteredValue(item.getValue());
                        data.setValue(item.getValue());
                    }

                    radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (-1 != getAdapterPosition()) {
                            FormData formData = formDataList.get(getAdapterPosition());
                            //add selected item into the data widgetDataList

                            if (isChecked) {
                                formData.setEnteredValue(formData.getWidgetData().get((int) buttonView.getTag()).getValue());
                                formDataList.get(getAdapterPosition()).setEnteredValue(formData.getWidgetData().get((int) buttonView.getTag()).getValue());
                                formDataList.get(getAdapterPosition()).setValue(formData.getWidgetData().get((int) buttonView.getTag()).getValue());
                            }

                            List<WidgetData> itemList = formData.getWidgetData();
                            if (itemList != null) {
                                for (int j = 0; j < itemList.size(); j++) {
                                    try {
                                        //if position is matched with widgetDataList item then mark that item as true else false.
                                        formData.getWidgetData().get(j).setSelected(j == (int) buttonView.getTag());

                                    } catch (NullPointerException e) {
                                        Log.e("Radio Button", "Title is null");
                                    }
                                }
                            }
//                            ((DynamicFormActivity) context).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    notifyDataSetChanged();
//                                }
//                            });


                        }
                    });
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(0,0,0,10);
                    radioButton.setLayoutParams(params);
                    mBinding.radioGroup.addView(radioButton);
                    if (!isEditable) {
                        radioButton.setEnabled(false);
                    }
                    if (data.getValue() != null && !data.getValue().isEmpty()) {
                        if (radioButton.getText().toString().equalsIgnoreCase(data.getValue())) {
                            radioButton.setChecked(true);
                        } else
                            radioButton.setChecked(false);
                    }
                }
            }
            radioViewModel = new FormRadioViewModel(data);
            mBinding.setViewModel(radioViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.


            if (!isEditable) {
                mBinding.radioGroup.setEnabled(false);
            }
            mBinding.executePendingBindings();
        }
    }

    /**
     * Class used to show the checkbox.
     */
    private class CheckBoxViewHolder extends BaseViewHolder {
        ItemDynamicFormCheckboxBinding mBinding;
        FormCheckBoxViewModel checkBoxViewModel;

        CheckBoxViewHolder(ItemDynamicFormCheckboxBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);
            //get the data from the widgetDataList.
            List<WidgetData> adapterItemList = data.getWidgetData();
            //check if item contains any item
            if (adapterItemList != null && adapterItemList.size() > 0) {
                //set data into the enter value field
                setSelectedValue(data);

                //iterate through the widgetDataList and create radio buttons and add
                // into the radio group.
                mBinding.linearLayout.removeAllViews();
                for (int i = 0; i < adapterItemList.size(); i++) {
                    WidgetData item = adapterItemList.get(i);
                    CheckBox checkBox = new CheckBox(context);
                    //set font family of a text view
                    Typeface typeface;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/campton_book.ttf");
                    } else {
                        typeface = ResourcesCompat.getFont(context, R.font.campton_book);
                    }
                    checkBox.setTypeface(typeface);
                    checkBox.setTag(i);
                    checkBox.setButtonDrawable(R.drawable.check_box_new);
                    checkBox.setId(View.generateViewId());
                    checkBox.setPadding(checkBox.getPaddingLeft() + 20,
                            checkBox.getPaddingTop(),
                            checkBox.getPaddingRight(),
                            checkBox.getPaddingBottom());
                    checkBox.setText(item.getTitle());
                    checkBox.setChecked(item.getSelected());
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow_punch_btn)));
                    }
                    checkBox.setHighlightColor(context.getResources().getColor(R.color.yellow_punch_btn));*/
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (-1 != getAdapterPosition()) {
                            FormData formData = formDataList.get(getAdapterPosition());
                            int checkboxPosition = (int) buttonView.getTag();
                            formData.getWidgetData().get(checkboxPosition).setSelected(isChecked);
                            //set data into the enter value field
                            //  data.setEnteredValue(checkBox.getText().toString().toUpperCase());
                            item.setSelected(isChecked);
                            setSelectedValue(formData);
                            // notifyDataSetChanged();
                        }
                    });
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                    params.setMargins(0,0,0,10);
                    checkBox.setLayoutParams(params);
                    mBinding.linearLayout.addView(checkBox);
                    if (data.getValue() != null && !data.getValue().isEmpty()) {
                        if (data.getValue().contains(",")) {
                            String valueList[] = data.getValue().split(",");
                            for (int j = 0; j < valueList.length; j++) {
                                if (checkBox.getText().equals(valueList[j])) {
                                    checkBox.setChecked(true);
                                }
                            }
                        } else {
                            if (checkBox.getText().equals(data.getValue())) {
                                checkBox.setChecked(true);
                            } else
                                checkBox.setChecked(false);
                        }


                    }
                    if (!isEditable) {
                        checkBox.setEnabled(false);
                    }
                }
            }
            checkBoxViewModel = new FormCheckBoxViewModel(data);
            mBinding.setViewModel(checkBoxViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
        }

        /**
         * Method is used to set the checked values into the entered value field.
         *
         * @param formData data item where i need to change the value.
         */
        private void setSelectedValue(FormData formData) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < formData.getWidgetData().size(); j++) {
                if (formData.getWidgetData().get(j).getSelected()) {
                    if (j != 0) {
                        sb.append(",");
                    }
                    sb.append(formData.getWidgetData().get(j).getValue());
                }
            }
//            Log.e("setSelectedValue", formData.getWidgetData() + " <----> " + sb);
            formData.setEnteredValue(sb.toString());
            formData.setValue(sb.toString());
        }
    }

    /**
     * Class used to handle the dropdown view with number of items as a multi-select dialog.
     */
    private class MultiSelectViewHolder extends BaseViewHolder
            implements MultiSelectSpinner.MultiSpinnerListener {
        ItemDynamicFormMultiSelectBinding mBinding;
        FormData data;

        MultiSelectViewHolder(ItemDynamicFormMultiSelectBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            data = formDataList.get(position);
            List<WidgetData> adapterItemList = data.getWidgetData();
            String multiSpinnerTexts = "";

            if (adapterItemList != null)
                for (WidgetData data : adapterItemList) {
                    multiSpinnerTexts += data.getTitle();

                }
            Log.e("onBind", "widgetDataList of the items: " + adapterItemList);
            if (adapterItemList != null && adapterItemList.size() > 0) {

                List<String> strings = new ArrayList<>();
                // set values
                setValues(strings, adapterItemList);
                //set the spinner as multi select
                mBinding.spinnerMultiSelect.setItems(data.getTitle(), strings, data.getSelectedValues(), this);

            }
            FormMultiSelectViewModel multiSelectViewModel = new FormMultiSelectViewModel(data);
            mBinding.setViewModel(multiSelectViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
//            List<String> items = new ArrayList<>();
//            if (data.getValue() != null && !data.getValue().isEmpty()) {
//                items = new ArrayList<String>(Arrays.asList(data.getValue().split(",")));
//            }
//
//            mBinding.spinnerMultiSelect.setItems("Items", items, multiSpinnerTexts, this);

            if (!isEditable) {
                mBinding.spinnerMultiSelect.setEnabled(false);
            }
        }

        /**
         * Method used to get the values form the adapterItem
         * list and create a list of type string and create a builder
         * of selected items from the list.
         *
         * @param strings         list of string list
         * @param adapterItemList adapter item list.
         */
        private void setValues(@Nullable List<String> strings, @NonNull List<WidgetData> adapterItemList) {
            StringBuilder sbTitle = new StringBuilder();
            StringBuilder sbValue = new StringBuilder();
            for (int i = 0; i < adapterItemList.size(); i++) {
                if (strings != null) {
                    strings.add(adapterItemList.get(i).getTitle());
                }
                if (adapterItemList.get(i).getSelected()) {
                    if (sbTitle.length() != 0) {
                        sbTitle.append(",");
                        sbValue.append(",");
                    }
                    sbTitle.append(adapterItemList.get(i).getTitle());
                    sbValue.append(adapterItemList.get(i).getValue());
                }
            }
//            Log.e("setValues", "title: " + sbTitle);
//            Log.e("setValues", "values: " + sbValue);

            data.setSelectedValues(sbTitle.toString());
            data.setEnteredValue(sbValue.toString());
        }

        @Override
        public void onItemsSelected(boolean[] selected) {
            List<WidgetData> itemList = data.getWidgetData();
            if (itemList != null) {
                for (int i = 0; i < selected.length; i++) {
                    itemList.get(i).setSelected(selected[i]);
                }
                setValues(null, itemList);
            }
            //notify only this position
            notifyItemChanged(getAdapterPosition());
        }
    }

    /**
     * Class used to show label as a view
     */
    private class LabelViewHolder extends BaseViewHolder {

        private ItemDynamicFormLabelBinding mBinding;

        LabelViewHolder(ItemDynamicFormLabelBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            FormData formData = formDataList.get(position);
            FormLabelViewModel labelViewModel = new FormLabelViewModel(formData);
            mBinding.setViewModel(labelViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
//            formDataList.get(position).setEnteredValue("Arbitrary");
            if (formData.getValue() != null && !formData.getValue().isEmpty())
                mBinding.label.setText(formData.getValue());

            if (!isEditable) {
                mBinding.label.setEnabled(false);
            }
        }
    }

    /**
     * Class used to pick time only.
     */
    private class TimeViewHolder extends BaseViewHolder implements
            FormTimeViewModel.TimeListener {

        ItemDynamicFormTimeBinding mBinding;
        int mHour;
        int mMinute;
        FormTimeViewModel dateTimeViewModel;

        TimeViewHolder(ItemDynamicFormTimeBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }

        @Override
        public void onBind(int position) {
            FormData data = formDataList.get(position);
            dateTimeViewModel = new FormTimeViewModel(data, this);
            mBinding.setViewModel(dateTimeViewModel);
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            if (data.getValue() != null && !data.getValue().isEmpty())
                mBinding.time.setText(data.getValue());

            if (!isEditable) {
                mBinding.time.setEnabled(false);
            }
        }

        @Override
        public void timeClick() {
            Calendar calendar = Calendar.getInstance();
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
            //open time picker
            CommonUtils.openTimePicker(context, mHour, mMinute,
                    (view, hourOfDay, minute) -> {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, 0);
                        dateTimeViewModel.getTime().set(DateTimeUtil.getParsedTime(cal.getTimeInMillis()));
                        formDataList.get(getAdapterPosition()).setMaxRange(cal.getTimeInMillis());
                    });
        }
    }

    /**
     * If hashMap is empty show empty view
     */
    private class EmptyViewHolder extends BaseViewHolder
            implements FormEmptyItemViewModel.ClickListener {

        private ItemDynamicFormEmptyViewBinding mBinding;

        EmptyViewHolder(ItemDynamicFormEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            FormEmptyItemViewModel emptyItemViewModel = new FormEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }
    }

    /**
     * If view type is not handled then show this view
     */
    private class UnknownViewHolder extends BaseViewHolder {

        UnknownViewHolder(ItemDynamicFormUnknownBinding unknownBinding) {
            super(unknownBinding.getRoot());
        }

        @Override
        public void onBind(int position) {

        }
    }

    /**
     * This is for otp handling
     */
    private class OtpViewHolder extends BaseViewHolder {
        ItemDynamicFormVerifyOtpBinding otpBinding;

        public OtpViewHolder(ItemDynamicFormVerifyOtpBinding otpBinding) {
            super(otpBinding.getRoot());
            this.otpBinding = otpBinding;
        }

        @Override
        public void onBind(int position) {
            FormData formData = formDataList.get(position);
            FormVerifyMobileViewModel viewModel = new FormVerifyMobileViewModel(formData);
            otpBinding.setViewModel(viewModel);
            isSubmitButtonEnable.setValue(false);

            otpBinding.btnVeriFy.setOnClickListener(view -> {

                String mobile = otpBinding.etMobile.getText().toString().trim();
                String otp = otpBinding.edOtp.getText().toString().trim();

                if (CommonUtils.isViewNullOrEmpty(mobile)) {
                    TrackiToast.Message.showShort(context, "Please Enter Mobile Number");
                } else if (!CommonUtils.isMobileValid(mobile)) {
                    TrackiToast.Message.showShort(context, context.getString(R.string.invalid_mobile));
                } else if (CommonUtils.isViewNullOrEmpty(otp)) {
                    TrackiToast.Message.showShort(context, "Please Enter Otp");
                } else if (otp.length() > formData.getMaxLength()) {
                    TrackiToast.Message.showShort(context, "Maximum " + formData.getMaxLength() + " digit of otp  is allowed");
                } else {
                    formData.setEnteredValue(otp);
                    formDataList.get(getAdapterPosition()).setEnteredValue(otp);
                    adapterListener.onVeriFyOtpButtonClick(formData, mobile);
                }
            });


        }
    }

    /**
     * This is for otp handling
     */
    private class GeoViewHolder extends BaseViewHolder {
        ItemDynamicFormLocationBinding locationBinding;

        public GeoViewHolder(ItemDynamicFormLocationBinding locationBinding) {
            super(locationBinding.getRoot());
            this.locationBinding = locationBinding;
        }

        @Override
        public void onBind(int position) {
            FormData formData = formDataList.get(position);
            FormLocationViewModel viewModel = new FormLocationViewModel(formData);
            locationBinding.setViewModel(viewModel);

            if (formData.getValue() != null && !formData.getValue().isEmpty()) {
//                JSONConverter jsonConverter = new JSONConverter();
//                HubLocation hubLocation = (HubLocation) jsonConverter.jsonToObject(formData.getValue(), HubLocation.class);
                //locationBinding.edLocation.setText(hubLocation.getAddress());
                locationBinding.edLocation.setText(formData.getValue());
            } else {
                TrackThat.getCurrentLocation(new TrackThatCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse successResponse) {
                        TrackthatLocation loc = (TrackthatLocation) successResponse.getResponseObject();
                        LatLng currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                        String address = CommonUtils.getAddress(context, currentLocation);
                        formDataList.get(position).setValue(address);
                        formDataList.get(position).setEnteredValue(address);
                        locationBinding.edLocation.setText(address);
                        Log.e(TAG, "getCurrentLocation(): onSuccess: " + currentLocation);
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        Log.e(TAG, "onError: " + errorResponse.getErrorMessage());
                    }
                });
            }
            locationBinding.edLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapterListener != null)
                        adapterListener.openPlacePicker(getAdapterPosition(), formData);

                }
            });


        }
    }

    /**
     * This is for otp handling
     */
    private class ImageViewHolder extends BaseViewHolder {
        ItemDynamicFormImageBinding imageBinding;

        public ImageViewHolder(ItemDynamicFormImageBinding imageBinding) {
            super(imageBinding.getRoot());
            this.imageBinding = imageBinding;
        }

        @Override
        public void onBind(int position) {
            FormData formData = formDataList.get(position);
            FormImageViewModel viewModel = new FormImageViewModel(formData);
            imageBinding.setViewModel(viewModel);

//            if (formData.getValue() != null && !formData.getValue().isEmpty()) {
//                GlideApp.with(context).load(formData.getValue()).placeholder(R.drawable.ic_picture).into(imageBinding.ivImage);
//                imageBinding.ivImage.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        openDialougShowImage(formData.getValue());
//                    }
//                });
//            }
            if (formData.getActionConfig() != null) {
//                "action": "API",
//                        "target": "FIELD_DATA_AUTO_POPULATE",
//                        "skipTarget": false,
//                        "formula": "TASK|0|jhhjh-jkj22-2k883sbbb|estimated_amount"
                CommonUtils.showLogMessage("e", "hit aata", "hit task");
                if (formData.getActionConfig().getAction() != null && formData.getActionConfig().getAction() == Type.API) {
                    if (formData.getActionConfig().getTarget() != null && formData.getActionConfig().getTarget().equals(ApiType.FIELD_DATA_AUTO_POPULATE.name())) {
                        Api api = TrackiApplication.getApiMap().get(ApiType.FIELD_DATA_AUTO_POPULATE);
                        if (api != null && formData.getActionConfig().getFormula() != null) {
                            if (NetworkUtils.isNetworkConnected(context))
                                new LoadDataAsyncTask(api, formData.getActionConfig().getFormula(), imageBinding.ivImage, getAdapterPosition()).execute();
                        }
                    }
                }
            }

        }
    }
}

