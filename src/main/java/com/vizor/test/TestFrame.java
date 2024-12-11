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

import com.vizor.test.pagination.PaginationHandler;
import com.vizor.test.pagination.listener.PaginationMouseListener;
import com.vizor.test.pagination.event.PaginationEvent;
import com.vizor.test.service.ImageService;
import com.vizor.test.service.impl.FileServiceImpl;
import com.vizor.test.service.FileService;
import com.vizor.test.service.impl.ImageServiceImpl;

public class TestFrame extends JFrame {

    private static final int ITEMS_ROW = 2;
    private static final int ITEMS_COL = 2;
    private static final int ITEMS_PER_PAGE = ITEMS_ROW * ITEMS_COL;

    private static final ImageService imageService = new ImageServiceImpl();
    private static final FileService fileService = new FileServiceImpl();

    private final List<File> files = new ArrayList<>();
    private final JPanel container = new JPanel();
    private JScrollPane scrollPane;
    private PaginationHandler paginationHandler;
    private int totalItems;

    public TestFrame() {

        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the window to occupy the entire screen

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1024, 768);
        setTitle("GALLERY");

        try {
            Image appIcon = new ImageIcon(getClass().getResource("/image.png")).getImage();
            setIconImage(appIcon);
        } catch (Exception e) {
            System.err.println("Icon not found: " + e.getMessage());
        }

        File[] fs = fileService.getFilesInFolder("assets");
        files.addAll(Arrays.asList(fs));
        totalItems = files.size();

        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        container.setLayout(new GridLayout(0, ITEMS_COL, 10, 10));
        scrollPane = new JScrollPane(container);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        paginationHandler = new PaginationHandler(ITEMS_PER_PAGE, totalItems, 4);

        paginationHandler.modifyNextAndPreviousButton((previous, next) -> {
            previous.setBackground(new Color(36, 42, 90));
            previous.setForeground(Color.white);
            previous.setText("Previous");
            next.setBackground(new Color(36, 42, 90));
            next.setForeground(Color.white);
            next.setText("Next");
        });

        paginationHandler.modifyButton(label -> {
            label.setOpaque(true);
            label.setBackground(new Color(113, 122, 170));
            label.setForeground(Color.BLACK);
        });

        paginationHandler.addMouseListener(new PaginationMouseListener() {
            @Override
            public void onClick(PaginationEvent e) {
                populate(e.startIndex(), e.endIndex());
            }

            @Override
            public void onSelected(PaginationEvent e) {
                e.getButton().setBackground(Color.DARK_GRAY);
                e.getButton().setForeground(Color.white);
            }

            @Override
            public void onHover(PaginationEvent e) {
                e.getButton().setBackground(new Color(80, 94, 179));
            }
        });

        add(paginationHandler.getPagination(), BorderLayout.SOUTH);

        populate(0, Math.min(ITEMS_PER_PAGE, totalItems) - 1);
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
                    JOptionPane.showMessageDialog(container, "File uploaded successfully!");
                    refreshFiles();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(container, "Failed to upload the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return topPanel;
    }

    private void refreshFiles() {
        File[] fs = fileService.getFilesInFolder("assets");
        files.clear();
        files.addAll(Arrays.asList(fs));
        totalItems = files.size();
        paginationHandler.setTotalItems(totalItems);
        populate(0, Math.min(ITEMS_PER_PAGE, totalItems) - 1);
    }

    private void populate(int start, int end) {
        container.removeAll();

        List<File> _files = files.subList(start, Math.min(end + 1, totalItems));

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

        for (File file : files) {
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
        imageNameLabel.setForeground(Color.BLACK);
        imageNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageNameLabel.setVisible(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
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
