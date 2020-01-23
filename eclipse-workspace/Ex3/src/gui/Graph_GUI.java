package gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;

public class Graph_GUI extends JFrame implements ActionListener, MouseListener
{
	private static DGraph graph;

	public Graph_GUI()
	{
		this.graph = null;
		gui();
	}

	public Graph_GUI(DGraph graph)
	{
		this.graph = graph;
		gui();
	}

	private void gui()
	{

		this.setSize(500,500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		MenuBar menuBar = new MenuBar();
		Menu file = new Menu ("File");
		menuBar.add(file);
		Menu algorithms = new Menu("Algorithms");
		menuBar.add(algorithms);
		this.setMenuBar(menuBar);

		MenuItem draw = new MenuItem("draw");
		draw.addActionListener(this);

		MenuItem save = new MenuItem("save");
		save.addActionListener(this);

		MenuItem upload = new MenuItem("upload");
		upload.addActionListener(this);


		file.add(draw);
		file.add(save);
		file.add(upload);

		MenuItem isConnected = new MenuItem("isConnected");
		isConnected.addActionListener(this);

		MenuItem shortestPathDist = new MenuItem("shortestPathDist");
		shortestPathDist.addActionListener(this);

		MenuItem shortestPath = new MenuItem("shortestPath");
		shortestPath.addActionListener(this);

		MenuItem TSP = new MenuItem("TSP");
		TSP.addActionListener(this);

		algorithms.add(isConnected);
		algorithms.add(shortestPathDist);
		algorithms.add(shortestPath);
		algorithms.add(TSP);

		this.addMouseListener(this);


	}
	public void paint(Graphics g)
	{
		super.paint(g);

		/*g.setColor(Color.BLACK);
		g.drawLine(this.size().width*(1/2), this.size().height*(1/4), this.size().width*(1/2), this.size().height*(3/4));
		g.drawLine(this.size().width*(1/4), this.size().height*(1/2), this.size().width*(3/4), this.size().height*(1/2)); */
		if(this.graph != null) 
		{
			Collection <node_data> Nodes = this.graph.getV();
			for (node_data singleNode : Nodes) 
			{
				Point3D p1 = singleNode.getLocation();
				g.setColor(Color.BLUE);
				g.fillOval(p1.ix()*2 , p1.iy()*2, 10, 10);
				g.drawString(Integer.toString(singleNode.getKey()), (p1.ix()*2)+1, (p1.iy()*2)-2);

				Collection<edge_data> Edge = this.graph.getE(singleNode.getKey());
				for (edge_data singleEdge : Edge) 
				{
					g.setColor(Color.RED);
					node_data dest = graph.getNode(singleEdge.getDest());
					Point3D p2 = dest.getLocation();
					if (p2 != null) {
						g.drawLine(p1.ix()*2, p1.iy()*2, p2.ix()*2, p2.iy()*2);
						g.drawString(Double.toString(singleEdge.getWeight()),((p1.ix()*2)+(p2.ix()*2))/2 , ((p1.iy()*2)+(p2.iy()*2))/2);
						g.setColor(Color.YELLOW);
						int xDirection = (((((p1.ix()+p2.ix())/2)+p2.ix())/2)+p2.ix())/2;
						int yDirection = (((((p1.iy()+p2.iy())/2)+p2.iy())/2)+p2.iy())/2;
						g.fillOval(xDirection*2, yDirection*2, 7, 7);	
					}
				}
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		String str = e.getActionCommand();

		switch(str)
		{
		case "draw" : repaint();
		break; 
		case "upload" : deserialize();
		break;
		case "save" : serialize();
		break;
		case "shortestPathDist" : shortestPathDist ();
		break;
		case "shortestPath" : shortestPath();
		break;
		case "TSP" : TSP();
		break;
		case "isConnected" : isConnected();
		break;
		}

	}


	private void serialize() 
	{
		Graph_Algo graphA = new Graph_Algo();
		graphA.init(this.graph); 
		JFrame pF = new JFrame();
		JFileChooser choose = new JFileChooser();
		choose.setDialogTitle("choose a file");
		int selected = choose.showSaveDialog(pF);
		if (selected == JFileChooser.APPROVE_OPTION) 
		{
			File forSave = choose.getSelectedFile();
			String file= forSave.getAbsolutePath();
			graphA.save(file);		
			System.out.println("Saved as: " + forSave.getAbsolutePath());
		}

		System.out.println("Object has been serialized"); 
		JOptionPane.showMessageDialog(null, "Object has been saved");
	}

	private void deserialize()
	{
		Graph_Algo graphA = new Graph_Algo();

		JFrame pF = new JFrame();
		JFileChooser choose = new JFileChooser();
		choose.setDialogTitle("choose a file");
		int selected = choose.showSaveDialog(pF);
		if (selected == JFileChooser.APPROVE_OPTION)
		{
			File forUpload = choose.getSelectedFile();
			String str = forUpload.getAbsolutePath();
			graphA.init(str);
			repaint();
			System.out.println("file name: " + forUpload.getAbsolutePath());
		}
		System.out.println("Object has been deserialized"); 
		JOptionPane.showMessageDialog(null, "Object has been uploaded");
	}

	private void shortestPathDist() 
	{
		String src = JOptionPane.showInputDialog("choose the source point");
		String dest = JOptionPane.showInputDialog("choose the destination point");
		try {
			Graph_Algo graphA = new Graph_Algo();
			graphA.init(this.graph);
			double length = graphA.shortestPathDist(Integer.parseInt(src), Integer.parseInt(dest));	
			JOptionPane.showMessageDialog(null, "the shortest distance is: " + length);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void shortestPath() 
	{
		String str = "";
		String src = JOptionPane.showInputDialog("choose the source point");
		String dest = JOptionPane.showInputDialog("choose the destination point");
		if (!src.equals(dest)) 
		{
			try {
				Graph_Algo graphA = new Graph_Algo();
				graphA.init(this.graph);
				ArrayList <node_data> ansPath = new ArrayList<node_data>();
				ansPath = (ArrayList<node_data>) graphA.shortestPath(Integer.parseInt(src), Integer.parseInt(dest));
				for (int i = 0 ; i < ansPath.size() ; i++) 
					str += ansPath.get(i).getKey() + " >>> ";
				repaint();
				JOptionPane.showMessageDialog(null, "the shortest path is: " +str);
			}

			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	private void TSP() 
	{
		//JFrame input = new JFrame();
		Graph_Algo graphA = new Graph_Algo();
		graphA.init(this.graph);
		String targ = JOptionPane.showInputDialog("mention the points you want to get through, with space after every point");
		String [] tragSplit=targ.split(" ");
		LinkedList <Integer> targetsToIneteger=new LinkedList<>();
		for(int i=0; i < tragSplit.length; i++) 
			targetsToIneteger.add(Integer.parseInt(tragSplit[i]));
		LinkedList <node_data> ansPath = (LinkedList<node_data>) graphA.TSP(targetsToIneteger);
		if (ansPath != null ) 
		{
			String str = "";
			for (int k = 0; k < ansPath.size() ; k++)
				str += ansPath.get(k).getKey() + "--> ";
			repaint();
			JOptionPane.showMessageDialog(null, "the TSP is: " +str);
		}
	}

	private void isConnected() 
	{
		Graph_Algo graphA = new Graph_Algo();
		graphA.init(this.graph);
		if (graphA.isConnected())
			JOptionPane.showMessageDialog(null, "the graph is connected");
		else
			JOptionPane.showMessageDialog(null, "the graph is diconnected");
	}

}