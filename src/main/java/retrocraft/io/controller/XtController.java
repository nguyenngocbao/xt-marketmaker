package retrocraft.io.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrocraft.io.xt.model.SetupInfo;
import retrocraft.io.xt.model.SpotPostOrderRequest;
import retrocraft.io.xt.model.XtAccount;
import retrocraft.io.xt.service.XtService;
import retrocraft.io.xt.utils.XtAccountProperties;
import retrocraft.io.xt.utils.XtHttpUtil;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/xt", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class XtController {

    @Autowired
    private XtAccountProperties accountProperties;

    @Autowired
    private XtService service;

    @PostMapping
    public ResponseEntity<?> postOrder(@RequestBody() SpotPostOrderRequest request) throws JsonProcessingException {
        String uri = "/v4/order";
        ObjectMapper mapper = new ObjectMapper();

        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey2());
        account.setPrivateKey(accountProperties.getPrivateKey2());

        String result = XtHttpUtil.post(account,uri,mapper.writeValueAsString(request));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/setup")
    public ResponseEntity<?> postSetup(@RequestBody() SetupInfo request) throws JsonProcessingException {

        XtService.minPrice = request.getMinPrice();
        XtService.maxPrice = request.getMaxPrice();

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/stop")
    public ResponseEntity<?> setStop() {

        XtService.stop = !XtService.stop;
        return new ResponseEntity<>(XtService.stop, HttpStatus.OK);
    }

    @GetMapping("/getDexPrice")
    public ResponseEntity<?> getDexPrice() throws Exception {
        return new ResponseEntity<>(service.getGeckoPriceDex(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) {
        String uri = "/v4/order";
        Map<String, Object> param = new HashMap<>();
        param.put("orderId", id);
        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey1());
        account.setPrivateKey(accountProperties.getPrivateKey1());
        return new ResponseEntity<>(XtHttpUtil.get(account,uri, param), HttpStatus.OK);
    }
    @PostMapping("delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        String uri = "/v4/order/"+id;
        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey1());
        account.setPrivateKey(accountProperties.getPrivateKey1());
        return new ResponseEntity<>(XtHttpUtil.delete(account,uri, null), HttpStatus.OK);
    }

    @GetMapping("/openOrder")
    public ResponseEntity<?> openOrder() {
        String uri = "/v4/open-order";
        Map<String, Object> param = new HashMap<>();
        param.put("symbol", "retro_usdt");
        param.put("bizType", "SPOT");

        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey2());
        account.setPrivateKey(accountProperties.getPrivateKey2());
        return new ResponseEntity<>(XtHttpUtil.get(account,uri, param), HttpStatus.OK);
    }

    @GetMapping("/deleteAll")
    public ResponseEntity<?> deleteAllOrder() throws JsonProcessingException {
        String uri = "/v4/open-order";
        Map<String, Object> param = new HashMap<>();
        param.put("symbol", "retro_usdt");
        param.put("bizType", "SPOT");

        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey1());
        account.setPrivateKey(accountProperties.getPrivateKey1());
        String result = XtHttpUtil.get(account,uri, param);
        JSONObject jsonObject = new JSONObject(result);
        JSONArray resultArray = jsonObject.getJSONArray("result");

        for (int i = 0; i < resultArray.size(); i++) {
            JSONObject resultObject = resultArray.getJSONObject(i);
            String orderId = resultObject.getStr("orderId");
            deleteOrder(orderId);

        }
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @GetMapping("/fullTicket")
    public ResponseEntity<?> fullTicket() {
        String uri = "/v4/public/ticker";
        Map<String, Object> param = new HashMap<>();
        param.put("symbol", "retro_usdt");
        //param.put("bizType", "SPOT");

        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey1());
        account.setPrivateKey(accountProperties.getPrivateKey1());
        return new ResponseEntity<>(XtHttpUtil.get(account,uri, param), HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {
        String uri = "/v4/balance";
        Map<String, Object> param = new HashMap<>();
        param.put("currency", "usdt");
        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey2());
        account.setPrivateKey(accountProperties.getPrivateKey2());
        return new ResponseEntity<>(XtHttpUtil.get(account,uri, param), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<?> history() {
        String uri = "/v4/history-order";
        Map<String, Object> param = new HashMap<>();
        param.put("bizType", "SPOT");
        XtAccount account = new XtAccount();
        account.setAppKey(accountProperties.getAppKey1());
        account.setPrivateKey(accountProperties.getPrivateKey1());
        return new ResponseEntity<>(XtHttpUtil.get(account,uri, param), HttpStatus.OK);
    }
}
