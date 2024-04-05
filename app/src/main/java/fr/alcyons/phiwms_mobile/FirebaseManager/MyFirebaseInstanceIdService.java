package fr.alcyons.phiwms_mobile.FirebaseManager;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by quentinlanusse on 27/07/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {

        /*PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-76c656ba-6c93-11e7-85aa-0619f8945a4f");
        pnConfiguration.setSecure(false);

        PubNub pubnub = new PubNub(pnConfiguration);

        pubnub.subscribe()
                .channels(Arrays.asList(getString(R.string.pubnubCommonChannel))) // subscribe to channels
                .execute();

        pubnub.addPushNotificationsOnChannels()
                .pushType(PNPushType.GCM)
                .channels(Arrays.asList(getString(R.string.pubnubCommonChannel)))
                .deviceId(token)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                        int test = 1;
                        test++;
                    }
                });*/
    }

}
