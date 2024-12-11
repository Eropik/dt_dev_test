/*
package com.vizor.test;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

import com.vizor.test.constant.AppConstants;
import com.vizor.test.pagination.PaginationHandler;
import com.vizor.test.pagination.listener.PaginationMouseListener;
import com.vizor.test.pagination.event.PaginationEvent;
import com.vizor.test.service.ImageService;
import com.vizor.test.service.impl.FileServiceImpl;
import com.vizor.test.service.FileService;
import com.vizor.test.service.impl.ImageServiceImpl;

public class TestFrame extends JFrame {

    private static final ImageService imageService = new ImageServiceImpl();
    private static final FileService fileService = new FileServiceImpl();

    private List<File> files = new ArrayList<>();
    private final JPanel container = new JPanel();
    private JScrollPane scrollPane;
    private PaginationHandler paginationHandler;
    private int totalItems;
    private int curPage;
    private int endOffset;

    public TestFrame() {

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setTitle("GALLERY");

        try {
            Image appIcon = new ImageIcon(getClass().getResource(AppConstants.ICON_PATH)).getImage();
            setIconImage(appIcon);
        } catch (Exception e) {
            System.err.println("Icon not found: " + e.getMessage());
        }

        File[] fs = fileService.getFilesInFolder("assets");
        this.files.addAll(Arrays.asList(fs));
        totalItems = files.size();

        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        container.setLayout(new GridLayout(0, AppConstants.ITEMS_COL, 10, 10));
        scrollPane = new JScrollPane(container);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        paginationHandler = new PaginationHandler(AppConstants.ITEMS_PER_PAGE, totalItems, AppConstants.PAGINATION_BUTTONS);

        paginationHandler.modifyNextAndPreviousButton((previous, next) -> {
            previous.setBackground(AppConstants.BUTTON_BACKGROUND_COLOR);
            previous.setForeground(AppConstants.BUTTON_FOREGROUND_COLOR);
            previous.setText("Previous");
            next.setBackground(AppConstants.BUTTON_BACKGROUND_COLOR);
            next.setForeground(AppConstants.BUTTON_FOREGROUND_COLOR);
            next.setText("Next");
        });

        paginationHandler.modifyButton(label -> {
            label.setOpaque(true);
            label.setBackground(AppConstants.THUMBNAIL_HOVER_SCALE_COLOR);
            label.setForeground(AppConstants.BUTTON_TEXT_COLOR);
        });

        paginationHandler.addMouseListener(new PaginationMouseListener() {
            @Override
            public void onClick(PaginationEvent e) {
                populate(e.startIndex(), e.endIndex());
                curPage = e.startIndex();
                endOffset = e.endIndex();
            }

            @Override
            public void onSelected(PaginationEvent e) {
                e.getButton().setBackground(AppConstants.BUTTON_SELECTED_COLOR);
                e.getButton().setForeground(AppConstants.BUTTON_FOREGROUND_COLOR);
            }

            @Override
            public void onHover(PaginationEvent e) {
                e.getButton().setBackground(AppConstants.BUTTON_HOVER_COLOR);
            }
        });

        add(paginationHandler.getPagination(), BorderLayout.SOUTH);

        populate(0, Math.min(AppConstants.ITEMS_PER_PAGE, totalItems) - 1);
        this.curPage = 0;
        this.endOffset = 3;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchAndUploadPanel = new JPanel();

        JTextField searchField = new JTextField(20);
        JButton uploadButton = new JButton("Upload Image");

        searchAndUploadPanel.add(searchField);
        searchAndUploadPanel.add(uploadButton);
        topPanel.add(searchAndUploadPanel, BorderLayout.NORTH);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterAndLoadImages(query);
            }
        });

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(container);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    fileService.uploadFile(selectedFile, "assets");
                    refreshFiles();
                    JOptionPane.showMessageDialog(container, "File uploaded successfully!");
                    endOffset++;
                    populate(curPage, endOffset);
                } catch (IOException ex) {
                    System.err.println("Upload error:" + ex.getMessage());
                    JOptionPane.showMessageDialog(container, "Failed to upload the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return topPanel;
    }

    private void refreshFiles() {
        File[] fs = fileService.getFilesInFolder("assets");
        this.files.clear();
        if (fs != null) {
            this.files.addAll(Arrays.asList(fs));
            this.totalItems = files.size();
            this.paginationHandler = new PaginationHandler(AppConstants.ITEMS_PER_PAGE, totalItems, AppConstants.PAGINATION_BUTTONS);
        }
    }

    private void populate(int start, int end) {
        container.removeAll();

        List<File> _files = this.files.subList(start, Math.min(end + 1, totalItems));

        for (File file : _files) {
            try {
                JPanel panel = createLabel(file);
                JLabel thumbnailLabel = (JLabel) panel.getComponent(0);
                JLabel imageNameLabel = (JLabel) panel.getClientProperty("imageNameLabel");

                thumbnailLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JFrame fullImageFrame = new JFrame(file.getName());
                        fullImageFrame.setSize(700, 700);
                        fullImageFrame.add(new JLabel(new ImageIcon(file.getAbsolutePath())));
                        fullImageFrame.setVisible(true);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        resizeAndShowName(file, thumbnailLabel, 1.8);
                        if (imageNameLabel != null) {
                            imageNameLabel.setVisible(true);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        resizeAndShowName(file, thumbnailLabel, 2);
                        if (imageNameLabel != null) {
                            imageNameLabel.setVisible(false);
                        }
                    }
                });

                container.add(panel);
            } catch (Exception e) {
                System.err.println("populate func error: " + e.getMessage());
            }
        }

        container.revalidate();
        container.repaint();
    }


    private void filterAndLoadImages(String query) {
        container.removeAll();

        for (File file : this.files) {
            if (file.getName().toLowerCase().contains(query)) {
                try {
                    JPanel panel = createLabel(file);
                    JLabel thumbnailLabel = (JLabel) panel.getComponent(0);
                    JLabel imageNameLabel = (JLabel) panel.getClientProperty("imageNameLabel");

                    thumbnailLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            resizeAndShowName(file, thumbnailLabel, 2.1);
                            if (imageNameLabel != null) {
                                imageNameLabel.setVisible(true);
                            }
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            resizeAndShowName(file, thumbnailLabel, 2.1);
                            if (imageNameLabel != null) {
                                imageNameLabel.setVisible(false);
                            }
                        }
                    });

                    container.add(panel);
                } catch (Exception e) {
                    System.err.println("filterAndLoadImages func error: " + e.getMessage());
                }
            }
        }

        container.revalidate();
        container.repaint();
    }

    private JPanel createLabel(File file) throws IOException {
        ImageIcon thumbnailIcon = new ImageIcon(imageService.createThumbnail(file));
        JLabel thumbnailLabel = new JLabel(thumbnailIcon);
        thumbnailLabel.setToolTipText(file.getName());

        JLabel imageNameLabel = new JLabel(file.getName());
        imageNameLabel.setForeground(AppConstants.LABEL_TEXT_COLOR);
        imageNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageNameLabel.setVisible(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppConstants.PANEL_BACKGROUND_COLOR);
        panel.add(thumbnailLabel, BorderLayout.CENTER);
        panel.add(imageNameLabel, BorderLayout.SOUTH);

        panel.putClientProperty("imageNameLabel", imageNameLabel);

        return panel;
    }

    private void resizeAndShowName(File file, JLabel thumbnailLabel, double scale) {
        try {
            BufferedImage resizedImage = imageService.resizeImage(file, scale);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);
            thumbnailLabel.setIcon(resizedIcon);
        } catch (IOException e) {
            System.err.println("resizeAndShowName func error: "+ e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TestFrame().setVisible(true));
    }
}
*/

package com.vizor.test;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import com.vizor.test.constant.AppConstants;
import com.vizor.test.pagination.PaginationHandler;
import com.vizor.test.pagination.listener.PaginationMouseListener;
import com.vizor.test.pagination.event.PaginationEvent;
import com.vizor.test.service.ImageService;
import com.vizor.test.service.impl.FileServiceImpl;
import com.vizor.test.service.FileService;
import com.vizor.test.service.impl.ImageServiceImpl;

public class TestFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(TestFrame.class.getName());
    private static final ImageService imageService = new ImageServiceImpl();
    private static final FileService fileService = new FileServiceImpl();

    private List<File> files = new ArrayList<>();
    private final JPanel container = new JPanel();
    private JScrollPane scrollPane;
    private PaginationHandler paginationHandler;
    private int totalItems;
    private int curPage;
    private int endOffset;

    public TestFrame() {
        setWindowProperties();
        loadFiles();
        setupLayout();
        initializePagination();
        populate(0, Math.min(AppConstants.ITEMS_PER_PAGE, totalItems) - 1);
    }

    private void setWindowProperties() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setTitle("GALLERY");

        try {
            Image appIcon = new ImageIcon(getClass().getResource(AppConstants.ICON_PATH)).getImage();
            setIconImage(appIcon);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Icon not found: ", e);
        }
    }

    private void loadFiles() {
        File[] fs = fileService.getFilesInFolder(AppConstants.IMGS_PATH);
        this.files.addAll(Arrays.asList(fs));
        totalItems = files.size();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        container.setLayout(new GridLayout(0, AppConstants.ITEMS_COL, 10, 10));
        scrollPane = new JScrollPane(container);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initializePagination() {
        paginationHandler = new PaginationHandler(AppConstants.ITEMS_PER_PAGE, totalItems, AppConstants.PAGINATION_BUTTONS);

        paginationHandler.modifyNextAndPreviousButton((previous, next) -> {
            previous.setBackground(AppConstants.BUTTON_BACKGROUND_COLOR);
            previous.setForeground(AppConstants.BUTTON_FOREGROUND_COLOR);
            previous.setText("Previous");
            next.setBackground(AppConstants.BUTTON_BACKGROUND_COLOR);
            next.setForeground(AppConstants.BUTTON_FOREGROUND_COLOR);
            next.setText("Next");
        });

        paginationHandler.modifyButton(label -> {
            label.setOpaque(true);
            label.setBackground(AppConstants.THUMBNAIL_HOVER_SCALE_COLOR);
            label.setForeground(AppConstants.BUTTON_TEXT_COLOR);
        });

        paginationHandler.addMouseListener(new PaginationMouseListener() {
            @Override
            public void onClick(PaginationEvent e) {
                populate(e.startIndex(), e.endIndex());
                curPage = e.startIndex();
                endOffset = e.endIndex();
            }

            @Override
            public void onSelected(PaginationEvent e) {
                e.getButton().setBackground(AppConstants.BUTTON_SELECTED_COLOR);
                e.getButton().setForeground(AppConstants.BUTTON_FOREGROUND_COLOR);
            }

            @Override
            public void onHover(PaginationEvent e) {
                e.getButton().setBackground(AppConstants.BUTTON_HOVER_COLOR);
            }
        });

        add(paginationHandler.getPagination(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchAndUploadPanel = new JPanel();

        JTextField searchField = new JTextField(20);
        JButton uploadButton = new JButton("Upload Image");

        searchAndUploadPanel.add(searchField);
        searchAndUploadPanel.add(uploadButton);
        topPanel.add(searchAndUploadPanel, BorderLayout.NORTH);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterAndLoadImages(query);
            }
        });

        uploadButton.addActionListener(e -> uploadFile());

        return topPanel;
    }

    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(container);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                fileService.uploadFile(selectedFile, AppConstants.IMGS_PATH);
                refreshFiles();
                JOptionPane.showMessageDialog(container, "File uploaded successfully!");
                endOffset++;
                populate(curPage, endOffset);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Upload error: ", ex);
                JOptionPane.showMessageDialog(container, "Failed to upload the file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshFiles() {
        File[] fs = fileService.getFilesInFolder(AppConstants.IMGS_PATH);
        this.files.clear();
        if (fs != null) {
            this.files.addAll(Arrays.asList(fs));
            this.totalItems = files.size();
            this.paginationHandler = new PaginationHandler(AppConstants.ITEMS_PER_PAGE, totalItems, AppConstants.PAGINATION_BUTTONS);
        }
    }

    private void populate(int start, int end) {
        container.removeAll();

        List<File> filesToDisplay = this.files.subList(start, Math.min(end + 1, totalItems));
        for (File file : filesToDisplay) {
            try {
                addThumbnailToContainer(file);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error populating images: ", e);
            }
        }

        container.revalidate();
        container.repaint();
    }

    private void addThumbnailToContainer(File file) throws IOException {
        JPanel panel = createLabel(file);
        JLabel thumbnailLabel = (JLabel) panel.getComponent(0);
        JLabel imageNameLabel = (JLabel) panel.getComponent(1);

        thumbnailLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showFullImage(file);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                resizeAndShowName(file, thumbnailLabel, 1.8);
                imageNameLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                resizeAndShowName(file, thumbnailLabel, 2);
                imageNameLabel.setVisible(false);
            }
        });

        container.add(panel);
    }


    private void showFullImage(File file) {
        JFrame fullImageFrame = new JFrame(file.getName());
        fullImageFrame.setSize(700, 700);
        fullImageFrame.add(new JLabel(new ImageIcon(file.getAbsolutePath())));
        fullImageFrame.setVisible(true);
    }

    private void filterAndLoadImages(String query) {
        container.removeAll();

        for (File file : this.files) {
            if (file.getName().toLowerCase().contains(query)) {
                try {
                    addThumbnailToContainer(file);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error filtering images: ", e);
                }
            }
        }

        container.revalidate();
        container.repaint();
    }

    private JPanel createLabel(File file) throws IOException {
        ImageIcon thumbnailIcon = new ImageIcon(imageService.createThumbnail(file));
        JLabel thumbnailLabel = new JLabel(thumbnailIcon);
        thumbnailLabel.setToolTipText(file.getName());

        JLabel imageNameLabel = new JLabel(file.getName());
        imageNameLabel.setForeground(AppConstants.LABEL_TEXT_COLOR);
        imageNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageNameLabel.setVisible(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppConstants.PANEL_BACKGROUND_COLOR);
        panel.add(thumbnailLabel, BorderLayout.CENTER);
        panel.add(imageNameLabel, BorderLayout.SOUTH);

        return panel;
    }


    private void resizeAndShowName(File file, JLabel thumbnailLabel, double scale) {
        try {
            BufferedImage resizedImage = imageService.resizeImage(file, scale);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);
            thumbnailLabel.setIcon(resizedIcon);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error resizing image: ", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TestFrame().setVisible(true));
    }
}

