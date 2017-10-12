package com.getlosthere.peebuddy.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getlosthere.peebuddy.R;
import com.getlosthere.peebuddy.helpers.AlertDialogManager;
import com.getlosthere.peebuddy.models.User;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiClient;
import com.getlosthere.peebuddy.rest_clients.PeeBuddyApiService;
import com.getlosthere.peebuddy.rest_clients.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by violetaria on 7/16/17.
 */

public class LoginRegisterActivity extends AppCompatActivity {
    @BindView(R.id.btnRegister) Button btnRegister;
    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.tvSwitch) TextView tvSwitch;
    private boolean isLogonButtonVisible = true;
    private static final String TAG = "LoginRegisterActivity";
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);
        ButterKnife.bind(this);

        tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogonButtonVisible){
                    btnLogin.setVisibility(View.GONE);
                    btnRegister.setVisibility(View.VISIBLE);
                    tvSwitch.setText(R.string.click_here_to_login);
                } else {
                    btnLogin.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.GONE);
                    tvSwitch.setText(R.string.click_here_to_register);
                }
                isLogonButtonVisible = !isLogonButtonVisible;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().length() == 0 || etPassword.getText().length() == 0) {
                    alert.showAlertDialog(LoginRegisterActivity.this, "Email and Password are required",
                            "Try again", false);
                } else {
                    PeeBuddyApiService
                            peeBuddyApiService = PeeBuddyApiClient.getClient().create(PeeBuddyApiService.class);
                    // TODO - do some error handling
                    Call<User> call = peeBuddyApiService.loginUser(etEmail.getText().toString(), etPassword.getText().toString());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            if (response.body() != null) {
                                User user = response.body();
                                sessionManager.createLoginSession(user.getAuthenticationToken(), user.getEmail());
                                Toast.makeText(LoginRegisterActivity.this, "Login Succeeded", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Log.d(TAG,response.errorBody().toString());
                                Toast.makeText(LoginRegisterActivity.this, "e-mail or password not correct.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            alert.showAlertDialog(LoginRegisterActivity.this, "Something went wrong", "Try again", false);
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().toString().length() == 0 || etPassword.getText().toString().length() == 0) {
                    // TODO redo this
                    alert.showAlertDialog(LoginRegisterActivity.this, "Email and Password are required", "Try again", false);
                } else {
                    PeeBuddyApiService
                            peeBuddyApiService = PeeBuddyApiClient.getClient().create(PeeBuddyApiService.class);
                    String email = etEmail.getText().toString();
                    String password =  etPassword.getText().toString();
                    Call<User> call = peeBuddyApiService.registerUser(email, password);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            SessionManager sessionManager = new SessionManager(
                                    getApplicationContext());
                            if (response.body() != null) {
                                User user = response.body();
                                sessionManager.createLoginSession(user.getAuthenticationToken(), user.getEmail());
                                Toast.makeText(LoginRegisterActivity.this, "Registration Complete", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                String error = response.errorBody().toString();
                                Toast.makeText(LoginRegisterActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            alert.showAlertDialog(LoginRegisterActivity.this, "Login Failed", "Try again", false);
                            Log.e(TAG, t.toString());
                        }
                    });
                }
            }
        });
    }
}
