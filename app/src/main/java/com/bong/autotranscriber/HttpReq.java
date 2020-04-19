package com.bong.autotranscriber;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class HttpReq {
    public static StringRequest makeHttpRequest(String url, byte[] byteArray) {
        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return byteArray;
            };
            public String getBodyContentType()
            {
                return "application/octet-stream; charset=utf-8";
            }
        };

        return myReq;
    }

    private static Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("app","Ski data from server - "+response);
            }
        };
    }


    private static Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("app","Ski error connect - "+error);
            }
        };
    }

}