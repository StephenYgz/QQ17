package com.itheima.qq17.view;

import com.itheima.qq17.view.fragment.BaseFragment;
import com.itheima.qq17.view.fragment.ContactsFragment;
import com.itheima.qq17.view.fragment.ConversationFragment;
import com.itheima.qq17.view.fragment.PluginFragment;

/**
 * 作者： itheima
 * 时间：2016-12-02 10:54
 * 网址：http://www.itheima.com
 */

public class FragmentFactory {

    private static ConversationFragment sConversationFragment;
    private static ContactsFragment sContactsFragment;
    private static PluginFragment sPluginFragment;

    public static BaseFragment getFragment(int position){
        switch (position){
            case 0:
                if (sConversationFragment==null){
                    sConversationFragment = new ConversationFragment();
                }
                return sConversationFragment;
            case 1:
                if (sContactsFragment==null){
                    sContactsFragment = new ContactsFragment();
                }
                return sContactsFragment;
            case 2:
                if (sPluginFragment==null){
                    sPluginFragment = new PluginFragment();
                }
                return sPluginFragment;
        }
        return  null;
    }
}
