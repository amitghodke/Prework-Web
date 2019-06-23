package com.sample.preworkassignment.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sample.preworkassignment.R;
import com.sample.preworkassignment.app.SampleApp;
import com.sample.preworkassignment.app.SharedPrefConst;
import com.sample.preworkassignment.constant.Constants;
import com.sample.preworkassignment.constant.Utils;
import com.sample.preworkassignment.model.CommentsModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    SharedPrefConst sharedPrefConst;
    ImageView img;
    EditText edtId;
    Button btnGet;
    LinearLayout lnearLayoutComments;
    List<CommentsModel> oLstComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sharedPrefConst = SharedPrefConst.getInstance(this);
        initViews();
        edtId.setText("wy4pCR0");
        setToolBar();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Main Activity");
    }

    EditText edtComment;
    Button btnSend;

    private void initViews() {
        btnGet = findViewById(R.id.btnGetImage);
        img = findViewById(R.id.imageView);
        edtId = findViewById(R.id.edtImageId);
        btnSend = findViewById(R.id.btnsend);
        edtComment = findViewById(R.id.edtComment);


        textView = findViewById(R.id.txtUsername);
        lnearLayoutComments = findViewById(R.id.lnearLayoutComments);
        textView.setText("Signed In As :\n" + "Username: " + sharedPrefConst.getString("username") +
                "\nEmail Id :- " + sharedPrefConst.getString("email"));
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtId.getText().toString().isEmpty()) {
                    getImageFromID();
//                    getCommentsFromId();
                    new loadimage().execute();
                } else Utils.showToast(getApplicationContext(), "please enter valid image Id");
            }

        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtId.getText().toString())) {
                    Utils.showToast(getApplicationContext(), "Please enter image id");
                    return;
                }
                if (TextUtils.isEmpty(edtComment.getText().toString())) {
                    Utils.showToast(getApplicationContext(), "Please enter Comment");
                    return;
                } else postComment();
            }
        });
    }

    private void postComment() {
        String URL = "comment";
        final ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        pdLoading.setMessage("adding comment...");
        pdLoading.show();
        StringRequest request = new StringRequest(Method.POST, Constants.BASE_URL + URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdLoading.dismiss();
                if (!response.equals(null)) {
                    try {
                        Log.v("response", response);
                        //                      JSONObject jsonObject1 = new JSONObject(response);
//                        JSONObject jsonObject = jsonObject1.getJSONObject("data");
                        Utils.showToast(getApplicationContext(), "Comment Posted");
                        edtComment.setText("");
                        getCommentsFromId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.e("Your Array Response", response);
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdLoading.dismiss();
                Utils.showToast(getApplicationContext(), error.getMessage());
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + Constants.ACCESS_TOKEN);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringStingMap = new HashMap<>();
                stringStingMap.put("image_id", edtId.getText().toString().trim());
                stringStingMap.put("comment", edtComment.getText().toString().trim());
                return stringStingMap;
            }
        };
        SampleApp.getInstance().addToRequestQueue(request);
    }

    private void getCommentsFromId() {
        String URL = "image/" + edtId.getText().toString() + "/comments";
        final ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        pdLoading.setMessage("Getting Comments...");
        pdLoading.show();
        StringRequest request = new StringRequest(Method.GET, Constants.BASE_URL + URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdLoading.dismiss();
                if (!response.equals(null)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Gson gson = new Gson();
                        if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                            edtComment.setVisibility(View.VISIBLE);
                            btnSend.setVisibility(View.VISIBLE);
                            CommentsModel[] comments = gson.fromJson(jsonObject.getString("data"), CommentsModel[].class);
                            oLstComments = Arrays.asList(comments);
                            MainActivity.this.lnearLayoutComments.removeAllViews();
                            if (oLstComments.size() > 0) {
                                for (CommentsModel comment : oLstComments) {
                                    TextView textView = new TextView(getApplicationContext());
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    textView.setLayoutParams(layoutParams);
                                    textView.setText(comment.getComment() + "\t By :" + comment.getAuthor());
                                    textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    lnearLayoutComments.addView(textView);
                                }
                            } else {
//                                TextView textView = new TextView(getApplicationContext());
//                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                                textView.setLayoutParams(layoutParams);
//                                textView.setText("No Comments Yet");
//                                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
//                                lnearLayoutComments.addView(textView);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.showToast(getApplicationContext(), "something went wrong");
                    }

                    Log.e("Your Array Response", response);
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdLoading.dismiss();
                Utils.showToast(getApplicationContext(), error.getMessage());
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Client-ID " + Constants.CLIENT_ID);
                return params;
            }
        };
        SampleApp.getInstance().addToRequestQueue(request);
    }

    private void getImageFromID() {
        String URL = "image/" + edtId.getText().toString();
        final ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        pdLoading.setMessage("Loading Image...");
        pdLoading.show();
        StringRequest request = new StringRequest(Method.GET, Constants.BASE_URL + URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdLoading.dismiss();
                if (!response.equals(null)) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        JSONObject jsonObject = jsonObject1.getJSONObject("data");
                        Picasso.with(getApplicationContext()).load(jsonObject.getString("link"))
                                .error(R.drawable.ic_launcher_background)
                                .into(img);
                        //Utils.showToast(getApplicationContext(), jsonObject.getString("link"));
                        getCommentsFromId();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("Your Array Response", response);
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdLoading.dismiss();
                Utils.showToast(getApplicationContext(), error.getMessage());
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Client-ID " + Constants.CLIENT_ID);
                return params;
            }
        };
        SampleApp.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sharedPrefConst.clearSharedPref();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class loadimage extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading Image...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String stringurl = "https://imgur.com/a/ywMQNfC";
            try {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(stringurl).getContent());
                Log.v("biiiiiiiiiiii", bitmap + "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
            // do what you want with your bitmap
            return;
        }
    }
}
