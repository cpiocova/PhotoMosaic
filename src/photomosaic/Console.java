/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photomosaic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 *
 * @author Jose Pio Montilva y Giselt Parra
 */
public class Console {

    private String[] args;

    public Console(String[] args) {
        this.args = args;
    }

    public void chooseOption() {
        switch (args[1]) {
            case "download":
                int radioDownload = Integer.parseInt(args[2]);
                SaveImage.ReadURL(radioDownload);
                break;

            case "vector":
                int radioCV = Integer.parseInt(args[2]);
                File alternative = new File("repositories/repository" + args[2]);
                File directory = alternative.exists() ? alternative : new File("C:\\Users\\thecnomacVZLA\\Documents\\NetBeansProjects\\Photomosaic\\repositories\\repository" + args[2]);
                VectorsCriteria1(directory, radioCV);
                VectorsCriteria2(directory, radioCV);
                break;
                
            case "mosaic":
                
                break;
        }
    }

    public static void VectorsCriteria1(File directory, int index) {
        int fileCount = directory.list().length;
        PrintWriter outFile = null;
        File cvFile = new File("vectors/criteria0/vector" + index + ".txt");
        cvFile.getParentFile().mkdirs();
        try {
            cvFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            outFile = new PrintWriter(cvFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        int id = 0;
        for (int i = 0; i < fileCount; i++) {

            double r = 0, g = 0, b = 0;

            Image image = new Image("file:" + directory + "/" + i + ".jpg");
            PixelReader pixelReader = image.getPixelReader();

            double pixelNumber = image.getWidth() * image.getHeight();

            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = pixelReader.getColor(x, y);
                    r = r + color.getRed();
                    g = g + color.getGreen();
                    b = b + color.getBlue();
                    if (r == Double.NaN || g == Double.NaN || b == Double.NaN) {
                        break;
                    }
                }
            }

            r = r / pixelNumber;
            g = g / pixelNumber;
            b = b / pixelNumber;

            if (!Double.isNaN(r) && !Double.isNaN(g) && !Double.isNaN(b)) {
                outFile.println(index + " " + i + " " + r + " " + g + " " + b);
                id++;
            } else {
                id = id < 0 ? 0 : id--;
            }
        }
        outFile.close();
    }

    public static void VectorsCriteria2(File directory, int index) {
        try {
            int fileCount = directory.list().length;
            PrintWriter outFile = null;
            File cvFile = new File("./vectors/criteria1/vector" + index + ".txt");
            cvFile.getParentFile().mkdirs();
            cvFile.createNewFile();
            try {
                outFile = new PrintWriter(cvFile);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            int id = 0;
            for (int i = 0; i < fileCount; i++) {

                double r = 0, g = 0, b = 0;

                Image image = new Image("file:" + directory + "/" + i + ".jpg");
                PixelReader pixelReader = image.getPixelReader();

                double pixelNumber = image.getWidth() * image.getHeight();

                for (int x = 0; x < image.getWidth(); x++) {
                    for (int y = 0; y < image.getHeight(); y++) {
                        Color color = pixelReader.getColor(x, y);
                        r = r + color.getRed();
                        g = g + color.getGreen();
                        b = b + color.getBlue();
                        if (r == Double.NaN || g == Double.NaN || b == Double.NaN) {
                            break;
                        }
                    }
                }

                r = r / pixelNumber;
                g = g / pixelNumber;
                b = b / pixelNumber;

                if (!Double.isNaN(r) && !Double.isNaN(g) && !Double.isNaN(b)) {
                    outFile.println(index + " " + i + " " + r + " " + g + " " + b);
                    id++;
                } else {
                    id = id < 0 ? 0 : id--;
                }
            }
            outFile.close();

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
