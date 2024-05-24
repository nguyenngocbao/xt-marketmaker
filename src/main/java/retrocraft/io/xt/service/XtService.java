package retrocraft.io.xt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrocraft.io.xt.model.PriceDexResponse;
import retrocraft.io.xt.model.SpotPostOrderRequest;
import retrocraft.io.xt.model.XtAccount;
import retrocraft.io.xt.utils.MoralisAPIUtils;
import retrocraft.io.xt.utils.XtAccountProperties;
import retrocraft.io.xt.utils.XtHttpUtil;


import java.util.Random;
import java.util.Stack;

@Component
public class XtService {

    public static boolean buy = true;
    public static boolean stop = true;
    public static boolean accountBuy = true;
    public static boolean accountSell = false;
    public static double  prevPrice = 0;
    public static double  maxPrice = 0.0030;
    public static double  minPrice = 0.002;


    @Autowired
    private XtAccountProperties accountProperties;

    private Stack<String> account1 = new Stack<>();
    private Stack<String> account2 = new Stack<>();

    // Execute every 15 minutes
    @Scheduled(cron = "0/30 * * * * ?")
    public void runCronJob() throws JsonProcessingException {

        try {
            if (stop){
                return;
            }
            double price = randomPrice();
            sell(price);
            Thread.sleep(1000);
            buy(price);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private double getPrice(){
        if (prevPrice == 0 || prevPrice >= maxPrice){
            prevPrice = randomPrice();
            return prevPrice;
        }else{
            prevPrice = prevPrice + 0.00003;
            return prevPrice;

        }
    }

    private void buy(double price) throws JsonProcessingException {
        String uri = "/v4/order";

        SpotPostOrderRequest request = new SpotPostOrderRequest();
        request.setSymbol("retro_usdt");
        request.setSide("BUY");
        request.setType("LIMIT");
        request.setTimeInForce("GTC");
        request.setBizType("SPOT");
        request.setPrice(String.valueOf(price));
        request.setQuantity("10000");
        ObjectMapper mapper = new ObjectMapper();

        XtAccount account = new XtAccount();
        if (accountBuy){
            account.setAppKey(accountProperties.getAppKey1());
            account.setPrivateKey(accountProperties.getPrivateKey1());
        }else{
            account.setAppKey(accountProperties.getAppKey2());
            account.setPrivateKey(accountProperties.getPrivateKey2());
        }
        accountBuy = !accountBuy;


        String result = XtHttpUtil.post(account,uri,mapper.writeValueAsString(request));


    }

    private void sell(double price) throws JsonProcessingException {
        String uri = "/v4/order";

        SpotPostOrderRequest request = new SpotPostOrderRequest();
        request.setSymbol("retro_usdt");
        request.setSide("SELL");
        request.setType("LIMIT");
        request.setTimeInForce("GTC");
        request.setBizType("SPOT");
        request.setPrice(String.valueOf(price));
        request.setQuantity("10000");
        ObjectMapper mapper = new ObjectMapper();

        XtAccount account = new XtAccount();
        if (accountSell){
            account.setAppKey(accountProperties.getAppKey1());
            account.setPrivateKey(accountProperties.getPrivateKey1());
        }else{
            account.setAppKey(accountProperties.getAppKey2());
            account.setPrivateKey(accountProperties.getPrivateKey2());
        }
        accountSell = !accountSell;

        String result = XtHttpUtil.post(account,uri,mapper.writeValueAsString(request));

    }


    private double randomPrice(){
        // Tạo một đối tượng Random
        Random random = new Random();

        // Đặt giới hạn dưới và giới hạn trên cho khoảng giá trị double
        double lowerBound = minPrice;
        double upperBound = maxPrice;
        double dexPrice = 0;
        try {
            dexPrice = getGeckoPriceDex();
        }catch (Exception e){
            dexPrice = 0;
        }
        if (dexPrice < minPrice){
            dexPrice = lowerBound + (upperBound - lowerBound) * random.nextDouble();
        }

        return Math.round(dexPrice * 1000000.0) / 1000000.0;
    }

    public PriceDexResponse getPriceDex() throws Exception {
        String dexPrice = MoralisAPIUtils.getMoralisData();
        ObjectMapper objectMapper = new ObjectMapper();
        PriceDexResponse priceDexResponse = objectMapper.readValue(dexPrice,PriceDexResponse.class);

        return priceDexResponse;
    }

    public double getGeckoPriceDex() throws Exception {

        String dexPrice = MoralisAPIUtils.getFromGecko();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(dexPrice);
        String priceUsdString = jsonNode.at("/data/attributes/price_usd").asText();
        double priceUsdDouble = Double.parseDouble(priceUsdString);

        return priceUsdDouble;
    }



}
