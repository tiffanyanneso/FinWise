package ph.edu.dlsu.finwise.personalFinancialManagementModule.AsyncTask;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpHandler extends AsyncTask <String, Void, String> {

    public interface HttpResponse {
        void processFinish(String output);
    }

    public HttpResponse delegate = null;

    public HttpHandler(HttpResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(String result){
        delegate.processFinish(result);
    }

    @Override
    protected String doInBackground(String... args) {
        String url_string = args[0];
        String AuthHeader = args[1];
        String method = args[2];

        String result = "";
        URL url = null;
        try {
            url = new URL(url_string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpURLConnection.addRequestProperty("Authorization", AuthHeader);

        // Code for "GET" method requests
        if(method.equals("GET")){
            InputStream in = null;
            try {
                in = new BufferedInputStream(httpURLConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
                for (String line = r.readLine(); line != null; line =r.readLine()){
                    sb.append(line);
                }
                in.close();
                result = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Code for "POST" method requests
        if(method.equals("POST")){
            String post_data = args[3];
            try {
                httpURLConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setDoOutput(true);
            try(OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = post_data.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                result = response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
