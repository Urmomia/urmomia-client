package dev.urmomia.rendering;

import dev.urmomia.MainClient;
import dev.urmomia.rendering.text.CustomTextRenderer;

import java.io.*;

public class Fonts {
    public static void reset() {
        File[] files = MainClient.FOLDER.exists() ? MainClient.FOLDER.listFiles() : new File[0];
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".ttf") || file.getName().endsWith(".TTF")) {
                    file.delete();
                }
            }
        }
    }

    public static void init() {
        File[] files = MainClient.FOLDER.exists() ? MainClient.FOLDER.listFiles() : new File[0];
        File fontFile = null;
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".ttf") || file.getName().endsWith(".TTF")) {
                    fontFile = file;
                    break;
                }
            }
        }

        if (fontFile == null) {
            try {
                fontFile = new File(MainClient.FOLDER, "best_font.ttf");
                fontFile.getParentFile().mkdirs();

                InputStream in = MainClient.class.getResourceAsStream("/assets/urmomia-client/best_font.ttf");
                OutputStream out = new FileOutputStream(fontFile);

                byte[] bytes = new byte[255];
                int read;
                while ((read = in.read(bytes)) > 0) out.write(bytes, 0, read);

                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MainClient.FONT = new CustomTextRenderer(fontFile);
    }
}
