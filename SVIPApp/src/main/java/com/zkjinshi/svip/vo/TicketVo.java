package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by djd on 2015/8/27.
 */
public class TicketVo implements Serializable {
    private int id;
    private String invoice_title;
    private String is_default;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoice_title() {
        return invoice_title;
    }

    public void setInvoice_title(String invoice_title) {
        this.invoice_title = invoice_title;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }
}
