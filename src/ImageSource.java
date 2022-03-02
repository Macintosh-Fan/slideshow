import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * The class that contains the image itself, some related data, and a method to get the {@link ImageIcon} of the image.
 *
 * @author Macintosh-Fan
 */
public class ImageSource {
    /**
     * The {@link ImageIcon} of the image.
     */
    protected ImageIcon imageIcon;

    /**
     * The buffered image.
     */
    protected final BufferedImage BUFFERED_IMAGE;

    /**
     * The {@link JComponent} to use for image resizing.
     */
    protected final JComponent J_COMPONENT;

    /**
     * The flag to indicate the type of algorithm to use for image resampling.
     *
     * @see java.awt.Image java.awt.Image constants for image resampling flags
     * @see BufferedImage#getScaledInstance(int, int, int)
     */
    protected final int HINTS;

    /**
     * Constructs the {@link ImageSource}.
     *
     * @param bufferedImage the buffered image
     * @param jComponent    the {@link JComponent} to use for image resizing
     * @param hints         the flag to indicate the type of algorithm to use for image resampling
     * @see ImageSource#HINTS
     */
    public ImageSource(BufferedImage bufferedImage, JComponent jComponent, int hints) {
        BUFFERED_IMAGE = bufferedImage;
        J_COMPONENT = jComponent;
        HINTS = hints;
    }

    /**
     * Gets the {@link ImageIcon} of the image, with size checks to resize if necessary.
     *
     * @return the {@link ImageIcon} of the image
     */
    public ImageIcon getImage() {
        if (imageIcon == null || (imageIcon.getIconWidth() != J_COMPONENT.getWidth() || imageIcon.getIconHeight() != J_COMPONENT.getHeight())) {
            double imageWidthToHeight = (double) BUFFERED_IMAGE.getWidth() / BUFFERED_IMAGE.getHeight();
            double jComponentWidthToHeight = (double) J_COMPONENT.getWidth() / J_COMPONENT.getHeight();
            double divisionFactor;

            if (imageWidthToHeight == jComponentWidthToHeight) {
                imageIcon = new ImageIcon(BUFFERED_IMAGE.getScaledInstance(J_COMPONENT.getWidth(), J_COMPONENT.getHeight(), HINTS));
            } else if (imageWidthToHeight > jComponentWidthToHeight) {
                divisionFactor = (double) BUFFERED_IMAGE.getWidth() / J_COMPONENT.getWidth();
                imageIcon = new ImageIcon(BUFFERED_IMAGE.getScaledInstance(J_COMPONENT.getWidth(), (int) Math.round(BUFFERED_IMAGE.getHeight() / divisionFactor), HINTS));
            } else {
                divisionFactor = (double) BUFFERED_IMAGE.getHeight() / J_COMPONENT.getHeight();
                imageIcon = new ImageIcon(BUFFERED_IMAGE.getScaledInstance((int) Math.round(BUFFERED_IMAGE.getWidth() / divisionFactor), J_COMPONENT.getHeight(), HINTS));
            }
        }

        return imageIcon;
    }
}
