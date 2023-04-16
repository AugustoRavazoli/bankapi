package io.github.augustoravazoli.bankapi.account;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "brazil-api")
record BankClientProperties(String baseUrl) {}
