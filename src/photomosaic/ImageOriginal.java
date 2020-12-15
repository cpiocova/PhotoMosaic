/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photomosaic;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 *
 * @author Jose Pio Montilva y Giselt Parra
 */
public class ImageOriginal {
    
    private Color [][] weightedAverage;
    private Image currentImage;
    private int [] subdivisions;
    

    public Image getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(Image currentImage) {
        this.currentImage = currentImage;
    }

    public Color[][] getWeightedAverage() {
        return weightedAverage;
    }

    public void setWeightedAverage(Color[][] weightedAverage) {
        this.weightedAverage = weightedAverage;
    }   

    public int[] getSubdivisions() {
        return subdivisions;
    }

    public void setSubdivisions(int[] subdivisions) {
        this.subdivisions = subdivisions;
    }
    
    
    
}
