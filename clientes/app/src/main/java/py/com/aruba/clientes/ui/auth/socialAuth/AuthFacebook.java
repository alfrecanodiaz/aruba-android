package py.com.aruba.clientes.ui.auth.socialAuth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Arrays;

import py.com.aruba.clientes.data.helpers.RestAdapter;
import py.com.aruba.clientes.data.helpers.request.RequestResponseDataJsonObject;
import py.com.aruba.clientes.data.models.User;
import py.com.aruba.clientes.data.service.ProfileInterface;
import py.com.aruba.clientes.ui.main.MainActivity;
import py.com.aruba.clientes.utils.alerts.AlertGlobal;

public class AuthFacebook {
    public int RC_SIGN_IN;
    private final AppCompatActivity activity;
    public final CallbackManager callbackManager;
    private Profile profile;
    private AccessToken accessToken;
    public boolean login;


    public AuthFacebook(AppCompatActivity activity) {
        this.activity = activity;
        RC_SIGN_IN = CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = AccessToken.getCurrentAccessToken();
                profile = Profile.getCurrentProfile();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            prepareData(me);
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
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
    }

    public boolean isUserLogged() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    public void signIn() {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }

    public void signOut() {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
    }

    /**
     * Método para preparar los datos de login y enviar al backend
     *
     * @param me
     */
    private void prepareData(JSONObject me) {
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("facebook_token", accessToken.getToken());


        if (login) {
            loginToBackend(jsonData, me);
        } else {
            loginToBackend(jsonData, me);
        }
    }

    /**
     * Método para enviar al backend los datos del usuario y verificar que ya esté logueado
     *
     * @param jsonData
     * @param me
     */
    private void loginToBackend(JsonObject jsonData, JSONObject me) {
        ProfileInterface restInterface = RestAdapter.getNoAuthClient(activity).create(py.com.aruba.clientes.data.service.ProfileInterface.class);

        // Retrofit request
        restInterface.facebook(jsonData).enqueue(new RequestResponseDataJsonObject("authFB") {
            @Override
            public void successful(JsonObject object) {
                User.createOrUpdate(object, activity);

                // Le tiramos al usuario al mainActivity
                Intent newIntent = new Intent(activity, MainActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(newIntent);
                activity.finish();
            }

            @Override
            public void successful(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }

            @Override
            public void error(String msg) {
                AlertGlobal.showCustomError(activity, "Atención", msg);
            }
        });
    }


}
