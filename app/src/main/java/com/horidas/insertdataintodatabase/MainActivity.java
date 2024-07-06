package com.horidas.insertdataintodatabase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText edName,edPhone,edEmail;
    Button InsertBtn;
    ProgressBar progressBar,progressBar2;
    ListView listView;
    HashMap <String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        hooks
        edName = findViewById(R.id.edName);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        InsertBtn = findViewById(R.id.InsertBtn);
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.listView);
        progressBar2 = findViewById(R.id.progressBar2);

        loadData();



        InsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                String name = edName.getText().toString();
                String phone = edPhone.getText().toString();
                String email = edEmail.getText().toString();

                if (name.isEmpty()){
                    edName.setError("Please enter your name!");
                    return;
                }
                if (phone.isEmpty()) {
                    edPhone.setError("Please enter your phone number!");
                    return;
                }
                if(email.isEmpty()){
                    edEmail.setError("Enter your email!");
                    return;
                }

                String url = "https://hey-php1.000webhostapp.com/apps/data.php?n="+name
                        +"&p="+phone+"&e="+email;

                StringRequest request = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                       progressBar.setVisibility(View.GONE);
                       new AlertDialog.Builder(MainActivity.this)
                               .setTitle("Server Response")
                               .setMessage(s)
                               .show();

                       //again load data
                       loadData();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                       progressBar.setVisibility(View.GONE);
                       new AlertDialog.Builder(MainActivity.this)
                               .setTitle("Server error")
                               .setMessage(volleyError.getMessage())
                               .show();
                    }
                });

                if(name.length()>0 && phone.length()>0 &&email.length()>0){
                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                        queue.add(request);
                }

            }
        });

    }
    //-----------Adapter-----------
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            @SuppressLint("ViewHolder") View myView = layoutInflater.inflate(R.layout.item,null);

            TextView tvId = myView.findViewById(R.id.tvId);
            TextView tvName = myView.findViewById(R.id.tvName);
            TextView tvPhone = myView.findViewById(R.id.tvPhone);
            TextView tvEmail = myView.findViewById(R.id.tvEmail);
            Button updateBtn = myView.findViewById(R.id.updateBtn);
            Button deleteBtn = myView.findViewById(R.id.deleteBtn);

            hashMap = arrayList.get(position);
            String id = hashMap.get("id");
            String name = hashMap.get("name");
            String phone = hashMap.get("phone");
            String email = hashMap.get("email");

            tvId.setText(id);
            tvName.setText(name);
            tvPhone.setText(phone);
            tvEmail.setText(email);

            return myView;
        }
    };


    //----------load Data------------
    private void loadData(){

        arrayList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        progressBar2.setVisibility(View.VISIBLE);
        String url = "https://hey-php1.000webhostapp.com/apps/usersJson.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        progressBar2.setVisibility(View.GONE);
                        for(int i=0; i<jsonArray.length();i++){
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("id");
                                String name = jsonObject.getString("name");
                                String phone = jsonObject.getString("phone");
                                String email = jsonObject.getString("email");

                                hashMap = new HashMap<>();
                                hashMap.put("id",id);
                                hashMap.put("name",name);
                                hashMap.put("phone",phone);
                                hashMap.put("email",email);
                                arrayList.add(hashMap);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // =======================
                        if(!arrayList.isEmpty()){
                            MyAdapter myAdapter = new MyAdapter();
                            listView.setAdapter(myAdapter);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar2.setVisibility(View.GONE);
                Log.d("Server error: ",volleyError.toString());
            }
        });

        queue.add(jsonArrayRequest);
    }



}