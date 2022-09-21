package com.rocketflyer.rocketflow.ui.tasklisting.ihaveassigned;

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
import com.tracki.data.model.response.config.ActionConfig;
import com.tracki.data.model.response.config.AssigneeDetail;
import com.tracki.data.model.response.config.GeoCoordinates;
import com.tracki.data.model.response.config.Navigation;
import com.tracki.data.model.response.config.Task;
import com.tracki.data.model.response.config.UserGroup;
import com.tracki.databinding.ItemIHaveAssignedEmptyViewBinding;
import com.tracki.databinding.LayoutRowAssigndToMeBinding;
import com.tracki.ui.base.BaseViewHolder;
import com.tracki.ui.tasklisting.TaskClickListener;
import com.tracki.ui.tasklisting.TaskComparator;
import com.tracki.ui.tasklisting.assignedtome.TaskAssignToMeViewModel;
import com.tracki.ui.tasklisting.ihaveassigned.IhaveAssignedEmptyItemViewModel;
import com.tracki.ui.webview.WebViewActivity;
import com.tracki.utils.AppConstants;
import com.tracki.utils.CommonUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by rahul on 5/10/18
 */
public class IhaveAssignedAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Task> mResponseList;
    private TaskClickListener mListener;
    private Context context;
    private String categoryId = null;
    private PreferencesHelper preferencesHelper;
    private String reffranceLabel=null;
    public  void setReffLabel(String reffranceLabel){
        this.reffranceLabel=reffranceLabel;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    IhaveAssignedAdapter(List<Task> responseList, PreferencesHelper preferencesHelper) {
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
        context = parent.getContext();
        switch (viewType) {
            case VIEW_TYPE_NORMAL:

//                ItemIHaveAssignedViewBinding itemIHaveAssignedViewBinding = ItemIHaveAssignedViewBinding.inflate(LayoutInflater.from(parent.getContext()),
//                        parent, false);
                LayoutRowAssigndToMeBinding itemAssignedToMeViewBinding = LayoutRowAssigndToMeBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new IhaveAssignedViewHolder(itemAssignedToMeViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemIHaveAssignedEmptyViewBinding emptyViewBinding = ItemIHaveAssignedEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }
    public List<Task> getList(){
        return mResponseList;
    }
    public void addItems(List<Task> stringList) {
       // clearItems();
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
    public void clearItems() {
        mResponseList.clear();
    }

    public void setListener(TaskClickListener listener) {
        this.mListener = listener;
    }

//    public interface TaskHandleListener {
//
//        void onItemClick(Task bean);
//
//        void onCancelTask(Task task);
//
////        void onEndTask(Task task);
//
//        void onEditTask(Task task);
//
//        void onCallClick(Task task);
//    }

    public class IhaveAssignedViewHolder extends BaseViewHolder implements
            TaskAssignToMeViewModel.AssignedtoMeItemViewModelListener {

        private TaskAssignToMeViewModel itemViewModel;
        private LayoutRowAssigndToMeBinding mBinding;

        IhaveAssignedViewHolder(LayoutRowAssigndToMeBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            final Task task = mResponseList.get(position);
            itemViewModel = new TaskAssignToMeViewModel(task, this, context, preferencesHelper, categoryId);
            mBinding.setViewModel(itemViewModel);
            Boolean isVendorVisible=false;
            Boolean isPointOfContactVisible=false;
            if(categoryId!=null)
            {
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
            if(task.getAssignmentType()!=null&&task.getAssignmentType().equals("USER"))
            {
                CommonUtils.showLogMessage("e","task.getAssignmentType()",task.getAssignmentType());


                AssigneeDetail assigneeDetail=task.getBuddyDetail();
                if(assigneeDetail!=null){
                    mBinding.rlVendor.setVisibility(View.VISIBLE);
                    isVendorVisible=true;
                    mBinding.tvRole.setText("Executive");
                    if(assigneeDetail.getProfileImage()!=null)
                        Glide.with(context).load(assigneeDetail.getProfileImage()).placeholder(R.drawable.ic_social_media).apply(new RequestOptions().circleCrop()).error(R.drawable.ic_social_media).into(mBinding.ivVendor);
                    if(assigneeDetail.getMobile()!=null){
                        mBinding.tvMobile.setText(assigneeDetail.getMobile());
                        mBinding.tvMobile.setVisibility(View.VISIBLE);
                        mBinding.rlCall.setVisibility(View.VISIBLE);
                        mBinding.rlCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String mobile = assigneeDetail.getMobile();
                                CommonUtils.openDialer(context, mobile);
                            }
                        });
                    }
                    if(assigneeDetail.getName()!=null){
                        mBinding.tvName.setText(assigneeDetail.getName());
                    }
                    mBinding.rlChat.setVisibility(View.VISIBLE);
                    mBinding.rlChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                               context.startActivity(MessagesActivity.Companion.newIntent(context)
//                                       .putExtra(AppConstants.Extra.EXTRA_BUDDY_ID, assigneeDetail.getBuddyId())
//                                       .putExtra(AppConstants.Extra.EXTRA_BUDDY_NAME, assigneeDetail.getName())
//                                       .putExtra(AppConstants.Extra.FROM, AppConstants.Extra.ASSIGNED_BY_ME));
                            mListener.onChatClick(assigneeDetail.getBuddyId(),assigneeDetail.getName());

                        }
                    });
                }else{
                   // mBinding.rlVendor.setVisibility(View.VISIBLE);
                }

            }
            else if(task.getAssignmentType()!=null&&task.getAssignmentType().equals("GROUP")){
                UserGroup userGroup=task.getUserGroup();
              //  mBinding.rlVendor.setVisibility(View.VISIBLE);
                if(userGroup!=null){
                    isVendorVisible=true;
                    mBinding.rlVendor.setVisibility(View.VISIBLE);
                    mBinding.tvRole.setText("GROUP");
                    mBinding.tvMobile.setVisibility(View.GONE);
                    mBinding.rlCall.setVisibility(View.GONE);
                    if(userGroup.getGroupName()!=null){
                        mBinding.tvName.setText(userGroup.getGroupName());
                    }

                    mBinding.rlChat.setVisibility(View.GONE);
                }

            }
            if(task.getReferenceId()!=null&&!task.getReferenceId().isEmpty())
            {
                mBinding.rlVehicleInfo.setVisibility(View.VISIBLE);
                if(reffranceLabel.isEmpty())
                    reffranceLabel="Reference Id";
                mBinding.tvLabelVehicleNumber.setText(reffranceLabel+" :");
                CommonUtils.showLogMessage("e","label adapter=>","=>"+reffranceLabel);
                mBinding.tvVehicleNumber.setText(task.getReferenceId());

            }else{
                mBinding.rlVehicleInfo.setVisibility(View.GONE);
            }
            mBinding.rlInnerStartLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(task.getSource()!=null&&task.getSource().getLocation()!=null&&task.getDestination()!=null&&task.getDestination().getLocation()!=null){
//                        CommonUtils.showLogMessage("e","two point map","two start points map");
//                        onClickMapIcon(task);
//                    }else if(task.getSource()!=null&&task.getSource().getLocation()!=null&&(task.getDestination()==null||task.getDestination().getLocation()==null)){
//                        CommonUtils.showLogMessage("e","source point map","source start points map");
//                        GeoCoordinates geoCoordinates=task.getSource().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context,geoCoordinates.getLatitude(),geoCoordinates.getLongitude());
//
//                    } else  if(task.getDestination()!=null&&task.getDestination().getLocation()!=null&&(task.getSource()==null||task.getSource().getLocation()==null)){
//                        CommonUtils.showLogMessage("e","destination point map","destination start points map");
//                        GeoCoordinates geoCoordinates=task.getDestination().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context,geoCoordinates.getLatitude(),geoCoordinates.getLongitude());
//                    }
                    if (task.getSource() != null && task.getSource().getLocation() != null ) {
                        CommonUtils.showLogMessage("e", "source point map", "source start points map");
                        GeoCoordinates geoCoordinates = task.getSource().getLocation();
                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
                    }
                }
            });
            mBinding.rlEndLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(task.getSource()!=null&&task.getSource().getLocation()!=null&&task.getDestination()!=null&&task.getDestination().getLocation()!=null){
//                        CommonUtils.showLogMessage("e","two point map","two end points map");
//                        onClickMapIcon(task);
//                    } else  if(task.getDestination()!=null&&task.getDestination().getLocation()!=null&&(task.getSource()==null||task.getSource().getLocation()==null)){
//                        CommonUtils.showLogMessage("e","destination point map","destination end points map");
//                        GeoCoordinates geoCoordinates=task.getDestination().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context,geoCoordinates.getLatitude(),geoCoordinates.getLongitude());
//                    }else if(task.getSource()!=null&&task.getSource().getLocation()!=null&&(task.getDestination()==null||task.getDestination().getLocation()==null)){
//                        CommonUtils.showLogMessage("e","source point map","source end points map");
//                        GeoCoordinates geoCoordinates=task.getSource().getLocation();
//                        CommonUtils.openGoogleMapWithOneLocation(context,geoCoordinates.getLatitude(),geoCoordinates.getLongitude());
//
//                    }
                    if (task.getDestination() != null && task.getDestination().getLocation() != null) {
                        CommonUtils.showLogMessage("e", "destination point map", "destination end points map");
                        GeoCoordinates geoCoordinates = task.getDestination().getLocation();
                        CommonUtils.openGoogleMapWithOneLocation(context, geoCoordinates.getLatitude(), geoCoordinates.getLongitude());
                    }
                }
            });
            if(task.getStartTime()==0L)
                mBinding.rlSchedule.setVisibility(View.GONE);
            if(task.getContact()!=null){
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
            if(!isVendorVisible&&!isPointOfContactVisible){
                mBinding.cardContacts.setVisibility(View.GONE);
            }else{
                mBinding.cardContacts.setVisibility(View.VISIBLE);
            }
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.executePendingBindings();
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
                    if(task.getTrackingUrl()!=null) {
                        Navigation navigation = new Navigation();
                        ActionConfig actionConfig = new ActionConfig();
                        String userId = preferencesHelper.getUserDetail().getUserId();
                        actionConfig.setActionUrl(task.getTrackingUrl()+"&userId="+userId);
                        navigation.setActionConfig(actionConfig);
                        navigation.setTitle("Tracking Details");
                        context.startActivity(WebViewActivity.Companion.newIntent(context)
                                .putExtra(AppConstants.Extra.EXTRA_WEB_INFO, navigation));
                    }

                }
            });

            //handle call to actions
           // CommonUtils.handleCallToActions(context, task, mBinding.llCallToAction, mListener);
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
//        public void onCancelTask(Task task) {
//            mListener.onCancelTask(task);
//        }

//        @Override
//        public void onEndTask(Task task) {
//            /*TODO Not defined*/
////            mListener.onEndTask(task);
//        }

//        @Override
//        public void onEditTask(Task task) {
//            mListener.onEditTask(task);
//        }

//        @Override
//        public void onCallClick(Task task) {
//
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
            IhaveAssignedEmptyItemViewModel.IhaveAssignedEmptyItemViewModelListener {

        private ItemIHaveAssignedEmptyViewBinding mBinding;

        EmptyViewHolder(ItemIHaveAssignedEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            IhaveAssignedEmptyItemViewModel emptyItemViewModel = new IhaveAssignedEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }

        @Override
        public void onRetryClick() {
            //    mListener.onRetryClick();
        }
    }

}