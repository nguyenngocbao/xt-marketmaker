package retrocraft.io.xt.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class XtAccountProperties {
    @Value("${xt.appKey1}")
    private String appKey1;
    @Value("${xt.appKey2}")
    private String appKey2;
    @Value("${xt.privateKey1}")
    private String privateKey1;
    @Value("${xt.privateKey2}")
    private String privateKey2;

}
