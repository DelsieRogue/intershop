package ru.yandex.practicum.intershop.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataBaseRequestUtils {
    
    public static PageRequest productView(int page, int size, String sortName) {
        String field;
        if ("ALPHA".equalsIgnoreCase(sortName)) {
            field = "title";
        } else if ("PRICE".equalsIgnoreCase(sortName)) {
            field = "price";
        } else {
            field = "createdAt";
        }

        Sort sort = Sort.by(field).ascending();
        return PageRequest.of(page, size, sort);
    }

    public static Sort defaultSort() {
        return Sort.by("createdAt").ascending();
    }
}
