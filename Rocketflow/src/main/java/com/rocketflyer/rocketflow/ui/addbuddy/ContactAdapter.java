package com.rocketflyer.rocketflow.ui.addbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tracki.R;
import com.tracki.databinding.ContactBinding;
import com.tracki.ui.addbuddy.Contact;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vikas on 06/04/2020
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> implements Filterable {
    private List<Contact> mList=new ArrayList<>();
    private List<Contact> mFilteredList=new ArrayList<>();

    private Context mContext;
    private CallBack mCallBack;

    public ContactAdapter(Context context) {
        this.mContext = context;
        this.mCallBack = (CallBack) context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());

        ContactBinding itemView = DataBindingUtil.inflate(layoutInflater,R.layout.layout_row_show_contact, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
       Contact contact=mFilteredList.get(position);
       if(contact!=null)
       holder.bind(contact);

    }



    @Override
    public int getItemCount() {
        return mFilteredList==null?0:mFilteredList.size();
    }

    public void addData(List<Contact> mList) {
       if(mList!=null&&!this.mList.isEmpty())
           this.mList.clear();
        if(this.mFilteredList!=null&&!this.mFilteredList.isEmpty())
            this.mFilteredList.clear();
        this.mList.addAll(mList);
        this.mFilteredList.addAll(mList);
        notifyDataSetChanged();
    }



    public void clear() {
        this.mList.clear();
        this.mFilteredList.clear();
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ContactBinding itemView;
        MyViewHolder(ContactBinding itemView){
            super(itemView.getRoot());
            this.itemView=itemView;
        }
        void bind(Contact contact){

            if(contact!=null&&!contact.isColorSet()){
//                Random rnd = new Random();
//                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//                itemView.ivCircle.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                //itemView.ivCircle.setColorFilter(Color.parseColor(mColors[getAdapterPosition() % 40]), PorterDuff.Mode.SRC_IN);
                contact.setColorSet(true);
            }
            itemView.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallBack!=null)
                        mCallBack.onItemClick(contact);
                }
            });
            itemView.setData(contact);
            itemView.executePendingBindings();
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mList;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact contact :
                            mList) {
                        if (contact.getName().toLowerCase().contains(charString)) {

                            filteredList.add(contact);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }

        };
    }

    public interface CallBack{
        void onItemClick(Contact contact);
    }

}
