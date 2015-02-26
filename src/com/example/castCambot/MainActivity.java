/*
 * Copyright (C) 2014 Google Inc. All Rights Reserved. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.castCambot;

//import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
//import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.casthelloworld.R;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.Cast.ApplicationConnectionResult;
import com.google.android.gms.cast.Cast.MessageReceivedCallback;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Main activity to send messages to the receiver.
 */
public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 1;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private Cast.Listener mCastListener;
    private ConnectionCallbacks mConnectionCallbacks;
    private ConnectionFailedListener mConnectionFailedListener;
    private HelloWorldChannel mHelloWorldChannel;
    private boolean mApplicationStarted;
    private boolean mWaitingForReconnect;
    protected String cambotIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(
                android.R.color.transparent));

        // When the user clicks on the button, use Android voice recognition to
        // get text

       
        Button voiceButton = (Button) findViewById(R.id.voiceButton);
        voiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });

        
        Button buttonOK = (Button) findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startVoiceRecognitionActivity();
            	EditText editText1 = (EditText) findViewById(R.id.editText1);
            	cambotIP = editText1.getText().toString();
            	Button buttonOK = (Button) findViewById(R.id.buttonOK);
            	buttonOK.setVisibility(View.INVISIBLE);
            	
            	/*
            	WebView	mWebView = (WebView) findViewById(R.id.webView1);
            	mWebView.getSettings().setJavaScriptEnabled(true);     
            	mWebView.getSettings().setLoadWithOverviewMode(true);
            	mWebView.getSettings().setUseWideViewPort(true);     
            	//mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            	mWebView.loadUrl("file:///android_asset/cambot.html");
            	*/
            }
        });
        
        
        
        
        
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnTouchListener(new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View view, MotionEvent event) {
        		int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN)
                    //System.out.println("Touch");
                	myHttpPost("go_forward");
                else if (action == MotionEvent.ACTION_UP)
                    //System.out.println("Release");
                	myHttpPost("stop");
                return false;   //  the listener has NOT consumed the event, pass it on
            }});

        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnTouchListener(new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View view, MotionEvent event) {
        		int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN)
                    //System.out.println("Touch");
                	myHttpPost("turn_left");
                else if (action == MotionEvent.ACTION_UP)
                    //System.out.println("Release");
                	myHttpPost("stop");
                return false;   //  the listener has NOT consumed the event, pass it on
            }});
        
        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnTouchListener(new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View view, MotionEvent event) {
        		int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN)
                    //System.out.println("Touch");
                	myHttpPost("turn_right");
                else if (action == MotionEvent.ACTION_UP)
                    //System.out.println("Release");
                	myHttpPost("stop");
                return false;   //  the listener has NOT consumed the event, pass it on
            }});

        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnTouchListener(new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View view, MotionEvent event) {
        		int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN)
                    //System.out.println("Touch");
                	myHttpPost("go_backward");
                else if (action == MotionEvent.ACTION_UP)
                    //System.out.println("Release");
                	myHttpPost("stop");
                return false;   //  the listener has NOT consumed the event, pass it on
            }});

        
        
        
        // Configure Cast device discovery
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(getResources()
                                .getString(R.string.app_id))).build();
        mMediaRouterCallback = new MyMediaRouterCallback();
    }


    
    @Override
    protected void onResume() {
        super.onResume();
        // Start media router discovery
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            // End media router discovery
            mMediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        teardown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat
                .getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        return true;
    }

    /**
     * Callback for MediaRouter events
     */
    private class MyMediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, RouteInfo info) {
            Log.d(TAG, "onRouteSelected");
            // Handle the user route selection.
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            launchReceiver();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, RouteInfo info) {
            Log.d(TAG, "onRouteUnselected: info=" + info);
            teardown();
            mSelectedDevice = null;
        }
    }

    /**
     * Start the receiver app
     */
    private void launchReceiver() {
        try {
            mCastListener = new Cast.Listener() {

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d(TAG, "application has stopped");
                    teardown();
                }

            };
            // Connect to Google Play services
            mConnectionCallbacks = new ConnectionCallbacks();
            mConnectionFailedListener = new ConnectionFailedListener();
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastListener);
            mApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "Failed launchReceiver", e);
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "onConnected");

            if (mApiClient == null) {
                // We got disconnected while this runnable was pending
                // execution.
                return;
            }

            try {
                if (mWaitingForReconnect) {
                    mWaitingForReconnect = false;

                    // Check if the receiver app is still running
                    if ((connectionHint != null)
                            && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                        Log.d(TAG, "App  is no longer running");
                        teardown();
                    } else {
                        // Re-create the custom message channel
                        try {
                            Cast.CastApi.setMessageReceivedCallbacks(mApiClient,
                                    mHelloWorldChannel.getNamespace(),
                                    mHelloWorldChannel);
                        } catch (IOException e) {
                            Log.e(TAG, "Exception while creating channel", e);
                        }
                    }
                } else {
                    // Launch the receiver app
                    Cast.CastApi
                            .launchApplication(mApiClient,
                                    getString(R.string.app_id), false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(
                                                ApplicationConnectionResult result) {
                                            Status status = result.getStatus();
                                            Log.d(TAG,
                                                    "ApplicationConnectionResultCallback.onResult: statusCode"
                                                            + status.getStatusCode());
                                            if (status.isSuccess()) {
                                                ApplicationMetadata applicationMetadata = result
                                                        .getApplicationMetadata();
                                                String sessionId = result
                                                        .getSessionId();
                                                String applicationStatus = result
                                                        .getApplicationStatus();
                                                boolean wasLaunched = result
                                                        .getWasLaunched();
                                                Log.d(TAG,
                                                        "application name: "
                                                                + applicationMetadata
                                                                        .getName()
                                                                + ", status: "
                                                                + applicationStatus
                                                                + ", sessionId: "
                                                                + sessionId
                                                                + ", wasLaunched: "
                                                                + wasLaunched);
                                                mApplicationStarted = true;

                                                // Create the custom message
                                                // channel
                                                mHelloWorldChannel = new HelloWorldChannel();
                                                try {
                                                    Cast.CastApi
                                                            .setMessageReceivedCallbacks(
                                                                    mApiClient,
                                                                    mHelloWorldChannel
                                                                            .getNamespace(),
                                                                    mHelloWorldChannel);
                                                } catch (IOException e) {
                                                    Log.e(TAG,
                                                            "Exception while creating channel",
                                                            e);
                                                }

                                                // set the initial instructions
                                                // on the receiver
                                                //sendMessage(getString(R.string.instructions));
                                                sendMessage(cambotIP);
                                                
                                            } else {
                                                Log.e(TAG,
                                                        "application could not launch");
                                                teardown();
                                            }
                                        }
                                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch application", e);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "onConnectionSuspended");
            mWaitingForReconnect = true;
        }
    }

    /**
     * Google Play services callbacks
     */
    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.e(TAG, "onConnectionFailed ");

            teardown();
        }
    }

    /**
     * Tear down the connection to the receiver
     */
    private void teardown() {
        if (mApiClient != null) {
            if (mApplicationStarted) {
                try {
                    Cast.CastApi.stopApplication(mApiClient);
                    if (mHelloWorldChannel != null) {
                        Cast.CastApi.removeMessageReceivedCallbacks(mApiClient,
                                mHelloWorldChannel.getNamespace());
                        mHelloWorldChannel = null;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Exception while removing channel", e);
                }
                mApplicationStarted = false;
            }
            if (mApiClient.isConnected()) {
                mApiClient.disconnect();
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
    }

    /**
     * Send a text message to the receiver
     * 
     * @param message
     */
    private void sendMessage(String message) {
        if (mApiClient != null && mHelloWorldChannel != null) {
            try {
                Cast.CastApi.sendMessage(mApiClient,
                        mHelloWorldChannel.getNamespace(), message)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                    Log.e(TAG, "Sending message failed");
                                }
                            }
                        });
            } catch (Exception e) {
                Log.e(TAG, "Exception while sending message", e);
            }
        } else {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Custom message channel
     */
    class HelloWorldChannel implements MessageReceivedCallback {

        /**
         * @return custom namespace
         */
        public String getNamespace() {
            return getString(R.string.namespace);
        }

        /*
         * Receive message from the receiver app
         */
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace,
                String message) {
            Log.d(TAG, "onMessageReceived: " + message);
        }

    }

    /**
     * HTTP POST
     */
    private void myHttpPost(String message) {
    	//String	uriAPI = "http://192.168.0.230:8000/macros/" + message + "/";
    	
    	// Create a new HttpClient and Post Header
        new myHttpPostAsyncTask().execute(message);
    	
    	
    }


	private class myHttpPostAsyncTask extends AsyncTask<String, Void, Void>{
	@Override
	    protected Void doInBackground(String... s){
	        // get zero index of nameValuePairs and use that to post
		
			HttpClient httpclient = new DefaultHttpClient();
			// String postUrl = "http://192.168.0.230:8000/macros/"+s[0]+"/";
			String postUrl = "http://" + cambotIP + ":8000/macros/" + s[0] + "/";
			System.out.println(postUrl);
			
		    HttpPost httppost = new HttpPost(postUrl);
		
		    try {
		        // Add your data
		        //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        //nameValuePairs.add(new BasicNameValuePair("id", "12345"));
		        //nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
		        //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        
		    	} catch (ClientProtocolException e) {
		    		// TODO Auto-generated catch block
		    	} catch (IOException e) {
		    		// TODO Auto-generated catch block
		    }
	
			return null;
	    }
	
	}
	
	/**
     * Android voice recognition
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.message_to_cast));
        startActivityForResult(intent, REQUEST_CODE);
    }

    /*
     * Handle the voice recognition response
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0) {
                Log.d(TAG, matches.get(0));
                //sendMessage(matches.get(0));
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                String	text = matches.get(0);
                
                Toast toast = Toast.makeText(context, text, duration);
            	toast.show();
                
                
                String goForward 	= "forward";
                String turnLeft		= "left";
                String turnRight	= "right";
                String goBackwards  = "backward";
                
                if (text.contains(goForward))	{
                	myHttpPost("go_forward");
                	mySleep(1000);
                	myHttpPost("stop");
                }
                
                if (text.contains(turnLeft))	{
                	myHttpPost("turn_left");            	
                	mySleep(500);
                	myHttpPost("stop");
                	
                }
                
                if (text.contains(turnRight))	{
                	myHttpPost("turn_right");
                	mySleep(500);
                	myHttpPost("stop");
                	
                }
                
                if (text.contains(goBackwards))	{
                	myHttpPost("go_backward");
                	mySleep(1000);
                	myHttpPost("stop");
                	
                }
                
                //auto trigger voiceButton on click
                //Button voiceButton = (Button) findViewById(R.id.voiceButton);
                //voiceButton.performClick();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private	void mySleep(long i)
    {
    	try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
    
}
