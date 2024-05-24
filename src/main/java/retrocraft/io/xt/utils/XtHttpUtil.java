package retrocraft.io.xt.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.GlobalHeaders;
import cn.hutool.http.HttpRequest;
import org.apache.commons.codec.digest.HmacUtils;
import retrocraft.io.xt.model.XtAccount;


import java.util.Map;
import java.util.TreeMap;

public class XtHttpUtil {

    private static final String encry = "HmacSHA256";
    private static final String contentType = "application/json";
    private static final String baseUrl = "https://sapi.xt.com";
    private static final String window = "6000";
    static {
        GlobalHeaders.INSTANCE.clearHeaders();
    }

    public static String get(XtAccount account, String uri, Map<String, Object> queryMap) {
        return getOrDel(account,uri, queryMap, "GET");
    }

    public static String delete(XtAccount account,String uri, Map<String, Object> queryMap) {
        return getOrDel(account,uri, queryMap, "DELETE");
    }

    public static String deleteWithBody(XtAccount account,String uri, String jsonBody) {
        Long time = System.currentTimeMillis();
        String url = baseUrl + uri;
        String signature = generateSign(account,time + "", window, "DELETE", uri, null, jsonBody);
        HttpRequest httpRequest = HttpRequest.delete(url);
        config(account,httpRequest, time + "", signature);
        httpRequest.body(jsonBody);
        System.out.println("request===="+httpRequest.getMethod()+" "+httpRequest);
        return httpRequest.execute().body();
    }

    public static String post(XtAccount account,String uri, String jsonBody) {
        Long time = System.currentTimeMillis();
        String url = baseUrl + uri;
        String signature = generateSign(account,time + "", window, "POST", uri, null, jsonBody);
        HttpRequest httpRequest = HttpRequest.post(url);
        config(account,httpRequest, time + "", signature);
        httpRequest.body(jsonBody);
        System.out.println("request===="+httpRequest.getMethod()+" "+httpRequest);

        String result =  httpRequest.execute().body();

        System.out.println("result===="+ result);

        return result;



    }

    private static String getOrDel(XtAccount account,String uri, Map<String, Object> queryMap, String method) {
        Long time = System.currentTimeMillis();
        String url = baseUrl + uri;
        StringBuilder querySb = new StringBuilder();
        String query = null;
        if (!CollectionUtil.isEmpty(queryMap)) {
            TreeMap<String, Object> treeMap = new TreeMap(queryMap);
            for (String key : treeMap.keySet()) {
                querySb.append(key).append("=").append(queryMap.get(key)).append("&");
            }
            String substring = querySb.substring(0, querySb.lastIndexOf("&"));
            url += "?" + substring;
            query = substring;
        }
        String signature = generateSign(account,time + "", window, method, uri, query, null);
        HttpRequest httpRequest = null;
        if ("GET".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.get(url);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            httpRequest = HttpRequest.delete(url);
        }
        config(account,httpRequest, time + "", signature);
        System.out.println("request===="+httpRequest.getMethod()+" "+httpRequest);
        return httpRequest.execute().body();
    }

    private static void config(XtAccount account,HttpRequest httpRequest, String time, String sign) {
        httpRequest
                .contentType(contentType)
                .timeout(3000)
                .header("validate-algorithms", encry)
                .header("validate-appkey", account.getAppKey())
                .header("validate-recvwindow", window)
                .header("validate-timestamp", time)
                .header("validate-signature", sign);
//                .setHttpProxy("127.0.0.1",7890);
    }


    private static String generateSign(XtAccount account,String timestamp, String window, String method, String uri, String query, String jsonBody) {
        String x = String.format("validate-algorithms=%s&validate-appkey=%s&validate-recvwindow=%s&validate-timestamp=%s", encry, account.getAppKey(), window, timestamp);
        String y = String.format("#%s#%s", method, uri);
        if (query != null && query.length() > 0) {
            y += "#" + query;
        }
        if (jsonBody != null && jsonBody.length() > 0) {
            y += "#" + jsonBody;
        }
        String origin = x + y;
        System.out.println("origion===" + origin);
        return HmacUtils.hmacSha256Hex(account.getPrivateKey(), origin);
    }

}
