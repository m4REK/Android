package com.example.marko.chatclient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*

TODO: AppIcon
TODO: Sounds
TODO: Push Benachritigung??
*/

public class MainActivity extends Activity {

    private Button sendButton;
    private EditText messageToSend;
    private TextView messagesArea;
    private CheckBox soundCheckBoxx;

    private PrintStream outStream;
    private BufferedReader bufferedReader;

    private String host, userName;

    private Socket client;
    private Toast toastAvalible, toastNotAvalible;

    //fot Line splitting
    private String[] splitedLine;
    private String command, details, lineRecivedFromServer;

    //Audio feedback for Messages
    private MediaPlayer sound;


    public class ClientConnector extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                client = new Socket(host, 1234);
                outStream = new PrintStream(client.getOutputStream());
                outStream.println(userName);

                if (client.isConnected()) {
                    toastAvalible.show();
                    messagesArea.post(new Runnable() {
                        @Override
                        public void run() {
                            messagesArea.append(userName + " WELCOME @" + host + "\n");
                        }
                    });
                } else {
                    //if server not found or dead
                    sendButton.setEnabled(false);
                    toastNotAvalible.show();
                }

                //Server listening
                Thread listenThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

                            while (true) {

                                lineRecivedFromServer = bufferedReader.readLine();
                                messagesArea.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        showMessage(splittedString(lineRecivedFromServer));
                                    }
                                });
                            }

                        } catch (IOException e) {
                        }
                    }
                });

                listenThread.start();

            } catch (UnknownHostException uHE) {
                toastNotAvalible.show();
                sendButton.setEnabled(false);
                sendButton.setText("X");
                sendButton.setTextColor(Color.parseColor("#ff0000"));
                messagesArea.post(new Runnable() {
                    @Override
                    public void run() {
                        messagesArea.append("Server @" + host + " ist nicht erreichbar! \n");
                        messagesArea.append("BITTE GÜLTIGE SERVERADRESSE EINGEBEN!");
                    }
                });

            } catch (IOException iOE) {
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = MediaPlayer.create(getApplicationContext(),R.raw.sound);

        toastAvalible = Toast.makeText(this, "Verbunden", Toast.LENGTH_LONG);
        toastNotAvalible = Toast.makeText(this, "Server nicht erreichbar", Toast.LENGTH_LONG);

        Intent reciveData = getIntent();

        //UI ELEMENTE
        sendButton = (Button) findViewById(R.id.sendButton);
        messagesArea = (TextView) findViewById(R.id.messagesArea);
        messagesArea.setMovementMethod(new ScrollingMovementMethod());   //mesaggesArea SCROLL-BAR
        messageToSend = (EditText) findViewById(R.id.messageTextField);
        soundCheckBoxx = (CheckBox) findViewById(R.id.soundCheckBox);

        //Daten von der ClientActivity holen
        host = reciveData.getStringExtra("host");
        userName = reciveData.getStringExtra("user");

        //Client Registrieren
        new ClientConnector().execute();

        //Button Listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void sendMessage() {
        outStream.println(messageToSend.getText());
        messageToSend.setText("");
    }

    public void showMessage(String message) {
        messagesArea.append(message + "\n");
        if(soundCheckBoxx.isChecked()){
            sound.start();
        }
        //Scrolling TextView to a last line of message field
        final Layout layout = messagesArea.getLayout();
        if (layout != null) {
            int scrollDelta = layout.getLineBottom(messagesArea.getLineCount() - 1)
                    - messagesArea.getScrollY() - messagesArea.getHeight();
            if (scrollDelta > 0)
                messagesArea.scrollBy(0, scrollDelta);
        }
    }

    public String splittedString(String line) {

        splitedLine = line.split("\\|");
        command = splitedLine[0];
        details = splitedLine[1];

        return details;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
