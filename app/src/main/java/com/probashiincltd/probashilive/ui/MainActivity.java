package com.probashiincltd.probashilive.ui;

import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_PASSWORD;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_STATUS;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_TYPE;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_TYPE_GOOGLE;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_TYPE_RAW;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;
import static com.probashiincltd.probashilive.utils.Configurations.USER_EMAIL;
import static com.probashiincltd.probashilive.utils.Configurations.USER_PROFILE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.callbacks.RegisterCallback;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.databinding.ActivityMainBinding;
import com.probashiincltd.probashilive.databinding.RegistrationLayoutBinding;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.IpModel;
import com.probashiincltd.probashilive.viewmodels.ActivityMainViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityMainViewModel viewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(ActivityMainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        Functions.init(this);
        initViewModel();
        new Thread(() -> mGoogleSignInClient = Functions.getGoogleSigninClient(MainActivity.this)).start();

        Log.e("MainActivityOnc","called");
    }

    void startRegisterOrLogin(String user,String password,String action){
        openProgress = new OpenProgress(this).showProgress();
        Functions.requestHttp("http://checkip.amazonaws.com/", response -> {
            if(response!=null){
                Functions.requestHttp("http://ip-api.com/json/"+response, response1 -> CM.setIPModel(new Gson().fromJson(response1, IpModel.class)));
            }
        });
        new CM(MainActivity.this, user,password, action, new RegisterCallback() {
            @Override
            public void registerSuccessful() {
                openProgress.dismissProgress();
                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                finish();
                if(account!=null){
                    Functions.setSetSharedPreference(LOGIN_TYPE,LOGIN_TYPE_GOOGLE);
                    Functions.setSetSharedPreference(USER_EMAIL,account.getEmail());
                    Functions.setSetSharedPreference(USER_PROFILE,account.getPhotoUrl() == null?"":account.getPhotoUrl().toString());
                }else {
                    Functions.setSetSharedPreference(LOGIN_TYPE,LOGIN_TYPE_RAW);
                }

                Functions.setSetSharedPreference(LOGIN_STATUS,"true");
                Functions.setSetSharedPreference(LOGIN_USER,user);
                Functions.setSetSharedPreference(LOGIN_PASSWORD,password);

            }

            @Override
            public void registerFailed(int reason) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Something went Wrong!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    OpenProgress openProgress;
    @Override
    protected void onStart() {
        super.onStart();
        openProgress = new OpenProgress(this).showProgress();
        if(Functions.getSP(LOGIN_STATUS,"false").equals("false")){
            account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
            if(account!=null){
                startEjabberdRegistration(account.getEmail(),account.getId());
            }else {
                openProgress.dismissProgress();
            }
        }else {
            startRegisterOrLogin(Functions.getSP(LOGIN_USER,""), Functions.getSP(LOGIN_PASSWORD,""),"login");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(openProgress!=null){
            openProgress.dismissProgress();
        }
    }

    void initViewModel() {
        viewModel.getLoginAction().observe(this,newText -> performLogin());

        viewModel.getRegisterAction().observe(this, newText -> {
            switch (newText) {
                case "fb": {
                    performFacebookLogin();
                    break;
                }
                case "go": {
                    performGoogleLogin();
                    break;
                }
                case "ra": {
                    performRawRegistration();
                    break;
                }
                default: {
                    break;
                }
            }
        });
    }

    private void performLogin() {
        String user = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        if(user.isEmpty()){
            binding.email.setError("Field Can Not Be Empty!");
            binding.email.requestFocus();
        }else if(password.isEmpty()){
            binding.password.setError("Field Can Not Be Empty!");
            binding.password.requestFocus();
        }else {
            startRegisterOrLogin(user.split("@")[0],password,"login");
        }
    }

    void performFacebookLogin() {

    }

    void performGoogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        account = task.getResult(ApiException.class);
                        startEjabberdRegistration(account.getEmail(),account.getId());
                    } catch (ApiException e) {
                        Log.e("GoogleSignIn", "Google sign in failed", e);
                    }
                }
            });

    private void startEjabberdRegistration(String userEmail,String password) {
        try {
            startRegisterOrLogin(userEmail.split("@")[0],password,"register");
            Functions.setSetSharedPreference(USER_EMAIL,userEmail);
        }catch (Exception e){
            Toast.makeText(this, "Email Not Valid!", Toast.LENGTH_SHORT).show();
        }

    }

    AlertDialog registerDialog;
    void performRawRegistration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_Theme_ProbashiLive);
        RegistrationLayoutBinding registrationLayoutBinding = RegistrationLayoutBinding.inflate(getLayoutInflater());
        builder.setView(registrationLayoutBinding.getRoot());
        registerDialog = builder.create();
        registerDialog.show();
        Objects.requireNonNull(registerDialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        registerDialog.getWindow().setGravity(Gravity.CENTER);
        registerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        registerDialog.getWindow().setDimAmount(0.5f);

        registrationLayoutBinding.register.setOnClickListener(v -> {
            String name = registrationLayoutBinding.name.getText().toString();
            String email = registrationLayoutBinding.email.getText().toString();
            String password = registrationLayoutBinding.password.getText().toString();
            String confirmpassword = registrationLayoutBinding.confirmpassword.getText().toString();
            if(name.isEmpty()){
               registrationLayoutBinding.name.setError("Field can not be empty!");
               registrationLayoutBinding.name.requestFocus();
            }else  if(email.isEmpty()){
                registrationLayoutBinding.email.setError("Field can not be empty!");
                registrationLayoutBinding.email.requestFocus();
            }else  if(password.isEmpty()){
                registrationLayoutBinding.password.setError("Field can not be empty!");
                registrationLayoutBinding.password.requestFocus();
            }else  if(confirmpassword.isEmpty()){
                registrationLayoutBinding.confirmpassword.setError("Field can not be empty!");
                registrationLayoutBinding.confirmpassword.requestFocus();
            }else  if(!confirmpassword.equals(password)){
                registrationLayoutBinding.confirmpassword.setError("Password did not match!");
                registrationLayoutBinding.confirmpassword.requestFocus();
            }else {
                registerDialog.dismiss();
                startEjabberdRegistration(email,password);
            }
        });
    }





}