package spr018.tcss450.clientapplication;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *  Intent service to handle all connection requests and chat notifications.
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class NotificationIntentService extends IntentService {

    /**Notification id for connection request notifications*/
    public static final int NOTIFICATION_REQUEST_ID = -1;

    public static final String HOME_FRAGMENT = "homeFragment";
    private static final int POLL_INTERVAL = 2 * 60000;
    private static final String NOTIFICATION_GROUP = "TCSS450 NOTIFICATION";

    private NotificationManager notifManager;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //Log.d("TAG", "Service started");
            getRequests(intent.getStringExtra(getString(R.string.keys_editor_username)));
            getChatRequests(intent.getStringExtra(getString(R.string.keys_editor_username)));
        }
    }

    /**
     *  Starts the notification service while polling web service.
     * @param context - current state of the app.
     * @param isInForeground - checks whether app is in foreground
     * @param username - username of user.
     */
    public static void startServiceAlarm(Context context, boolean isInForeground, String username) {
        Intent i = new Intent(context, NotificationIntentService.class);
        i.putExtra(context.getString(R.string.keys_is_foreground), isInForeground);
        i.putExtra(context.getString(R.string.keys_editor_username), username);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int startAfter = isInForeground ? POLL_INTERVAL : POLL_INTERVAL * 2;
        try {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                    , startAfter
                    , POLL_INTERVAL, pendingIntent);
        } catch ( NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stops the notification service.
     * @param context - state of the app.
     */
    public static void stopServiceAlarm(Context context) {
        Intent i = new Intent(context, NotificationIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**
     *  Gets all requests for a particular user.
     * @param mUsername - username
     */
    private void getRequests(String mUsername) {
        //Log.wtf("TAG420", mUsername);
        AsyncTask<String, Void, String> task = new RequestNotificationTask();
        task.execute(getString(R.string.ep_base_url),
                getString(R.string.ep_getConnectionRequestsNotifications),
                mUsername);
    }

    /**
     *  Gets all of the notifications if any recent messages have been sent.
     * @param mUsername
     */
    private void getChatRequests(String mUsername) {
        AsyncTask<String, Void, String> task = new ChatNotificationTask();
        task.execute(getString(R.string.ep_base_url),
                getString(R.string.ep_getChatNotifications),
                mUsername);
    }

    /**
     *  Creates a notification for a new connection request.
     * @param aMessage - body of the message.
     * @param idNotify - channel  of notification
     */
    public void createRequestNotification(String aMessage, int idNotify) {
         int NOTIFY_ID = idNotify;
        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent mainActivityIntent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        SharedPreferences sp;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        //Where?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            mainActivityIntent = new Intent(this, MainActivity.class);

            //use this to notify main activity to open home fragment.
            mainActivityIntent.putExtra(MainActivity.INTENT_EXTRA_NOTIFICATION, HOME_FRAGMENT);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(this);

            mainActivityIntent = new Intent(this, MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

            builder.setContentTitle(aMessage)                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setColor(4)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setGroup(NOTIFICATION_GROUP);
        }

        Notification notification = builder.build();
        notifManager.notify(NOTIFICATION_REQUEST_ID, notification);
    }

    /**
     *  Request a notification for connections
     */
    private class RequestNotificationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            String username = sp.getString(getString(R.string.keys_prefs_user_name), "");
            String date = sp.getString(getString(R.string.keys_timestamp) + username, "1970-01-01T00:00:01.000Z");
            //instead of using a hard coded (found in end_points.xml) url for our web service
            // address, here we will build the URL from parts. This can be helpful when
            // sending arguments via GET. In this example, we are sending plain text.
            String url = strings[0];
            String endPoint = strings[1];
            String args = strings[2];
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(url)
                    .appendPath(endPoint)
                    .appendQueryParameter("username", args)
                    .appendQueryParameter("after", date)
                    .build();
            try {
                URL urlObject = new URL(uri.toString());
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: "
                        + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject res = new JSONObject(result);
                JSONArray resArr = res.getJSONArray("recieved_requests");
                if (resArr.length() > 0) {
                    JSONObject timeStamp = resArr.getJSONObject(0);
                    SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
                    String username = sp.getString(getString(R.string.keys_prefs_user_name), "");
                    String timestampStr = sp.getString(getString(R.string.keys_timestamp) + username, "");
                    String sentTimestampstr = timeStamp.getString("timestamp");
                    String usernameSent;
                    if (sentTimestampstr.compareTo(timestampStr) > 0) {
                        createRequestNotification("You have a new connection request!", 1000);
                    }

                    sp.edit().putString(getString(R.string.keys_timestamp) + username, sentTimestampstr).apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /***
     * Requests all recent messages in chat and notifies user.
     */
    private class ChatNotificationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (strings.length != 3) {
                throw new IllegalArgumentException("Three String arguments required.");
            }
            String response = "";
            HttpURLConnection urlConnection = null;
            SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            String username = sp.getString(getString(R.string.keys_prefs_user_name), "");
            String date = sp.getString(getString(R.string.keys_chatTimestamp) + username, "1970-01-01T00:00:01.000Z");

            String url = strings[0];
            String endPoint = strings[1];
            String args = strings[2];
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(url)
                    .appendPath(endPoint)
                    .appendQueryParameter("username", args)
                    .build();
            try {
                URL urlObject = new URL(uri.toString());
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                response = "Unable to connect, Reason: "
                        + e.getMessage();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject res = new JSONObject(result);
                JSONArray resArr = res.getJSONArray("message");
                if (resArr.length() > 0) {
                    JSONObject timeStamp = resArr.getJSONObject(0);
                    SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
                    String username = sp.getString(getString(R.string.keys_prefs_user_name), "");
                    String currtimestampStr = sp.getString(getString(R.string.keys_chatTimestamp) + username, "");
                    String sentTimestampstr = timeStamp.getString("timestamp");
                    if (sentTimestampstr.compareTo(currtimestampStr) > 0 && !username.equals(timeStamp.getString("username"))) {
                        createRequestNotification("You have new message(s)", 1001);
                        sp.edit().putString(getString(R.string.keys_chatTimestamp) + username, sentTimestampstr).apply();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}