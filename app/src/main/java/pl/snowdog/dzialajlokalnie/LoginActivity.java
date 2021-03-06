package pl.snowdog.dzialajlokalnie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import pl.snowdog.dzialajlokalnie.events.CreateNewObjectEvent;
import pl.snowdog.dzialajlokalnie.events.ObjectAddedEvent;
import pl.snowdog.dzialajlokalnie.fragment.ApiActionDialogFragment;
import pl.snowdog.dzialajlokalnie.fragment.ApiActionDialogFragment_;
import pl.snowdog.dzialajlokalnie.model.Login;
import pl.snowdog.dzialajlokalnie.model.Session;
import pl.snowdog.dzialajlokalnie.model.User;
import pl.snowdog.dzialajlokalnie.util.PasswordValidator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by chomi3 on 2015-07-23.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    protected ApiActionDialogFragment mApiActionDialogFragment;

    @ViewById(R.id.etEmail)
    EditText etEmail;

    @ViewById(R.id.etPassword)
    EditText etPassword;

    @ViewById(R.id.etEmailWrapper)
    TextInputLayout etEmailWrapper;

    @ViewById(R.id.etPasswordWrapper)
    TextInputLayout etPasswordWrapper;


    @ViewById(R.id.btnLoginFacebook)
    LoginButton btnFacebookLogin;


    CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    private Login.Facebook mFacebookLogin;


    @Click(R.id.btnLogin)
    void onLoginClicked() {
        if(validateInput()) {
            toggleProgressWheel(true);
            login(etEmail.getText().toString(), etPassword.getText().toString());
        }
    }

    @Click(R.id.btnRegister)
    void onRegisterClicked() {
        AddUserActivity_.intent(this).start();
        /*AddUserFacebookActivity_.intent(LoginActivity.this).start();
        hideKeyboard();*/
        this.finish();
    }

    @Click(R.id.btnLoginFacebook)
    void onFacebookLoginClicked() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loginResult(Session session) {
        toggleProgressWheel(false);
        super.loginResult(session);
        if(session != null) {
            this.finish();
        }
    }

    @Override
    protected void afterView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        btnFacebookLogin.setReadPermissions("public_profile");
        btnFacebookLogin.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                profileTracker.startTracking();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                toggleProgressWheel(true);
                                Log.d(TAG, "fbdbg: " + response.toString() + " object: " + object.toString() + " accessToken: " + loginResult.getAccessToken().getToken());

                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                mFacebookLogin = gson.fromJson(object.toString(), Login.Facebook.class);
                                mFacebookLogin.setAccessToken(loginResult.getAccessToken().getToken());
                                loginWithFacebook(mFacebookLogin);


                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {

                //Profile profile = Profile.getCurrentProfile();
                if(currentProfile != null) {
                    Log.d(TAG, "fbdbg profile: " + currentProfile.getName() + " " + currentProfile.getLastName());
                }
                // App code
            }
        };
    }


    public void onEvent(CreateNewObjectEvent event) {
        switch (event.getType()) {
            case facebook:
                if(mFacebookLogin != null) {
                    loginWithFacebook(mFacebookLogin);
                }


        }
    }

    @Override
    protected void getUserById(int userId) {
        DlApplication.userApi.getUserById(userId, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                toggleProgressWheel(false);
                user.save();
                EventBus.getDefault().post(new ObjectAddedEvent(ObjectAddedEvent.Type.user));
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                finish();
                toggleProgressWheel(false);
            }
        });
    }

    private void loginWithFacebook(Login.Facebook login) {
        DlApplication.userApi.loginFb(login, new Callback<Login.Facebook>() {
            @Override
            public void success(Login.Facebook user, Response response) {

                Log.d(TAG, "fbdbg facebookLogin user post success: " + response);// + " user: " + user.toString());
                if(user != null) {
                    if (user.getSession() != null && user.getUser() == null) {
                        //Consecutive facebook login, user was already created - store session
                        //and start regular app usage
                        Log.d(TAG, "fbdbg facebookLogin SESSION RECEIVED "+user.getSession());
                        try {
                            user.getSession().save();
                            DlApplication.refreshCurrentSession();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getUserById(user.getSession().getUserID());
                        /*EventBus.getDefault().post(new ObjectAddedEvent(ObjectAddedEvent.Type.user));
                        finish();*/

                    }
                    if (user.getSession() != null && user.getUser() != null) {
                        //First user login by facebook, we need to get his location to update account
                        //if no location will be provided - log user out
                        Log.d(TAG, "fbdbg facebookLogin USER RECEIVED");
                        try {
                            user.getSession().save();
                            DlApplication.refreshCurrentSession();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        toggleProgressWheel(false);
                        hideKeyboard();
                        AddUserFacebookActivity_.intent(LoginActivity.this).userID(user.getUser().getUserID()).start();
                        finish();
                    }
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "userApi.postNewUser post error: " + error);
                toggleProgressWheel(false);
            }
        });
    }

    @TextChange(R.id.etEmail)
    void onTextChangeEmail(TextView tv, CharSequence text) {
        if(text.length() > 0) {
            validateEmail();
        }
    }

    @TextChange(R.id.etPassword)
    void onTextChangePassword(TextView tv, CharSequence text) {
        if(text.length() > 0) {
            validatePassword();
        }
    }

    boolean validateInput() {
        boolean validEmail = validateEmail();
        boolean validPassword = validatePassword();

        if(validEmail && validPassword) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateEmail() {
        boolean isInputValid = true;
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            etEmailWrapper.setErrorEnabled(true);
            etEmailWrapper.setError(getString(R.string.warning_fill_email));
            isInputValid = false;
        } else {
            etEmailWrapper.setErrorEnabled(false);
        }
        return isInputValid;
    }

    private boolean validatePassword() {
        boolean isInputValid = true;
        if(etPassword.getText().toString().trim().length() == 0
                || new PasswordValidator().validate(etPassword.getText().toString()) == false) {
            etPasswordWrapper.setErrorEnabled(true);
            etPasswordWrapper.setError(getString(R.string.warning_fill_password));
            isInputValid = false;
        } else {
            etPasswordWrapper.setErrorEnabled(false);
        }
        return isInputValid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void toggleProgressWheel(boolean show) {
        if(show) {
            if(mApiActionDialogFragment == null) {
                mApiActionDialogFragment = ApiActionDialogFragment_.builder().build();
            }
            mApiActionDialogFragment.show(getSupportFragmentManager(), ApiActionDialogFragment.TAG);
        } else {
            mApiActionDialogFragment.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OptionsItem(android.R.id.home)
    void homeSelected() {
        this.finish();
    }
}
