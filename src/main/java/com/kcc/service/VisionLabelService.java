package com.kcc.service;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature.Type;

@Service
public class VisionLabelService extends AbstractVisionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(VisionLabelService.class);

  public String label(String typeStr, String imagePath) throws IOException {
    Type type = null;
    if ("LANDMARK".equals(typeStr)) {
      type = Type.LANDMARK_DETECTION;
    } else if ("LABEL".equals(typeStr)) {
      type = Type.LABEL_DETECTION;
    } else if ("WEB".equals(typeStr)) {
      type = Type.WEB_DETECTION;
    } else if ("UNSPECIFIED".equals(typeStr)) {
      type = Type.TYPE_UNSPECIFIED;
    }

    BatchAnnotateImagesResponse response = vision(imagePath, type);
    if (response == null) {
      LOGGER.error("BatchAnnotateImagesResponse is null.");
      return null;
    }

    List<AnnotateImageResponse> responses = response.getResponsesList();
    for (AnnotateImageResponse res : responses) {
      if (res.hasError()) {
        LOGGER.error("ErrorMsg={}", res.getError().getMessage());
        return null;
      }
      for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
        annotation.getAllFields().forEach((k, v) -> LOGGER.info("{} : {}", k, v));
      }
    }

    return response.toString();
  }

}
