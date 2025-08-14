package com.boringland.mocardserver.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Txt2imgDTO implements Serializable {

    private String uid;

    private String openid;

    private String appid;

    private String text;

    private String sdModelCheckpoint;

    private String  negativePrompt;

    private String samplingMethod;

    private Integer samplingSteps;

    private Integer width;

    private Integer height;

    private Integer CFGScale;

    private Integer batchSize;

    private Boolean enableHr;

    private String hiresUpscale;

    private String hiresUpscaler;

    private String denoisingStrength;

    private String seed;

    private Integer scene;

    private String uniqueID;


}
