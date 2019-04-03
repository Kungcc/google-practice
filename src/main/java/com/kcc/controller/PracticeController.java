package com.kcc.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.kcc.service.ImageService;
import com.kcc.service.VisionLabelService;
import com.kcc.utils.ImageUtil;

@RestController
@RequestMapping(value = "/api/v1")
public class PracticeController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PracticeController.class);

  @Autowired
  private ImageService imageService;

  @Autowired
  private VisionLabelService visionLabelService;

  @PostMapping("/vision")
  public String vision(@RequestParam("type") String type,
      @RequestParam("image") MultipartFile image) {
    try {
      String destination = imageService.preUpload(image);
      return visionLabelService.label(type, destination);
    } catch (IOException e) {
      LOGGER.error("label ErrorMsg={}", e.getMessage());
      return null;
    }
  }

  @PostMapping("/upload")
  public String upload(@RequestParam("image") MultipartFile image) {
    return imageService.upload(image);
  }

  @GetMapping("/image")
  public void image(@RequestParam("name") String name, HttpServletResponse response) {
    try {
      response.setContentType("image/jpg");
      try (InputStream fi = new FileInputStream(ImageUtil.IMAGE_DIR + name)) {
        StreamUtils.copy(fi, response.getOutputStream());
      }
    } catch (IOException e) {
      LOGGER.error("image ErrorMsg={}", e.getMessage());
    }
  }

}
