package retrocraft.io.xt.utils;

import cn.hutool.http.HttpRequest;

public class MoralisAPIUtils {
    public static  final String contractAddress = "0xd6ef2222cc850fdc7ee30f2b2d5384e0167700a3" ;
    public static  final String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjBlODRlODhmLTc3Y2UtNGUxMy04OGUyLWE2NTgxZjVhMWU2MCIsIm9yZ0lkIjoiMzg1Nzc2IiwidXNlcklkIjoiMzk2Mzk2IiwidHlwZUlkIjoiY2U0NzQyN2QtMzc2ZC00YzU0LWE5NjAtOGNjNWUwYmUzODg5IiwidHlwZSI6IlBST0pFQ1QiLCJpYXQiOjE3MTE5NjI0NTYsImV4cCI6NDg2NzcyMjQ1Nn0.mMU0sSrpiEOmzNkJ6X0QiuTh2CawAS6aOC-begMCsuM" ;
    public static String getMoralisData() throws Exception {
        String url = "https://deep-index.moralis.io/api/v2.2/erc20/" + contractAddress + "/price?chain=bsc&include=percent_change";
        HttpRequest httpRequest = HttpRequest.get(url);
        httpRequest
                .timeout(3000)
                .header("accept","application/json")
                .header("X-API-Key",apiKey);
        return httpRequest.execute().body();
    }

    public static String getFromGecko(){
        String url = "https://api.geckoterminal.com/api/v2/networks/bsc/tokens/"+ contractAddress;
        HttpRequest httpRequest = HttpRequest.get(url);
        httpRequest
                .timeout(3000)
                .header("accept","application/json");
        return httpRequest.execute().body();
    }
}
