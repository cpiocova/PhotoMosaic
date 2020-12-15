/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photomosaic;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 *
 * @author Jose Pio Montilva y Giselt Parra
 */
public class SaveImage {
    
    public static void ReadURL(int radioButton) {

        try {
            String path = "urls/links" + radioButton + ".txt";
            Scanner scanner = new Scanner(new File(path));
            int i = 0;
            while (scanner.hasNextLine()) {
                String imageUrl = scanner.nextLine();

                String format = imageUrl.substring(imageUrl.lastIndexOf(".") + 1, imageUrl.length());
                if ("jpg".equals(format)) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;

                    try {
                        URL url = new URL(imageUrl);
                        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
                        URLConnection con = url.openConnection();

                        con.setRequestProperty("User-Agent", USER_AGENT);

                        int contentLength = con.getContentLength();

                        inputStream = con.getInputStream();

                        File file = new File("repositories/repository" + radioButton + "/" + i + ".jpg");
                        file.getParentFile().mkdirs(); 
                        file.createNewFile();

                        outputStream = new FileOutputStream(file, false);

                        byte[] buffer = new byte[2048];
                        int length;

                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }

                    } catch (MalformedURLException e) {
//                        System.out.println("MalformedURLException :- " + e.getMessage() + ". Index: " + i);
                        i--;

                    } catch (FileNotFoundException e) {
//                        System.out.println("FileNotFoundException :- " + e.getMessage() + ". Index: " + i);
                        i--;

                    } catch (IOException e) {
//                        System.out.println("IOException :- " + e.getMessage() + ". Index: " + i);
                        i--;
                    }

                    i++;
                }
            }
            
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
