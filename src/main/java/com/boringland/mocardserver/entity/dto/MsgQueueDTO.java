package com.boringland.mocardserver.entity.dto;

import java.io.Serializable;

public class MsgQueueDTO implements Serializable {
    private String openid;
    private String content;

    public MsgQueueDTO() {
    }

    public MsgQueueDTO(String openid, String content) {
        this.openid = openid;
        this.content = content;
    }

    public String getOpenid() {
        return this.openid;
    }

    public String getContent() {
        return this.content;
    }

    public void setOpenid(final String openid) {
        this.openid = openid;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MsgQueueDTO)) {
            return false;
        } else {
            MsgQueueDTO other = (MsgQueueDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$openid = this.getOpenid();
                Object other$openid = other.getOpenid();
                if (this$openid == null) {
                    if (other$openid != null) {
                        return false;
                    }
                } else if (!this$openid.equals(other$openid)) {
                    return false;
                }

                Object this$content = this.getContent();
                Object other$content = other.getContent();
                if (this$content == null) {
                    if (other$content != null) {
                        return false;
                    }
                } else if (!this$content.equals(other$content)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MsgQueueDTO;
    }



    public String toString() {
        return "MsgQueueDTO(openid=" + this.getOpenid() + ", content=" + this.getContent() + ")";
    }
}