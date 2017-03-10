package com.clock.daemon.util;

import android.content.ContentProvider;
import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class AsyncTextViewLoader extends AsyncTask<String, Integer, Boolean> {
    //private ContentProvider provider;
    private TextView tv;
    private String txtResult;
    public AsyncTextViewLoader(TextView tv) {
        this.tv = tv;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url = "http://ichess.sinaapp.com/ext/channel.php";
        HttpClient httpclient=null;
        HttpResponse response=null;
        try {
             httpclient = new DefaultHttpClient();
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            // 执行get请求.
             response = httpclient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();

            // 打印响应状态
            System.out.println(response.getStatusLine().getStatusCode());
            if (entity != null) {
                txtResult = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            httpclient.getConnectionManager().shutdown();
        }

        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        this.tv.setText(txtResult);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        this.tv.setText("Getting...");
    }
}