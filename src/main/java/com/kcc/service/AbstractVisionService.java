package com.kcc.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

public abstract class AbstractVisionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVisionService.class);

  protected BatchAnnotateImagesResponse vision(InputStream is, Type type) throws IOException {
    // Instantiates a client
    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int len;
      while ((len = is.read(buffer)) != -1) {
        bos.write(buffer, 0, len);
      }
      byte[] data = bos.toByteArray();
      ByteString imgBytes = ByteString.copyFrom(data);

      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Image img = Image.newBuilder().setContent(imgBytes).build();

      AnnotateImageRequest request = null;
      if (type == null) {
        request = AnnotateImageRequest.newBuilder().setImage(img).build();
      } else {
        Feature feature = Feature.newBuilder().setType(type).build();
        request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(img).build();
      }
      requests.add(request);

      // Performs label detection on the image file
      return vision.batchAnnotateImages(requests);
    } catch (Exception e) {
      LOGGER.error("msg={}", e.getMessage(), e);
      return null;
    }
  }

  protected BatchAnnotateImagesResponse vision(String fileName, Type type) throws IOException {
    // Instantiates a client
    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
      // Reads the image file into memory
      LOGGER.info("fileName={}", fileName);
      Path path = Paths.get(fileName);
      byte[] data = Files.readAllBytes(path);
      ByteString imgBytes = ByteString.copyFrom(data);

      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Image img = Image.newBuilder().setContent(imgBytes).build();
      Feature feat = null;
      if (type == null) {
        feat = Feature.newBuilder().build();
      } else {
        feat = Feature.newBuilder().setType(type).build();
      }
      AnnotateImageRequest request =
          AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
      requests.add(request);

      // Performs label detection on the image file
      BatchAnnotateImagesResponse res = vision.batchAnnotateImages(requests);
      LOGGER.info("Type={}, RS={}", type, res);
      return res;
    } catch (Exception e) {
      LOGGER.error("msg={}", e.getMessage(), e);
      return null;
    }
  }

}
