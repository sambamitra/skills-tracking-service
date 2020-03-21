package uk.gov.nhs.sts.config;

import static springfox.documentation.schema.AlternateTypeRules.newRule;
import java.time.LocalDate;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.Api;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Autowired
  private TypeResolver typeResolver;

  private ApiInfo apiInfo() {
    return new ApiInfo("Skill Management API",
        "The Skill Management API provides operations relating to people and skills in NHS",
        "1.0.0", "http://terms-of-service.url",
        new Contact("Samba Mitra", "www.nhs.gov.uk", "email@domain.com"), "License of API",
        "API license URL", Collections.emptyList());
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
        .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
        .paths(PathSelectors.regex("/api/.*")).build().pathMapping("/")
        .directModelSubstitute(LocalDate.class, String.class)
        .genericModelSubstitutes(ResponseEntity.class)
        .alternateTypeRules(newRule(
            this.typeResolver.resolve(DeferredResult.class,
                this.typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
            this.typeResolver.resolve(WildcardType.class)))
        .useDefaultResponseMessages(false).forCodeGeneration(true);
  }

}
