package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import algorithms.Game_Algo;
import dataStructure.*;
import gameClient.*;
import utils.*;

/**
 * This class makes a gui window to represent a game where robots travels on a graph 
 * and collect fruits to earn coins.
 * @author YosefTwito and EldarTakach
 */
public class GraphGui extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	public Game_Algo mg;
	graph gr;
	ArrayList<Robot> robots;
	ArrayList<Fruit> fruits;
	double [] exPos;

	public GraphGui(DGraph g){
		g.addListener(this);
		this.gr=g;
		initGUI(g);
	}

	private static int nextNode(graph g, int src) {
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		Iterator<edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}


	public GraphGui(DGraph g, double [] size,Game_Algo game){
		g.addListener(this);
		this.gr = g;
		this.fruits = game.listFruit;
		this.robots = game.listRobot;
		this.exPos = size;
		this.mg=game;
		initGUI(g);
		mg.game.startGame();
	}

	private static double scale(double data, double r_min, double r_max, double t_min, double t_max)
	{
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}



	public void paint(Graphics d) {
		super.paint(d);

		//	synchronized(this.mg) {

		if (gr != null && gr.nodeSize()>=1) {
			//get nodes
			Collection<node_data> nodes = gr.getV();

			for (node_data n : nodes) {
				//draw nodes
				Point3D p = n.getLocation();
				d.setColor(Color.BLACK);
				d.fillOval(p.ix(), p.iy(), 11, 11);

				//draw nodes-key's
				d.setColor(Color.BLUE);
				d.drawString(""+n.getKey(), p.ix()-4, p.iy()-5);

				//check if there are edges
				if (gr.edgeSize()==0) { continue; }
				if ((gr.getE(n.getKey())!=null)) {
					//get edges
					Collection<edge_data> edges = gr.getE(n.getKey());
					for (edge_data e : edges) {
						//draw edges
						d.setColor(Color.GREEN);
						((Graphics2D) d).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
						Point3D p2 = gr.getNode(e.getDest()).getLocation();
						d.drawLine(p.ix()+5, p.iy()+5, p2.ix()+5, p2.iy()+5);
					}	
				}

				//draw fruits
				if (mg.listFruit != null) {

					if (mg.listFruit.size()>0) {
						//get icons
						ImageIcon apple = new ImageIcon("Apple.jpeg");
						ImageIcon banana = new ImageIcon("Banana.jpeg");
						//draw
						int srcF, destF;
						Point3D tempS, tempD;
						for (int i=0; i<mg.listFruit.size(); i++) {
							srcF = mg.listFruit.get(i).getSrc();
							destF = mg.listFruit.get(i).getDest();
							if (mg.listFruit.get(i).getType()==-1) {
								tempS = this.gr.getNode(srcF).getLocation();
								tempD = this.gr.getNode(destF).getLocation();
								d.drawImage(apple.getImage(), (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))-5, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))-10, (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))+15, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))+10, 0, 0, 500, 500, null);
							}
							else {
								tempS = this.gr.getNode(srcF).getLocation();
								tempD = this.gr.getNode(destF).getLocation();
								d.drawImage(banana.getImage(), (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))-5, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))-10, (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))+15, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))+10, 0, 0, 532, 470, null);
							}
						}
					}
				}
				//draw robots
				if (this.robots !=null) {
					//get icon
					ImageIcon robocop = new ImageIcon("Unicorn.jpeg");
					if (this.robots.size()>0) {
						for (int i=0; i< robots.size(); i++) {
							//reposition to robots
							Point3D pos = new Point3D((int)scale(mg.listRobot.get(i).getPos().x(),this.exPos[0],this.exPos[1],50,1230), (int)scale(mg.listRobot.get(i).getPos().y(),this.exPos[2],this.exPos[3],80,670));
							//draw
							d.drawImage(robocop.getImage(), pos.ix()-10, pos.iy()-13, pos.ix()+10, pos.iy()+13, 0, 0, 500, 500, null);

						}
					}
				}		
			}	
		}
		//	}
	}


	private void initGUI(graph g) {
		this.gr=g;
		this.setSize(1280, 720);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);

		ImageIcon img = new ImageIcon("Unicorn.jpeg");
		this.setIconImage(img.getImage());

		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);

		Menu file = new Menu("File ");
		menuBar.add(file);

		MenuItem item1 = new MenuItem("Init Original Graph");
		item1.addActionListener(this);
		file.add(item1);
	}

	@Override
	public void actionPerformed(ActionEvent Command) {
		String str = Command.getActionCommand();		

		switch(str) {

		case "Init Original Graph":
			break;
		}
	}

	public void graphUpdater() {	
		repaint();	
	}
}