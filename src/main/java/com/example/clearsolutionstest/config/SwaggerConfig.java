package com.example.clearsolutionstest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {


  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage("com.example.clearsolutionstest.controller"))
        .paths(PathSelectors.any()).build().apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title("Clear Solutions API Documentation").description(
            "It has the following functionality:\n"
                + "1. Create user. It allows to register users who are more than [18] years old. The value [18] should be taken from properties file.\n"
                + "2. Update one/some user fields\n" + "2.3. Update all user fields\n"
                + "4. Delete user\n"
                + "5. Search for users by birth date range. Add the validation which checks that “From” is less than “To”.  Should return a list of objects\n")
        .version("1.0").build();
  }
}
