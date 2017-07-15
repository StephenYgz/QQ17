package com.itheima.qq17.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.itheima.qq17.R;

import java.util.Date;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-05 14:39
 * 网址：http://www.itheima.com
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<EMConversation> emConversationList;

    public ConversationAdapter(List<EMConversation> emConversationList) {
        this.emConversationList = emConversationList;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation, parent, false);
        ConversationViewHolder conversationViewHolder = new ConversationViewHolder(view);
        return conversationViewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        final EMConversation emConversation = emConversationList.get(position);
        String userName = emConversation.getUserName();
        holder.mTvUsername.setText(userName);
        EMMessage lastMessage = emConversation.getLastMessage();
        long msgTime = lastMessage.getMsgTime();
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
        EMMessageBody lastMessageBody = lastMessage.getBody();
        if (lastMessageBody instanceof EMTextMessageBody){
            EMTextMessageBody emTextMessageBody = (EMTextMessageBody) lastMessageBody;
            String message = emTextMessageBody.getMessage();
            holder.mTvMsg.setText(message);
        }
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        if (unreadMsgCount>99){
            holder.mTvUnread.setText("99+");
            holder.mTvUnread.setVisibility(View.VISIBLE);
        }else if (unreadMsgCount>0){
            holder.mTvUnread.setText(unreadMsgCount+"");
            holder.mTvUnread.setVisibility(View.VISIBLE);
        }else{
            holder.mTvUnread.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnConversationClickListener!=null){
                    mOnConversationClickListener.onConversationClick(emConversation);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return emConversationList==null?0:emConversationList.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTvMsg;
        private final TextView mTvUnread;
        private final TextView mTvTime;
        private final TextView mTvUsername;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvUnread = (TextView) itemView.findViewById(R.id.tv_unread);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);

        }
    }

    public interface OnConversationClickListener{
        void onConversationClick(EMConversation emConversation);
    }
    private OnConversationClickListener mOnConversationClickListener;
    public void setOnConversationClickListener(OnConversationClickListener onConversationClickListener){
        this.mOnConversationClickListener = onConversationClickListener;
    }
}
