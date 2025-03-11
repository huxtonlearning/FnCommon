package com.huxton.common.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import com.huxton.common.configs.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "notification", ignoreUnknownFields = true)
@Slf4j
public class NotificationRegisterConfig {

    @Bean(name = "NotificationRunner")
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            try {
                register();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }


    @Async
    public void register() throws IOException {

        log.info("************************** " + "Registering notification config");
        String host = Config.getEnvironmentProperty("huxton.notification-service");
        if (!StringUtils.hasLength(host)) {
            log.info("************************** " + "Chưa khai báo biến môi trường huxton.notification-service");

            return;
        }
        String path = "/v1/notification/discovery/register_batch";
        String apiUrl = host + path;
        System.out.println(host);
        RestTemplate restTemplate = new RestTemplate();

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/notification-config/*");

//    Resource directory = new ClassPathResource("notification-config");
//
//    File folder = directory.getFile(); // Works only for file system resources

        if (resources != null && resources.length > 0) {

//    if (folder.isDirectory()) {
            for (Resource resource : resources) {
                try {
                    log.info("************************** " + "Registering notification config type: " + resource.getFilename());

                    MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
                    headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));

                    HttpEntity entity = new HttpEntity(resource, headers);

                    restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
