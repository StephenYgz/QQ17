package com.itheima.qq17.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.qq17.R;
import com.itheima.qq17.utils.StringUtils;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-02 16:46
 * 网址：http://www.itheima.com
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements SlideBarAdapter {

    private List<String> contactsList;

    public ContactAdapter(List<String> contactsList) {

        this.contactsList = contactsList;
    }
    @Override
    public List<String> getData(){
        return  contactsList;
    }

    @Override
    public int getItemCount() {
        return contactsList==null?0:contactsList.size();
    }

    /**
     * 如果界面最多能看到10个条目，那么这个方法被调用10次
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * 将布局文件转换为View。然后创建ViewHolder
         */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);

        ContactViewHolder contactViewHolder = new ContactViewHolder(view);

        return contactViewHolder;
    }

    /**
     * 每显示出来一个View就调用一次
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        /***
         * 将position对应的数据拿到，然后展示到holder上
         */
        final String contact = contactsList.get(position);
        holder.mTvUsername.setText(contact);
        //获取到contact的大写首字母
        String initial = StringUtils.getInitial(contact);
        holder.mTvSection.setText(initial);
        /**
         * 如果position==0，则肯定显示，否则需要获取一下上一个条目的首字母
         * 如果当前首字母跟上一个首字母一样，则隐藏，否则显示
         */
        if (position==0){
            holder.mTvSection.setVisibility(View.VISIBLE);
        }else{
            String preContact = contactsList.get(position - 1);
            String preInitial = StringUtils.getInitial(preContact);
            if (preInitial.equals(initial)){
                holder.mTvSection.setVisibility(View.GONE);
            }else{
                holder.mTvSection.setVisibility(View.VISIBLE);
            }
        }
        /**
         *  holder.itemView 其实就是对应的条目View
         *  给当前条目绑定点击监听器
         */

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnContactClickListener!=null){
                    mOnContactClickListener.onClick(contact);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnContactClickListener!=null){
                    mOnContactClickListener.onLongClick(contact);
                }
                return true;
            }
        });

    }

    class ContactViewHolder extends RecyclerView.ViewHolder{

        TextView mTvSection;
        TextView mTvUsername;

        public ContactViewHolder(View itemView) {
            super(itemView);
            /**
             * 将itemView中的子控件获取到
             */
            mTvSection = (TextView) itemView.findViewById(R.id.tv_section);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
        }
    }

    public interface OnContactClickListener{
        void onClick(String contact);
        void onLongClick(String contact);
    }
    private OnContactClickListener mOnContactClickListener;
    public void setOnContactClickListener(OnContactClickListener onContactClickListener){
        this.mOnContactClickListener = onContactClickListener;
    }

}
