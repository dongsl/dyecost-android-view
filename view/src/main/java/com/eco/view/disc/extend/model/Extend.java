package com.eco.view.disc.extend.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 扩展盘
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Extend implements Serializable {

    private Integer type; //扩展类型
    private String name; //名称
    private String image; //图片

}
