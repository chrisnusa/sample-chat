package com.quickblox.sample.chat.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.ui.activities.ChatActivity.Mode;
import com.quickblox.sample.chat.ui.activities.MainActivity.Action;
import com.quickblox.sample.chat.ui.activities.MainActivity.SectionsPagerAdapter;
import com.quickblox.sample.chat.ui.fragments.RoomsFragment;

public class ProfileActivity extends Activity {
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private static final int POSITION_ROOM = 1;

    public static final String EXTRA_MODE = "mode";
	//private static final String GROUP = null;
    public static enum Mode {SINGLE, GROUP,test}
	MainActivity m=new MainActivity();
	RoomsFragment r = new RoomsFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		// Show the Up button in the action bar.
		setupActionBar();
        QBUser qbUser = ((App) getApplication()).getQbUser();
        TextView v1;
    	v1=(TextView) findViewById(R.id.textView1);

        if(qbUser!=null){
        	v1.setText(""+qbUser.getEmail());
        }else
        	v1.setText("please login first");
        
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
	

		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
		//	Intent i = new Intent(this, MainActivity.class);
			//i.putExtra("please","647");
//			startActivity(i);
          //  viewPager.setCurrentItem(POSITION_ROOM);
          //  ((RoomsFragment) sectionsPagerAdapter.getItem(POSITION_ROOM)).loadRooms();
			
		//	m.setLastAction(Action.ROOM_LIST);
			//m.showRoomsFragment();
			//m.showUsersFragment();
          //  ((RoomsFragment) sectionsPagerAdapter.getItem(POSITION_ROOM)).loadRooms();
        
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
