package com.clock.daemon.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.clock.daemon.MainActivity;
import com.clock.daemon.service.WhiteService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

public class AsyncTextViewLoader extends AsyncTask<String, Integer, Boolean> {
    private String txtResult;
    private WhiteService context;
    public AsyncTextViewLoader(WhiteService context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url = "http://ichess.sinaapp.com/ext/channel.php";
        HttpClient httpclient = null;
        HttpResponse response = null;
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
                WhiteService.strUrl = txtResult;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //throw new RuntimeException(e);
        } finally {
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
        startSocket(txtResult);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        System.out.println("Getting...");
    }

    private void startSocket(String strUrl){
        if(WhiteService.socket!=null && WhiteService.socket.session1 !=null && WhiteService.socket.session1.isOpen())
            return;

        WebSocketClient client = new WebSocketClient();
        WhiteService.socket = new SimpleEchoSocket(context);
        try {
            client.start();
            URI echoUri = new URI(strUrl);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(WhiteService.socket, echoUri, request);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}