package com.vizor.test.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface FileService {


    File[] getFilesInFolder(String folderPath);

    void uploadFile(File source, String destinationPath) throws IOException;

    List<File> refreshFiles(String folderPath);

    int getTotalItems();
}
