package com.vizor.test;



import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

import com.vizor.test.service.ImageService;
import com.vizor.test.service.impl.FileServiceImpl;
import com.vizor.test.service.FileService;
import com.vizor.test.service.impl.ImageServiceImpl;

public class TestFrame extends JFrame {


    private static final ImageService imageService = new ImageServiceImpl();
    private static final FileService fileService = new FileServiceImpl();

    List<File> files = new ArrayList<>();
    JPanel container = new JPanel();
    JScrollPane scrollPane;
    int totalItems;

    public TestFrame() {
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

        container.setLayout(new GridLayout(0,2, 10, 10));

        scrollPane = new JScrollPane(container);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);




        populate(0, totalItems - 1);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel searchAndUploadPanel = new JPanel();

        JTextField searchField = new JTextField(20);
        JButton uploadButton = new JButton("Upload Image");

        searchAndUploadPanel.add(searchField);
        searchAndUploadPanel.add(uploadButton);
        topPanel.add(searchAndUploadPanel, BorderLayout.NORTH);




        return topPanel;
    }




    private void populate(int start, int end) {
        container.removeAll();

        List<File> _files = files.subList(start, Math.min(end + 1, totalItems));

        for (File file : _files) {
            try {
                ImageIcon thumbnailIcon = new ImageIcon(imageService.createThumbnail(file));
                JLabel thumbnailLabel = new JLabel(thumbnailIcon);
                thumbnailLabel.setToolTipText(file.getName());

                JLabel imageNameLabel = new JLabel(file.getName());
                imageNameLabel.setForeground(Color.WHITE);
                imageNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageNameLabel.setVisible(false);

                thumbnailLabel.add(imageNameLabel);

                  container.add(thumbnailLabel);
            } catch (Exception e) {
                break;
            }
        }

        container.revalidate();
        container.repaint();
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TestFrame().setVisible(true));
    }
}
