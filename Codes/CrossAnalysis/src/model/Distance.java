package model;

/** 
 * @author Wei
 * 4/10/2014
 */

public class Distance{
	
	private Double dis;
	private int index;
     
    public Distance(double dis, int index) {
    	super();
    	this.dis = dis;
    	this.index = index;
    }
    
    public double getDis() {
    	return this.dis;
    }
    
    public int getIndex() {
    	return this.index;
    }
 }
