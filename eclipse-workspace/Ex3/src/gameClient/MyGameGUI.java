package gameClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.EdgeData;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;

public class MyGameGUI extends JFrame implements ActionListener, MouseListener, Runnable
{
	public DGraph graph;
	int mode;
	double [] size;
	public ArrayList <Robot> rb= new ArrayList<>();
	public ArrayList <Fruit> fr = new ArrayList<>();
	static game_service game;
	public int level;
	int numOfRobots;
	boolean paintRobots = false;
	Thread clientThread;
	private int score;

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
		clientThread = new Thread(this);

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

		String strGame = game.toString();
		this.numOfRobots = Integer.parseInt(strGame.substring(strGame.indexOf("robots")+8, strGame.indexOf("graph")-2));

		this.size=organizedScale(graph.nd);
		graph.nd.forEach((k, v) -> {
			Point3D loc = v.getLocation();
			Point3D newL = new Point3D((int)scale(loc.x(),size[0],size[1],50,1230), (int)scale(loc.y(),size[2],size[3],80,670));
			v.setLocation(newL); });
		initFruits();

		String[] options = {"Automate", "Manual"};
		this.mode = JOptionPane.showOptionDialog(null, "Choose the mode of the game", "Message", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if(mode==0) 
		{
			initRobots();
			game.startGame();
		/*	Thread kmlThread = new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					try
					{
						KML_Logger.createKML(game);
					}
					catch (ParseException | InterruptedException | IOException e)
					{
						throw new RuntimeErrorException(null, "Error running kmlThread");
					}
				}
			});
			kmlThread.start();
			*/
			clientThread.start();

		}

		else if(mode==1){
			repaint();
			manualRobots();
			game.startGame();
			clientThread.start();
		}
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
			drawFruits(g);
			if(paintRobots=true) drawRobots(g);
			timer(g);
			score(g);

		}





	}
	public edge_data fruitToEdge(Fruit f) 
	{
		edge_data ans = null;
		Point3D fruitPoint = f.getPos();
		for(node_data n:graph.getV()) {
			Collection<edge_data> ed = graph.getE(n.getKey());
			if(ed!=null) 
			{
				for(edge_data e : ed) {
					Point3D srcPoint = graph.getNode(e.getSrc()).getLocation();
					Point3D destPoint = graph.getNode(e.getDest()).getLocation();
					if ( Math.abs(fruitPoint.distance2D(srcPoint) + fruitPoint.distance2D(destPoint)-srcPoint.distance2D(destPoint))<0.01) 
					{
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
		if(fr!=null) fr.clear();
		if (game.getFruits().size() > 0) 
		{
			int iterF = game.getFruits().size();
			for(int i=0; i < iterF; i++)
			{
				Fruit f=new Fruit((String)game.getFruits().get(i));
				fr.add(f);
				Point3D pos = f.getPos();
				f.setPos(new Point3D((int)scale(pos.x(),size[0],size[1],50,1230), (int)scale(pos.y(),size[2],size[3],80,670)));
				fruitToEdge(f);
			}
		}

	}

	public void drawFruits(Graphics g)
	{
		ImageIcon apple = new ImageIcon("Apple.png");
		ImageIcon banana = new ImageIcon("Bananna.png");
		//draw
		int srcF, destF;
		Point3D tempS, tempD;
		for (int i=0; i < fr.size(); i++) 
		{
			srcF = fr.get(i).getSrc();
			destF = fr.get(i).getDest();
			if (fr.get(i).getType() == -1) 
			{
				tempS = this.graph.getNode(srcF).getLocation();
				tempD = this.graph.getNode(destF).getLocation();
				g.drawImage(apple.getImage(), (int)(fr.get(i).getPos().ix())-10, (int)(fr.get(i).getPos().iy())-25, 70,60, null);
				//g.drawImage(apple.getImage(), (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))-5, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))-10, (int)((tempS.ix()*0.3)+(0.7*tempD.ix()))+15, (int)((tempS.iy()*0.3)+(0.7*tempD.iy()))+10, 0, 0, 500, 500, null);
			}
			else {
				tempS = this.graph.getNode(srcF).getLocation();
				tempD = this.graph.getNode(destF).getLocation();
				g.drawImage(banana.getImage(), (int)(fr.get(i).getPos().ix())-10, (int)(fr.get(i).getPos().iy())-25, 70,70, null);
				//g.drawImage(banana.getImage(), (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))-5, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))-10, (int)((tempS.ix()*0.7)+(0.3*tempD.ix()))+15, (int)((tempS.iy()*0.7)+(0.3*tempD.iy()))+10, 0, 0, 532, 470, null);
			}
		}
	}

	public void initRobots()
	{
		if(rb!=null) rb.clear();
		for(int i = 0; i < numOfRobots; i++)
		{
			game.addRobot(i);
			Robot r = new Robot((String)game.getRobots().get(i));
			rb.add(r);

			int rand = (int)(Math.random()*(fr.size()));
			Fruit f = fr.get(rand);
			if (f.getType()==1) 
			{
				r.setSrc(f.getSrc());
				r.setDest(f.getDest());
				r.setPos(graph.getNode(r.getSrc()).getLocation());
			}
			else 
			{
				r.setSrc(f.getDest());
				r.setDest(f.getSrc());
				r.setPos(graph.getNode(r.getSrc()).getLocation());
			}
			fr.remove(f);

		}

		paintRobots = true;
	}

	public void drawRobots(Graphics g)
	{
		if (rb !=null) 
		{
			Point3D pos;
			ImageIcon imageR = new ImageIcon("Unicorn1.png");
			if (rb.size() > 0) 
			{
				for (int j=0; j< rb.size(); j++) 
				{
					pos = new Point3D((int)scale(rb.get(j).getPos().x(),this.size[0],this.size[1],50,1230), (int)scale(rb.get(j).getPos().y(),this.size[2],this.size[3],80,670));
					g.drawImage(imageR.getImage(), rb.get(j).getPos().ix()-55, rb.get(j).getPos().iy()-55, 130,130, null);
				}
			}
		}
	}

	public boolean checkNode (int num) //return true if the key belongs to one of the graph nodes
	{
		Collection <node_data> Nodes = this.graph.getV();
		for (node_data n:Nodes)
			if ((num==n.getKey()))
				return true;
		return false;
	}

	public int checkPos(double x,double y)  //return key of node if the point excepted equals to one of the the nodes location 
	{
		Collection <node_data> Nodes = this.graph.getV();
		for (node_data n:Nodes)
		{
			if(x==n.getLocation().x()&&y==n.getLocation().y()) 
			{
				return n.getKey();
			}
		}
		return -1;
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

	public void manualRobots()
	{
		if (mode == 1)
		{
			if (game.getRobots().size()==0)
			{
				for(int i=0; i< numOfRobots; i++)
				{
					String ans = JOptionPane.showInputDialog(null, "choose the number of the node you want to place the robot");
					while (! checkNode(Integer.parseInt(ans)))
					{
						ans = JOptionPane.showInputDialog(null, "the robot can be placed only on one of the nodes. try again");
						checkNode(Integer.parseInt(ans));
					}
					this.game.addRobot(i);
					Robot r = new Robot((String)game.getRobots().get(i));
					rb.add(r);
					r.setSrc(Integer.parseInt(ans));
					r.setPos(this.graph.getNode(Integer.parseInt(ans)).getLocation());
					paintRobots = true;
				}
				repaint();
			}
		}

	}

	private void timer(Graphics g) 
	{
		g.setColor(Color.BLACK);
		g.drawString("TIME TO END: "+ (int)(game.timeToEnd()/1000), 1000, 100);
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
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

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub

		while (game!= null && game.isRunning()) 
		{
			try 
			{
				if(mode==0)
				{
					repaint();
					moveRobots();
					initFruits();
					initRobots();
					game.move();
					Thread.sleep(1000);

				}
				else if(mode == 1)
				{
					game.move();
					initFruits();
					initRobots();
					repaint();
					Thread.sleep(1000);
				}

			}
			catch (Exception e)
			{
				throw new RuntimeException("Exception in run time");
			}

		}
		Object[] option = {"Yes","No"};
		JOptionPane.showMessageDialog(null, "GameOver, Final Score is: "+ score);
		int toKML = JOptionPane.showOptionDialog(null, "Game over:\nyou got "+score+" points with "
				+ "Do you want to save this game to a kml file?","Game over",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option,null);
		if(toKML == 0) {
			try {
				KML_Logger.createKML(game);
			} catch (ParseException | IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}  

	}

	public void moveRobots() throws JSONException 
	{
		List<String> log = game.move();
		if(log!=null) {
			game.timeToEnd();
			for(int i=0;i<log.size();i++) 
			{
				String robot_json = log.get(i);
				Robot r = new Robot (robot_json);
				if(r.getDest()==-1) 
				{
					game.chooseNextEdge(r.getID(), nextNode(r.getSrc()));
				}
				game.move();
			}
		}
	}

	private int nextNode(int src) 
	{
		Graph_Algo graphAlgo=new Graph_Algo();
		int robID =-1;
		double shortestpathdist=Integer.MAX_VALUE;
		graphAlgo.init(graph);
		for (Fruit f:fr) 
		{
			double returnshortst = graphAlgo.shortestPathDist(src, f.getDest());
			if (returnshortst < shortestpathdist) 
			{
				try 
				{
					shortestpathdist = graphAlgo.shortestPathDist(src, f.getDest());
					robID = graphAlgo.shortestPath(src, f.getDest()).get(1).getKey();
				}
				catch (Exception e)
				{
					robID=f.getSrc();
				}
			}
		}
		return robID;
	}
	private void score(Graphics g)
	{
		try 
		{
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject GameServer = line.getJSONObject("GameServer");
			score = GameServer.getInt("grade");
			g.drawString("Score: "+ score, 1000,120);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		} 
	}



}