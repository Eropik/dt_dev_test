package com.vizor.test.service.impl;


import com.vizor.test.service.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileServiceImpl implements FileService {

    private List<File> files = new ArrayList<>();

    @Override
    public File[] getFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        return folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
    }

    @Override
    public void uploadFile(File source, String destinationPath) throws IOException {
        File destination = new File(destinationPath, source.getName());
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }



}
