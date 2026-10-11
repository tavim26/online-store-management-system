package io.github.tavim26.onlinestore.service;

/** A business rule was broken (not enough stock, wrong password, duplicate review, ...). */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}