package algo.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class EnvLoader {

    private static final Logger logger = LoggerFactory.getLogger(EnvLoader.class.getName());

    public static void loadEnv() {
        try {
            InputStream inputStream = EnvLoader.class.getClassLoader()
                    .getResourceAsStream("tokens.env");

            if (inputStream != null) {
                Properties props = new Properties();
                props.load(inputStream);
                inputStream.close();

                for (String key : props.stringPropertyNames()) {
                    if (System.getProperty(key) == null) {
                        System.setProperty(key, props.getProperty(key));
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}