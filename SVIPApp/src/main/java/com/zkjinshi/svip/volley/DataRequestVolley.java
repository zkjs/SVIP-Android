package com.zkjinshi.svip.volley;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

public class DataRequestVolley extends Request<String> {

    private Response.Listener<String> mListener;

    public DataRequestVolley(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url,errorListener);
        mListener=listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        Log.i("服务器响应码",response.statusCode+"");
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.i("响应data",json);
            if(response.statusCode == 200){
                Log.v("json", json);
                return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
            }else{
                Log.i("msg", "网络访问错误");
                return Response.error(new VolleyError("网络访问错误"));
            }
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

}

