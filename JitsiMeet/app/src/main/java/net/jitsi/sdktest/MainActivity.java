package net.jitsi.sdktest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText userNameEditText1;
    EditText passwordEditText;

    Context context = this;
    JsonObject jsonObject;

    ProgressDialog progressDialog;
    Dialog dialog;

    String urlLogin = CommonUtils.IP + "/JitsiMeet/login.php";
    String urlForgotPassword = CommonUtils.IP + "/JitsiMeet/forgotpasswordgenerator.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        userNameEditText1 = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        dialog = new Dialog(this);
        dialog.setCancelable(true);

        Button login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(userNameEditText1.getText().toString().length() > 0){
                    if(passwordEditText.getText().toString().length() > 0){
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        jsonObject = new JsonObject();
                        jsonObject.addProperty("userName", userNameEditText1.getText().toString());
                        jsonObject.addProperty("password", passwordEditText.getText().toString());

                        PostLogin postLogin = new PostLogin(context);
                        postLogin.checkServerAvailability(2);
                    }else {
                        Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Enter Username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView signUp = findViewById(R.id.signUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        TextView forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.forgot_password);

                EditText userNameEditText2 = dialog.findViewById(R.id.username);
                Button ok = dialog.findViewById(R.id.ok);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(userNameEditText2.getText().toString().length() > 0) {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            jsonObject = new JsonObject();
                            jsonObject.addProperty("username", userNameEditText2.getText().toString());

                            PostForgotPassword postForgotPassword = new PostForgotPassword(context);
                            postForgotPassword.checkServerAvailability(2);
                        }else{
                            Toast.makeText(context, "Enter Username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();
            }
        });

    }

    private class PostLogin extends PostRequest{
        public PostLogin(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlLogin, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    Intent intent = new Intent(MainActivity.this, CreateJoinActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "Username or Password \n\t\t\t\t\t\t not correct", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    private class PostForgotPassword extends PostRequest{
        public PostForgotPassword(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlForgotPassword, jsonObject);
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    Intent intent = new Intent(MainActivity.this, ForgotActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "Username not valid", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}