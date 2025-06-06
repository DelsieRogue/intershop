package ru.yandex.practicum.intershop.entity;

public enum Role {
    USER, ADMIN, ANONYMOUS;

    public String getWithPrefix() {
        return "ROLE_" + this.name();
    }
}
