package com.vizor.test.pagination.listener;

import com.vizor.test.pagination.event.PaginationEvent;

public interface CustomComponentListener {

    PaginationEvent onClick();
    void onNext();
    void onPrevious();

}
