/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photomosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author Jose Pio Montilva y Giselt Parra
 */
public class Console {

    private String[] args;
    private ImageOriginal imageOriginal;
    WritableImage writableImage; 

    public Console(String[] args) {
        this.args = args;
        imageOriginal = new ImageOriginal();
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
                String imagePath = args[2];
                boolean [] enableRepo = new boolean[] {Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]), Boolean.parseBoolean(args[5]), Boolean.parseBoolean(args[6])};
                int[] mosaicSize = new int[]{Integer.parseInt(args[7]), Integer.parseInt(args[8])};
                int[] imageSize = new int[] {Integer.parseInt(args[9]), Integer.parseInt(args[10])};
                int criteria = Integer.parseInt(args[11]);
                
                generatePhotoMosaic(imagePath, enableRepo, mosaicSize, imageSize, criteria);
                break;
                
            case "print": 
                break;
                
            case "pdf":
                
                break;
        }
    }
    
      private void generatePhotoMosaic(String imagePath, boolean[] enableRepo, int[] mosaicSize, int[] imageSize, int criteria) {
//        File img = new File(imagePath);
        String pathAbs = "C:\\Users\\thecnomacVZLA\\Documents\\NetBeansProjects\\Photomosaic\\" + imagePath;
        Image imageLoaded = new Image("file:" + pathAbs);
        System.out.println(imagePath);
        imageOriginal.setCurrentImage(imageLoaded);

        int width = (int) (imageOriginal.getCurrentImage().getWidth() / mosaicSize[0]);
        int height = (int) (imageOriginal.getCurrentImage().getHeight() / mosaicSize[1]);

        Color[][] waAvg = new Color[width][height];
        imageOriginal.setWeightedAverage(waAvg);

        for (int Q = 0; Q < height; Q++) {
            for (int P = 0; P < width; P++) {
                subdivisionsImage(P, Q, imageOriginal.getWeightedAverage(), mosaicSize);
            }
        }
        calculateCandidates(width, height, enableRepo, criteria);
        calculateImage(mosaicSize, imageSize);
        saveImageJPG();
      }
      
    private void calculateImage(int[] mosaicSize, int[] imageSize) {

        int wmos = mosaicSize[0];
        int hmos = mosaicSize[1];

        int inputWidth = imageSize[0];
        int inputHeight = imageSize[1];
        
        int originalWidth = (int) imageOriginal.getCurrentImage().getWidth();
        int originalHeight = (int) imageOriginal.getCurrentImage().getHeight();

        double reasonWidth = (double) inputWidth / (double) originalWidth;
        double reasonHeight = (double) inputHeight / (double) originalHeight;

        int mScaleWidth = (int) Math.round(reasonWidth * wmos);
        int mScaleHeight = (int) Math.round(reasonHeight * hmos);

        int width = (int) Math.floor(inputWidth / mScaleWidth);
        int height = (int) Math.floor(inputHeight / mScaleHeight);

        writableImage = new WritableImage(inputWidth, inputHeight);

        for (int Q = 0; Q < height; Q++) {
            for (int P = 0; P < width; P++) {
                writerImage(P, Q, mScaleWidth, mScaleHeight);
            }
        }
    }
      
    
    private void writerImage(int P, int Q, int m, int n) {
        Indexes candidates = imageOriginal.getCandidates()[P][Q];
        int photoIndex = candidates.getPhotoIndex();
        int fileIndex = candidates.getFileIndex();
        
        String imagePath = "C:\\Users\\thecnomacVZLA\\Documents\\NetBeansProjects\\Photomosaic\\repositories\\repository" + fileIndex + "/" + photoIndex + ".jpg";
//        Image imageCandidate = new Image("file:" + "repositories/repository" + fileIndex + "/" + photoIndex + ".jpg");
        Image imageCandidate = new Image("file:" + imagePath);
        Image scaled = scaleImage(imageCandidate, m, n);
        PixelReader pixelReader = scaled.getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < m; x++) {
                Color color = pixelReader.getColor(x, y);
                pixelWriter.setColor(x + (P * m), y + (Q * n), color);
            }
        }
    }


    private Image scaleImage(Image image, double mosX, double mosY) {
        double zoomValueX = (double) mosX / (double) image.getWidth();
        double zoomValueY = (double) mosY / (double) image.getHeight();

        int width = (int) Math.round(image.getWidth() * zoomValueX);
        int height = (int) Math.round(image.getHeight() * zoomValueY);

        width = width > 0 ? width : 1;
        height = height > 0 ? height : 1;

        WritableImage scaleWritable = new WritableImage(width, height);
        PixelWriter scaleWriter = scaleWritable.getPixelWriter();
        PixelReader imageRepo = image.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < width && y < height) {
                    int zX = (int) (x / zoomValueX);
                    int zY = (int) (y / zoomValueY);
                    Color zoomColor = imageRepo.getColor(zX, zY);
                    scaleWriter.setColor(x, y, zoomColor);
                }
            }
        }

        return scaleWritable;
    }    
      

    private void subdivisionsImage(int P, int Q, Color[][] waAvg, int [] mosaicSize) {
        int m = mosaicSize[0];
        int n = mosaicSize[1];
        
        Image imageLoaded = imageOriginal.getCurrentImage();
        double r = 0, g = 0, b = 0;
        PixelReader pixelReader = imageLoaded.getPixelReader();
        double pixelNumber = m * n;
        for (int y = Q * n; y < n + (Q * n); y++) {
            for (int x = P * m; x < m + (P * m); x++) {
                Color color = pixelReader.getColor(x, y);
                r = r + color.getRed();
                g = g + color.getGreen();
                b = b + color.getBlue();
            }
        }
        r = r / pixelNumber;
        g = g / pixelNumber;
        b = b / pixelNumber;
        Color wa = new Color(r, g, b, 1.0);
        waAvg[P][Q] = wa;
        imageOriginal.setWeightedAverage(waAvg);
    }
    
    private void calculateCandidates(int width, int height, boolean[]enableRepo, int criteria) {      
        Indexes [][] candidatesImg = new Indexes[width][height];
        Indexes candidates;

        for (int Q = 0; Q < height; Q++) {
            for (int P = 0; P < width; P++) {
                candidates = findCandidate(P, Q, enableRepo, criteria);
                candidatesImg[P][Q] = candidates;
            }
        }

        imageOriginal.setCandidates(candidatesImg);
    }

    
    private Indexes findCandidate(int P, int Q, boolean[] enableRepo, int criteria) {
        Color color = imageOriginal.getWeightedAverage()[P][Q];
        Scanner scan;
        
        double[] firgb = new double[5];
        double min = -1;
        double temp = 0;
        Indexes candidates = new Indexes();  

        for (int i = 0; i < enableRepo.length; i++) {
            if (enableRepo[i]) {
                File alternative = new File("vectors/criteria" + criteria + "/vector" + i + ".txt");
                File file = alternative.exists() ? alternative : new File("C:\\Users\\thecnomacVZLA\\Documents\\NetBeansProjects\\Photomosaic\\vectors\\criteria" + criteria + "/vector" + i + ".txt");
                try {
                    scan = new Scanner(file);
                    if (scan.hasNextDouble() && min == -1) {
                        for (int channels = 0; channels < firgb.length; channels++) {
                            firgb[channels] = scan.nextDouble();
                        }
                        min = calculateDistance(firgb, color);
                    }
                    while (scan.hasNextDouble()) {
                        for (int channels = 0; channels < firgb.length; channels++) {
                            firgb[channels] = scan.nextDouble();
                        }
                        temp = calculateDistance(firgb, color);
                        if (temp < min) {
                            min = temp;
                            candidates.setFileIndex((int) firgb[0]);
                            candidates.setPhotoIndex((int) firgb[1]);
                            candidates.setDistance(min);
                        }
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return candidates;
    }
    
    private double calculateDistance(double[] color1, Color color2) {
        double r = color1[2] - color2.getRed();
        double g = color1[3] - color2.getGreen();
        double b = color1[4] - color2.getBlue();

        r = r * r;
        g = g * g;
        b = b * b;

        return Math.sqrt(r + g + b);
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
    
    private void saveImageJPG() {
        File file = new File("mosaic00.jpg");
        jpgSaver(writableImage, file);
    }
    
        private void jpgSaver(Image content, File file) {
            BufferedImage bfImage = SwingFXUtils.fromFXImage(content, null);
            BufferedImage bfImage2 = null;
            bfImage2 = new BufferedImage(bfImage.getWidth(), bfImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            bfImage2.getGraphics().drawImage(bfImage, 0, 0, null);
        try {
            ImageIO.write(bfImage2, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
