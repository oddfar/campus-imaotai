package com.oddfar.campus.framework.api.file;

import org.springframework.web.multipart.MultipartFile;

public class LocalFileOperator implements FileOperatorApi {

    private String currentSavePath;

    public LocalFileOperator(String currentSavePath) {
        this.currentSavePath = currentSavePath;
//        initClient();
    }

    @Override
    public void storageFile(String bucketName, MultipartFile file, String[] allowedExtension) {

    }
}
