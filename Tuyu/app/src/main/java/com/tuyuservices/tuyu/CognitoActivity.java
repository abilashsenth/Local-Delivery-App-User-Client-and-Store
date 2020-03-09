package com.tuyuservices.tuyu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.Response;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.annotation.Nonnull;

public class CognitoActivity extends AppCompatActivity {

    private static PinpointManager pinpointManager;
    EditText usernameEdit, passEdit;
    Button loginButton;

    // instance of mobile client
    private final AWSMobileClient CLIENT = AWSMobileClient.getInstance();
    private String TAG = "CognitoActivity";
    private AWSAppSyncClient mAWSAppSyncClient;
    private AWSMobileClient mAWSMobileClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cognito);
        usernameEdit = findViewById(R.id.username_edit);
        passEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button_cognito);



        CLIENT.initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {

                switch (userStateDetails.getUserState()) {
                    case SIGNED_IN:
                        Log.e(TAG, "user signed in: " + userStateDetails.getUserState());
                        break;
                    case SIGNED_OUT:
                        showSignIn();
                        break;
                    default:
                        CLIENT.signOut();
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("INIT", "Initialization error.", e);
            }
        });

        /**
        // create AppSync client
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                // add our user pool provider
                .cognitoUserPoolsAuthProvider(new CognitoUserPoolsAuthProvider() {
                    @Override
                    public String getLatestAuthToken() {
                        try {
                            return mAWSMobileClient.getTokens().getIdToken().getTokenString();
                        } catch (Exception e){
                            Log.e("APPSYNC_ERROR", e.getLocalizedMessage());
                            return e.getLocalizedMessage();
                        }
                    }
                }).build();



        final PinpointManager pinpointManager = getPinpointManager(getApplicationContext());
        pinpointManager.getSessionClient().startSession();
**/

    }


    // launches auth UI
    protected void showSignIn() {
        CLIENT.showSignIn(
                this,
                SignInUIOptions.builder()
                        .nextActivity(MapAddressActivity.class)
                        .build(),
                new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails result) {
                        Log.d(TAG, "onResult: " + result.getUserState());
                        switch (result.getUserState()) {
                            case SIGNED_IN:
                                Log.i("INIT", "logged in!");
                                break;
                            case SIGNED_OUT:
                                Log.i(TAG, "onResult: User did not choose to sign-in");
                                break;
                            default:
                                AWSMobileClient.getInstance().signOut();
                                break;
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError: ", e);
                    }
                }
        );
    }

    //gets called when the user presses login button
    public void loginCognito(View view) {
    }



    /**
    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            // Initialize the AWS Mobile Client
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("PINPOINT", String.valueOf(userStateDetails.getUserState()));
                }

                @Override
                public void onError(Exception e) {
                    Log.e("PINPOINT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);
        }
        return pinpointManager;
    }
     **/



    @Override
    protected void onDestroy() {
        super.onDestroy();

      /**  pinpointManager.getSessionClient().stopSession();
        pinpointManager.getAnalyticsClient().submitEvents();
       **/
    }



}
