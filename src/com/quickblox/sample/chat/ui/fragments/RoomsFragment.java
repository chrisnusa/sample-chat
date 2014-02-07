package com.quickblox.sample.chat.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.quickblox.module.chat.QBChatRoom;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.RoomReceivingListener;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.R;
import com.quickblox.sample.chat.core.RoomChat;
import com.quickblox.sample.chat.ui.activities.ChatActivity;

public class RoomsFragment extends Fragment implements RoomReceivingListener {

    private static final String KEY_ROOM_NAME = "roomName";
    private ListView roomsList;
    private List<QBChatRoom> rooms;
    private ProgressDialog progressDialog;
    //private Context chatRoom;
    public static RoomsFragment getInstance() {
        return new RoomsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rooms, container, false);
        roomsList = (ListView) v.findViewById(R.id.roomsList);
    
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rooms, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
   //     if (id == R.id.action_bar) {

       if (id == R.id.action_add) {
    	   Intent intent = new Intent("com.google.zxing.client.android.SCAN"); intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE"); startActivityForResult(intent, 0);
       }
         /*   AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Title");
            final EditText input = new EditText(getActivity());
            builder.setView(input);

            builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle bundle = createChatBundle(input.getText().toString(), true);
                    ChatActivity.start(getActivity(), bundle);
                }
            });
            builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		 IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		 
	 
	        if(scanResult != null) {
	            String scanContent = " ";
	 
	            scanContent += scanResult.getContents();
	            Log.d("a", scanContent);

	         //   textContent.setText(scanContent);
	       //     Intent i = new Intent(QRScanner.this, ChooseChat.class);
		//		  startActivity(i); // brings up the second activity

	}
	}
	
   
    @Override
    public void onReceiveRooms(List<QBChatRoom> list) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        
        rooms = list;

        // Prepare rooms list for simple adapter.
        final List<Map<String, String>> roomsListForAdapter = new ArrayList<Map<String, String>>();
        for (QBChatRoom room : rooms) {
           Map<String, String> roomMap = new HashMap<String, String>();
            roomMap.put(KEY_ROOM_NAME, room.getName());
            roomsListForAdapter.add(roomMap);
        }

        // Put rooms list into adapter.
        final SimpleAdapter roomsAdapter = new SimpleAdapter(getActivity(), roomsListForAdapter,
                R.layout.list_item_room,
                new String[]{KEY_ROOM_NAME},
                new int[]{R.id.roomName});

        roomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	

                Bundle bundle = createChatBundle(rooms.get(position).getName(), false);
                ((App)getActivity().getApplication()).setCurrentRoom(rooms.get(position));
                ChatActivity.start(getActivity(), bundle);
             
              
                
                  
            }
        });

        roomsList.setAdapter(roomsAdapter);
    }



	public void loadRooms() {
        if (getActivity() != null) {
            progressDialog = ProgressDialog.show(getActivity(), null, "Loading rooms list");
        }
        QBChatService.getInstance().getRooms(this);
    }

    private Bundle createChatBundle(String roomName, boolean createChat) {
        Bundle bundle = new Bundle();
       
        bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);
        bundle.putString(RoomChat.EXTRA_ROOM_NAME, roomName);
   //     if (createChat) {
       //     bundle.putSerializable(RoomChat.EXTRA_ROOM_ACTION, RoomChat.RoomAction.CREATE);
       // } else  
       // if(qbUser.getTags().get(0).equals(room.getName())){ 
            bundle.putSerializable(RoomChat.EXTRA_ROOM_ACTION, RoomChat.RoomAction.JOIN);
     //   } else
  //          bundle.putSerializable(RoomChat.EXTRA_ROOM_ACTION, RoomChat.RoomAction.DONTJOIN);

        return bundle;
    }
}
