package dataStructure;

import java.util.Collection;
import java.util.HashMap;

public class DGraph implements graph{
	private HashMap<Integer,node_data> nd=new HashMap<>();
	private HashMap<Integer,HashMap<Integer,edge_data>> ed=new HashMap<>();
	@Override
	public node_data getNode(int key) {
		return  nd.get(key);
	}

	@Override
	public edge_data getEdge(int src, int dest) {
		return ed.get(src).get(dest);
	}

	@Override
	public void addNode(node_data n) {
		nd.put(n.getKey(), n);
		
	}

	@Override
	public void connect(int src, int dest, double w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<node_data> getV() {
		// TODO Auto-generated method stub
		return nd.values();
	}

	@Override
	public Collection<edge_data> getE(int node_id) {
		// TODO Auto-generated method stub
		return ed.get(node_id).values();
	}

	@Override
	public node_data removeNode(int key) {
		// TODO Auto-generated method stub
		return nd.remove(key);
	}

	@Override
	public edge_data removeEdge(int src, int dest) {
		// TODO Auto-generated method stub
		return ed.remove(src).remove(dest);
	}

	@Override
	public int nodeSize() {
		// TODO Auto-generated method stub
		return nd.size();
	}

	@Override
	public int edgeSize() {
		// TODO Auto-generated method stub
		return ed.size();
	}

	@Override
	public int getMC() {
		// TODO Auto-generated method stub
		return 0;
	}

}
