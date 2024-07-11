import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The main class for the program.
 *
 * @author Macintosh_Fan
 * @deprecated Due to Macintosh_Fan no longer using Java's swing library.
 */
@Deprecated
public final class Main {
    /**
     * Default image scaling for the program.
     *
     * @see Image java.awt.Image constants for scaling options
     */
    private static final int HINTS = Image.SCALE_SMOOTH;

    /**
     * The required properties for {@code config.properties}.
     */
    private static final String[] REQUIRED_PROPERTIES = {
            "directory",
            "pxWidth",
            "pxHeight",
            "sleepMillis",
            "maxThreads"
    };

    /**
     * The JFrame (window) title.
     */
    private static final String TITLE = "Slideshow";

    /**
     * The main method.
     *
     * @param args not used at this time
     * @throws InterruptedException if any thread interrupts the main thread
     * @throws ExecutionException if the retrieval of a task failed
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new FileReader("config.properties")));
        } catch (IOException e) {
            System.err.println("Properties file does not exist!");
            e.printStackTrace();
            System.exit(1);
        }

        for (String requiredProperty : REQUIRED_PROPERTIES) {
            if (properties.getProperty(requiredProperty) == null) {
                System.err.println("Missing required property (only counting first missing property): " + requiredProperty);
                System.exit(1);
            }
        }
        String picturesName = properties.getProperty("directory");
        String analyzingProperty;
        int maxThreads;
        long sleepMillis;
        int height;
        int width;
        try {
            analyzingProperty = properties.getProperty("pxWidth");
            width = Integer.parseInt(analyzingProperty);

            analyzingProperty = properties.getProperty("pxHeight");
            height = Integer.parseInt(analyzingProperty);

            analyzingProperty = properties.getProperty("sleepMillis");
            sleepMillis = Integer.parseInt(analyzingProperty);

            analyzingProperty = properties.getProperty("maxThreads");
            maxThreads = Integer.parseInt(analyzingProperty);
        } catch (NumberFormatException numberFormatException) {
            System.err.println("Not a valid integer for ");
            System.exit(1);
            return;
        }

        File PICTURES = new File(picturesName);
        File[] IMAGE_FILES = PICTURES.listFiles();
        if (IMAGE_FILES == null) {
            System.err.println('"' + picturesName + "\" is not a valid directory.");
            System.exit(1);
        }

        ExecutorService service = Executors.newFixedThreadPool(Math.min(IMAGE_FILES.length, maxThreads));
        List<Callable<BufferedImage>> callableBufferedImages = new ArrayList<>(IMAGE_FILES.length);
        for (File imageFile : IMAGE_FILES) {
            if (imageFile.isDirectory()) {
                continue;
            }
            callableBufferedImages.add(() -> ImageIO.read(imageFile));
        }

        List<Future<BufferedImage>> bufferedFutureImages;
        bufferedFutureImages = service.invokeAll(callableBufferedImages);
        service.shutdown();

        BufferedImage bufferedImage;
        List<BufferedImage> bufferedImages = new ArrayList<>(bufferedFutureImages.size());
        for (Future<BufferedImage> bufferedFutureImage : bufferedFutureImages) {
            bufferedImage = bufferedFutureImage.get();
            if (bufferedImage != null) {
                bufferedImages.add(bufferedImage);
            }
        }

        JFrame frame = new JFrame(TITLE);
        JPanel panel = new JPanel();
        JLabel imageLabel = null;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.add(panel);

        panel.setSize(frame.getContentPane().getSize());
        panel.setLayout(null);
        panel.setBackground(Color.BLACK);

        frame.setVisible(true);

        ImageSource[] IMAGE_SOURCES = new ImageSource[bufferedImages.size()];
        for (int i = 0; i < IMAGE_SOURCES.length; i++) {
            IMAGE_SOURCES[i] = new ImageSource(bufferedImages.get(i), panel, HINTS);
        }

        boolean start = true;
        List<ImageSource> imageSourcesAsList = Arrays.asList(IMAGE_SOURCES);

        // Can exit with clicking 'x' on the window
        while (true) {
            Collections.shuffle(imageSourcesAsList);
            for (ImageSource imageSource : imageSourcesAsList) {
                if (start) {
                    imageLabel = new JLabel(imageSource.getImage());
                    imageLabel.setSize(panel.getSize());
                    panel.add(imageLabel);
                    start = false;
                } else {
                    imageLabel.setIcon(imageSource.getImage());
                    imageLabel.setSize(panel.getSize());
                }
                Thread.sleep(sleepMillis);
            }
        }
    }
}
