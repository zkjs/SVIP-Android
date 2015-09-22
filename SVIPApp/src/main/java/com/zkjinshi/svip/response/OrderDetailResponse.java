package com.zkjinshi.svip.response;

import com.zkjinshi.svip.http.post.HttpResponse;

import java.util.ArrayList;

/**
 * Created by djd on 2015/9/6.
 */
public class OrderDetailResponse  extends HttpResponse {

    private OrderRoomResponse room;//订房信息
    private ArrayList<OrderUsersResponse> users;//入住人信息
    private OrderInvoiceResponse invoice;//发票信息
    private ArrayList<OrderRoomTagResponse> room_tag;//房间标签
    private ArrayList<OrderPrivilegeResponse> privilege;//服务特权
    private String user_applevel;
    private String user_shoplevel;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OrderRoomResponse getRoom() {
        return room;
    }

    public void setRoom(OrderRoomResponse room) {
        this.room = room;
    }

    public ArrayList<OrderUsersResponse> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<OrderUsersResponse> users) {
        this.users = users;
    }

    public OrderInvoiceResponse getInvoice() {
        return invoice;
    }

    public void setInvoice(OrderInvoiceResponse invoice) {
        this.invoice = invoice;
    }

    public ArrayList<OrderRoomTagResponse> getRoom_tag() {
        return room_tag;
    }

    public void setRoom_tag(ArrayList<OrderRoomTagResponse> room_tag) {
        this.room_tag = room_tag;
    }

    public ArrayList<OrderPrivilegeResponse> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(ArrayList<OrderPrivilegeResponse> privilege) {
        this.privilege = privilege;
    }

    public String getUser_applevel() {
        return user_applevel;
    }

    public void setUser_applevel(String user_applevel) {
        this.user_applevel = user_applevel;
    }

    public String getUser_shoplevel() {
        return user_shoplevel;
    }

    public void setUser_shoplevel(String user_shoplevel) {
        this.user_shoplevel = user_shoplevel;
    }
}
