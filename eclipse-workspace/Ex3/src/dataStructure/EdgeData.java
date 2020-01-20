package dataStructure;

import java.io.Serializable;

public class EdgeData implements edge_data,Serializable {
	private NodeData src,dest;
	private double weight;
	private int tag;
	String info="";
	
	public EdgeData(NodeData src,NodeData dest,double weight) {
		this.src=src;
		this.dest=dest;
		this.weight=weight;
	}

	public EdgeData(EdgeData edge) {
		this.src=edge.src;
		this.dest=edge.dest;
		this.weight=edge.weight;
		
	}

	@Override
	public int getSrc() {
		// TODO Auto-generated method stub
		return this.src.getKey();
	}

	@Override
	public int getDest() {
		// TODO Auto-generated method stub
		return this.dest.getKey();
	}

	@Override
	public double getWeight() {
		// TODO Auto-generated method stub
		return this.weight;
	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return this.info;
	}

	@Override
	public void setInfo(String s) {
		// TODO Auto-generated method stub
		this.info=s;
	}

	@Override
	public int getTag() {
		// TODO Auto-generated method stub
		return this.tag;
	}

	@Override
	public void setTag(int t) {
		// TODO Auto-generated method stub
		this.tag=t;
	}
	public node_data getNodeSrc() {
		return this.src;
	}
	
	public node_data getNodeDest() {
		return this.dest;
	}
	
	public String toString() {
		String ans="source: "+this.getSrc()+" destnation: "+this.getDest()+" weight: "+this.getWeight();
		return ans;
	}

}
