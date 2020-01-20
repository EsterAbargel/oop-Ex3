
package algorithms;

import java.util.Iterator;
import java.util.LinkedList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import java.util.ArrayList;
import java.util.Stack;

import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;


/**
 * This empty class represents the set of graph-theory algorithms
 * which should be implemented as part of Ex2 - Do edit this class.
 * @author 
 *
 */
public class Graph_Algo implements graph_algorithms,  Serializable {
	
	public graph g;

	public Graph_Algo() {
		g=new DGraph();
	}

	public Graph_Algo(graph _graph) {
		this.init(_graph);
	}

	@Override
	public void init(graph g) {
		this.g=g;
	}

	@Override
	public void init(String file_name) {
		ObjectInputStream ois = null;
		try {
			FileInputStream fin = new FileInputStream(file_name);
			ois = new ObjectInputStream(fin);
			this.g = (graph) ois.readObject();
			ois.close();
		} catch (Exception e) {
			throw new RuntimeException("error");
		}

	}

	@Override
	public void save(String file_name) {
		// TODO Auto-generated method stub
		try {
			FileOutputStream fos = new FileOutputStream(file_name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.g);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean isConnected() {

		if (this.g.getV().size() == 1) {
			return true;
		}
		resteNodes();
		Collection<node_data> temp = new ArrayList<>();
		temp = this.g.getV();
		Iterator it = temp.iterator();
		while (it.hasNext()) {
			node_data currentNode = (node_data) it.next();
			int numOfReachableNodes = reachableNodes(currentNode);
			if (numOfReachableNodes < g.getV().size()) {//check if the number of nodes that are reachable from the current node is equal
				return false;
			}
		}

		return true;
	}
	/**
	 * 
	 * @param node is the current vertic we check
	 * @return number of vertices the node can reach to
	 */

	private int reachableNodes(node_data node) {
		this.resteNodes();
		Stack <node_data> nodes =new Stack<>();
		int ans=0;
		nodes.push(node);
		while (!nodes.isEmpty()){
			node_data temp = nodes.pop();
			if (temp.getTag()==0) {
				temp.setTag(1);
				ans++;
				Collection<node_data> notVisitedNeighboors = new ArrayList<node_data>();
				notVisitedNeighboors = neighboorsList(temp);
				Iterator iter = notVisitedNeighboors.iterator();
				while (iter.hasNext()){
					node_data v = (node_data)iter.next();
					nodes.push(v);
				}

			}
		}

		return  ans;

	}
	/**
	 * 
	 * @param node
	 * @return a list of the current vertic's neighboors  which were not visited yet. 
	 */

	private Collection<node_data> neighboorsList(node_data node){
		ArrayList<node_data> list=new ArrayList<node_data>();
		Iterator<edge_data> iterator=g.getE(node.getKey()).iterator();
		while(iterator.hasNext())
		{
			list.add(this.g.getNode(iterator.next().getDest()));
		}
		for (int i = 0; i <list.size() ; i++) {
			if(list.get(i).getTag()==1){
				list.remove(i);
			}
		}

		return list;

	}

	@Override
	public double shortestPathDist(int src, int dest) {
		double ans=0;
		List<node_data> nodes=shortestPath(src, dest);
		Iterator< node_data> iter=nodes.iterator();
		while(iter.hasNext()) {
			ans=iter.next().getWeight();
		}
		return ans;
	}

	@Override
	public List<node_data> shortestPath(int src, int dest) {
		resteNodes();
		g.getNode(src).setWeight(0);
		while(!isVisited()) {
			int minNode=findMin();
			g.getNode(minNode).setTag(1);
			if(g.getE(minNode)!=null) {
				Iterator<edge_data> it =  g.getE(minNode).iterator();
				while(it.hasNext()) {
					edge_data tempEdge = it.next();
					if(g.getNode(minNode).getWeight()+tempEdge.getWeight()<=g.getNode(tempEdge.getDest()).getWeight()) {
						g.getNode(tempEdge.getDest()).setWeight(g.getNode(minNode).getWeight()+tempEdge.getWeight());
						g.getNode(tempEdge.getDest()).setInfo(tempEdge.getSrc()+"");
					}
				}
			}
		}
		//create a list from every node in the path
		List<node_data> ans = new ArrayList<node_data>();
		ans.add(g.getNode(dest));
		node_data temp = g.getNode(dest);
		while(temp.getKey()!=src) {
			int prevNode=Integer.parseInt(temp.getInfo());
			List<node_data> tsp = new ArrayList<node_data>();
			tsp.add(g.getNode(prevNode));
			tsp.addAll(ans);
			ans=tsp;
			temp=g.getNode(prevNode);
		}

		return ans;
	}

	@Override
	public List<node_data> TSP(List<Integer> targets) {
		List <node_data> ans = new LinkedList<node_data>();
		if (!isConnected()) {
			return null;
		}
		if (targets.size()==1){
			ans.add(this.g.getNode(targets.get(0)));
			return ans;
		}
		ans.addAll(shortestPath(targets.get(0),targets.get(1)));
		for (int i = 1; i <targets.size()-1 ; i++) {
			ans.addAll(shortestPath(targets.get(i),targets.get(i+1)));
		}
		for (int i = 0; i <ans.size()-1 ; i++) {
			if (ans.get(i).getKey()==ans.get(i+1).getKey()){
				ans.remove(i);
			}
		}

		return ans;
	}

	@Override
	public graph copy() {
		graph copy= new DGraph();
		Collection<node_data> node=	g.getV();
		Iterator<node_data> iter=node.iterator();
		while(iter.hasNext()){
			copy.addNode(iter.next());
		}
		iter=node.iterator();
		while(iter.hasNext()){
			Collection<edge_data> edge=g.getE(iter.next().getKey());
			if(edge==null)
				break;

			Iterator<edge_data> iterEdges=edge.iterator();
			while(iterEdges.hasNext()) {
				edge_data t=iterEdges.next();
				copy.connect(t.getSrc(), t.getDest(), t.getWeight());
			}
		}
		return copy;
	}
	/**
	 * the method sets the tag of all of the nodes in the graph to be 0;
	 */

	private void resteNodes() { 
		Iterator<node_data> iter = g.getV().iterator();
		while(iter.hasNext()) {
			node_data temp=iter.next();
			temp.setTag(0);
			temp.setInfo("");
			temp.setWeight(Double.POSITIVE_INFINITY);
		}

	}
	/**
	 * the method finds the next minimum vertic in the graph
	 * @return the minimum node's key
	 */
	private int findMin() { 
		Iterator<node_data> iter =  g.getV().iterator();
		node_data ans=iter.next();
		iter= g.getV().iterator();

		while (iter.hasNext()) {
			node_data temp= iter.next();
			if(temp.getTag()==0) {
				ans=temp;
				break;
			}
		}
		while(iter.hasNext()) {
			node_data t = iter.next();
			if(t.getWeight()<ans.getWeight()&&t.getTag()==1) {
				ans=t;
			}
		}
		return ans.getKey();
	}
	/**
	 * the method check if all the nodes in the graph were visited
	 * @return true if all tag as 1(visited) else false 
	 */
	private boolean isVisited() { 
		Iterator<node_data> iter =  g.getV().iterator();
		while(iter.hasNext()) {
			if(iter.next().getTag()==0) {
				return false;
			}
		}
		return true;
	}



}

