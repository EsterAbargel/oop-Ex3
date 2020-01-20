package gameClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Server.Game_Server;
import Server.game_service;
import algorithms.AlgoGame;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;

public class MyGameGUI extends JFrame implements ActionListener, MouseListener
{
	public DGraph graph;
	int mode;
	double [] size;
	public ArrayList <Robot> rb= new ArrayList<>();
	public ArrayList <Fruit> fr = new ArrayList<>();
	AlgoGame ag;
	static game_service game;
	public int level;
	AlgoGame aaa;


	public static void main (String[]args)
	{
		MyGameGUI mmm = new MyGameGUI();
	}
	public MyGameGUI() 
	{
		gui();
	}

	public MyGameGUI(DGraph graph)
	{
		this.graph = new DGraph(graph);
		gui();
	}

	public void gui()
	{
		this.setSize(1280, 720);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		String lev= JOptionPane.showInputDialog("Choose the level you want to play between 0 to 23");
		this.level = Integer.parseInt(lev);
		while (level< 0 || level >23)
		{
			lev = JOptionPane.showInputDialog(null, "the level can be only a number between 0 to 23. choose again");
			level = Integer.parseInt(lev);
		}

		this.game = Game_Server.getServer(level);
		String strGraph = game.getGraph();
		graph = new DGraph();
		graph.init(strGraph);

		this.size=organizedScale(graph.nd);
		graph.nd.forEach((k, v) -> {
			Point3D loc = v.getLocation();
			Point3D newL = new Point3D((int)scale(loc.x(),size[0],size[1],50,1230), (int)scale(loc.y(),size[2],size[3],80,670));
			v.setLocation(newL); });

		String[] options = {"Automate", "Manual"};
		this.mode = JOptionPane.showOptionDialog(null, "Choose the mode of the game", "Message", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		repaint();
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		if (this.graph != null && this.graph.nd.size() > 0) 
		{
			Collection<node_data> nodes = graph.getV();
			for (node_data singleNode : nodes) {
				Point3D p1 = singleNode.getLocation();
				g.setColor(Color.BLUE);
				g.fillOval(p1.ix(), p1.iy(), 10, 10);
				g.drawString(Integer.toString(singleNode.getKey()), p1.ix()+1, p1.iy()-2);

				if (graph.edgeSize()!=0 && (graph.getE(singleNode.getKey())!=null)) {  
					Collection<edge_data> Edges = graph.getE(singleNode.getKey());
					for (edge_data singleEdge : Edges) 
					{
						g.setColor(Color.RED);
						node_data dest = graph.getNode(singleEdge.getDest());
						Point3D p2 = dest.getLocation();
						int x2 = p2.ix();
						int y2 = p2.iy();
						if (p2 != null) 
						{
							g.drawLine(p1.ix(), p1.iy(), x2, y2);
							g.setColor(Color.YELLOW);
							int xDirection = (((((p1.ix()+x2)/2)+x2)/2)+x2)/2;
							int yDirection = (((((p1.iy()+y2)/2)+y2)/2)+y2)/2;
							g.fillOval(xDirection, yDirection, 7, 7);	
						}
					}	

				}
			}

			initFruits();
			drawFruits(g);
			initRobots();
			drawRobots(g);
			
	 		 if (mode==0)
			{
				int i=0;
				AlgoGame moveRobots = AlgoGame.getAlgoGame(this,game);
				moveRobots.addRobot();
				game.startGame();
				//	MyGameGUI.autoManagerIsReady = true;
				double speed;
				System.out.println("start playing...");
				System.out.println(level);
				while(game.isRunning()) {
					i++;
					List<String> log = game.move();
					speed = moveRobots.optimalPath(log);
				}
				System.out.println("finish playing... moves = "+i);
					/*	ArrayList<String>robots = new ArrayList<>();
						String strGame = game.toString();
						int iterR = Integer.parseInt(strGame.substring(strGame.indexOf("robots")+8, strGame.indexOf("graph")-2));
						for(int i = 0; i < iterR; i++)
						{
							robots.add((String)game.getRobots().get(i));
						}
						aaa = new AlgoGame(this, this.game);
						aaa.optimalPath(robots);
						aaa.addRobot();  */
			}


		}

	}
	public edge_data fruitToEdge(Fruit f) 
	{
		edge_data ans = null;
		Point3D f_p = f.pos;
		Collection<node_data> nd = graph.getV();
		for(node_data n:nd) {
			Point3D ns_p = n.getLocation();
			Collection<edge_data> ed = graph.getE(n.getKey());
			if(ed!=null) 
			{
				for(edge_data e : ed) {
					Point3D nd_p = graph.getNode(e.getDest()).getLocation();
					if((ns_p.distance3D(f_p)+f_p.distance3D(nd_p))-ns_p.distance3D(nd_p)<0.000001) {
						f.setSrc(e.getSrc());
						f.setDest(e.getDest());
						return e;
					}
				}
			}
		}

		return ans;
	}

	public void initFruits()
	{
		System.out.println("initf");
		if (game.getFruits().size() > 0) 
		{
			int iterF = game.getFruits().size();
			for(int i=0; i < iterF; i++)
			{
				Fruit f=new Fruit((String)game.getFruits().get(i));
				fr.add(f);
				fruitToEdge(f);
				Point3D pos = f.getPos();
				f.setPos(new Point3D((int)scale(pos.x(),size[0],size[1],50,1230), (int)scale(pos.y(),size[2],size[3],80,670)));

			}
		}
	}

	public void drawFruits(Graphics g)
	{
		ImageIcon apple = new ImageIcon("Apple.jpeg");
		ImageIcon banana = new ImageIcon("Bananna.jpeg");
		//draw
		int srcF, destF;
		Point3D tempS, tempD;
		for (int i=0; i < fr.size(); i++) 
		{
			srcF = fr.get(i).getSrc();
			destF = fr.get(i).getDest();
			System.out.println(srcF + " " + destF);
			if (fr.get(i).getType() == -1) 
			{
				tempS = this.graph.getNode(srcF).getLocation();
				tempD = this.graph.getNode(destF).getLocation();
				g.drawImage(apple.getImage(), (int)(fr.get(i).getPos().ix())-5, (int)(fr.get(i).getPos().iy())-15, 70,70, null);
				//g.drawImage(apple.getImage(), (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))-5, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))-10, (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))+15, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))+10, 0, 0, 500, 500, null);
			}
			else {
				tempS = this.graph.getNode(srcF).getLocation();
				tempD = this.graph.getNode(destF).getLocation();
				g.drawImage(banana.getImage(), (int)(fr.get(i).getPos().ix())-10, (int)(fr.get(i).getPos().iy())-15, 70,70, null);
				//g.drawImage(banana.getImage(), (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))-5, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))-10, (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))+15, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))+10, 0, 0, 532, 470, null);
			}
		}
	}



	public void initRobots()
	{
		System.out.println();
		String strGame = game.toString();
		int iterR = Integer.parseInt(strGame.substring(strGame.indexOf("robots")+8, strGame.indexOf("graph")-2));
		//ArrayList<String>robots = new ArrayList<>();
		for(int i = 0; i < iterR; i++)
		{
			//robots.add((String)game.getRobots().get(i));
			game.addRobot(i);
			Robot r = new Robot((String)game.getRobots().get(i));
			rb.add(r);
		}
	}

	public void drawRobots(Graphics g)
	{
		if (rb !=null) 
		{
			Point3D pos;
			ImageIcon imageR = new ImageIcon("Unicorn.jpeg");
			if (rb.size() > 0) 
			{
				for (int j=0; j< rb.size(); j++) 
				{
					pos = new Point3D((int)scale(rb.get(j).getPos().x(),this.size[0],this.size[1],50,1230), (int)scale(rb.get(j).getPos().y(),this.size[2],this.size[3],80,670));
					//g.drawImage(imageR.getImage(), pos.ix()-10, pos.iy()-13, pos.ix()+10, pos.iy()+13, 0, 0, 500, 500, null);
					g.drawImage(imageR.getImage(), pos.ix()-10, pos.iy()-13, 100,100, null);
				}
			}
		}
	}

	private static double scale(double data, double r_min, double r_max, double t_min, double t_max)
	{
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}

	private static double[] organizedScale(HashMap<Integer, node_data> n) 
	{
		double [] ans = {Double.MAX_VALUE, Double.MIN_VALUE ,Double.MAX_VALUE ,Double.MIN_VALUE};
		n.forEach((k, v) -> {
			if (v.getLocation().x()<ans[0]) ans[0] = v.getLocation().x();
			if (v.getLocation().x()>ans[1]) ans[1] = v.getLocation().x();
			if (v.getLocation().y()<ans[2]) ans[2] = v.getLocation().y();
			if (v.getLocation().y()>ans[3]) ans[3] = v.getLocation().y();
		});
		return ans;
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
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}


}