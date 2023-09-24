package lib;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataGenerator {

    public static String getRandomEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "learnqa" + timestamp + "@example.com";
    }
}
