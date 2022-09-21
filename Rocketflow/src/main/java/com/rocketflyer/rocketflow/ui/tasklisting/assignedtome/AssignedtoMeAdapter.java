package com.rocketflyer.rocketflow.ui.tasklisting.assignedtome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tracki.R;
import com.tracki.data.local.prefs.PreferencesHelper;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.Task;
import com.tracki.databinding.ItemAssignedToMeEmptyViewBinding;
import com.tracki.databinding.LayoutRowAssigndToMeBinding;
import com.tracki.ui.base.BaseViewHolder;
import com.tracki.ui.taskdetails.TaskDetailActivity;
import com.tracki.ui.tasklisting.TaskClickListener;
import com.tracki.ui.tasklisting.TaskComparator;
import com.tracki.ui.tasklisting.assignedtome.AssignedtoMeEmptyItemViewModel;
import com.tracki.ui.tasklisting.assignedtome.TaskAssignToMeViewModel;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by rahul on 5/10/18
 */
public class AssignedtoMeAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Task> mResponseList;
    private TaskClickListener mListener;
    private Context context;
    private PreferencesHelper preferencesHelper;
    private String reffranceLabel = null;
    private String categoryId = null;


    public void setReffLabel(String reffranceLabel) {
        this.reffranceLabel = reffranceLabel;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    AssignedtoMeAdapter(List<Task> responseList, PreferencesHelper preferencesHelper) {
        this.mResponseList = responseList;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public int getItemCount() {
        if (mResponseList != null && mResponseList.size() > 0) {
            return mResponseList.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mResponseList != null && !mResponseList.isEmpty()) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
//                ItemAssignedToMeViewBinding itemAssignedToMeViewBinding = ItemAssignedToMeViewBinding.inflate(LayoutInflater.from(parent.getContext()),
//                        parent, false);
                LayoutRowAssigndToMeBinding itemAssignedToMeViewBinding = LayoutRowAssigndToMeBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new AssignedtoMeViewHolder(itemAssignedToMeViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemAssignedToMeEmptyViewBinding emptyViewBinding = ItemAssignedToMeEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    public void addItems(List<Task> stringList) {
        //  clearItems();

        mResponseList.addAll(stringList);
        Set<Task> set = new TreeSet<>(new TaskComparator());
        set.addAll(mResponseList);
        mResponseList.clear();
        mResponseList.addAll(set);
        notifyDataSetChanged();
    }

    public Task getTaskByTaskId(String taskId) {
        if (mResponseList != null && !mResponseList.isEmpty()) {
            Task reqTask = new Task();
            reqTask.setTaskId(taskId);
            if (mResponseList.contains(reqTask)) {
                int position = mResponseList.indexOf(reqTask);
                if (position != -1) {
                    return mResponseList.get(position);
                }
            }
        }
        return null;
    }
   public List<Task> getList(){
       return mResponseList;
   }
    public void clearItems() {
        mResponseList.clear();
        notifyDataSetChanged();
    }

    public void setListener(TaskClickListener listener) {
        this.mListener = listener;
    }


    public class AssignedtoMeViewHolder extends BaseViewHolder implements TaskAssignToMeViewModel
            .AssignedtoMeItemViewModelListener {
        private LayoutRowAssigndToMeBinding mBinding;

        AssignedtoMeViewHolder(LayoutRowAssigndToMeBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Task task = mResponseList.get(position);
            TaskAssignToMeViewModel itemViewModel = new TaskAssignToMeViewModel(task,
                    this, context, preferencesHelper,categoryId);
            mBinding.setViewModel(itemViewModel);
            Boolean isVendorVisible=false;
            Boolean isPointOfContactVisible=false;
            Boolean isAssignedToVisible=false;
            if(categoryId!=null)
            {
                //START_LOCATION, END_LOCATION, SELECT_BUDDY, SELECT_CLIENT, START_TIME, END_TIME, POINT_OF_CONTACT, TASK_NAME,
                //        TASK_TYPE, DESCRIPTION, SELECT_FLEET, SELECT_CITY
                String startLocationLabel=CommonUtils.getAllowFieldLabelName("START_LOCATION",categoryId,preferencesHelper);
                String systemLocationLabel=CommonUtils.getAllowFieldLabelName("SYSTEM_LOCATION",categoryId,preferencesHelper);
                if(!startLocationLabel.isEmpty()){
                    mBinding.labelStartLocation.setText(startLocationLabel);
                }
                if(!systemLocationLabel.isEmpty()){
                    mBinding.labelStartLocation.setText(systemLocationLabel);
                }
                String taskId=CommonUtils.getAllowFieldLabelName("TASK_ID",categoryId,preferencesHelper);
                if(!taskId.isEmpty()){
                    mBinding.labelId.setText(taskId);
                }

                String endLocationName=CommonUtils.getAllowFieldLabelName("END_LOCATION",categoryId,preferencesHelper);
                if(!endLocationName.isEmpty()){
                    mBinding.labelEndLocation.setText(endLocationName);
                }
                String startTime=CommonUtils.getAllowFieldLabelName("START_TIME",categoryId,preferencesHelper);
                if(!startTime.isEmpty()){
                    mBinding.labelScheduledAt.setText(startTime);
                }
                String endTime=CommonUtils.getAllowFieldLabelName("END_TIME",categoryId,preferencesHelper);
                if(!endTime.isEmpty()){
                    mBinding.labelEndAt.setText(endTime);
                }
            }
            mBinding.rlRequestToCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (task.getRequestedUser().getMobile() != null) {
                        String mobile = task.getRequestedUser().getMobile();
                        CommonUtils.openDialer(context, mobile);
                    }
                }
            });

            if (task.getAssigneeDetail() != null && task.getAssigneeDetail().getProfileImage() != null)
                Glide.with(context).load(task.getAssigneeDetail().getProfileImage()).placeholder(R.drawable.ic_social_media).apply(new RequestOptions().circleCrop()).error(R.drawable.ic_social_media).into(mBinding.ivVendor);
            if (task.getAssigneeDetail() != null) {
                isVendorVisible=true;
                mBinding.rlVendor.setVisibility(View.VISIBLE);
                mBinding.tvRole.setText("Initiated By");
                if (task.getAssigneeDetail().getMobile() != null) {
                    mBinding.tvMobile.setText(task.getAssigneeDetail().getMobile());
                    mBinding.rlCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String mobile = task.getAssigneeDetail().getMobile();
                            CommonUtils.openDialer(context, mobile);
                        }
                    });
                }
                if (task.getAssigneeDetail().getName() != null) {
                    mBinding.tvName.setText(task.getAssigneeDetail().getName());
                }
                if (task.getStartTime() == 0L)
                    mBinding.rlSchedule.setVisibility(View.GONE);

                mBinding.rlChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onChatClick(task.getAssigneeDetail().getBuddyId(), task.getAssigneeDetail().getName());

                    }
                });

            }
            if (task.getContact() != null) {
                isPointOfContactVisible=true;
                mBinding.tvContactName.setText(task.getContact().getName());
                mBinding.tvContactMobile.setText(task.getContact().getMobile());
                mBinding.rlPointOfContact.setVisibility(View.VISIBLE);
                mBinding.rlCallContactPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (task.getContact().getMobile() != null) {
                            String mobile = task.getContact().getMobile();
                            CommonUtils.openDialer(context, mobile);
                        }
                    }
                });
            }
            mBinding.rlAssignedToTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(TaskDetailActivity.Companion.newIntent(context)
                            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId()));
                }
            });
            if (task.getBuddyDetail() != null) {
                isAssignedToVisible=true;
                mBinding.rlAssignedTo.setVisibility(View.VISIBLE);
                if (task.getBuddyDetail().getProfileImage() != null && !task.getBuddyDetail().getProfileImage().isEmpty())
                    Glide.with(context).load(task.getBuddyDetail().getProfileImage()).placeholder(R.drawable.ic_social_media).apply(new RequestOptions().circleCrop()).error(R.drawable.ic_social_media).into(mBinding.ivAssignedTo);
                mBinding.tvAssignedToRole.setText("Assigned To");
                if (task.getBuddyDetail().getName() != null)
                    mBinding.tvAssignedToName.setText(task.getBuddyDetail().getName());
                if (task.getBuddyDetail().getMobile() != null) {
                    mBinding.tvAssignedToMobile.setText(task.getBuddyDetail().getMobile());
                    mBinding.rlAssignedToCall.setVisibility(View.VISIBLE);
                    mBinding.rlAssignedToCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (task.getBuddyDetail().getMobile() != null) {
                                String mobile = task.getBuddyDetail().getMobile();
                                CommonUtils.openDialer(context, mobile);
                            }
                        }
                    });
                }

                if (task.getBuddyDetail().getBuddyId() != null && task.getBuddyDetail().getBuddyId().isEmpty()) {
                    mBinding.rlIvAssignedToChat.setVisibility(View.VISIBLE);
                } else {
                    mBinding.rlIvAssignedToChat.setVisibility(View.GONE);
                }
                mBinding.rlIvAssignedToChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onChatClick(task.getBuddyDetail().getBuddyId(), task.getBuddyDetail().getName());

                    }
                });

            } else {
                mBinding.rlAssignedTo.setVisibility(View.GONE);
            }

            if (task.getReferenceId() != null && !task.getReferenceId().isEmpty()) {
                mBinding.rlVehicleInfo.setVisibility(View.VISIBLE);
                if(reffranceLabel.isEmpty())
                    reffranceLabel="Reference Id";
                mBinding.tvLabelVehicleNumber.setText(reffranceLabel + " :");
                mBinding.tvVehicleNumber.setText(task.getReferenceId());

            } else {
                mBinding.rlVehicleInfo.setVisibility(View.GONE);
            }
            mBinding.rlInnerStartLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (task.getSource() != null && task.getSource().getLocation() != null && task.getDestination() != null && task.getDestination().getLocation() != null) {
//                        CommonUtils.showLogMessage("e", "two point map", "two start points map");
//                        onClickMapIcon(task);
//                    } else if (task.getSource() != null && task.getSource().getLocation() != null && (task.getDestination() == null || task.getDestination().getLocation() == null)) {
//                        CommonUtils.showLogMessage("e", "source point map", "source start points map");
//                        GeoCoordinates geoCoordinates = task.getSource().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
//
//                    } else if (task.getDestination() != null && task.getDestination().getLocation() != null && (task.getSource() == null || task.getSource().getLocation() == null)) {
//                        CommonUtils.showLogMessage("e", "destination point map", "destination start points map");
//                        GeoCoordinates geoCoordinates = task.getDestination().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
//                    }

                    if (task.getSource() != null && task.getSource().getLocation() != null){
                        CommonUtils.showLogMessage("e", "source point map", "source start points map");
                        GeoCoordinates geoCoordinates = task.getSource().getLocation();
                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
                    }

                }
            });
            mBinding.rlInnerEndLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (task.getSource() != null && task.getSource().getLocation() != null && task.getDestination() != null && task.getDestination().getLocation() != null) {
//                        CommonUtils.showLogMessage("e", "two point map", "two end points map");
//                        onClickMapIcon(task);
//                    } else if (task.getDestination() != null && task.getDestination().getLocation() != null && (task.getSource() == null || task.getSource().getLocation() == null)) {
//                        CommonUtils.showLogMessage("e", "destination point map", "destination end points map");
//                        GeoCoordinates geoCoordinates = task.getDestination().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
//                    } else if (task.getSource() != null && task.getSource().getLocation() != null && (task.getDestination() == null || task.getDestination().getLocation() == null)) {
//                        CommonUtils.showLogMessage("e", "source point map", "source end points map");
//                        GeoCoordinates geoCoordinates = task.getSource().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
//
//                    }
                    if (task.getDestination() != null && task.getDestination().getLocation() != null) {
                        CommonUtils.showLogMessage("e", "destination point map", "destination end points map");
                        GeoCoordinates geoCoordinates = task.getDestination().getLocation();
                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
                    }
                }
            });
             mBinding.ivExpandDate.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(mBinding.llAllDateData.getVisibility()==View.VISIBLE){
                         mBinding.llAllDateData.setVisibility(View.GONE);
                         mBinding.ivExpandDate.setImageResource(R.drawable.ic_expand_down_arrow);
                     }else{
                         mBinding.llAllDateData.setVisibility(View.VISIBLE);
                         mBinding.ivExpandDate.setImageResource(R.drawable.ic_expand_up);
                     }
                 }
             });
            mBinding.ivExpandLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBinding.llLocations.getVisibility()==View.VISIBLE){
                        mBinding.llLocations.setVisibility(View.GONE);
                        mBinding.ivExpandLocation.setImageResource(R.drawable.ic_expand_down_arrow);
                    }else{
                        mBinding.llLocations.setVisibility(View.VISIBLE);
                        mBinding.ivExpandLocation.setImageResource(R.drawable.ic_expand_up);
                    }
                }
            });
            if(!isVendorVisible&&!isPointOfContactVisible&&!isAssignedToVisible){
                mBinding.cardContacts.setVisibility(View.GONE);
            }else{
                mBinding.cardContacts.setVisibility(View.VISIBLE);
            }
            mBinding.ivExpandContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mBinding.llContacts.getVisibility()==View.VISIBLE){
                        mBinding.llContacts.setVisibility(View.GONE);
                        mBinding.ivExpandContact.setImageResource(R.drawable.ic_expand_down_arrow);
                    }else{
                        mBinding.llContacts.setVisibility(View.VISIBLE);
                        mBinding.ivExpandContact.setImageResource(R.drawable.ic_expand_up);
                    }
                }
            });


            mBinding.rlTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(TaskDetailActivity.Companion.newIntent(context)
                            .putExtra(AppConstants.Extra.EXTRA_TASK_ID, task.getTaskId()));
                }
            });
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
            //handle call to actions
            //CommonUtils.handleCallToActions(context, task, mBinding.llCallToAction, mListener);
            CommonUtils.handleCallToActionsNew(context, task, mBinding.recyclerCtaButton, mListener);
        }

        @Override
        public void onItemClick(Task bean) {
            mListener.onItemClick(bean, getAdapterPosition());
        }

        @Override
        public void onCallClick(String mobile) {

        }

//        @Override
//        public void onCallClick(Task task) {
//            mListener.onCallClick(task, getAdapterPosition());
//        }

        @Override
        public void onClickMapIcon(Task task) {
            mListener.onClickMapIcon(task, getAdapterPosition());
        }

        @Override
        public void onDetailsClick(Task task) {
            mListener.onDetailsTaskClick(task);

        }

        @Override
        public void onChatStart(String buddyId, String buddyName) {

        }
    }

    public class EmptyViewHolder extends BaseViewHolder implements
            AssignedtoMeEmptyItemViewModel.
                    AssignedtoMeEmptyItemViewModelListener {

        private ItemAssignedToMeEmptyViewBinding mBinding;

        EmptyViewHolder(ItemAssignedToMeEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            AssignedtoMeEmptyItemViewModel emptyItemViewModel = new AssignedtoMeEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }

        @Override
        public void onRetryClick() {
            //     mListener.onRetryClick();
        }
    }

}