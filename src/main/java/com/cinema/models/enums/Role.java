package com.cinema.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@Getter
public enum Role implements GrantedAuthority {
    USER("Покупатель"),
    ADMIN("Администратор"),
    PUB("Продавец");

    private final String name;

    @Override
    public String getAuthority() {
        return name();
    }
}
