package com.kcc;

import java.time.format.DateTimeFormatterBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@SpringBootApplication
public class GooglePracticeApplication {

  public static void main(String[] args) {
    SpringApplication.run(GooglePracticeApplication.class, args);
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
    return new Jackson2ObjectMapperBuilderCustomizer() {
      @Override
      public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.serializationInclusion(Include.NON_NULL);
        jacksonObjectMapperBuilder.serializationInclusion(Include.NON_EMPTY);
        jacksonObjectMapperBuilder.serializers(new LocalDateSerializer(
            new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter()));
        jacksonObjectMapperBuilder.serializers(new LocalDateTimeSerializer(
            new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss").toFormatter()));
        jacksonObjectMapperBuilder.failOnUnknownProperties(false);
        jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
      }
    };
  }
  
}
