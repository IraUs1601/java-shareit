package ru.practicum.shareit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.comment")
@Getter
@Setter
public class CommentProperties {
    private int delayHours = 0;
}