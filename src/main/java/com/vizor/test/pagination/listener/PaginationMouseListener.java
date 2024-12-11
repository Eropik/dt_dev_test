package com.vizor.test.pagination.listener;


import com.vizor.test.pagination.event.PaginationEvent;

import java.awt.*;

public interface PaginationMouseListener {

    default void onSelected(PaginationEvent e) {
        e.getButton().setBackground(Color.BLUE);
        e.getButton().setForeground(Color.white);

    }

    default void onUnselected(PaginationEvent e) { }

    default void onHover(PaginationEvent e){ }

    void onClick(PaginationEvent e);

}