package com.itheima.qq17.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.itheima.qq17.R;

import java.util.Date;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-05 09:42
 * 网址：http://www.itheima.com
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<EMMessage> emMessageList;

    public ChatAdapter(List<EMMessage> emMessageList) {
        this.emMessageList = emMessageList;
    }

    @Override
    public int getItemCount() {
        return emMessageList==null?0:emMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage emMessage = emMessageList.get(position);
        //区分是收到的消息还是发送的消息
        EMMessage.Direct direct = emMessage.direct();
        return direct== EMMessage.Direct.RECEIVE?0:1;
    }

    /**
     * @param parent
     * @param viewType 根据该参数确定要填充哪个布局
     * @return
     */
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType==0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_receive,parent,false);
        }else if (viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_send,parent,false);
        }
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        EMMessage emMessage = emMessageList.get(position);

        long msgTime = emMessage.getMsgTime();
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
        if (position==0){
            holder.mTvTime.setVisibility(View.VISIBLE);
        }else{
            EMMessage preMessage = emMessageList.get(position - 1);
            long preTime = preMessage.getMsgTime();
            if (DateUtils.isCloseEnough(msgTime,preTime)){
                //不显示时间
                holder.mTvTime.setVisibility(View.GONE);
            }else{
                //显示时间
                holder.mTvTime.setVisibility(View.VISIBLE);
            }
        }
        EMMessageBody emMessageBody = emMessage.getBody();
        if (emMessageBody instanceof EMTextMessageBody){
            EMTextMessageBody textMessageBody = (EMTextMessageBody) emMessageBody;
            String message = textMessageBody.getMessage();
            holder.mTvMsg.setText(message);
        }

        if (emMessage.direct()== EMMessage.Direct.SEND){
            //处理消息的状态
            EMMessage.Status status = emMessage.status();
            switch (status){
                case CREATE:
                case INPROGRESS:
                    holder.mIvMsgState.setVisibility(View.VISIBLE);
                    //需要重新给ImageView设置帧动画资源
                    holder.mIvMsgState.setImageResource(R.drawable.msg_sending_anim);
                    //播放帧动画资源
                    Drawable drawable = holder.mIvMsgState.getDrawable();
                    //将drawable强转为AnimationDrawable
                    AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                    if (animationDrawable.isRunning()){
                        animationDrawable.stop();
                    }
                    animationDrawable.start();
                    break;
                case FAIL:
                    holder.mIvMsgState.setImageResource(R.mipmap.msg_error);
                    holder.mIvMsgState.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    holder.mIvMsgState.setVisibility(View.GONE);
                    break;
            }

        }
    }



    class ChatViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTvTime;
        private final TextView mTvMsg;
        private final ImageView mIvMsgState;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mIvMsgState = (ImageView) itemView.findViewById(R.id.iv_msg_state);
        }
    }
}
