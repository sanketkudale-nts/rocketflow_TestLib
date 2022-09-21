package com.tracki.ui.newcreatetask;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.tracki.R;
import com.tracki.data.model.request.ClientSearchRequest;
import com.tracki.data.model.response.config.Api;
import com.tracki.data.model.response.config.ClientData;
import com.tracki.data.model.response.config.ClientDataResponse;
import com.tracki.data.network.APIError;
import com.tracki.data.network.ApiCallback;
import com.tracki.data.network.HttpManager;
import com.tracki.ui.custom.CircleTransform;
import com.tracki.ui.custom.GlideApp;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.JSONConverter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BaseAutocompleteAdapter extends BaseAdapter implements Filterable,SuggestionListNavigator {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<ClientData> resultList = new ArrayList<ClientData>();
    private GetUserSuggestionListViewModel viewModel;
    private HttpManager httpManager;
    private List<String> requestUserType;
    private Api api;
    private OnItemSelectedAUtoComplete listener;
    public BaseAutocompleteAdapter(Context context) {
        mContext = context;
        listener= (OnItemSelectedAUtoComplete) mContext;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public ClientData getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_client_list, parent, false);
        }
        String name="";
         if(getItem(position).getFirstName()!=null&&getItem(position).getLastName()!=null){
             name=getItem(position).getFirstName()+" "+getItem(position).getLastName();
         }
        else if(getItem(position).getFirstName()!=null){
            name=getItem(position).getFirstName();
        }
       else if(getItem(position).getLastName()!=null){
            name=getItem(position).getLastName();
        }
        ((TextView) convertView.findViewById(R.id.tvName)).setText(name);
        ((TextView) convertView.findViewById(R.id.tvEmail)).setText(getItem(position).getEmail());
        ((TextView) convertView.findViewById(R.id.tvMobile)).setText(getItem(position).getMobile());
        ImageView userImage=convertView.findViewById(R.id.ivUser);
        if (getItem(position).getProfileImg() != null && !getItem(position).getProfileImg().equals("")) {
            GlideApp.with(mContext)
                    .asBitmap()
                    .load(getItem(position).getProfileImg() )
                    .apply(new RequestOptions()
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.ic_my_profile))
                    .error(R.drawable.ic_my_profile)
                    .into(userImage);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                    listener.userSelected(getItem(position));
            }
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<ClientData> user = findUser(mContext, constraint.toString());
                    filterResults.values=user;
                    filterResults.count=user.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<ClientData>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<ClientData> findUser(Context context, String name) {

        List<ClientData> clientData=new ArrayList<>();

        if(viewModel!=null&&httpManager!=null){
//            ArrayList<String> userType=new ArrayList<>();
//            userType.add(requestUserType);
//            CommonUtils.showLogMessage("e","requestType",requestUserType);
            //userType.add("ADMIN");
            ClientSearchRequest request=new ClientSearchRequest(name,requestUserType);
            viewModel.clientSearchApi(httpManager, request,api);
        }
        return clientData;
    }

    @Override
    public void handleResponse(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {

    }

    @Override
    public void showTimeOutMessage(@NonNull @NotNull ApiCallback callback) {

    }
    public  void setViewModel(GetUserSuggestionListViewModel getUserSuggestionListViewModel){
        this.viewModel=getUserSuggestionListViewModel;
        viewModel.setNavigator(this);
    }

    public void setHttpManager(@NotNull HttpManager httpManager) {
        this.httpManager=httpManager;
    }

    public void setUserType(@NotNull List<String> requestUserType) {
       this.requestUserType=requestUserType;
    }

    public void setApi(@Nullable Api api) {
     this.api=api;
    }

    @Nullable
    @Override
    public List<ClientData> returnList(@NotNull ApiCallback callback, @Nullable Object result, @Nullable APIError error) {
      List<ClientData> searchList=new ArrayList<>();
        if (CommonUtils.handleResponse(callback, error, result, mContext))   {
            JSONConverter jsonConverter=new JSONConverter();
            ClientDataResponse response= (ClientDataResponse) jsonConverter.jsonToObject(result.toString(),ClientDataResponse.class);
            if(response.getSuccessful()&&response.getUserInfo()!=null&&!response.getUserInfo().isEmpty()){
                searchList.addAll(response.getUserInfo());
            }else{
                if(listener!=null)
                    listener.handleUserNotFound();
            }
        }else{
//            if(listener!=null)
//                listener.handleUserNotFound();
        }
        if ( searchList.size() > 0) {
            resultList = searchList;
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }
        return searchList;
    }
    interface OnItemSelectedAUtoComplete{
        void userSelected(@NonNull ClientData data);
        void handleUserNotFound();
    }
}