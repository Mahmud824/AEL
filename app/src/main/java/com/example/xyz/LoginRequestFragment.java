package com.example.xyz;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginRequestFragment extends Fragment {

    LoginButton loginButton;
    CallbackManager callbackManager;
    ImageView imageView;
    TextView txtUsername, txtEmail;
    Context mContext;
    private Button mSkipButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /*private Button mLoginFacebook;
    private Button mLoginGoogle;
    private CallbackManager mCallbackManager;
    private static final String EMAIL = "email";*/

    public LoginRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_request,container,false);
        loginButton = v.findViewById(R.id.login_button);
        imageView = v.findViewById(R.id.imageView);
        txtUsername = v.findViewById(R.id.txtUsername);
        txtEmail = v.findViewById(R.id.txtEmail);
        mSkipButton = v.findViewById(R.id.skip_btn);

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;

        if (!loggedOut) {
            Picasso.with(mContext).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(imageView);
            Log.d("TAG", "Username is: " + Profile.getCurrentProfile().getName());

            //Using Graph API
            getUserProfile(AccessToken.getCurrentAccessToken());
        }

        AccessTokenTracker fbTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {
                    txtUsername.setText(null);
                    txtEmail.setText(null);
                    imageView.setImageResource(0);
                    Toast.makeText(getApplicationContext(),"User Logged Out.",Toast.LENGTH_LONG).show();
                }
            }
        };
        fbTracker.startTracking();
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                //loginResult.getAccessToken();
                //loginResult.getRecentlyDeniedPermissions()
                //loginResult.getRecentlyGrantedPermissions()
                boolean loggedOut = AccessToken.getCurrentAccessToken() == null;

                if (!loggedOut) {
                    Picasso.with(mContext).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(imageView);
                    Log.d("TAG", "Username is: " + Profile.getCurrentProfile().getName());

                    //Using Graph API
                    getUserProfile(AccessToken.getCurrentAccessToken());
                }

            }
            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        /*mCallbackManager = CallbackManager.Factory.create();
        mLoginFacebook = v.findViewById(R.id.login_facebook_btn);
        ParseFacebookUtils.initialize(this);
        mLoginFacebook.setReadPermissions(Arrays.asList(EMAIL));
        mLoginFacebook.setFragment(this);
        mLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });*/
        /*mLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                            txtUsername.setText("First Name: " + first_name + "\nLast Name: " + last_name);
                            txtEmail.setText(email);
                            Picasso.with(mContext).load(image_url).into(imageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

}
