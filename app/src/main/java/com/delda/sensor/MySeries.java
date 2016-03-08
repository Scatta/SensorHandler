package com.delda.sensor;

import org.achartengine.model.XYSeries;

import java.io.Serializable;
import java.util.LinkedList;

public class MySeries implements Serializable {
    private LinkedList<XY> mySeries;
    private XYSeries temp = new XYSeries("");
    MySeries(){
        mySeries = new LinkedList<XY>();
    }

    public void add(double time, int mag){
        if(mySeries.size()==200) {
            mySeries.removeFirst();
        }
        mySeries.addLast(new XY(time,mag));
        temp.clear();
        for(XY t : mySeries){
            temp.add(t.X,t.Y);
        }
    }
    public XYSeries getSeries(){
        return temp;
    }
    public class XY{
        private double X;
        private int Y;
        XY(double t, int m){
            this.X = t;
            this.Y = m;
        }
    }

}
