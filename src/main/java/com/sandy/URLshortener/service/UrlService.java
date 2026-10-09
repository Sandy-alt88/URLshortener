package com.sandy.URLshortener.service;

import com.sandy.URLshortener.dto.ShortenRequest;
import com.sandy.URLshortener.dto.ShortenResponse;
import com.sandy.URLshortener.model.UrlMapping;
import com.sandy.URLshortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UrlService {

    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;

    private final UrlMappingRepository repository;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public ShortenResponse shorten(ShortenRequest request) {
        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(request.getOriginalUrl());
        mapping.setShortCode(generateUniqueCode());

        repository.save(mapping);

        return new ShortenResponse(
                mapping.getShortCode(),
                baseUrl + "/" + mapping.getShortCode(),
                mapping.getOriginalUrl()
        );
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = randomCode();
        } while (repository.existsByShortCode(code));
        return code;
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}