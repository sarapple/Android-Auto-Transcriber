package com.bong.autotranscriber;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tarcisojunior on 18/04/18.
 */

public class ByteArrayRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;


    public ByteArrayRequest(String url, Response.Listener<byte[]> listener,
                            Response.ErrorListener errorListener) {
        this(Method.POST, url, listener, errorListener);
    }

    public ByteArrayRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Log.v("app", "getting headers");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/octet-stream");
        params.put("Cache-Control", "no-cache");
        return params;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        if (null != mListener) {
            mListener.onResponse(response);
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/octet-stream;";
    }
}
