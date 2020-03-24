package com.example.imageloader;

import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnections {
    public static void sendRequestWithHttpURLConnection(final Request request, final Callback callback) throws Exception {
        final Handler handler = new Handler();
        SingleThreadManger.getInstance().getService().execute(new Runnable()
        {final String address=request.getAddress();
            HttpURLConnection httpURLConnection=null;
            @Override
            public void run() {
                try {
                    URL url=new URL(address);
                    httpURLConnection=(HttpURLConnection)url.openConnection();
                    httpURLConnection.setReadTimeout(8*1000);
                    httpURLConnection.setConnectTimeout(8*1000);
                    httpURLConnection.setRequestMethod("Get");
                    httpURLConnection.setDoInput(true);
                    if(httpURLConnection.getResponseCode()==200) {
                        final byte[] temp = read(httpURLConnection.getInputStream());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(new Response(temp));
                            }
                        });
                    }
                }catch (final Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(e);
                        }
                    });
                }finally {
                    if(httpURLConnection!=null)
                    {
                        httpURLConnection.disconnect();
                    }
                }
            }
        });
    }
    private static byte[] read(InputStream is) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int len;
        while ((len = is.read(temp)) != -1)
            outputStream.write(temp, 0, len);
        is.close();
        outputStream.close();
        return outputStream.toByteArray();
    }
    public static class Response {
        private byte[] mData;
        Response(byte[] response) {
            if(response!=null)
            mData=response;
        }

        public byte[] getmData() {
            return mData;
        }
    }
}
