package com.eco.view.disc.stickers.model;

import lombok.Data;

@Data
public class StickersPageRange {

    public StickersPageRange(Integer begin, Integer end) {
        this.begin = begin;
        this.end = end;
        this.count = end - begin + 1;
    }

    private Integer begin; //起始页数
    private Integer end; //结束页数
    private Integer count; //总页数

}
