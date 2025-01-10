package com.hvs.musicreleasenotifierprocessmanager.client.response;

import java.util.List;

public class UserPageResponse<T> {
    private List<T> content;
    private Pageable pageable;
    private int totalPages;
    private long totalElements;
    private boolean last;
    private int size;
    private int number;
    private Sort sort;
    private int numberOfElements;
    private boolean first;
    private boolean empty;

    public List<T> getContent() {
        return content;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public boolean isLast() {
        return last;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public Sort getSort() {
        return sort;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    // Nested Pageable and Sort Classes

    public static class Pageable {
        private Sort sort;
        private int offset;
        private int pageSize;
        private int pageNumber;
        private boolean paged;
        private boolean unpaged;

        // Getters and Setters

        public Sort getSort() {
            return sort;
        }

        public int getOffset() {
            return offset;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public boolean isPaged() {
            return paged;
        }

        public boolean isUnpaged() {
            return unpaged;
        }

        public void setSort(Sort sort) {
            this.sort = sort;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public void setPaged(boolean paged) {
            this.paged = paged;
        }

        public void setUnpaged(boolean unpaged) {
            this.unpaged = unpaged;
        }
    }

    public static class Sort {
        private boolean sorted;
        private boolean unsorted;
        private boolean empty;

        // Getters and Setters

        public boolean isSorted() {
            return sorted;
        }

        public boolean isUnsorted() {
            return unsorted;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setSorted(boolean sorted) {
            this.sorted = sorted;
        }

        public void setUnsorted(boolean unsorted) {
            this.unsorted = unsorted;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
    }
}
