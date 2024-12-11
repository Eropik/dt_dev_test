package com.vizor.test.pagination;



import com.vizor.test.pagination.event.PaginationEvent;
import com.vizor.test.pagination.listener.CustomComponentListener;
import com.vizor.test.pagination.listener.PaginationMouseListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PaginationHandler implements CustomComponentListener {

    private final Pagination PAGINATION;
    private final PaginationComponent COMPONENT;
    private JPanel CONTAINER;
    private  int totalItems;

    public void setTotalItems(int totalItems) {
        this.totalItems =totalItems;
    }

    public int getTotalItems() {
        return totalItems;
    }


    public PaginationHandler(int set, int totalItems, int numberOfPageToShow) {

        this.PAGINATION = new Pagination(set, totalItems == 0 ? 1 : totalItems, Math.max(numberOfPageToShow, 5));
        COMPONENT = new PaginationComponent(Math.max(numberOfPageToShow, 5));
        this.totalItems = totalItems;

        createPagination();
        COMPONENT.addMouseListener(new PaginationMouseListener() {
            @Override
            public void onClick(PaginationEvent e) {
            }
        });

    }


    private void addListener() {
        COMPONENT.addListener(this);
    }

    public void addMouseListener(PaginationMouseListener listener) {
        COMPONENT.addMouseListener(listener);
    }

    private void createPagination() {
        addListener();
        CONTAINER = COMPONENT.createPages(
                PAGINATION.start(),
                PAGINATION.end(),
                PAGINATION.onFirst(),
                PAGINATION.onLast()
        );
        CONTAINER.setVisible(totalItems != 0);
    }

    public JPanel getPagination() {
        return CONTAINER;
    }

    public void modifyButton(Consumer<JLabel> c) {
        COMPONENT.modifyButton(c);
    }

    public void modifyNextAndPreviousButton(BiConsumer<JLabel, JLabel> c) {
        COMPONENT.modifyPreviousAndNextButton(c);
    }

    public int getCurrentPage(){
        return PAGINATION.getCurrent();
    }

    @Override
    public PaginationEvent onClick() {
        return new PaginationEvent(PAGINATION);
    }

    @Override
    public void onNext() {
        PAGINATION.onNext();
        loadPages();
    }

    @Override
    public void onPrevious() {
        PAGINATION.onPrevious();
        loadPages();
    }

    public void loadPages() {
        COMPONENT.createPages(
                PAGINATION.start(),
                PAGINATION.end(),
                PAGINATION.onFirst(),
                PAGINATION.onLast()
        );
    }

}