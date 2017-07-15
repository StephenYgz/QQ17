package com.itheima.qq17.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.itheima.qq17.R;
import com.itheima.qq17.utils.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-04 16:05
 * 网址：http://www.itheima.com
 */

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.AddFriendViewHolder> {

    //从服务器上搜索出来的相关的数据
    private List<AVUser> mAVUserList;
    //当前用户的好友（已经成为的）
    private List<String> mContactList;
    public AddFriendAdapter(List<AVUser> AVUserList, List<String> contactList) {
        mAVUserList = AVUserList;
        mContactList = contactList;
    }

    @Override
    public int getItemCount() {
        return mAVUserList==null?0:mAVUserList.size();
    }


    @Override
    public AddFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_friend_item, parent, false);
        AddFriendViewHolder addFriendViewHolder = new AddFriendViewHolder(view);
        return addFriendViewHolder;
    }

    @Override
    public void onBindViewHolder(AddFriendViewHolder holder, int position) {
        AVUser avUser = mAVUserList.get(position);
        final String username = avUser.getUsername();
        Date createdAt = avUser.getCreatedAt();
        holder.mTvUsername.setText(username);
        holder.mTvTime.setText(StringUtils.getDateString(createdAt));
        //判断当前username是不是我的好友
        if (mContactList.contains(username)){
            //已经是好友了
            holder.mBtnAdd.setEnabled(false);
            holder.mBtnAdd.setText("已经是好友");
        }else{
            //还不是好友
            holder.mBtnAdd.setEnabled(true);
            holder.mBtnAdd.setText("添加");
        }
        //给Button绑定点击监听
        holder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAddFriendClickListener!=null){
                    mOnAddFriendClickListener.onClick(username);
                }
            }
        });
    }

    public interface OnAddFriendClickListener{
        void onClick(String username);
    }
    private OnAddFriendClickListener mOnAddFriendClickListener;
    public void setOnAddFriendClickListener(OnAddFriendClickListener onAddFriendClickListener){
        this.mOnAddFriendClickListener = onAddFriendClickListener;
    }



    class AddFriendViewHolder extends RecyclerView.ViewHolder{

        private final Button mBtnAdd;
        private final TextView mTvTime;
        private final TextView mTvUsername;

        public AddFriendViewHolder(View itemView) {
            super(itemView);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_add);
        }
    }
}
