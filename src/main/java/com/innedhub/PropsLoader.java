package com.innedhub;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropsLoader {
    public static void load(Properties properties) {
        try {
            File file = new File("src/main/resources/lib.properties");
            properties.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
