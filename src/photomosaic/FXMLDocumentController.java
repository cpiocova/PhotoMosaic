/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photomosaic;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 *
 * @author Jose Pio Montilva y Giselt Parra
 */
public class FXMLDocumentController implements Initializable {

    ImageOriginal imageOriginal;

    @FXML
    private RadioButton generateAnimals;
    @FXML
    private ToggleGroup radioGenerate;
    @FXML
    private RadioButton generateMountains;
    @FXML
    private RadioButton generateGames;
    @FXML
    private RadioButton generateCars;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField subWidth;
    @FXML
    private TextField subHeight;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        imageOriginal = new ImageOriginal();
    }

    @FXML
    private void downloadImages(ActionEvent event) {
        int radioButtonGenerate = radioGenerate.getToggles().indexOf(radioGenerate.getSelectedToggle());
        SaveImage.ReadURL(radioButtonGenerate);
    }

    @FXML
    private void generateCVAnimals(ActionEvent event) {
        File directory = new File("repositories/repository0");
        generateCV(directory, 0);
    }

    @FXML
    private void generateCVMountains(ActionEvent event) {
        File directory = new File("repositories/repository0");
        generateCV(directory, 1);
    }

    @FXML
    private void generateCVGames(ActionEvent event) {
        File directory = new File("repositories/repository0");
        generateCV(directory, 2);
    }

    @FXML
    private void generateCVCars(ActionEvent event) {
        File directory = new File("repositories/repository0");
        generateCV(directory, 3);
    }

    private void generateCV(File directory, int index) {
        int fileCount = directory.list().length;
        PrintWriter outFile = null;
        File cvFile = new File("vectors/vector" + index + ".txt");
        try {
            outFile = new PrintWriter(cvFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < fileCount; i++) {
            double r = 0, g = 0, b = 0;

            Image image = new Image("file:" + directory + "/" + i + ".jpg");
            PixelReader pixelReader = image.getPixelReader();

            double pixelSize = image.getWidth() * image.getHeight();

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

            r = r / pixelSize;
            g = g / pixelSize;
            b = b / pixelSize;

            if (!Double.isNaN(r) && !Double.isNaN(g) && !Double.isNaN(b)) {
                outFile.println(r + " " + g + " " + b);
            }
        }
        outFile.close();

    }

    @FXML
    private void imageChooser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load image in JPG format");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"));

        File imgPath = fileChooser.showOpenDialog(null);
        if (imgPath != null) {
            Image imageLoaded = new Image("file:" + imgPath.getAbsolutePath());
            imageOriginal.setCurrentImage(imageLoaded);
            imageView.setImage(imageLoaded);
        }
    }

    private void averageSegments() {
        Image imageLoaded = imageOriginal.getCurrentImage();
        double r = 0, g = 0, b = 0;

        PixelReader pixelReader = imageLoaded.getPixelReader();

        double pixelSize = imageLoaded.getWidth() * imageLoaded.getHeight();

        for (int x = 0; x < imageLoaded.getWidth(); x++) {
            for (int y = 0; y < imageLoaded.getHeight(); y++) {
                Color color = pixelReader.getColor(x, y);
                r = r + color.getRed();
                g = g + color.getGreen();
                b = b + color.getBlue();
                if (r == Double.NaN || g == Double.NaN || b == Double.NaN) {
                    break;
                }
            }
        }

        r = r / pixelSize;
        g = g / pixelSize;
        b = b / pixelSize;

        Color wa = new Color(r, g, b, 1.0);

    }

    @FXML
    private void generatePhotoMosaic(ActionEvent event) {
        int[] sub = new int[2];
        sub[0] = 10; sub[1] = 10;
        Color [][] waAvg = new Color[10][10];
        imageOriginal.setWeightedAverage(waAvg);
        
        imageOriginal.setSubdivisions(sub);
        for(int Q = 0; Q < 10; Q++) {
            for(int P = 0; P < 10; P++ ) {
                subdivisionsImage(P, Q,imageOriginal.getWeightedAverage());
            }
        }
             
        
        Scanner scan;
        File file = new File("vectors/vector0.txt");
        double[] rgb = new double[3];
        try {
            scan = new Scanner(file);
            while (scan.hasNextDouble()) {
                for (int channels = 0; channels < 3; channels++) {
                    rgb[channels] = scan.nextDouble();
                }
                System.out.println();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
    
    private void subdivisionsImage(int P, int Q, Color [][] waAvg){
        int [] sub = imageOriginal.getSubdivisions();
        int m = sub[0], n = sub[1];
        
            Image imageLoaded = imageOriginal.getCurrentImage();
            double r = 0, g = 0, b = 0;
            PixelReader pixelReader = imageLoaded.getPixelReader();
            double pixelSize = imageLoaded.getWidth() * imageLoaded.getHeight();
            for (int y = 0 + (Q * n); y < n + (Q * n); y++) {
                for (int x = 0 + (P * m); x < m + (P * m); x++) {
                    Color color = pixelReader.getColor(x, y);
                    r = r + color.getRed();
                    g = g + color.getGreen();
                    b = b + color.getBlue();
                }
            }
            r = r / pixelSize;
            g = g / pixelSize;
            b = b / pixelSize;
            Color wa = new Color(r, g, b, 1.0);
            waAvg[P][Q] = wa;
            imageOriginal.setWeightedAverage(waAvg);    
    }

}
