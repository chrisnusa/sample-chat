package com.quickblox.sample.chat.ui.activities;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.internal.core.exception.BaseServiceException;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.locations.QBLocations;
import com.quickblox.module.locations.model.QBLocation;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.ui.fragments.RoomsFragment;
import com.quickblox.sample.chat.ui.fragments.UsersFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, QBCallback {

    private static final int AUTHENTICATION_REQUEST = 1;
    private static final int POSITION_USER = 0;
    private static final int POSITION_ROOM = 1;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private Action lastAction;
	final Context context = this;
	private String locationInfo="Please scan your location first";
    private QBUser user;
    private static final String TOKEN = "TOKEN";

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
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
	  Log.d("state state", "state1");
		 
		try {
			double	TOKEnTime = QBAuth.getBaseService().getTokenExpirationDate().getTime();
	        Log.d("TOKENTIME",""+TOKEnTime);
	        savedInstanceState.putDouble(TOKEN, TOKEnTime);
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      QBUser qbUser = ((App) getApplication()).getQbUser();
      if(qbUser!=null){
      savedInstanceState.putString("USER_ID", qbUser.getId().toString());
      savedInstanceState.putString("PASSWORD", qbUser.getPassword().toString());
	  Log.d("state state", "state2");
	  
    }
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate. 
//      if (savedInstanceState != null) {
	  Log.d("state state", "state3");

	  if(user==null &&savedInstanceState != null){
		  
          //  mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        	  String idSIS = savedInstanceState.getString("USER_ID");
        	  String passSIS = savedInstanceState.getString("PASSWORD");
        	  Log.d("state state", idSIS);

             //user = new QBUser(idSIS, passSIS);
      //       QBUsers.signIn(user, this);

          //   ((App)getApplication()).setQbUser(user);
        	  //trying to sign in when the token has expired
              //
              // Create session with additional parameters
              try {
          		double TOKEnTime2=   QBAuth.getBaseService().getTokenExpirationDate().getTime();
                  Log.d("TOKENTIME",""+TOKEnTime2);
                  double token1= savedInstanceState.getDouble(TOKEN);
                  if(TOKEnTime2-token1>300000){//token expires in 2 hours, if the difference in token time is more than 5 minutes, log in again
            	  QBUser qbUser = new QBUser();
                  qbUser.setLogin(idSIS);
                  qbUser.setPassword(passSIS);
                  QBAuth.createSession(qbUser, this);
                  Log.d("TOKEN WORKS","TOKEN WORKS");
                  }
          	} catch (BaseServiceException e) {
          		// TODO Auto-generated catch block
          		e.printStackTrace();
          	}


	  }
     //   }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);  
        getMenuInflater().inflate(R.menu.rooms, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        QBUser qbUser = ((App) getApplication()).getQbUser();
       if (id == R.id.action_profile&&qbUser!=null) {
    	   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    	     
			// set title
			alertDialogBuilder.setTitle("Account Information");

			// set dialog messageif (qbUser !=null){
//	    	setLastAction(Action.TEST);
			alertDialogBuilder
			
				.setMessage("My Login: "+qbUser.getLogin()+"\nMy id: "+qbUser.getId()+"\nMy Group: "+qbUser.getTags().get(0)+"\nMy Email: "+qbUser.getEmail()+"\nMy Name: "+qbUser.getFullName()+
						"\nMy Location: "+locationInfo)

				.setCancelable(false)
	    	 
			.setNegativeButton("Okay",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
				}
       else if (id == R.id.action_map&&qbUser!=null) {

         
	    	 Intent intent = new Intent(this, MapActivity.class);
	            startActivity(intent);
       } else {
    	   Toast.makeText(MainActivity.this, "Please login first. ", Toast.LENGTH_LONG).show();
	    	 showAuthenticateDialog();
          	setLastAction(Action.TEST);
       }
        return super.onOptionsItemSelected(item);
    }
    public void scanNow(View view) {
        QBUser qbUser = ((App) getApplication()).getQbUser();
if (qbUser !=null){
    	Log.d("test", "button works!");
    	(new IntentIntegrator(this)).initiateScan();
     
    } else{
    	setLastAction(Action.TEST);
        Toast.makeText(MainActivity.this, "Please login first.", Toast.LENGTH_LONG).show();

    	 showAuthenticateDialog();
    	 Log.d("test", "show authenti!");
  

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
    protected void onActivityResult(int requestCode, int resultCode, Intent Intent) {

    	IntentResult scan=IntentIntegrator.parseActivityResult(requestCode,
                resultCode,
                Intent);
    	if(scan!=null){
    	Log.d("ccc","result from scan: "+scan.getContents());
    	locationInfo=""+scan.getContents();
    	
    	String[] splitStrings = scan.getContents().split(",");
    	double x = Double.parseDouble(scan.getContents().split(",")[1]);
    	double y = Double.parseDouble(scan.getContents().split(",")[2]);

      	QBLocation location = new QBLocation(x,y, ""+splitStrings[0]);
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

   /*public void showRoomsFragment() {

        getSupportActionBar().selectTab(getSupportActionBar().getTabAt(POSITION_ROOM));
        viewPager.setCurrentItem(POSITION_ROOM);
        ((RoomsFragment) sectionsPagerAdapter.getItem(POSITION_ROOM)).loadRooms();


    }*/
    
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

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onComplete(Result arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onComplete(Result arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
}
