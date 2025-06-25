import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageFilterGUI {

    private JFrame frame;
    private JLabel imageLabel;
    private BufferedImage originalImage;
    private BufferedImage grayscaleImage;
    private BufferedImage processedImage;
    private String[] filterOptions = {"Original Image", "Grayscale", "Differential", "Gradient", "Prewitt", "Sobel", "Modified Sobel"};

    public ImageFilterGUI() {
        frame = new JFrame("Image Filter Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setLayout(new BorderLayout());


        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(imageLabel, BorderLayout.CENTER);


        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());


        JComboBox<String> filterDropdown = new JComboBox<>(filterOptions);
        filterDropdown.addActionListener(e -> applyFilter((String) filterDropdown.getSelectedItem()));
        controlPanel.add(new JLabel("Select Filter:"));
        controlPanel.add(filterDropdown);

        JButton saveButton = new JButton("Save Image");
        saveButton.addActionListener(e -> saveImage());
        controlPanel.add(saveButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        loadImageAutomatically();
        frame.setVisible(true);
    }

    private void loadImageAutomatically() {
        try {
            originalImage = ImageIO.read(new File("Image.jpg")); 
            grayscaleImage = applyGrayscaleFilter(originalImage);
            processedImage = originalImage;
            imageLabel.setIcon(new ImageIcon(originalImage));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error loading image: " + e.getMessage());
        }
    }

    private void applyFilter(String filterName) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(frame, "No image loaded.");
            return;
        }

        switch (filterName) {
            case "Original Image":
                processedImage = originalImage;
                break;
            case "Grayscale":
                processedImage = grayscaleImage;
                break;
            case "Differential":
                processedImage = applyDifferentialFilter(grayscaleImage);
                break;
            case "Gradient":
                processedImage = applyGradientFilter(grayscaleImage);
                break;
            case "Prewitt":
                processedImage = applyPrewittFilter(grayscaleImage);
                break;
            case "Sobel":
                processedImage = applySobelFilter(grayscaleImage);
                break;
            case "Modified Sobel":
                processedImage = applyModifiedSobelFilter(grayscaleImage);
                break;
            
        }

        imageLabel.setIcon(new ImageIcon(processedImage));
    }


    private void saveImage() {
        if (processedImage == null) {
            JOptionPane.showMessageDialog(frame, "No processed image to save.");
            return;
        }

        try {
            File outputFile = new File("processed_image.jpg");
            ImageIO.write(processedImage, "jpg", outputFile);
            JOptionPane.showMessageDialog(frame, "Image saved as " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving image: " + e.getMessage());
        }
    }

    private BufferedImage applyGrayscaleFilter(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    private BufferedImage applyDifferentialFilter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 1; x < width; x++) {
            for (int y = 1; y < height; y++) {
                int rgb1 = image.getRGB(x, y);
                int rgb2 = image.getRGB(x - 1, y);
                int diff = Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF));
                int newColor = (diff << 16) | (diff << 8) | diff;
                result.setRGB(x, y, newColor);
            }
        }

        return result;
    }

    private BufferedImage applyGradientFilter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int gx = ((image.getRGB(x + 1, y) & 0xFF) - (image.getRGB(x - 1, y) & 0xFF));
                int gy = ((image.getRGB(x, y + 1) & 0xFF) - (image.getRGB(x, y - 1) & 0xFF));
                int gradient = (int) Math.sqrt(gx * gx + gy * gy);
                gradient = Math.min(255, gradient);
                int newColor = (gradient << 16) | (gradient << 8) | gradient;
                result.setRGB(x, y, newColor);
            }
        }

        return result;
    }

    private BufferedImage applyPrewittFilter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] kernelX = {
            {-1, 0, 1},
            {-1, 0, 1},
            {-1, 0, 1}
        };
        int[][] kernelY = {
            {-1, -1, -1},
            {0, 0, 0},
            {1, 1, 1}
        };

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int gx = 0;
                int gy = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = image.getRGB(x + i, y + j) & 0xFF;
                        gx += kernelX[i + 1][j + 1] * rgb;
                        gy += kernelY[i + 1][j + 1] * rgb;
                    }
                }

                int gradient = (int) Math.sqrt(gx * gx + gy * gy);
                gradient = Math.min(255, gradient);
                int newColor = (gradient << 16) | (gradient << 8) | gradient;
                result.setRGB(x, y, newColor);
            }
        }

        return result;
    }

    private BufferedImage applySobelFilter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] kernelX = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
        };
        int[][] kernelY = {
            {-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}
        };

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int gx = 0;
                int gy = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = image.getRGB(x + i, y + j) & 0xFF;
                        gx += kernelX[i + 1][j + 1] * rgb;
                        gy += kernelY[i + 1][j + 1] * rgb;
                    }
                }

                int gradient = (int) Math.sqrt(gx * gx + gy * gy);
                gradient = Math.min(255, gradient);
                int newColor = (gradient << 16) | (gradient << 8) | gradient;
                result.setRGB(x, y, newColor);
            }
        }

        return result;
    }

    private BufferedImage applyModifiedSobelFilter(BufferedImage image) {
        int[][] kernelX = {
            {-1, -2, 0, 2, 1},
            {-4, -10, 0, 10, 4},
            {-7, -17, 0, 17, 7},
            {-4, -10, 0, 10, 4},
            {-1, -2, 0, 2, 1}
        };

        int[][] kernelY = {
            {1, 4, 7, 4, 1},
            {2, 10, 17, 10, 2},
            {0, 0, 0, 0, 0},
            {-2, -10, -17, -10, -2},
            {-1, -4, -7, -4, -1}
        };

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 2; x < width - 2; x++) {
            for (int y = 2; y < height - 2; y++) {
                int gx = 0;
                int gy = 0;

                for (int i = -2; i <= 2; i++) {
                    for (int j = -2; j <= 2; j++) {
                        int rgb = image.getRGB(x + i, y + j) & 0xFF;
                        gx += kernelX[i + 2][j + 2] * rgb;
                        gy += kernelY[i + 2][j + 2] * rgb;
                    }
                }

                int magnitude = (int) Math.min(255, Math.sqrt(gx * gx + gy * gy));
                int newColor = (magnitude << 16) | (magnitude << 8) | magnitude;
                result.setRGB(x, y, newColor);
            }
        }

        return result;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageFilterGUI::new);
    }
}
