package com.vizor.test.pagination;



import com.vizor.test.pagination.event.PaginationEvent;
import com.vizor.test.pagination.listener.CustomComponentListener;
import com.vizor.test.pagination.listener.PaginationMouseListener;

import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.*;


class PaginationComponent{

    private final JLabel NEXT = new JLabel(">");
    private final JLabel PREVIOUS = new JLabel("<");
    JLabel next;
    JLabel previous;
    JPanel container =new JPanel();
    JLabel selectedPage;
    private final int numberOfPageToShow;
    JLabel[] pageButtons;
    Consumer<JLabel> pageButtonConsumer;
    private CustomComponentListener listener;
    private final List<PaginationMouseListener> mouseListeners = new ArrayList<>();


    public PaginationComponent(int numberOfPageToShow) {
        this.numberOfPageToShow = numberOfPageToShow;
        next =getNext();
        previous = getPrev();
    }



    public void modify(JLabel label) {
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setOpaque(true);
        label.setSize(200, 50);
        if (pageButtonConsumer != null) {
            pageButtonConsumer.accept(label);
        }
    }


    public void modifyButton(Consumer<JLabel> c){
        this.pageButtonConsumer=c;
        for (JLabel label : pageButtons) {
            modify(label);
        }
    }

    public void modifyPreviousAndNextButton(BiConsumer<JLabel,JLabel> c){
        c.accept(PREVIOUS, NEXT);
    }

    public void addListener(CustomComponentListener listener){
        this.listener = listener;
    }

    public void addMouseListener(PaginationMouseListener mouseListener){
        mouseListeners.add(mouseListener);
    }

    private void createPageButton(int start, int end) {
        pageButtons = new JLabel[numberOfPageToShow];
        int _start = start % numberOfPageToShow;
        int _end = end % numberOfPageToShow;
        if (_end == 0) {
            end = numberOfPageToShow;
        } else {
            end = _end;
        }
        for (int i = _start - 1; i < numberOfPageToShow; i++) {
            if (i < end) {
                pageButtons[i] = new JLabel(String.valueOf(i + start));
                pageButtons[i].setHorizontalAlignment(JLabel.CENTER);
                pageButtons[i].setOpaque(true);
                pageButtons[i].setBackground(new Color(113, 122, 170));
                pageButtons[i].setForeground(Color.BLACK);
                pageButtons[i].setSize(200, 40);
                pageButtons[i].setPreferredSize(new Dimension(200, 40));
                onClick(pageButtons[i]);
                if (pageButtonConsumer != null) {
                    modify(pageButtons[i]);
                }
            } else {
                pageButtons[i] = new JLabel("");
            }
        }
    }

    private JLabel getNext() {
        NEXT.setBackground(Color.gray);
        NEXT.setHorizontalAlignment(JLabel.CENTER);
        NEXT.setForeground(Color.black);
        NEXT.setOpaque(true);
        NEXT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        NEXT.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.onNext();
            }
        });
        return NEXT;
    }

    private JLabel getPrev() {
        PREVIOUS.setBackground(Color.gray);
        PREVIOUS.setHorizontalAlignment(JLabel.CENTER);
        PREVIOUS.setOpaque(true);
        PREVIOUS.setForeground(Color.black);
        PREVIOUS.setCursor(new Cursor(Cursor.HAND_CURSOR));
        PREVIOUS.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.onPrevious();
            }
        });
        return PREVIOUS;
    }

    public JPanel createPages( int start, int end, boolean onFirst, boolean onLast){
        container.removeAll();
        container.repaint();
        container.revalidate();
        container.setLayout(new GridLayout(0,2+numberOfPageToShow));
        createPageButton( start,  end);
        previous.setVisible(!onFirst);
        container.add(PREVIOUS);
        for(JLabel page:pageButtons){
            container.add(page);
        }
        next.setVisible(!onLast);
        container.add(NEXT);
        return container;

    }

    private void onClick(JLabel label) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel label = (JLabel) e.getComponent();
                PaginationEvent event = listener.onClick();
                event.setButton(label);
                for (PaginationMouseListener mouseListener : mouseListeners) {
                    if ( selectedPage!= null) {
                        event.setButton(selectedPage);
                        selectedPage.setBackground(new Color(113, 122, 170));
                        selectedPage.setForeground(Color.BLACK);
                        mouseListener.onUnselected(event);
                    }
                    selectedPage = label;
                    event.setButton(label);
                    mouseListener.onSelected(event);
                    mouseListener.onClick(event);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getComponent();

                if (label != selectedPage) {
                    for (PaginationMouseListener mouseListener : mouseListeners) {
                        PaginationEvent event = listener.onClick();
                        event.setButton(label);
                        mouseListener.onHover(event);
                    }
                }

            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getComponent();

                if (label != selectedPage) {
                    label.setBackground(new Color(113, 122, 170));
                    label.setForeground(Color.BLACK);
                }
            }
        });
    }
}



