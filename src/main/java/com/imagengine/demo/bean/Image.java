package com.imagengine.demo.bean;

import lombok.Data;
import oracle.ord.im.OrdImage;
import oracle.ord.im.OrdImageSignature;

import javax.persistence.Entity;
import java.math.BigDecimal;


public class Image {
    private  BigDecimal id;
    private  OrdImage image;
    private  OrdImageSignature signature;
    private  float score;
    
    

    public BigDecimal getId() {
		return id;
	}


	public void setId(BigDecimal id) {
		this.id = id;
	}


	public OrdImage getImage() {
		return image;
	}


	public void setImage(OrdImage image) {
		this.image = image;
	}


	public OrdImageSignature getSignature() {
		return signature;
	}


	public void setSignature(OrdImageSignature signature) {
		this.signature = signature;
	}


	public float getScore() {
		return score;
	}


	public void setScore(float score) {
		this.score = score;
	}


	public Image() {
    }


	@Override
	public String toString() {
		return "Image [id=" + id + ", image=" + image + ", signature=" + signature + ", score=" + score + "]";
	}
	
	
}
