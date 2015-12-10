package com.zkjinshi.svip.response;

import com.zkjinshi.svip.net.NetResponse;

/**
 * Created by djd on 2015/9/6.
 */
public class OrderInvoiceResponse extends NetResponse {

   private int id ;//            发票id int
   private String invoice_title ;//   发票抬头
   private String invoice_get_id ;//  取票方式 int

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

    public String getInvoice_get_id() {
        return invoice_get_id;
    }

    public void setInvoice_get_id(String invoice_get_id) {
        this.invoice_get_id = invoice_get_id;
    }
}
