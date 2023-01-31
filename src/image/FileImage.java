package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;

    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates
        int newWidth = changeBinaryDimensions(origWidth); //TODO: change
        int newHeight = changeBinaryDimensions(origHeight); //TODO: change
        pixelArray = new Color[newHeight][newWidth];
        reAdaptImage(im, origWidth, origHeight, newWidth, newHeight);
    }

    private void reAdaptImage(BufferedImage im, int origWidth, int origHeight, int newWidth, int newHeight) {
        for(int row = 0; row < newHeight; row++) {
            for(int col = 0; col < newWidth; col++) {
                if(!outOfBounds(origWidth, origHeight, col, row)) {
                    pixelArray[row][col] = new Color(im.getRGB(col, row));
                }
                else {
                    pixelArray[row][col] = DEFAULT_COLOR;
                }
            }
        }
    }

    private boolean outOfBounds(int origWidth, int origHeight, int col, int row) {
        return origWidth <= col || origHeight <= row;
    }

    private double logBase2(int number) {
        return (Math.log(number) / Math.log(2));
    }

    private int changeBinaryDimensions(int dimension) {
        return (int) (Math.pow(2, Math.ceil(logBase2(dimension))));
    }

    @Override
    public int getWidth() {
        //TODO: implement the function
        return pixelArray[0].length;
    }

    @Override
    public int getHeight() {
        //TODO: implement the function
        return pixelArray.length;
    }

    @Override
    public Color getPixel(int x, int y) {
        //TODO: implement the function
        return pixelArray[y][x];
    }
}
