package com.delda.sensor;

import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.Serializable;

public class LineGraph implements Serializable{
	
    private GraphicalView view;
    private double max = 100;

    private MySeries dataset = new MySeries();
    private MySeries dataset_2 = new MySeries();
    private MySeries dataset_3 = new MySeries();
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	
    private XYSeriesRenderer renderer = new XYSeriesRenderer();
    private XYSeriesRenderer renderer_2 = new XYSeriesRenderer();
    private XYSeriesRenderer renderer_3 = new XYSeriesRenderer();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    public LineGraph()
    {
        mDataset.addSeries(dataset.getSeries());
        mDataset.addSeries(dataset_2.getSeries());
        mDataset.addSeries(dataset_3.getSeries());
		
        renderer.setColor(Color.rgb(255,153,51));
        renderer.setLineWidth(5);
        renderer_2.setColor(Color.rgb(255,255,51));
        renderer_2.setLineWidth(5);
        renderer_3.setColor(Color.rgb(153,255,51));
        renderer_3.setLineWidth(5);
		
        mRenderer.setXTitle("Idő(s)");
        mRenderer.setAxisTitleTextSize(30);
        mRenderer.setYAxisMin(-max);
        mRenderer.setYAxisMax(max);
        mRenderer.setZoomEnabled(false,false);
        mRenderer.setPanEnabled(false,false);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setMarginsColor(Color.DKGRAY);
        mRenderer.setInScroll(false);
        mRenderer.setClickEnabled(false);
        mRenderer.setShowGrid(true);
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.addSeriesRenderer(renderer_2);
        mRenderer.addSeriesRenderer(renderer_3);
    }

    public GraphicalView getView(Context context)
    {
        view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
        view.setClickable(false);
        return view;
    }

    public void addNewPoints(double time, int acc)
    {
        if(acc>max)
		{
			max = acc;
			this.mRenderer.setYAxisMax(max);
		}
        dataset.add(time, acc);
	}
	
	public void addNewPoints_2(double time, int acc)
    {
        if(acc>max)
		{
			max = acc;
			this.mRenderer.setYAxisMax(max);
		}
        dataset_2.add(time, acc);
	}
	
	public void addNewPoints_3(double time, int acc)
    {
        if(acc>max)
		{
			max = acc;
			this.mRenderer.setYAxisMax(max);
		}
        dataset_3.add(time, acc);
	}
}
