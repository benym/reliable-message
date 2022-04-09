package com.test.message.enums;


public enum MessageStatus {
    PREPARED("准备中", (byte) 0),
    COMMIT("已提交", (byte) 1);

    private String desc;
    private Byte code;

    MessageStatus() {
    }

    MessageStatus(String desc, Byte code) {
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
