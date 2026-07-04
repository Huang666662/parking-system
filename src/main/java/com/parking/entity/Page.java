package com.parking.entity;

public class Page {
    private int currentPage;
    private int pageSize;
    private int totalCount;
    private int totalPages;

    public Page() {}

    public Page(int currentPage, int pageSize, int totalCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPages = totalCount > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 1;
    }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.totalPages = totalCount > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 1;
    }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getOffset() {
        return (currentPage - 1) * pageSize;
    }
}
