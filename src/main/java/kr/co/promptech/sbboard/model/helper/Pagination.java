package kr.co.promptech.sbboard.model.helper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter @Setter
public class Pagination {
    private static final int DEFAULT_GUTTER = 2;
    private static final int PAGING_ITEMS = 10;

    private Page<?> page;

    private int begin;

    private int current;

    private int perPage;

    private int end;

    private String url;

    private String query = "";

    public Pagination(Page<?> page, Pageable pageable, String url) {
        this.page = page;
        this.perPage = pageable.getPageSize();

        this.current = page.getNumber() + 1;
        this.begin = Math.max(1, current - DEFAULT_GUTTER);
        this.end = Math.min(this.begin + 2*DEFAULT_GUTTER, page.getTotalPages());
        this.url = url;
    }

    public void addQuery(String key, String value) {
        this.query += String.format("%s=%s&", key, value);
    }
}
