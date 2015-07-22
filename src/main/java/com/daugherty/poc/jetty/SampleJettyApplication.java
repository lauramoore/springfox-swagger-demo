/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.daugherty.poc.jetty;

import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Lists.*;
import static springfox.documentation.builders.PathSelectors.*;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.petstore.controller.PetController;

@Configuration
@EnableAutoConfiguration
@EnableSwagger2 //Enable swagger 2.0 spec
@ComponentScan(basePackageClasses = {
        PetController.class
})
public class SampleJettyApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleJettyApplication.class, args);
	}
	
	@Bean
	    public Docket petApi() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .groupName("full-petstore-api")
	                .apiInfo(apiInfo())
	                .select()
	                .paths(petstorePaths())
	                .build();
	    }

	    @Bean
	    public Docket multipartApi() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .groupName("multipart-api")
	                .apiInfo(apiInfo())
	                .select()
	                .paths(multipartPaths())
	                .build();
	    }

	    private Predicate<String> multipartPaths() {
	        return regex("/upload.*");
	    }

	    @Bean
	    public Docket userApi() {
	        AuthorizationScope[] authScopes = new AuthorizationScope[1];
	        authScopes[0] = new AuthorizationScopeBuilder()
	                .scope("read")
	                .description("read access")
	                .build();
	        SecurityReference securityReference = SecurityReference.builder()
	                .reference("test")
	                .scopes(authScopes)
	                .build();

	        ArrayList<SecurityContext> securityContexts = newArrayList(SecurityContext.builder().securityReferences
	                (newArrayList(securityReference)).build());
	        return new Docket(DocumentationType.SWAGGER_2)
	                .securitySchemes(newArrayList(new BasicAuth("test")))
	                .securityContexts(securityContexts)
	                .groupName("user-api")
	                .apiInfo(apiInfo())
	                .select()
	                .paths(userOnlyEndpoints())
	                .build();
	    }

	    private Predicate<String> petstorePaths() {
	        return or(
	                regex("/api/pet.*"),
	                regex("/api/user.*"),
	                regex("/api/store.*")
	        );
	    }

	    private Predicate<String> userOnlyEndpoints() {
	        return new Predicate<String>() {
	            @Override
	            public boolean apply(String input) {
	                return input.contains("user");
	            }
	        };
	    }

	    private ApiInfo apiInfo() {
	        return new ApiInfoBuilder()
	                .title("Springfox petstore API")
	                .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum " +
	                        "has been the industry's standard dummy text ever since the 1500s, when an unknown printer "
	                        + "took a " +
	                        "galley of type and scrambled it to make a type specimen book. It has survived not only five " +
	                        "centuries, but also the leap into electronic typesetting, remaining essentially unchanged. " +
	                        "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum " +
	                        "passages, and more recently with desktop publishing software like Aldus PageMaker including " +
	                        "versions of Lorem Ipsum.")
	                .termsOfServiceUrl("http://springfox.io")
	                .contact("springfox")
	                .license("Apache License Version 2.0")
	                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
	                .version("2.0")
	                .build();
	    }
	

}
