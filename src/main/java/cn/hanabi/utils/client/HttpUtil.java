package cn.hanabi.utils.client;


import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

/**
 * Created by John on 2017/04/24.
 */


public class HttpUtil {

    private static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    public static String performGetRequest(URL url) throws IOException {
        Validate.notNull(url);

        HttpURLConnection connection = createUrlConnection(url);
        InputStream inputStream = null;
        connection.setRequestProperty("User-agent", "Mozilla/5.0 AppIeWebKit");

        String var6;
        try {
            String result;
            try {
                inputStream = connection.getInputStream();
                return IOUtils.toString(inputStream, Charsets.UTF_8);
            } catch (IOException var10) {
                IOUtils.closeQuietly(inputStream);
                inputStream = connection.getErrorStream();
                if (inputStream == null) {
                    throw var10;
                }
            }

            result = IOUtils.toString(inputStream, Charsets.UTF_8);
            var6 = result;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return var6;
    }

    public static String performPostRequest(String Surl, String data) throws IOException {
        URL url = new URL(Surl);
        URLConnection uc = url.openConnection();
        uc.setDoOutput(true);// POST可能にする

        OutputStream os = uc.getOutputStream();// POST用のOutputStreamを取得

        PrintStream ps = new PrintStream(os);
        ps.print(data);// データをPOSTする
        ps.close();

        InputStream is = uc.getInputStream();// POSTした結果を取得
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        StringBuilder result = new StringBuilder();
        while ((s = reader.readLine()) != null) {
            result.append(s);
        }
        reader.close();

        return result.toString();
    }


    public static String doGet(String url) throws IOException{
        {
            HttpURLConnection httpurlconnection = (HttpURLConnection) new URL(url).openConnection();
            httpurlconnection.setRequestMethod("GET");
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            StringBuilder stringbuilder = new StringBuilder();
            String s;

            while ((s = bufferedreader.readLine()) != null)
            {
                stringbuilder.append(s);
                stringbuilder.append('\r');
            }

            bufferedreader.close();
            return stringbuilder.toString();
        }
    }


    public static Set<String> getHeaders(String url) throws Exception {
        URL urlConnection = new URL("url");
        URLConnection conn = urlConnection.openConnection();
        Map headers = conn.getHeaderFields();
        return (Set<String>) headers.keySet();
    }
}