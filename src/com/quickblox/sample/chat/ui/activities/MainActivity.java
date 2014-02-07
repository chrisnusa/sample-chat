package com.quickblox.sample.chat.ui.activities;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.module.chat.QBChatRoom;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.RoomListener;
import com.quickblox.module.locations.QBLocations;
import com.quickblox.module.locations.model.QBLocation;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.ui.fragments.RoomsFragment;
import com.quickblox.sample.chat.ui.fragments.UsersFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    private static final int AUTHENTICATION_REQUEST = 1;
    private static final int POSITION_USER = 0;
    private static final int POSITION_ROOM = 1;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private Action lastAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        List<Fragment> tabs = new ArrayList<Fragment>();
        tabs.add(UsersFragment.getInstance());
        tabs.add(RoomsFragment.getInstance());
        
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabs);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

		/*scanButton.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0){
		/*	Intent intent = new Intent(MainActivity.this,
					CaptureActivity.class);
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);*/
	    	 
	//		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	   /* 	  intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE"); 
	    	   startActivityForResult(intent, 0);
       //IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
		//		scanIntegrator.initiateScan();
				/*
			
		}
	});*/
        
  
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(actionBar.newTab()
                    .setText(sectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }


    }
    
    public void scanNow(View view) {
        QBUser qbUser = ((App) getApplication()).getQbUser();
if (qbUser !=null){
    	Log.d("test", "button works!");
    	(new IntentIntegrator(this)).initiateScan();
     
    } else{
    	setLastAction(Action.TEST);
    	 showAuthenticateDialog();
    	 Log.d("test", "show authenti!");
         Toast.makeText(MainActivity.this, "Please login first.", Toast.LENGTH_LONG).show();
  

    }
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        int position = tab.getPosition();
        QBUser qbUser = ((App) getApplication()).getQbUser();
        if (qbUser != null) {
            viewPager.setCurrentItem(position);

        } else if (position == POSITION_ROOM) {
            lastAction = Action.ROOM_LIST;
            showAuthenticateDialog();
           // viewPager.setCurrentItem(position);

        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
   

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Intent) {
    	IntentResult scan=IntentIntegrator.parseActivityResult(requestCode,
                resultCode,
                Intent);
    	if(scan!=null){
    	Log.d("ccc","result from scan: "+scan.getContents());
  //      QBUser qbUser = ((App) getApplication()).getQbUser();
    //    StringifyArrayList<String> tags = new StringifyArrayList<String>();
/*
        tags.add(""+scan.getContents());
        tags.add("test tags");
        qbUser.setTags(tags);
      
      	Log.d("axx", ""+qbUser.getTags());
*/
      	QBLocation location = new QBLocation(22.2783,114.1589, ""+scan.getContents());
      	QBLocations.createLocation(location, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
               //     Toast.makeText(MainActivity.this, "Check In was successful!",
                          //  Toast.LENGTH_LONG).show();
               

                	Toast.makeText(MainActivity.this, "Check In was successful! " ,
                              Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Error(s) occurred. " +
                            "Errors: " + result.getErrors()).create().show();
                }
            }
        });
    	}
        if (resultCode == RESULT_OK) {
        
            switch (lastAction) {
                case CHAT:
                    ((UsersFragment) sectionsPagerAdapter.getItem(POSITION_USER)).startChat();
                    break;
                case ROOM_LIST:
                    viewPager.setCurrentItem(POSITION_ROOM);
                    break;
                case TEST:
                	Log.d("ccc","wasn't logged in before scan-");
                	break;
              
            }
            showUsersFragment();
            ((RoomsFragment) sectionsPagerAdapter.getItem(POSITION_ROOM)).loadRooms();
        
        }     
    
}
    
    private void showUsersFragment() {
        getSupportActionBar().selectTab(getSupportActionBar().getTabAt(POSITION_USER));
        viewPager.setCurrentItem(POSITION_USER);
    }

    public void setLastAction(Action lastAction) {
        this.lastAction = lastAction;
    }

    public void showAuthenticateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Authorize first");
        builder.setItems(new String[]{"Login", "Register"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, AUTHENTICATION_REQUEST);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, RegistrationActivity.class);
                        startActivityForResult(intent, AUTHENTICATION_REQUEST);
                        break;
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showUsersFragment();
            }
        });
        builder.show();
    }

    public static enum Action {CHAT, ROOM_LIST, TEST}

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POSITION_USER:
                    return "Users";
                case POSITION_ROOM:
                    return "Rooms";
            }
            return null;
        }
    }
}
