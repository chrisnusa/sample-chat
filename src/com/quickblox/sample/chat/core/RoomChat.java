package com.quickblox.sample.chat.core;

import java.util.Calendar;
import java.util.Date;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.module.chat.QBChatRoom;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.ChatMessageListener;
import com.quickblox.module.chat.listeners.RoomListener;
import com.quickblox.module.chat.utils.QBChatUtils;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.chat.App;
import com.quickblox.sample.chat.model.ChatMessage;
import com.quickblox.sample.chat.ui.activities.ChatActivity;
import com.quickblox.sample.chat.ui.activities.MainActivity;

public class RoomChat implements Chat, RoomListener, ChatMessageListener {

    public static final String EXTRA_ROOM_NAME = "name";
    public static final String EXTRA_ROOM_ACTION = "action";
    private static final String TAG = RoomChat.class.getSimpleName();
    private ChatActivity chatActivity;
    private QBChatRoom chatRoom;

    public RoomChat(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
             
        String chatRoomName = chatActivity.getIntent().getStringExtra(EXTRA_ROOM_NAME);
        RoomAction action = (RoomAction) chatActivity.getIntent().getSerializableExtra(EXTRA_ROOM_ACTION);
 if(action==RoomAction.DONTJOIN)
     Log.d("dontjoin","dontjoin");


        QBUser qbUser = ((App) (chatActivity.getApplication())).getQbUser();
       QBChatRoom room=  ((App) chatActivity.getApplication()).getCurrentRoom();
        if(action==RoomAction.JOIN){
     
            if(qbUser.getTags().get(0).equals(room.getName())){
                Log.d("important info about room1",""+room.getName());
                Log.d("important info about room2",""+room.getName());
                Log.d("important info about room3",""+qbUser.getTags().get(0));
                Log.d("important info about room4",""+qbUser.getTags().get(0));
                join( ((App) chatActivity.getApplication()).getCurrentRoom());

            }
        
            else    {
                Toast.makeText(chatActivity, "can't join this group chat", Toast.LENGTH_LONG).show();
                Toast.makeText(chatActivity, "" + qbUser.getTags(), Toast.LENGTH_LONG).show();
            Log.d("important info about room2",""+room.getName());
            Log.d("important info about room3",""+qbUser.getTags().get(0));
//send intent to chat activity
            //QBChatService.getInstance().leaveRoom(room);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(chatActivity);
     
    			// set title
    			alertDialogBuilder.setTitle("You're not in this group");
     
    			// set dialog message
    			alertDialogBuilder
    				.setMessage("Please go to your group chat at: "+qbUser.getTags().get(0))
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

            }}
        
        
        	
        
   
        
    
        }

    @Override
    public void sendMessage(String message) throws XMPPException {
        if (chatRoom != null) {
            chatRoom.sendMessage(message);
        } else {
            Toast.makeText(chatActivity, "Join unsuccessful, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void release() throws XMPPException {
        if (chatRoom != null) {
            QBChatService.getInstance().leaveRoom(chatRoom);
            chatRoom.removeMessageListener(this);
        }
    }

    @Override
    public void onCreatedRoom(QBChatRoom room) {
        Log.d(TAG, "room was created");
        chatRoom = room;
        chatRoom.addMessageListener(this);
    }

    @Override
    public void onJoinedRoom(QBChatRoom room) {
      Log.d(TAG, "joined to room");
        chatRoom = room;
        chatRoom.addMessageListener(this);

    /*    QBChatService.getInstance().getRooms(new RoomReceivingListener() {
            @Override
            public void onReceiveRooms(List<QBChatRoom> qbChatRooms) {
                for (QBChatRoom room : qbChatRooms) {
                    Log.d(TAG, "Received room " + room.getName());
                   // QBUser qbUser = ((App) getApplication()).getQbUser();
        //            join(room);
                    if(room.getName()=="G1"){
                    	List<Integer> userIds = new ArrayList<Integer>();
                      
                        userIds.add(511854);
                        try {
							room.addRoomUsers(userIds);
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                    	
                  //  QBChatService.getInstance().destroyRoom(room);

                }
            }
        });
        // QBChatService.getInstance().destroyRoom(room);

        // Add users to this room
   /*     if(room.getName()=="g1"){
        List<Integer> userIds = new ArrayList<Integer>();
        userIds.add(832480);
        //userIds.add(357);
        try {
			chatRoom.addRoomUsers(userIds);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        QBUser qbUser = ((App) (chatActivity.getApplication())).getQbUser();

        if(qbUser.getId()!=832480){
        	try {
				QBChatService.getInstance().leaveRoom(chatRoom);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	chatRoom.removeMessageListener(this);

        }
        }*/
    }

    @Override
    public void onError(String msg) {
        Log.d(TAG, "error joining to room");
    }

    @Override
    public void processMessage(Message message) {
        Date time = QBChatUtils.parseTime(message);
        if (time == null) {
            time = Calendar.getInstance().getTime();
        }
        // Show message
        String sender = QBChatUtils.parseRoomOccupant(message.getFrom());
        QBUser qbUser = ((App) (chatActivity.getApplication())).getQbUser();
        if (sender.equals(qbUser.getFullName()) || sender.equals(qbUser.getId().toString())) {
            chatActivity.showMessage(new ChatMessage(message.getBody(), "me", time, false));
        } else {
            chatActivity.showMessage(new ChatMessage(message.getBody(), sender, time, true));
        }
    }

    @Override
    public boolean accept(Message.Type messageType) {
        switch (messageType) {
            case groupchat:
                return true;
            default:
                return false;
        }
    }
  
    public void create(String roomName) {
        // Creates member & persistent room
        QBChatService.getInstance().createRoom(roomName, true, true, this);
    }

    public void join(QBChatRoom room) {
        QBChatService.getInstance().joinRoom(room, this);
    }
    public static enum RoomAction {CREATE, JOIN,DONTJOIN}

}
