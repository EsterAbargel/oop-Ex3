package dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import dataStructure.edge_data;
import dataStructure.node_data;
import gui.Graph_GUI;
import utils.Point3D;

public class DGraph implements graph,Serializable{

	public HashMap<Integer,node_data> nd=new HashMap<>();
	public HashMap<Integer,HashMap<Integer,edge_data>> ed=new HashMap<>();
	private int mc=0;
	private int countEdges=0;
	private Graph_GUI updater;

	public DGraph(NodeData [] vertices,EdgeData[] edge) {
		for(int i=0;i<vertices.length;i++) {
			this.addNode(vertices[i]);
		}
		for(int j=0;j<edge.length;j++) {
			this.connect(edge[j].getSrc(), edge[j].getDest(), edge[j].getWeight());
		}

	}

	public DGraph(graph g) {
		// TODO Auto-generated constructor stub
		for(node_data i:g.getV()) {
			NodeData nodeDataTemp = new NodeData(i.getKey(),i.getLocation());
			this.addNode(nodeDataTemp);
			for(edge_data e:g.getE(nodeDataTemp.getKey())) {
				this.connect(e.getSrc(), e.getDest(), e.getWeight());
			}
		}
	}


	public DGraph() {
		this.nd=new HashMap<>();
		this.ed=new HashMap<>();
	}

	public void init(String Json_String) {
		// Reset nodes, edges, node id, and num of edges
		nd.clear();
		ed.clear();
		//    node.id = 0;
		countEdges = 0;
		// Read all information from string and enter DGraph
		try {
			JSONObject graph = new JSONObject(Json_String);
			JSONArray nodes_Json = graph.getJSONArray("Nodes");
			JSONArray edges_Json = graph.getJSONArray("Edges");
			// Add each node in the Json String
			for (int i = 0; i < nodes_Json.length(); i++) {
				int key = nodes_Json.getJSONObject(i).getInt("id");
				String location = nodes_Json.getJSONObject(i).getString("pos");
				Point3D p = new Point3D(location);
				this.addNode(new NodeData(key, p));
			}
			// Connect each edge in the Json String
			for (int i = 0; i < edges_Json.length(); i++) {
				int src = edges_Json.getJSONObject(i).getInt("src");
				int dest = edges_Json.getJSONObject(i).getInt("dest");
				double weight = edges_Json.getJSONObject(i).getDouble("w");
				this.connect(src, dest, weight);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public node_data getNode(int key) {
		return  nd.get(key);
	}

	@Override
	public edge_data getEdge(int src, int dest) {
		if(ed.containsKey(src)&&ed.get(src).containsKey(dest)&&ed.get(src).get(dest)!=null){
			return (edge_data)this.ed.get(src).get(dest);
		}
		else
			return null;
	}

	@Override
	public void addNode(node_data n) {
		nd.put(n.getKey(), n);
		mc++;

	}

	@Override
	public void connect(int src, int dest, double w) {
		if(this.nd.get(src)==null||this.nd.get(dest)==null) {
			System.out.println("one of the nodes do not exists in this graph");
		}
		else {
			EdgeData temp=new EdgeData((NodeData)nd.get(src), (NodeData)nd.get(dest), w);
			if(this.ed.get(src)==null) {
				this.ed.put(src,new HashMap<Integer,edge_data>());
				this.ed.get(src).put(dest, temp);
				countEdges++;
				mc++;
			}
			else {

				if(!(ed.get(src).containsKey(dest))) {
					this.ed.get(src).put(dest, temp);
					countEdges++;
					mc++;
				}
			}
		}


	}

	@Override
	public Collection<node_data> getV() {
		// TODO Auto-generated method stub
		return nd.values();
	}

	@Override
	public Collection<edge_data> getE(int node_id) {
		// TODO Auto-generated method stub
		if(!ed.containsKey(node_id)||ed.get(node_id).isEmpty()||ed.isEmpty()) {
			return null;
		}
		return ed.get(node_id).values();
	}

	@Override
	public node_data removeNode(int key) {
		// TODO Auto-generated method stub
		node_data removedNode = this.nd.remove(key);
		if (removedNode == null) {
			return null;
		}
		mc++;
		countEdges -= this.ed.get(key).size();
		this.ed.remove(key);
		for (node_data n : getV()) {
			int src = n.getKey();
			removeEdge(src, key);
		}
		return removedNode;
	}

	@Override
	public edge_data removeEdge(int src, int dest) {
		// TODO Auto-generated method stub
		edge_data removedEdge = null;
		if (ed.get(src).get(dest) != null) {
			removedEdge = ed.get(src).remove(dest); 
			if (removedEdge != null) {
				mc++;
				countEdges--;
			}
		}
		return removedEdge;
	}

	@Override
	public int nodeSize() {
		// TODO Auto-generated method stub
		return nd.size();
	}

	@Override
	public int edgeSize() {
		// TODO Auto-generated method stub

		return countEdges;
	}

	@Override
	public int getMC() {
		return mc;
	}

	public Collection<edge_data> getAllE() {
		ArrayList<node_data> nodes = new ArrayList<node_data>(nd.values());
		ArrayList<edge_data> edges = new ArrayList<edge_data>();
		for (node_data n:nodes) {
			edges.addAll(getE(n.getKey()));
		}
		return edges;
	}


}
