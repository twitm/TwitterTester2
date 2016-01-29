package nl.tamasja.tools;

import nl.tamasja.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TIS 27-7-2014.18:52
 */
public class WriteResource {

    public static void write(String resource) throws IOException {

        InputStream is = Main.class.getClass().getResourceAsStream("/scripts/" + resource);

        File file = new File(resource);


        FileOutputStream fop = new FileOutputStream(file);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = is.read(bytes)) != -1) {
            fop.write(bytes, 0, read);
        }


    }

    public static void clean(String resource) {
        File file = new File(resource);
        file.delete();
    }

}
