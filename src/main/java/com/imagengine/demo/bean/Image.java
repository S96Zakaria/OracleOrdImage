package com.imagengine.demo.bean;

import lombok.Data;
import oracle.ord.im.OrdImageSignature;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Data
public class Image {
    private BigDecimal id;
    private String path;
    private  OrdImageSignature signature;

    public Image() {
    }
}
