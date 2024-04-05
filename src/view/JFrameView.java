package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.AbstractAction;
import javax.swing.UIManager;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.Features;
import model.ReadOnlyModel;


/**
 * This class represents a GUI window for an image editing application. Such an application has
 * buttons, panes, radio-buttons and option panes.
 */
public class JFrameView extends JFrame implements IView {
  private final ReadOnlyModel roImages;
  private final JButton loadButton;
  private final JButton saveButton;
  private final JButton rgbCombineButton;
  private final JButton brightenButton;
  private final JButton blurButton;
  private final JButton sharpenButton;
  private final JButton ditherButton;
  private final JButton horizontalFlip;
  private final JButton verticalFlip;
  private final JButton rgbSplitButton;
  private final JRadioButton[] radioButtons;
  private final JPanel imagePanel;
  private final JFileChooser fileChooser;
  private static final int bins = 256;
  private HistogramDataset dataset;
  private XYLineAndShapeRenderer renderer;

  /**
   * Public constructor to initialise the GUI window.
   *
   * @param caption caption for the window
   * @param ro      read-only image object
   */
  public JFrameView(String caption, ReadOnlyModel ro) {
    super(caption);
    roImages = ro;
    setSize(500, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    //scroll bars around this main panel
    JScrollPane mainScrollPane = new JScrollPane(mainPanel);
    add(mainScrollPane);

    //the all the image manipulation methods are present in text panel
    JPanel textPanel = new JPanel();
    textPanel.setBorder(BorderFactory.createTitledBorder("Image Manipulation operations"));
    textPanel.setPreferredSize(new Dimension(300, 500));
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    //dialog boxes contains load and save
    JPanel dialogBoxesPanel = new JPanel();
    dialogBoxesPanel.setBorder(BorderFactory.createTitledBorder("I/O operations"));
    dialogBoxesPanel.setPreferredSize(new Dimension(300, 100));
    dialogBoxesPanel.setLayout(new BoxLayout(dialogBoxesPanel, BoxLayout.Y_AXIS));
    mainPanel.add(dialogBoxesPanel);
    // Adding logic for load, save, and brighten buttons
    JPanel fileopenPanel = new JPanel();
    fileopenPanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(fileopenPanel);
    loadButton = new JButton("Load");
    loadButton.setActionCommand("Load Button");
    fileopenPanel.add(loadButton);

    JPanel filesavePanel = new JPanel();
    filesavePanel.setLayout(new FlowLayout());
    dialogBoxesPanel.add(filesavePanel);
    saveButton = new JButton("Save");
    saveButton.setActionCommand("Save Button");
    filesavePanel.add(saveButton);

    // adding image manipulation buttons to view
    brightenButton = new JButton("Brighten");
    brightenButton.setActionCommand("Brighten Button");
    blurButton = new JButton("Blur");
    blurButton.setActionCommand("Blur Button");
    sharpenButton = new JButton("Sharpen");
    sharpenButton.setActionCommand("Sharpen Button");
    ditherButton = new JButton("Dither");
    ditherButton.setActionCommand("Dither Button");
    horizontalFlip = new JButton("horizontal-flip");
    horizontalFlip.setActionCommand("hf Button");
    verticalFlip = new JButton("vertical-flip");
    verticalFlip.setActionCommand("vf Button");
    rgbCombineButton = new JButton("RGB-Combine");
    rgbCombineButton.setActionCommand("RGB-combine button");
    rgbSplitButton = new JButton("RGB-Split");
    rgbSplitButton.setActionCommand("RGB Split button");

    textPanel.add(brightenButton);
    textPanel.add(blurButton);
    textPanel.add(sharpenButton);
    textPanel.add(ditherButton);
    textPanel.add(horizontalFlip);
    textPanel.add(verticalFlip);
    textPanel.add(rgbCombineButton);
    textPanel.add(rgbSplitButton);

    JPanel radioPanel = new JPanel();
    radioPanel.setBorder(BorderFactory.createTitledBorder("Image transformations"));

    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));

    //radio button
    radioButtons = new JRadioButton[8];

    //buttons groups are used to combine radio buttons. Only one radio
    ButtonGroup rGroup2 = new ButtonGroup();

    for (int i = 0; i < radioButtons.length; i++) {
      switch (i) {
        case 0:
          radioButtons[i] = new JRadioButton("red-component");
          break;
        case 1:
          radioButtons[i] = new JRadioButton("green-component");
          break;
        case 2:
          radioButtons[i] = new JRadioButton("blue-component");
          break;
        case 3:
          radioButtons[i] = new JRadioButton("value-component");
          break;
        case 4:
          radioButtons[i] = new JRadioButton("intensity-component");
          break;
        case 5:
          radioButtons[i] = new JRadioButton("luma-component");
          break;
        case 6:
          radioButtons[i] = new JRadioButton("Grey color transform");
          break;
        default:
          radioButtons[i] = new JRadioButton("Sepia color transform");
          break;
      }

      radioButtons[i].setActionCommand("RB" + (i + 1));
      rGroup2.add(radioButtons[i]);
      radioPanel.add(radioButtons[i]);

    }
    textPanel.add(radioPanel);
    mainPanel.add(textPanel);
    //initialise file chooser
    fileChooser = new JFileChooser(".");
    //show an image with a scrollbar in this panel
    imagePanel = new JPanel();
    //a border around the panel with a caption
    imagePanel.setBorder(BorderFactory.createTitledBorder("Showing an image"));
    imagePanel.setPreferredSize(new Dimension(800, 1000));
    mainPanel.add(imagePanel);
    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    loadButton.addActionListener(evt -> this.openFile(features));
    saveButton.addActionListener(evt -> this.saveFile(features));
    brightenButton.addActionListener(evt -> this.imgBrighten(features));
    rgbCombineButton.addActionListener(evt -> this.setRgbCombineButton(features));
    rgbSplitButton.addActionListener(evt -> this.setRgbSplitButton(features));
    blurButton.addActionListener(evt -> features.imageBlur());
    sharpenButton.addActionListener(evt -> features.imageSharpen());
    ditherButton.addActionListener(evt -> features.dither());
    horizontalFlip.addActionListener(evt -> features.hFlipImage());
    verticalFlip.addActionListener(evt -> features.vFlipImage());
    this.setRadioButtons(radioButtons, features);
    this.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
          case 'v': {
            features.vFlipImage();
            break;
          }
          case 'h': {
            features.hFlipImage();
            break;
          }
          default: {
            JOptionPane.showMessageDialog(JFrameView.this,
                    "Key pressed is not supported", "Error",
                    JOptionPane.ERROR_MESSAGE);
            break;
          }
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // No events for Key Pressed.
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // No events for Key Released.
      }
    });
  }

  @Override
  public void setImage() {
    // remove older image and set the new image in the window
    JLabel imageLabel = new JLabel();
    JScrollPane imageScrollPane = new JScrollPane(imageLabel);
    imageLabel.setIcon(new ImageIcon(roImages.getBufferedImg("view-image")));
    imageScrollPane.setPreferredSize(new Dimension(700, 500));
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    imageLabel.setVerticalAlignment(JLabel.CENTER);
    imagePanel.add(imageScrollPane);
    this.drawHistogram();

  }

  @Override
  public void resetFocus() {
    this.setFocusable(true);
    this.requestFocus();
  }

  public void errorMsg(Exception e) {
    JOptionPane.showMessageDialog(JFrameView.this, e.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
  }

  public void resetImgPanel() {
    imagePanel.removeAll();
  }

  private void openFile(Features features) {
    features.loadImageFile(this.getFile());
  }

  private void saveFile(Features features) {
    features.saveImage(this.getSaveFile());
  }

  private void setRadioButtons(JRadioButton[] radioButtons, Features features) {
    for (JRadioButton radioButton : radioButtons) {
      radioButton.addActionListener(evt -> {
        if (radioButton.getText().equals("Grey color transform")) {
          features.greyTransform();
        } else if (radioButton.getText().equals("Sepia color transform")) {
          features.sepiaTransform();
        } else {
          features.greyscale(radioButton.getText());
        }
      });
    }
  }

  private void setRgbCombineButton(Features features) {
    List<String> filePaths = new ArrayList<>();
    JButton red = new JButton("load red-comp");
    red.setActionCommand("load red component");
    red.addActionListener(event -> filePaths.add(getFile()));
    JButton green = new JButton("load green-comp");
    green.setActionCommand("load red component");
    green.addActionListener(event -> filePaths.add(getFile()));
    JButton blue = new JButton("load blue-comp");
    blue.setActionCommand("load red component");
    blue.addActionListener(event -> filePaths.add(getFile()));

    JPanel myPanel = new JPanel();
    myPanel.add(red);
    myPanel.add(green);
    myPanel.add(blue);

    int result = JOptionPane.showConfirmDialog(null, myPanel,
            "Load RGB components to combine them", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      features.rgbCombine(filePaths);
    }
  }

  private String getFile() {
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Image extensions", "jpg", "png", "bmp", "ppm");
    fileChooser.setFileFilter(filter);
    int retValue = fileChooser.showOpenDialog(this);
    if ((retValue == JFileChooser.APPROVE_OPTION)) {
      File file = fileChooser.getSelectedFile();
      return file.getAbsolutePath();
    } else {
      return "";
    }
  }

  private String getSaveFile() {
    int retValue = fileChooser.showSaveDialog(this);
    if ((retValue == JFileChooser.APPROVE_OPTION)) {
      File file = fileChooser.getSelectedFile();
      return file.getAbsolutePath();
    } else {
      return "";
    }
  }

  private void setRgbSplitButton(Features features) {
    List<String> filePaths = new ArrayList<>();
    JButton red = new JButton("save red-comp in path");
    red.setActionCommand("save red component");
    red.addActionListener(event -> filePaths.add(getSaveFile()));
    JButton green = new JButton("save green-comp in path");
    green.setActionCommand("save green component");
    green.addActionListener(event -> filePaths.add(getSaveFile()));
    JButton blue = new JButton("save blue-comp in path");
    blue.setActionCommand("save blue component");
    blue.addActionListener(event -> filePaths.add(getSaveFile()));

    JPanel myPanel = new JPanel();
    myPanel.add(red);
    myPanel.add(green);
    myPanel.add(blue);

    int result = JOptionPane.showConfirmDialog(null, myPanel,
            "Split RGB image and save components", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      features.rgbSplit(filePaths);
    }
  }

  private void drawHistogram() {
    dataset = new HistogramDataset();
    BufferedImage image = roImages.getBufferedImg("view-image");
    Raster raster = image.getRaster();
    final int w = image.getWidth();
    final int h = image.getHeight();
    double[] r = new double[w * h];
    r = raster.getSamples(0, 0, w, h, 0, r);
    dataset.addSeries("Red", r, bins);
    r = raster.getSamples(0, 0, w, h, 1, r);
    dataset.addSeries("Green", r, bins);
    r = raster.getSamples(0, 0, w, h, 2, r);
    dataset.addSeries("Blue", r, bins);
    double[] intensity = new double[w * h];
    int i = 0;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        //Retrieving contents of a pixel
        int pixel = image.getRGB(x, y);
        //Creating a Color object from pixel value
        Color color = new Color(pixel, true);
        //Retrieving the R G B values
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        intensity[i] = (red + green + blue) / 3.0;
        i++;
      }
    }
    dataset.addSeries("Intensity", intensity, bins);
    JFreeChart chart = ChartFactory.createXYLineChart("Histogram", "Value",
            "Count", dataset, PlotOrientation.VERTICAL, true, true,
            false);
    XYPlot plot = (XYPlot) chart.getPlot();
    renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    renderer.setDrawSeriesLineAsPath(true);
    Paint[] paintArray = {new Color(0xFFFF0000, true),
                          new Color(0xFF00FF00, true),
                          new Color(0xFF0000FF, true),
                          new Color(0xFFFF0090, true)};
    plot.setDrawingSupplier(new DefaultDrawingSupplier(
            paintArray,
            DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
    ChartPanel panel = new ChartPanel(chart);
    imagePanel.add(panel);
    imagePanel.add(createControlPanel(), BorderLayout.SOUTH);
    imagePanel.repaint();
  }

  private JPanel createControlPanel() {
    JPanel panel = new JPanel();
    panel.add(new JCheckBox(new VisibleAction(0)));
    panel.add(new JCheckBox(new VisibleAction(1)));
    panel.add(new JCheckBox(new VisibleAction(2)));
    panel.add(new JCheckBox(new VisibleAction(3)));
    return panel;
  }

  private class VisibleAction extends AbstractAction {

    private final int i;

    public VisibleAction(int i) {
      this.i = i;
      this.putValue(NAME, (String) dataset.getSeriesKey(i));
      this.putValue(SELECTED_KEY, true);
      renderer.setSeriesVisible(i, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      renderer.setSeriesVisible(i, !renderer.getSeriesVisible(i));
    }
  }

  private void imgBrighten(Features features) {
    UIManager.put("OptionPane.minimumSize", new Dimension(500, 200));
    JSlider slider = new JSlider(-255, 255, 0);
    slider.setMajorTickSpacing(50);
    slider.setMinorTickSpacing(10);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    JTextField value = new JTextField();
    value.setMaximumSize(new Dimension(50, 50));
    slider.addChangeListener(evt -> value.setText(String.valueOf(slider.getValue())));
    JPanel myPanel = new JPanel();
    myPanel.setSize(new Dimension(200, 200));
    myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.PAGE_AXIS));
    myPanel.add(value);
    myPanel.add(slider);
    int result = JOptionPane.showConfirmDialog(null, myPanel,
            "brighten image by value", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      features.brightenImage(value.getText());
    }
  }
}
