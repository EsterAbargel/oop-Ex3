package dataStructure;

import java.util.HashMap;

import utils.Point3D;

public class NodeData implements node_data {
	private int id;
	private double x,y,z;
	private double weight;
	
	
	@Override
	public int getKey() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public Point3D getLocation() {
		// TODO Auto-generated method stub
		return new Point3D(this.x,this.y,this.z);
	}

	@Override
	public void setLocation(Point3D p) {
		this.x=p.x();
		this.y=p.y();
		this.y=p.y();

	}

	@Override
	public double getWeight() {
		// TODO Auto-generated method stub
		return weight;
	}

	@Override
	public void setWeight(double w) {
		this.weight=w;

	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInfo(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getTag() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTag(int t) {
		// TODO Auto-generated method stub

	}
}
