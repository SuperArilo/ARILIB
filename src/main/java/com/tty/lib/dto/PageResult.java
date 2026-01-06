package com.tty.lib.dto;

import lombok.Getter;

import java.util.List;

public class PageResult<T> {

    @Getter
    private final List<T> records;  // 当前页数据
    @Getter
    private final long total;       // 总记录数
    @Getter
    private final long totalPages;  // 总页数
    @Getter
    private final long currentPage;  // 当前页码

    protected PageResult(List<T> records, long total, long totalPages, long currentPage) {
        this.records = records;
        this.total = total;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public static <T> PageResult<T> build(List<T> records, long total, long totalPages, long currentPage) {
        return new PageResult<>(records, total, totalPages, currentPage);
    }

}