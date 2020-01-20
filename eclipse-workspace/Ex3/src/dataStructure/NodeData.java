package dataStructure;

import java.io.Serializable;

import utils.Point3D;

public class NodeData implements node_data,Serializable {
	private int id;
	private double x,y,z;
	private double weight;
	private int tag;
	private String info="";
	

	public NodeData(int id,double x,double y,double z) {
		this.id=id;
		this.x=x;
		this.y=y;
		this.z=z;
		this.tag=0;
		this.weight=Double.POSITIVE_INFINITY;
		
	}
	public NodeData(int id,Point3D p) {
		this.id=id;
		setLocation(p);
		this.tag=0;
		this.weight=Double.POSITIVE_INFINITY;

	}
	public NodeData(NodeData node) {
		this.id=node.id;
		this.x=node.x;
		this.y=node.y;
		this.z=node.z;
	}
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
		return this.info;
	}

	@Override
	public void setInfo(String s) {
		this.info=s;

	}

	@Override
	public int getTag() {
		// TODO Auto-generated method stub
		return this.tag;
	}

	@Override
	public void setTag(int t) {
		this.tag=t;

	}
	
	public String toString() {
		String ans=Integer.toString(this.id);
		return ans;
		
	}
}
