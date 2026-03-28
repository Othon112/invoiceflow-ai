package com.invoiceflow.infrastructure.dto;

import java.util.List;

public class PageResponse<T> {
    public List<T> items;
    public int limit;
    public int offset;
    public long total;
    public boolean hasNext;

    public PageResponse(List<T> items, int limit, int offset, long total) {
        this.items = items;
        this.limit = limit;
        this.offset = offset;
        this.total = total;
        this.hasNext = (offset + limit) < total;
    }
}