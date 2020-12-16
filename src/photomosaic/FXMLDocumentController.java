/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photomosaic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;



/**
 *
 * @author Jose Pio Montilva y Giselt Parra
 */
public class FXMLDocumentController implements Initializable {

    ImageOriginal imageOriginal;
    WritableImage writableImage;

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
    private TextField returnHeight;
    @FXML
    private TextField returnWidth;
    @FXML
    private TextField mosWidth;
    @FXML
    private TextField mosHeight;

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
                outFile.println(i + " " + r + " " + g + " " + b);
                id++;
            } else {
                id = id < 0 ? 0 : id--;
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
            returnHeight.setText("" + (int) imageLoaded.getHeight());
            returnWidth.setText("" + (int) imageLoaded.getWidth());

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

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean validateString(String in) {
        if (in != null && !in.isEmpty() && isNumeric(in)) {
            return true;
        }
        return false;
    }

    private boolean checkEntries() {
        boolean mW = validateString(mosWidth.getText());
        boolean mH = validateString(mosHeight.getText());
        boolean iW = validateString(returnWidth.getText());
        boolean iH = validateString(returnHeight.getText());

        if (mW && mH && iW && iH) {
            int mWNumber = Integer.parseInt(mosWidth.getText());
            int mHNumber = Integer.parseInt(mosHeight.getText());
            int iWNumber = Integer.parseInt(returnWidth.getText());
            int iHNumber = Integer.parseInt(returnHeight.getText());
            return mWNumber <= iWNumber && mHNumber <= iHNumber && mWNumber > 0 && mHNumber > 0 && iWNumber > 0 && iHNumber > 0;
        } else {
            return false;
        }
    }

    private void subdivisionsImage(int P, int Q, Color[][] waAvg) {
        int m = Integer.parseInt(mosWidth.getText());
        int n = Integer.parseInt(mosHeight.getText());

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

    @FXML
    private void generatePhotoMosaic(ActionEvent event) {
        int wmos = Integer.parseInt(mosWidth.getText());
        int hmos = Integer.parseInt(mosHeight.getText());

        if (checkEntries()) {
            int width = (int) (imageOriginal.getCurrentImage().getWidth() / wmos);
            int height = (int) (imageOriginal.getCurrentImage().getHeight() / hmos);

            Color[][] waAvg = new Color[width][height];
            imageOriginal.setWeightedAverage(waAvg);

            for (int Q = 0; Q < height; Q++) {
                for (int P = 0; P < width; P++) {
                    subdivisionsImage(P, Q, imageOriginal.getWeightedAverage());
                }
            }

            calculateCandidates(width, height);
            calculateReason();

        }
    }

    private void calculateReason() {

        int wmos = Integer.parseInt(mosWidth.getText());
        int hmos = Integer.parseInt(mosHeight.getText());

        int inputWidth = Integer.parseInt(returnWidth.getText());
        int inputHeight = Integer.parseInt(returnHeight.getText());
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

        imageView.setImage(writableImage);

    }

    private void writerImage(int P, int Q, int m, int n) {
        int candidate = imageOriginal.getCandidates()[P][Q];
        Image imageCandidate = new Image("file:" + "repositories/repository0/" + candidate + ".jpg");
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

    private void calculateCandidates(int width, int height) {
        int[][] candidateImg = new int[width][height];
        int candidate;

        for (int Q = 0; Q < height; Q++) {
            for (int P = 0; P < width; P++) {
                candidate = findCandidate(imageOriginal.getWeightedAverage()[P][Q], new File("vectors/vector0.txt"));
                candidateImg[P][Q] = candidate;
            }
        }

        imageOriginal.setCandidates(candidateImg);
    }

    private int findCandidate(Color color, File file) {
        Scanner scan;
        double[] irgb = new double[4];
        double min = 0, temp = 0;
        int candidateIndex = 0;

        try {
            scan = new Scanner(file);
            if (scan.hasNextDouble()) {
                for (int channels = 0; channels < 4; channels++) {
                    irgb[channels] = scan.nextDouble();
                }
                min = calculateDistance(irgb, color);
            }
            while (scan.hasNextDouble()) {
                for (int channels = 0; channels < 4; channels++) {
                    irgb[channels] = scan.nextDouble();
                }
                temp = calculateDistance(irgb, color);
                if (temp < min) {
                    min = temp;
                    candidateIndex = (int) irgb[0];
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        return candidateIndex;
    }

    private double calculateDistance(double[] color1, Color color2) {
        double r = color1[1] - color2.getRed();
        double g = color1[2] - color2.getGreen();
        double b = color1[3] - color2.getBlue();

        r = r * r;
        g = g * g;
        b = b * b;

        return Math.sqrt(r + g + b);
    }

    @FXML
    private void saveImageJPG() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image in JPG format");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            jpgSaver(writableImage, file);
        }
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

    @FXML
    public void printImage() {
        final PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            final Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = job.getJobSettings().getPageLayout();
            pageLayout = printer.createPageLayout(pageLayout.getPaper(), PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
            job.getJobSettings().setPageLayout(pageLayout);
            pageLayout = job.getJobSettings().getPageLayout();
            if (job.printPage(imageView)) {
                job.endJob();
            }
        }
    }

    @FXML
    private void handleToPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image in PDF format");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);
        
        try {
            PDDocument doc = new PDDocument();
            PDPage page = new PDPage();
            
            String imagePath = "repositories/repository0/1.jpg";

            String fileName = file.getAbsolutePath();
            
            doc.addPage(page);
            
            PDImageXObject img = PDImageXObject.createFromFile(imagePath, doc);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            
            content.drawImage(img, 0, 0, (float) Integer.parseInt(returnWidth.getText()), (float) Integer.parseInt(returnHeight.getText()));
            content.close();
            doc.save(fileName);
            doc.close();
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
   
    }
    

    


}