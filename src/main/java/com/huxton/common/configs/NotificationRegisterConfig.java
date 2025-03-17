package com.huxton.common.configs;

import com.huxton.common.utils.SendMessageTelegram;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class NotificationRegisterConfig {

    int attempts = 0;

    @Autowired
    private  SendMessageTelegram sendMessageTelegram;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    @Retryable(
            maxAttempts = Integer.MAX_VALUE,
            value = Exception.class,
            exclude = {IOException.class, FileNotFoundException.class},
            backoff = @Backoff(delay = 1000 * 60, multiplier = 2))
    public void register() throws IOException {

        attempts++;
        log.info("************************** Registering notification config: {} attempts", attempts);
        if (attempts % 10 == 0) {
            sendMessageTelegram.send("Đăng ký thông báo bị lỗi lần thứ " + attempts);
        }
        String host = Config.getEnvironmentProperty("pm.notification-service");
        if (!StringUtils.hasLength(host)) {
            log.info(
                    "************************** "
                            + "Chưa khai báo biến môi trường pm.notification-service");
            return;
        }
        String path = "/v1/notification/discovery/register_batch";
        String apiUrl = host + path;
        System.out.println(host);
        RestTemplate restTemplate = new RestTemplate();

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath:/notification-config/*");

        if (resources != null && resources.length > 0) {

            for (Resource resource : resources) {
                log.info(
                        "************************** "
                                + "Registering notification config type: "
                                + resource.getFilename());

                MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
                headers.put(
                        HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));

                HttpEntity entity = new HttpEntity(resource, headers);

                restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            }
        }
    }
}