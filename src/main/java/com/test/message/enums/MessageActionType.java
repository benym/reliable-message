package com.test.message.enums;


public enum MessageActionType {
    INSERT("insert", (byte) 0),
    UPDATE("update", (byte) 1),
    DELETE("delete", (byte) 2);

    private String action;
    private Byte noticeTypeCode;

    MessageActionType() {
    }

    MessageActionType(String action, Byte noticeTypeCode) {
        this.action = action;
        this.noticeTypeCode = noticeTypeCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Byte getNoticeTypeCode() {
        return noticeTypeCode;
    }

    public void setNoticeTypeCode(Byte noticeTypeCode) {
        this.noticeTypeCode = noticeTypeCode;
    }
}
