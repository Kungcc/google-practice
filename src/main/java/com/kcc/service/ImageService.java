package com.kcc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.kcc.utils.ImageUtil;

@Service
public class ImageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

  public String preUpload(MultipartFile file) throws RuntimeException {
    String fileName = checkImageFile(file);
    if (fileName.equals("")) {
      return "image format is wrong";
    }

    String destination = ImageUtil.IMAGE_DIR + fileName;
    try {
      Files.copy(file.getInputStream(), new File(destination).toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      return "/home/chienchang0822/demo/images/" + fileName;
    } catch (IllegalStateException | IOException e) {
      LOGGER.error("upload image exception, msg={}", e.getMessage());
      return "";
    }
  }

  public String upload(MultipartFile file) throws RuntimeException {
    String fileName = checkImageFile(file);
    if (fileName.equals("")) {
      return "image format is wrong";
    }

    try {
      Files.copy(file.getInputStream(), new File(ImageUtil.IMAGE_DIR + fileName).toPath(),
          StandardCopyOption.REPLACE_EXISTING);
      return ImageUtil.IMAGE_DOMAIN + "api/v1/image?name=" + fileName;
    } catch (IllegalStateException | IOException e) {
      LOGGER.error("upload image exception, msg={}", e.getMessage());
      return "upload failed";
    }
  }

  private String checkImageFile(MultipartFile file) {
    int dotPos = file.getOriginalFilename().lastIndexOf('.');
    if (dotPos < 0) {
      return "";
    }
    String suffix = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
    if (!ImageUtil.IMAGE_FILE_EXT.contains(suffix)) {
      return "";
    }
    return UUID.randomUUID().toString().replace("-", "") + "." + suffix;
  }
}
