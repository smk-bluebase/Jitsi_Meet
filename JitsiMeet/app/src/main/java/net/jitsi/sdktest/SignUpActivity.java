package net.jitsi.sdktest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
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

public class SignUpActivity extends AppCompatActivity {
    EditText userNameEditText;
    EditText emailEditText;
    EditText passwordEditText;

    Context context = this;
    JsonObject jsonObject;

    ProgressDialog progressDialog;
    Dialog dialog;

    String urlSignUp = CommonUtils.IP + "/JitsiMeet/signup.php";
    String urlOTP = CommonUtils.IP + "/JitsiMeet/otpverifier.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.7);

        ImageView background = findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        userNameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        dialog = new Dialog(this);
        dialog.setCancelable(true);

        Button signUp = findViewById(R.id.signUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(userNameEditText.getText().toString().length() > 0){
                    if(emailEditText.getText().toString().length() > 0){
                        if(passwordEditText.getText().toString().length() > 0) {
                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();

                            jsonObject = new JsonObject();
                            jsonObject.addProperty("userName", userNameEditText.getText().toString());
                            jsonObject.addProperty("email", emailEditText.getText().toString());
                            jsonObject.addProperty("password", passwordEditText.getText().toString());

                            PostSignUp postSignUp = new PostSignUp(context);
                            postSignUp.checkServerAvailability(2);
                        }else{
                            Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Enter UserName", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView login = findViewById(R.id.goToLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }

    private class PostSignUp extends PostRequest {
        public PostSignUp(Context context) {
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable) {
            if(isServerAvailable){
                super.postRequest(urlSignUp, jsonObject);
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray) {
            progressDialog.dismiss();

            try {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);

                if (jsonObject1.getBoolean("status")) {
                    dialog.setContentView(R.layout.otp);

                    EditText otp = dialog.findViewById(R.id.otp);
                    Button ok = dialog.findViewById(R.id.ok);

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(otp.getText().toString().length() > 0) {
                                progressDialog = new ProgressDialog(context);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Loading...");
                                progressDialog.show();

                                jsonObject = new JsonObject();
                                jsonObject.addProperty("otp", otp.getText().toString());

                                PostOTP postOTP = new PostOTP(context);
                                postOTP.checkServerAvailability(2);
                            }else{
                                Toast.makeText(context, "Enter OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class PostOTP extends PostRequest{
        public PostOTP(Context context){
            super(context);
        }

        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlOTP, jsonObject);
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        public void onFinish(JSONArray jsonArray){
            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    Toast.makeText(context, "SignUp Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "SignUp Unsuccessful!", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(i);
    }
}