package com.newtest.test.test;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    public class RegisterActivity extends AppCompatActivity {

        private static final String TAG = "RegisterActivity";
        private static final String URL_FOR_REGISTRATION = "https://XXX.XXX.X.XX/android_login_example/register.php";
        ProgressDialog progressDialog;

        private EditText signupFirstName, signupLastName, signupUserName, signupEmail, signupPassword, signupConfirmPassword, signupAccountType;
        private Button btnSignUp;
        private Spinner spaccounttype;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);


            // Progress dialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);

            signupFirstName = (EditText) findViewById(R.id.Input_FirstName);
            signupLastName = (EditText) findViewById(R.id.Input_LastName);
            signupUserName = (EditText) findViewById(R.id.Input_UserName);
            signupEmail = (EditText) findViewById(R.id.Input_Email);
            signupPassword = (EditText) findViewById(R.id.Input_Password);
            signupConfirmPassword = (EditText) findViewById(R.id.Input_ConfirmPassword);

            btnSignUp = (Button) findViewById(R.id.btn_register);

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitForm();
                }
            });
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                }
            });
        }


        private void submitForm() {


            registerUser(signupFirstName.getText().toString(),
                    signupLastName.getText().toString(),
                    signupUserName.getText().toString(),
                    signupEmail.getText().toString(),
                    signupPassword.getText().toString(),
                    signupConfirmPassword.getText().toString());
        }

        private void registerUser(final String firstname, final String lastname, final String username, final String email, final String password,
                                  final String confirmpassword) {
            // Tag used to cancel the request
            String cancel_req_tag = "register";

            progressDialog.setMessage("Adding you ...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    URL_FOR_REGISTRATION, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Register Response: " + response.toString());
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        if (!error) {
                            String user = jObj.getJSONObject("user").getString("name");
                            Toast.makeText(getApplicationContext(), "Hi " + user + ", You are successfully Added!", Toast.LENGTH_SHORT).show();

                            // Launch login activity
                            Intent intent = new Intent(
                                    RegisterActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("firstname", firstname);
                    params.put("lastname", lastname);
                    params.put("username", username);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("gender", confirmpassword);
                    //params.put("accounttype", spaccounttype);
                    spaccounttype = (Spinner) findViewById(R.id.Account_Type);
                    return params;
                }
            };
            // Adding request to request queue
            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
        }

        private void showDialog() {
            if (!progressDialog.isShowing())
                progressDialog.show();
        }

        private void hideDialog() {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

        }

        private void addListenerOnButton() {

            spaccounttype = (Spinner) findViewById(R.id.Account_Type);
            btnSignUp.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View v) {

                    Toast.makeText(MainActivity.this,
                            "OnClickListener : " +
                            "\nspaccounttype : "+ String.valueOf(spaccounttype.getSelectedItem()),
                            Toast.LENGTH_SHORT).show();

                }
            });
        }


    }
    }