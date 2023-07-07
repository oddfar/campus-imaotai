package com.oddfar.campus.framework.api.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileOperatorApi {


    void storageFile(String bucketName, MultipartFile file, String[] allowedExtension);


}
