package com.quickblox.sample.chat.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserPagedResult;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.core.SingleChat;
import com.quickblox.sample.chat.ui.activities.ChatActivity;
import com.quickblox.sample.chat.ui.activities.MainActivity;

public class UsersFragment extends Fragment implements QBCallback {

    private static final String KEY_USER_LOGIN = "userLogin";
    private static final int PAGE_SIZE = 10;
    private PullToRefreshListView usersList;
    private QBUser companionUser;
    private int listViewIndex;
    private int listViewTop;
    //private Button scanButton1;

    public static UsersFragment getInstance() {
        return new UsersFragment();
    }

    public static QBPagedRequestBuilder getQBPagedRequestBuilder(int page) {
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(PAGE_SIZE);

        return pagedRequestBuilder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        usersList = (PullToRefreshListView) v.findViewById(R.id.usersList);
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                companionUser = ((App)getActivity().getApplication()).getAllQbUsers().get(position-1);
                if (((App)getActivity().getApplication()).getQbUser() != null) {
                    startChat();
                } else {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setLastAction(MainActivity.Action.CHAT);
                    activity.showAuthenticateDialog();
                }
            }
        });
		/*scanButton1 = (Button) v.findViewById(R.id.scanButton);
		scanButton1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0){
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				   	  intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE"); 
				   	  //startActivity(intent);
				    	   startActivityForResult(intent, 999);
			       //IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
					//		scanIntegrator.initiateScan();
			}
			});*/
        usersList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                loadNextPage();
                listViewIndex = usersList.getRefreshableView().getFirstVisiblePosition();
                View v = usersList.getRefreshableView().getChildAt(0);
                listViewTop = (v == null) ? 0 : v.getTop();
            }
        });

        loadNextPage();
        return v;
    }

    @Override
    public void onComplete(Result result) {
        if (result.isSuccess()) {
            QBUserPagedResult usersResult = (QBUserPagedResult) result;
            List<QBUser> users = usersResult.getUsers();

            if (users != null && !users.isEmpty()) {
                ((App)getActivity().getApplication()).addQBUsers(users.toArray(new QBUser[users.size()]));
            }

            // Prepare users list for simple adapter.
            ArrayList<Map<String, String>> usersListForAdapter = new ArrayList<Map<String, String>>();
            for (QBUser user : ((App)getActivity().getApplication()).getAllQbUsers()) {
                Map<String, String> userMap = new HashMap<String, String>();
                userMap.put(KEY_USER_LOGIN, user.getLogin());
                usersListForAdapter.add(userMap);
            }

            // Put users list into adapter.
            SimpleAdapter usersAdapter = new SimpleAdapter(getActivity(), usersListForAdapter,
                    R.layout.list_item_user,
                    new String[]{KEY_USER_LOGIN},
                    new int[]{R.id.userLogin});

            usersList.setAdapter(usersAdapter);
            usersList.onRefreshComplete();
            usersList.getRefreshableView().setSelectionFromTop(listViewIndex, listViewTop);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Error(s) occurred: " + result.getErrors()).create().show();
        }
    }

    @Override
    public void onComplete(Result result, Object context) {

    }

    public void startChat() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.SINGLE);
        bundle.putInt(SingleChat.EXTRA_USER_ID, companionUser.getId());
        ChatActivity.start(getActivity(), bundle);
    }

    private void loadNextPage() {
        int currentPage = ((App)getActivity().getApplication()).getCurrentPage();
        QBUsers.getUsers(getQBPagedRequestBuilder(currentPage), UsersFragment.this);
        ((App)getActivity().getApplication()).setCurrentPage(currentPage + 1);
    }

}
