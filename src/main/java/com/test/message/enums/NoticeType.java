package com.test.message.enums;


public enum NoticeType {

    GLOBAL("全局消息", (byte) 0),
    OTHER("其他消息", (byte) 1);

    private String desc;
    private Byte code;

    NoticeType() {
    }

    NoticeType(String desc, Byte code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }
}
