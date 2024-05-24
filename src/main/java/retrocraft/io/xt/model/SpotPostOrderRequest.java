package retrocraft.io.xt.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpotPostOrderRequest {
    private String symbol;
    private String side;
    private String type;
    private String timeInForce;

    private String bizType;
    private String price;

    private String quantity;
}
