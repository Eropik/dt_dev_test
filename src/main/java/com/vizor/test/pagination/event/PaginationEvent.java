package com.vizor.test.pagination.event;


import com.vizor.test.pagination.Pagination;

import javax.swing.*;

public class PaginationEvent {

    private final Pagination pagination;
    private JLabel button;

    public PaginationEvent(Pagination pagination) {
        this.pagination = pagination;
    }

    public int getSet() {
        return pagination.getSet();
    }

    public int getCurrentGroupPage() {
        return pagination.getCurrent();
    }
    public int getCurrentPage(){
        try {
            return (Integer.parseInt(button.getText()) );
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTotalPage() {
        return pagination.getNumberOfPages();
    }

    public int getOffSet() {
        try {
            return (Integer.parseInt(button.getText()) - 1) * getSet();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getLimit() {
        return pagination.getLimit();
    }

    public int getTotalItems(){
        return pagination.getTotalItems();
    }

    public int startIndex() {
        return getOffSet();
    }

    public int endIndex() {

        if(getCurrentPage()==pagination.getNumberOfItem()){
            return (pagination.getLimit() + getOffSet()) - 1;
        }else{
            return getOffSet() +(pagination.getSet()-1);
        }


    }

    public JLabel getButton() {
        return button;
    }

    public void setButton(JLabel label) {
        this.button = label;
    }

}
