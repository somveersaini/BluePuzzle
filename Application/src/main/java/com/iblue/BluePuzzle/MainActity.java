package com.iblue.BluePuzzle;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActity extends BaseActivity {

    private static final String TAG = "MainActity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private boolean bga = true;
    private boolean sfx = true;
    Switch sbga, ssfx;
    boolean myturn = true;
    int chal = 0;


    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;


    GridView grid;

    public String name = "Player One";
    GameAdapter adapter;
    ResideMenu resideMenu;
    int COL = 8;
    SharedPreferences settings;
    final String[] numbers = new String[]{
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    private int backButtonCount = 0;
    private long backButtonPreviousTime = 0;
    private boolean backButtonMessageHasBeenShown = false;
    private boolean starter = true;
    MediaPlayer mp;
    SoundPool sp;
    int scm, scb, scs, sce, wel, win;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getAttributes().windowAnimations = R.style.win_animation;

        sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        scm = sp.load(this, R.raw.scm, 1);
        scb = sp.load(this, R.raw.scb, 1);
        scs = sp.load(this, R.raw.scs, 1);
        wel = sp.load(this, R.raw.wel, 1);
        win = sp.load(this, R.raw.win, 1);
        sce = sp.load(this, R.raw.sce, 1);// in 2nd param u have to pass your desire ringtone

        mp = MediaPlayer.create(getApplicationContext(), R.raw.bgaudio);
        mp.setLooping(true);

        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        load();
        initGame();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            // Activity activity = this;
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.waterbg);
        resideMenu.attachToActivity(this);
        String titles[] = {"Home", "Profile", "Settings"};
        int icon[] = {R.drawable.ic_home, R.drawable.ic_face, R.drawable.ic_settings};
        for (int i = 0; i < titles.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            final int finalI = i;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sfx) {
                        sp.play(scs, 1, 1, 0, 0, 1);
                    }
                    itemClicked(finalI);
                }
            });
            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }

        String titles1[] = {"Logs", "Reset", "Connect"};
        int icon1[] = {R.drawable.reset, R.drawable.logs, R.drawable.connect};
        for (int i = 0; i < titles1.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this, icon1[i], titles1[i]);
            final int finalI = i + 3;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sfx) {
                        sp.play(scs, 1, 1, 0, 0, 1);
                    }
                    itemClicked(finalI);
                }
            });
            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_RIGHT); // or  ResideMenu.DIRECTION_RIGHT
        }
        welcome();
        setmyturn();

    }

    public void startbga() {
        if (bga) {
            // mp = MediaPlayer.create(getApplicationContext(), R.raw.bgaudio);
            mp.start();
        }
    }

    //saveState
    private void save() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", adapter.player1);
        editor.putBoolean("bga", bga);
        editor.putBoolean("sfx", sfx);
        editor.commit();
    }

    //loadState
    private void load() {
        name = settings.getString("name", "Player one");
        sfx = settings.getBoolean("sfx", true);
        bga = settings.getBoolean("bga", true);
    }

    //initialize the game
    private void initGame() {
        grid = (GridView) findViewById(R.id.gridView);
        adapter = new GameAdapter(this, COL);
        adapter.setName(name);
        grid.setAdapter(adapter);
        grid.setEnabled(true);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (sfx) {
                    sp.play(scm, 1, 1, 0, 0, 1);
                }
                TextView sam = (TextView) v;
                if (sam.getText() == "") {
                    keyboardDialog(sam, position);
                }
            }
        });
        startbga();
    }

    private void winner() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptsView = inflater.inflate(R.layout.logs, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final TextView logs = (TextView) promptsView
                .findViewById(R.id.logs);
        logs.setTextSize(30);
        int s1 = adapter.currentScore;
        int s2 = adapter.currentScore2;
        if (sfx) {
            sp.play(win, 1, 1, 0, 0, 1);
        }
        if (s1 == s2) {
            logs.setText("\n\n Game \n Darw \n\n");
        } else if (s1 > s2) {
            logs.setText("\n\n  " + name + " \n Winning By \n " + (s1 - s2) + " points\n");
        } else {
            logs.setText("\n\n  " + adapter.player2 + " \n Winning By \n " + (s2 - s1) + " points\n");
        }
        //logs.setText("\nWelcome " + name + "\n Let's play Blue\n");


        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void refresh() {
        initGame();
        welcome();
        setmyturn();
        bluetoothSetup();
    }

    //get name
    public void firstDialog() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptsView = inflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String user = userInput.getText().toString();
                                if (user.length() > 0 && user.length() < 11) {
                                    adapter.setName(user);
                                    name = user;
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void logs() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptsView = inflater.inflate(R.layout.logs, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);


        final TextView logs = (TextView) promptsView
                .findViewById(R.id.logs);
        logs.setText(adapter.logs);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();
    }

    public void welcome() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptsView = inflater.inflate(R.layout.welcomedialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final TextView logs = (TextView) promptsView
                .findViewById(R.id.thanku);
        logs.setTextSize(25);
        logs.setText(" Welcome " + name + " ");
        final TextView hh = (TextView) promptsView
                .findViewById(R.id.ht);

        hh.setText(" Get in the Game ");

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        setmyturn();
        if (sfx) {
            sp.play(wel, 1, 1, 0, 0, 1);
        }
        alertDialog.show();

    }

    public void setmyturn() {
        adapter.turn = 0;
    }

    public void setSettings() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptsView = inflater.inflate(R.layout.settings, null);
        sbga = (Switch) promptsView.findViewById(R.id.switch1);
        ssfx = (Switch) promptsView.findViewById(R.id.switch2);
        if (bga) {
            sbga.setChecked(true);
        }
        if (sfx) {
            ssfx.setChecked(true);
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();
    }

    public void keyboardDialog(final TextView sam, final int p) {

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GridView gridView;
        gridView = (GridView) inflater.inflate(R.layout.key_grid, null);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.key, numbers);

        gridView.setAdapter(adapter);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(gridView);
        final AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertD.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {


                int red = 0;
                int blue = 0;
                int green = 255;
                int color;


                red = (red + chal * 8) % 255;
                blue = (blue + chal * 8) % 255;
                color = Color.rgb(red, green, blue);
                sam.setBackgroundColor(color);
                chal++;

                if (myturn) {
                    myturn = false;
                } else {
                    myturn = true;
                    if (mChatService.getState() != 3) {
                        sam.setTextColor(Color.RED);
                    }
                }


                if (sfx) {
                    sp.play(scm, 1, 1, 0, 0, 1);
                }
                if (mChatService.getState() == 3) {
                    setmyturn();
                    sendMessage(p + " " + ((TextView) v).getText());
                    GridView g = (GridView) findViewById(R.id.gridView);
                    g.setEnabled(false);
                }


                alertD.dismiss();
                sam.setText(((TextView) v).getText());
                YoYo.with(Techniques.FlipInX)
                        .duration(700)
                        .playOn(sam);

            }
        });
    }

    public void bluetoothinput(int pos, String msg) {
        if (sfx) {
            sp.play(scb, 1, 1, 0, 0, 1);
        }
        if (pos == -1) {
            setOponentName(msg);
        } else {
            adapter.turn = 1;
            try {
                GridView g = (GridView) findViewById(R.id.gridView);
                g.setEnabled(true);
                View v = g.getChildAt(pos);
                Log.d("updated", msg + " at " + pos);
                if (v instanceof TextView) {
                    TextView sam = (TextView) v;
                    sam.setText(msg);
                    sam.setTextColor(Color.RED);

                    int red = 255;
                    int blue = 0;
                    int green = 255;
                    int color;

                    blue = (blue + chal * 8) % 255;
                    color = Color.rgb(red, green, blue);
                    sam.setBackgroundColor(color);
                    //Drawable d = ContextCompat.getDrawable(this, R.drawable.bluelable);
                    // Drawable tbg = sam.getBackground();
                    // tbg.setColorFilter(color, PorterDuff.Mode.ADD);
                    //  sam.setBackground(tbg);

                    YoYo.with(Techniques.FlipInX)
                            .duration(700)
                            .playOn(sam);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOponentName(String name) {
        adapter.setOponentName(name);
    }

    public void itemClicked(int i) {
        switch (i) {
            case 0:
                this.finish();
                break;

            case 1:
                firstDialog();
                break;

            case 2:
                setSettings();
                break;

            case 3:
                logs();
                winner();
                break;

            case 4:
                disconnet();
                refresh();
                break;

            case 5:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                ensureDiscoverable();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = new BluetoothChatService(this, mHandler);
        mOutStringBuffer = new StringBuffer("");
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    //* Sends a message.(String message)
    private void sendMessage(String message) {
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //  Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            mOutStringBuffer.setLength(0);

        }
    }

    // * Updates the status on the action bar. usin resID
    private void setStatus(int resId) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
    }

    private void setStatus(CharSequence subTitle) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
        TextView g = (TextView) findViewById(R.id.bluename);
        g.setText(subTitle);
        sendMessage(-1 + " " + name);
        refresh();
        adapter.turn = 0;
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getParent();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));


                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            // setStatus("Connecting...");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //  setStatus("Currently not connected");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("sam", readMessage);
                    String[] m = readMessage.split(" ");
                    bluetoothinput(Integer.parseInt(m[0]), m[1]);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
        }
    }

    //* Establish connection with other divice
    private void connectDevice(Intent data, boolean secure) {
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device, secure);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    public void bluetoothSetup() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
        save();
        mp.stop();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        final long currentTime = System.currentTimeMillis();
        final long timeDiff = currentTime - backButtonPreviousTime;

        backButtonPreviousTime = currentTime;

        if ((timeDiff < Constants.BACK_PRESS_DELAY) || (backButtonCount == 0)) {
            backButtonCount++;
        } else {
            backButtonCount = 1;
        }

        if (backButtonCount >= Constants.BACK_PRESS_COUNT) {
            finish();
            Intent intent = new Intent(this, MainMenu.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);

            startActivity(intent);
        }

        if (!backButtonMessageHasBeenShown) {
            final String msg = "Press back " + Constants.BACK_PRESS_COUNT + " times to exit";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            backButtonMessageHasBeenShown = true;
        }
    }

    public void buttonHandler2(View view) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        ensureDiscoverable();
    }

    public void buttonsettings(View view) {
        final int id = view.getId();
        if (sfx) {
            sp.play(sce, 1, 1, 0, 0, 1);
        }
        Switch s = (Switch) view;
        boolean isChecked = s.isChecked();
        if (id == R.id.switch1) {
            if (isChecked) {
                bga = true;
                mp.start();
            } else {
                mp.pause();
                bga = false;
            }

        } else if (id == R.id.switch2) {
            sfx = isChecked;
        }
    }

    public void disconnet() {
        if (mChatService != null) {
            mChatService.stop();
        }
        mChatService.start();
    }

    public void blue(View view) {
        View v = findViewById(R.id.imageView);
        YoYo.with(Techniques.RotateIn)
                .duration(700)
                .playOn(v);

    }
}
