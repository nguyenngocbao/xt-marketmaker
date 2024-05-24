package retrocraft.io;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XtMarketMaker {
    public static void main(String[] args) {
        SpringApplication.run(XtMarketMaker.class, args);
    }


}
