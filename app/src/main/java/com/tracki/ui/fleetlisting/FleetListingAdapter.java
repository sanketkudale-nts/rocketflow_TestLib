package com.tracki.ui.fleetlisting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.tracki.R;
import com.tracki.data.model.response.config.Fleet;
import com.tracki.databinding.ItemFleetListingEmptyViewBinding;
import com.tracki.databinding.ItemFleetListingViewBinding;
import com.tracki.ui.base.BaseViewHolder;
import com.tracki.ui.common.DoubleButtonDialog;
import com.tracki.ui.common.OnClickListener;
import com.tracki.ui.custom.GlideApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rahul on 18/9/18
 */
public class FleetListingAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private List<Fleet> mFleetResponseList;
    private List<Fleet> copyList;
    private Intent intent;
    private AdapterEventListener mListener;
    private Context context;

    FleetListingAdapter(List<Fleet> driverResponseList) {
        this.mFleetResponseList = driverResponseList;
        copyList = new ArrayList<>();
        copyList.addAll(mFleetResponseList);
    }

    @Override
    public int getItemCount() {
        if (mFleetResponseList != null && mFleetResponseList.size() > 0) {
            return mFleetResponseList.size();
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mFleetResponseList != null && !mFleetResponseList.isEmpty()) {
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
                ItemFleetListingViewBinding fleetListingViewBinding = ItemFleetListingViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new FleetListingViewHolder(fleetListingViewBinding);
            case VIEW_TYPE_EMPTY:
            default:
                ItemFleetListingEmptyViewBinding emptyViewBinding = ItemFleetListingEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
                return new EmptyViewHolder(emptyViewBinding);
        }
    }

    public void addItems(List<Fleet> stringList) {
        clearItems();
        mFleetResponseList.addAll(stringList);
        copyList.clear();
        copyList.addAll(mFleetResponseList);
        notifyDataSetChanged();
    }

    private void clearItems() {
        mFleetResponseList.clear();
    }

    public void setListener(AdapterEventListener listener, Intent intent) {
        this.mListener = listener;
        this.intent = intent;
    }

    void addFilter(String newText) {
        clearItems();
        if (newText.isEmpty()) {
            mFleetResponseList.addAll(copyList);
        } else {
            for (Fleet name : copyList) {
                if (name.getFleetName() != null &&
                        name.getFleetName().toLowerCase().contains(newText.toLowerCase())) {
                    mFleetResponseList.add(name);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Fleet> getFleetResponseList() {
        return mFleetResponseList;
    }

    public interface AdapterEventListener {

//        void onRetryClick();

        void onItemClick(Fleet fleetBean);

        void onItemLongClick(Fleet fleetBean);

        void enableDisableFleet(Fleet fleetBeen,Boolean status);
    }

    public class FleetListingViewHolder extends BaseViewHolder implements FleetListingItemViewModel.FleetListingItemViewModelListener {

        private ItemFleetListingViewBinding mBinding;

        private FleetListingItemViewModel mFleetListingItemViewModel;

        FleetListingViewHolder(ItemFleetListingViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
      private CompoundButton.OnCheckedChangeListener checkedChangeListener=new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

              String message="";
              if(isChecked){
                  message="Are you sure want to active this fleet";
              }else{
                  message="Are you sure want to inactive this fleet";
              }
              Fleet list = mFleetResponseList.get(getAdapterPosition());
              DoubleButtonDialog dialog = new DoubleButtonDialog(context,
                      true,
                      null,
                      message,
                      context.getString(R.string.yes),
                      context.getString(R.string.no),
                      new OnClickListener() {
                          @Override
                          public void onClickCancel() {
                               mBinding.enableDisableSwitch.setOnCheckedChangeListener(null);
                              mBinding.enableDisableSwitch.setChecked(!isChecked);
                              mBinding.enableDisableSwitch.setText(list.getStatus()==null?"NOT ACTIVE":list.getStatus().equals("ACTIVE")?"ACTIVE":"NOt ACTIVE");
                              mBinding.enableDisableSwitch.setTextColor(list.getStatus()==null?ContextCompat.getColor(context,R.color.gray):list.getStatus().equals("ACTIVE")? ContextCompat.getColor(context,R.color.black):
                                      ContextCompat.getColor(context,R.color.gray));
                              mBinding.enableDisableSwitch.setOnCheckedChangeListener(checkedChangeListener);
                          }

                          @Override
                          public void onClick() {
                              if(isChecked){
                                  list.setStatus("ACTIVE");
                              }else{
                                  list.setStatus("INACTIVE");
                              }
                              mBinding.enableDisableSwitch.setText(list.getStatus()==null?"NOT ACTIVE":list.getStatus().equals("ACTIVE")?"ACTIVE":"NOt ACTIVE");
                              mBinding.enableDisableSwitch.setTextColor(list.getStatus()==null?ContextCompat.getColor(context,R.color.gray):list.getStatus().equals("ACTIVE")? ContextCompat.getColor(context,R.color.black):
                                      ContextCompat.getColor(context,R.color.gray));
                              mListener.enableDisableFleet(list,isChecked);
                          }
                      });
              dialog.show();

          }
      };
        @Override
        public void onBind(int position) {
            Fleet list = mFleetResponseList.get(position);
            if(list.getInvImg()!=null&&!list.getInvImg().isEmpty()) {
                GlideApp.with(context)
                        .asBitmap()
                        .load(list.getInvImg())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder_car))
                        .error(R.drawable.placeholder_car)
                        .into(mBinding.tvName);
            }
            mFleetListingItemViewModel = new FleetListingItemViewModel(list, this);
            mBinding.setViewModel(mFleetListingItemViewModel);
            mBinding.enableDisableSwitch.setOnCheckedChangeListener(null);
            mBinding.enableDisableSwitch.setChecked(list.getStatus()==null?false:list.getStatus().equals("ACTIVE")?true:false);
            mBinding.enableDisableSwitch.setText(list.getStatus()==null?"INACTIVE":list.getStatus().equals("ACTIVE")?"ACTIVE":"INACTIVE");
            mBinding.enableDisableSwitch.setTextColor(list.getStatus()==null?ContextCompat.getColor(context,R.color.gray):list.getStatus().equals("ACTIVE")? ContextCompat.getColor(context,R.color.black):
                    ContextCompat.getColor(context,R.color.gray));
            // Immediate Binding
            // When a variable or observable changes, the binding will be scheduled to change before
            // the next frame. There are times, however, when binding must be executed immediately.
            // To force execution, use the executePendingBindings() method.
            mBinding.enableDisableSwitch.setOnCheckedChangeListener(checkedChangeListener);

            mBinding.executePendingBindings();


        }

        @Override
        public void onItemClick(@NonNull Fleet fleetBean) {
            //if intent is from main activity then call listener's method.
            /*if (intent.hasExtra(EXTRA_FLEET_LIST_CALLING_FROM_NAVIGATION_MENU)) {
                mListener.onItemClick(fleetBean);
            } else {
                for (int i = 0; i < mFleetResponseList.size(); i++) {
//                    if (fleetBean.getRegNumber() != null && fleetBean.getRegNumber().equals(mFleetResponseList.get(i).getRegNumber())) {
//                        if (fleetBean.isSelected() != null && fleetBean.isSelected()) {
//                            fleetBean.setSelected(false);
//                            mFleetResponseList.get(i).setSelected(false);
//                        } else {
//                            fleetBean.setSelected(true);
//                            mFleetResponseList.get(i).setSelected(true);
//                        }
//                    } else {
//                        mFleetResponseList.get(i).setSelected(false);
//                    }
                    if (fleetBean.getInvId() != null && fleetBean.getInvId().equals(mFleetResponseList.get(i).getInvId())) {
                        if (fleetBean.isSelected() != null && fleetBean.isSelected()) {
                            fleetBean.setSelected(false);
                            mFleetResponseList.get(i).setSelected(false);
                        } else {
                            fleetBean.setSelected(true);
                            mFleetResponseList.get(i).setSelected(true);
                        }
                    } else {
                        mFleetResponseList.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }*/
        }

        @Override
        public void onItemLongClick(Fleet buddyBean) {
            mListener.onItemLongClick(buddyBean);
        }
    }

    public class EmptyViewHolder extends BaseViewHolder implements FleetListingEmptyItemViewModel.FleetListingEmptyItemViewHolderListener {

        private ItemFleetListingEmptyViewBinding mBinding;

        public EmptyViewHolder(ItemFleetListingEmptyViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            FleetListingEmptyItemViewModel emptyItemViewModel = new FleetListingEmptyItemViewModel(this);
            mBinding.setViewModel(emptyItemViewModel);
        }

        @Override
        public void onRetryClick() {
//                mListener.onRetryClick();
        }
    }
}