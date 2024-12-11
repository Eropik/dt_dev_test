package com.vizor.test.pagination;



public class Pagination {

    private int set;
    private int totalItems;
    private int numberOfItem;
    private int remainingItem;
    int numberOfPageToShow;

    private int numberOfPages;
    private int remainingPage;

    private int currentPage = 1;

    private int start;
    private int end;

    public Pagination(int set, int totalItems, int numberOfPageToShow) {

        this.set = set;
        this.totalItems = totalItems;
        this.remainingItem = totalItems % set;
        this.numberOfPageToShow = numberOfPageToShow;
        setNumberOfPages();
    }

    private void setNumberOfPages() {
        this.numberOfItem = (totalItems - remainingItem) / set;


        if (remainingItem > 0) {
            this.numberOfItem++;
        }

        remainingPage = numberOfItem % numberOfPageToShow;


        this.numberOfPages = (numberOfItem - remainingPage) / numberOfPageToShow;

        if (remainingPage > 0) {
            this.numberOfPages++;
        }

        start = 1;
        if (numberOfItem < 5) {
            end = start + (remainingPage - 1);
        } else {
            end = start + (numberOfPageToShow) - 1;
        }
    }

    public boolean onFirst() {
        return currentPage == 1;
    }

    public boolean onLast() {
        return currentPage == numberOfPages;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public int getSet() {
        return set;
    }


    public int getLimit() {
        if (!onLast()) {
            return set;
        } else {
            if (remainingItem > 0) {
                return remainingItem;
            } else {
                return set;
            }
        }
    }


    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
        setNumberOfPages();
        this.remainingItem = totalItems % set;
    }

    public int getNumberOfItem() {
        return numberOfItem;
    }


    public int getNumberOfPages() {
        return numberOfPages;
    }

    public int getCurrent() {
        return currentPage;
    }

    public int getRemaining() {
        return remainingPage;
    }

    public void onNext() {
        if (currentPage < numberOfPages) {
            currentPage++;
            start += (numberOfPageToShow);
            if (onLast()) {
                if (remainingPage > 0) {
                    end = (start + remainingPage) - 1;
                } else {
                    end = start + (numberOfPageToShow - 1);
                }
            } else {
                end = start + (numberOfPageToShow - 1);
            }
        }

    }

    public void onPrevious() {
        if (currentPage > 0) {
            currentPage--;
            end = start - 1;
            if (onLast()) {
                if (remainingPage > 0) {
                    start -= remainingPage;
                } else {
                    start -= numberOfPageToShow;
                }
            } else {
                start -= numberOfPageToShow;
            }
        }
    }
}