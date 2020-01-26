package gameClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.management.RuntimeErrorException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

import gameClient.Fruit;
import Server.Game_Server;
import gameClient.Robot;
import Server.game_service;
import utils.Point3D;
import dataStructure.*;
import algorithms.*;
import gameClient.KML_Logger;



public class MyGameGUI extends JFrame implements ActionListener , MouseListener, Runnable 
{
	public ArrayList<Fruit> fr;
	public BufferedImage robotImage; //robot icon.
	public BufferedImage appleImage; 
	public BufferedImage bannaImage; 
	private DGraph graph;
	public static game_service game;
	private static double maxX = Double.NEGATIVE_INFINITY;
	private static double maxY = Double.NEGATIVE_INFINITY;
	private static double minX = Double.POSITIVE_INFINITY;
	private static double minY = Double.POSITIVE_INFINITY;
	private int numOfRobots;
	private Boolean PaintRobots;
	boolean manualMode;
	Thread clientThread;
	private boolean firstpress=false;
	private static int score=0;
	private static int moves=0;
	public static int level;
	public static int id;


	public static void main(String[] args) 
	{
		MyGameGUI gui = new MyGameGUI();
		gui.setVisible(true);
	}


	public MyGameGUI() 
	{
		gui();
	}

	private void gui() 
	{

		PaintRobots = false;
		this.setSize(1280, 720);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addMouseListener(this);
		
		try 
		{
			robotImage = ImageIO.read(new File("Unicorn1.png"));
			bannaImage = ImageIO.read(new File("Bananna.png"));
			appleImage = ImageIO.read(new File("Apple.png"));
		}

		catch (IOException e) 
		{
			e.printStackTrace();
		}



		MenuBar menuBar = new MenuBar();
		Menu mode = new Menu("Game Mode");
		menuBar.add(mode);
		MenuItem auto = new MenuItem("Auto Game");
		auto.addActionListener(this);
		MenuItem manual = new MenuItem("manual Game");
		manual.addActionListener(this);
		mode.add(auto);
		mode.add(manual);
		this.setMenuBar(menuBar);
		this.addMouseListener(this);
		clientThread = new Thread(this);
	}


	@Override
	public void actionPerformed(ActionEvent event) 
	{
		JFrame start = new JFrame();
		String action = event.getActionCommand();
		if (action.equals("manual Game")) 
		{
			manualMode=true;
			String user = JOptionPane.showInputDialog(start,"enter id: ");
			try 
			{
				id = Integer.parseInt(user);
				Game_Server.login(id);

			}
			catch (Exception e) 
			{
				throw new RuntimeException("Invalid Id");
			} 
			try 
			{
				level = chooseScenario();
				if (level < 0 &&level!=-31) 
				{
					JOptionPane.showMessageDialog(start, "Invalid level");
				} 
				else 
				{
					game = Game_Server.getServer(level);
					String Graph_str = game.getGraph();
					graph = new DGraph();
					graph.init(Graph_str);

					//get robots count for the scenario
					try 
					{
						String info = game.toString();
						JSONObject line;
						line = new JSONObject(info);
						JSONObject obj = line.getJSONObject("GameServer");
						numOfRobots = obj.getInt("robots");
					}
					catch (JSONException e)
					{
						throw new RuntimeException(" Json");
					}
					Collection<node_data> findMinMax = graph.getV();
					minX = findMinX(findMinMax);
					maxX = findMaxX(findMinMax);
					minY = findMinY(findMinMax);
					maxY = findMaxY(findMinMax);
					repaint();
					addManualRobots();
					game.startGame();

					//open kml thread to save data during game
					Thread kmlThread = new Thread(new Runnable() 
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
					clientThread.start();
				}
			} 
			catch (Exception e) 
			{
				JOptionPane.showMessageDialog(start, "Error in manual game");
				e.printStackTrace();
			}
		}
		if (action.equals("Auto Game")) 
		{
			manualMode=false;
			String idUser = JOptionPane.showInputDialog(start,"please enter your id: ");

			try 
			{
				id = Integer.parseInt(idUser);
				Game_Server.login(id);


			}
			catch (Exception e) 
			{
				throw new RuntimeException("Invalid Id");
			} 

			try 
			{
				level = chooseScenario();
				if (level < 0 ) 
				{
					JOptionPane.showMessageDialog(start, "Invalid level");
				} 
				else 
				{
					game = Game_Server.getServer(level);
					String Graph_str = game.getGraph();
					graph = new DGraph();
					graph.init(Graph_str);
					try 
					{
						String info = game.toString();
						JSONObject line;
						line = new JSONObject(info);
						JSONObject obj = line.getJSONObject("GameServer");
						numOfRobots = obj.getInt("robots");
					}
					catch (JSONException e)
					{
						throw new RuntimeException("parse Json");
					}

					fr= new ArrayList<Fruit>();
					if(!game.getFruits().isEmpty())
					{
						for (String fruit : game.getFruits()) 
						{
							Fruit currFruit = new Fruit(fruit);
							currFruit.setEdge(findFruitEdge(currFruit.getPos()));
							fr.add(currFruit);
						}
					}
					Collection<node_data> listNodes = graph.getV();
					minX = findMinX(listNodes);
					maxX = findMaxX(listNodes);
					minY = findMinY(listNodes);
					maxY = findMaxY(listNodes);
					drawAutoRobots();
					PaintRobots=true;
					game.startGame();
					//open kml thread to save data during game
					Thread kmlThread = new Thread(new Runnable() 
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
					clientThread.start();

				}
			} 
			catch (Exception e) 
			{
				JOptionPane.showMessageDialog(start, "Error in automatic game");
			}
		}
	}
	public int chooseScenario() 
	{
		JFrame window = new JFrame();
		int scenarioNum;
		try
		{
			String inputUser = JOptionPane.showInputDialog(window, "Please enter scenario number between 0-23");
			scenarioNum=Integer.parseInt(inputUser);

		}
		catch(Exception e)
		{
			throw new RuntimeException("The scenario must be a number, please try again");
		}
		if(scenarioNum<0 || scenarioNum>23) 
		{
			JOptionPane.showMessageDialog(window, "The number need to be between 0-23, please try again " );
			return -1;
		}
		else 
		{
			return scenarioNum;
		}

	}

	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);
		g.setFont(new Font ("Courier", Font.PLAIN,20));
		if (graph == null && game !=null ) 
		{
			JFrame mesg = new JFrame(); 
			JOptionPane.showMessageDialog(mesg, "There Isn't Graph To Show");
		}
		else if(game !=null  && graph!=null)
		{	
			drawGraph(g);
		}

		//draw fruits
		if(game!=null && !game.getFruits().isEmpty())
		{
			drawFruits(g);
		}
		//draw robots
		if (PaintRobots) 
		{
			drawRobots(g);
		}
		//draw timer
		timer(g);
		//calculate score and moves
		GameScoreAndMoves(g);
		//printLevel(graph);
	}


	public void drawGraph(Graphics g) {
		for (node_data currNode : graph.getV()) 
		{
			g.setColor(Color.blue);
			g.setFont(new Font ("Courier", Font.PLAIN,30));
			Point3D srcNode = currNode.getLocation();
			double srcScaleXNode = scale(srcNode.x(),minX, maxX, 50, 1230);
			double srcScaleYNode = scale(srcNode.y(),minY, maxY, 80, 670);
			g.fillOval((int)srcScaleXNode, (int)srcScaleYNode, 12, 12);
			g.setColor(Color.blue);
			g.drawString("" + currNode.getKey(), (int)srcScaleXNode, (int)srcScaleYNode+20);


			if ((graph.getE(currNode.getKey())!=null)) 
			{
				for (edge_data edge : graph.getE(currNode.getKey())) 
				{

					Point3D destPoint = graph.getNode(edge.getDest()).getLocation();
					g.setColor(Color.RED);
					double destScaleX = scale(destPoint.x(),minX, maxX, 50, 1230);
					double destScaleY = scale(destPoint.y(),minY, maxY, 80,670);
					Graphics2D g2 = (Graphics2D) g;
					g2.setStroke(new BasicStroke(2));
					g2.drawLine((int) srcScaleXNode, (int) srcScaleYNode, (int) destScaleX, (int) destScaleY);

				}
			}
		}
	}

	public void drawFruits(Graphics g) {
		for (String f:game.getFruits()) 
		{
			Fruit currFruit = new Fruit(f); 
			Point3D fruitLocation = currFruit.getPos();
			double fruitScaleX = scale(fruitLocation.x() , minX, maxX, 50, 1230);
			double fruitScaleY = scale(fruitLocation.y() , minY, maxY,80, 670);
			if(currFruit.getType()<0)
			{
				//apple
				g.drawString(currFruit.getV() + " v", (int) fruitScaleX - 9, (int) fruitScaleY + 11);
				g.drawImage(appleImage, (int)fruitScaleX-10, (int)fruitScaleY-25, 70,60,null);
			}
			else
			{
				//banana
				g.drawImage(bannaImage, (int)fruitScaleX-10, (int)fruitScaleY-25,70,70, null);
				g.drawString(currFruit.getV() + " ^", (int) fruitScaleX - 9, (int) fruitScaleY + 11);
			}

		}
	}

	public void drawRobots(Graphics g) {
		for (String r: game.getRobots()) 
		{

			Robot currRobot = new Robot (r);
			Point3D robotLocation = currRobot.getPos();
			double robotScaleX = scale(robotLocation.x() , minX, maxX, 50, 1230);
			double robotScaleY = scale(robotLocation.y() , minY, maxY, 80, 670);
			g.drawImage(robotImage, (int)robotScaleX-55, (int)robotScaleY-55,130,130, null);
		}
	}


	//calculate score and display on gui
	private void GameScoreAndMoves(Graphics graph)
	{
		try 
		{
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject GameServer = line.getJSONObject("GameServer");
			score = GameServer.getInt("grade");
			moves = GameServer.getInt("moves");
			graph.drawString("Score: "+ score, (int)minX+1500,(int)maxY+200);
			graph.drawString("Moves: "+ moves, (int)minX+1500,(int)maxY+300);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		} 
	}

	//draw robots in automatic game
	private void drawAutoRobots()
	{
		ArrayList<Fruit> fruitsTemp=new ArrayList<Fruit>();
		for (String f : game.getFruits()) 
		{
			Fruit currF = new Fruit(f);
			currF.setEdge(findFruitEdge(currF.getPos()));
			fruitsTemp.add(currF);
		}
		int robotKey=0;
		for (int i =0; i<numOfRobots;i++) 
		{
			if (fruitsTemp==null) 
			{
				robotKey = (int)(Math.random()*graph.getV().size());
				while (!game.getRobots().contains(robotKey) && !graph.getV().contains(robotKey)) 
				{
					game.addRobot(robotKey);
				}	
			}
			else 
			{
				edge_data fruitEdge = fruitsTemp.get(0).getEdge();
				//edge_data fruitEdge = findFruitEdge(fruitsTemp.get(0).getLocation());
				int fruitSrc=fruitEdge.getSrc();
				int fruitDest=fruitEdge.getDest();
				int robotLocation = 0;
				if (fruitsTemp.get(0).getType() == -1) 
				{
					robotLocation=Math.max(fruitDest, fruitSrc);
				} 
				else if (fruitsTemp.get(0).getType() == 1) 
				{
					robotLocation=Math.min(fruitDest, fruitSrc);
				}
				game.addRobot(robotLocation);
				fruitsTemp.remove(0);
			}
		}
		PaintRobots=true;
	}



	//find on which edge the fruit is on
	public edge_data findFruitEdge (Point3D fruitPoint) 
	{
		edge_data foundEdge = null;
		if(this.graph != null) 
		{
			if (this.graph.getV()!=null) 
			{
				for (node_data node: this.graph.getV()) 
				{
					if (this.graph.getE(node.getKey())!=null) 
					{
						for (edge_data edge : this.graph.getE(node.getKey())) 
						{
							Point3D src = this.graph.getNode(edge.getSrc()).getLocation();
							Point3D dest = this.graph.getNode(edge.getDest()).getLocation();
							double edgeDistance = Math.sqrt(Math.pow(src.x()-dest.x(), 2)+Math.pow(src.y()-dest.y(), 2));
							double fruitDistanceToPoints= Math.sqrt((Math.pow((src.x()-fruitPoint.x()), 2)+Math.pow((src.y()-fruitPoint.y()),2))) + Math.sqrt((Math.pow((fruitPoint.x()-dest.x()), 2)+Math.pow((fruitPoint.y()-dest.y()), 2)));
							if(Math.abs(fruitDistanceToPoints-edgeDistance)<0.0000001)
							{
								foundEdge=edge;
							}
						}

					}
				}
			}
		}
		if(foundEdge!=null)
		{
			return foundEdge;
		}
		else
		{
			return null;
		}


	}


	private double scale(double data, double r_min, double r_max, double t_min, double t_max) 
	{
		double res = ((data - r_min) / (r_max - r_min)) * (t_max - t_min) + t_min;
		return res;
	}


	public static double findMinX(Collection<node_data> nodes) 
	{
		for(node_data node : nodes) 
		{
			double temp = node.getLocation().x();
			if(temp<minX)
				minX=temp;
		}
		return minX;
	}
	public static double findMinY(Collection<node_data> nodes) 
	{
		for(node_data node : nodes) 
		{
			double temp = node.getLocation().y();
			if(temp<minY)
				minY=temp;
		}
		return minY;
	}
	public static double findMaxX(Collection<node_data> nodes) 
	{
		for(node_data node : nodes) 
		{
			double temp = node.getLocation().x();
			if(temp>maxX)
				maxX=temp;
		}
		return maxX;
	}
	public static double findMaxY(Collection<node_data> nodes) 
	{
		for(node_data node : nodes) 
		{
			double temp = node.getLocation().y();
			if(temp>maxY)
				maxY=temp;
		}
		return maxY;
	}

	public void moveRobots(game_service game , graph g) 
	{
		List<String> log = game.move();
		if(log!=null) 
		{
			for(int i=0;i<log.size();i++) 
			{
				String robot_json = log.get(i);
				try 
				{
					fr.clear();
					if(!game.getFruits().isEmpty())
					{
						for (String fruit : game.getFruits()) 
						{
							Fruit currFruit = new Fruit(fruit);
							currFruit.setEdge(findFruitEdge(currFruit.getPos()));
							fr.add(currFruit);
						}
					}

					JSONObject line = new JSONObject(robot_json);
					JSONObject obj = line.getJSONObject("Robot");
					int robotId = obj.getInt("id");
					int robotSrc = obj.getInt("src");
					int robotDest = obj.getInt("dest");
					if(robotDest==-1) 
					{
						try
						{
							robotDest = nextNode(robotSrc,game);

							game.chooseNextEdge(robotId, robotDest);
						}
						catch(Exception e)
						{
							throw new RuntimeException("Error in nextNode function");
						}


					}
				}
				catch (JSONException e) 
				{
					throw new RuntimeException("Error in moveRobots function");
				}
			}
		}
	}


	private int levelSleep(graph g)
	{
		fr= new ArrayList<Fruit>();
		if(!game.getFruits().isEmpty())
		{
			for (String fruit : game.getFruits()) 
			{
				Fruit currFruit = new Fruit(fruit);
				currFruit.setEdge(findFruitEdge(currFruit.getPos()));
				fr.add(currFruit);
			}
		}

		ArrayList<Robot> robotArrayList= new ArrayList<Robot>();
		if(!game.getRobots().isEmpty())
		{
			for (String rob : game.getRobots()) 
			{
				Robot currRob = new Robot(rob);
				robotArrayList.add(currRob);
			}
		}

		int ans = 100;
		for (Robot rob: robotArrayList)
		{
			for (String fruit: game.getFruits()) 
			{
				Fruit currf = new Fruit(fruit);
				edge_data temp = findFruitEdge(currf.getPos());
				if(temp.getSrc()==rob.getSrc() || temp.getDest()==rob.getSrc())
				{
					return 50;
				}
			}
		}
		return ans;
	}




	//game thread to update the gui during game
	@Override
	public void run() 
	{
		while (game!= null && game.isRunning()) 
		{
			try 
			{
				{
					if(manualMode==false)
					{
						moveRobots(MyGameGUI.game,this.graph);
						Thread.sleep(levelSleep(this.graph));
						repaint();


					}
					else if(manualMode==true)
					{
						game.move();
						repaint();
						Thread.sleep(1000);
					}
				}
			}
			catch (Exception e)
			{
				throw new RuntimeException("Exception in run time");
			}

		}
		new SimpleDB();
		String s = SimpleDB.printLogs(id, level);
		JOptionPane.showMessageDialog(null, s);
	}


	//Possibility to draw robots manually by user 
	private void addManualRobots()
	{
		for(int i=0; i<numOfRobots;i++)
		{
			int node = -1;
			try 
			{
				String userInput = JOptionPane.showInputDialog("enter src:");
				node = Integer.parseInt(userInput);
				if(node==-1|| this.graph.getNode(node) == null) 
				{
					JOptionPane.showMessageDialog(null,"invalid");
				}
				MyGameGUI.game.addRobot(node);
				PaintRobots = true;
			}
			catch (Exception e)
			{
				throw new RuntimeException("Invalid key");
			}
		}
		repaint();	

	}

	//draw timer on gui
	private void timer(Graphics g) 
	{
		g.drawString("TIME TO END: "+ (int)(game.timeToEnd()/1000), (int)minX+1500,(int)maxY+100);
	}




	private int nextNode(int src,game_service game) 
	{
		Graph_Algo graphAlgo=new Graph_Algo();
		int rid =-1;
		double spd=Integer.MAX_VALUE;
		graphAlgo.init(graph);
		for (String f:game.getFruits()) 
		{
			Fruit currFruit = new Fruit(f); 
			currFruit.setEdge(findFruitEdge(currFruit.getPos()));
			edge_data edge=currFruit.getEdge();
			double returnshortst = graphAlgo.shortestPathDist(src, edge.getDest());


			if (returnshortst < spd) 
			{
				try 
				{
					spd=returnshortst;
					if(graphAlgo.shortestPath(src, edge.getDest()).size()==1)
					{
						rid=edge.getSrc();
					}
					else
					{
						rid = graphAlgo.shortestPath(src, edge.getDest()).get(1).getKey();
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException("algo exception");
				}
			}
		}


		return rid;

	}


	public static int getLevel() 
	{
		return level;
	} 
	//listen to mouse clicks on manual game
	public void mouseClicked(MouseEvent clickPoint) 
	{
		Robot robot = null;
		boolean canMove=false;
		double clickPointY=clickPoint.getY();
		double clickPointX=clickPoint.getX();
		if(manualMode)
		{
			if(game.getRobots().size()==1)
			{
				Robot r = new Robot(game.getRobots().get(0));
				for (edge_data ed : graph.getE(r.getSrc())) 
				{
					double nodePointX = scale(graph.getNode(ed.getDest()).getLocation().x(), minX, maxX, 50, 1230);
					double nodePointY = scale(graph.getNode(ed.getDest()).getLocation().y(), minY, maxY,  80, 670);
					if (Math.abs(clickPointX - nodePointX) < 25 && Math.abs(clickPointY - nodePointY) < 25) 
					{
						game.chooseNextEdge(0, ed.getDest());
						canMove=true;
					}
				}
			}
			else 
			{
				if (!firstpress) 
				{
					for (int i = 0; i < game.getRobots().size(); i++) 
					{
						robot = new Robot(game.getRobots().get(i));
						double x = scale(graph.getNode(robot.getSrc()).getLocation().x(), minX, maxX, 50, 1230);
						double y = scale(graph.getNode(robot.getSrc()).getLocation().y(),  minY, maxY,  80, 670);
						if (Math.abs(x - clickPointX) < 35 && Math.abs(y - clickPointY) < 35) 
						{
							firstpress = true;
							break;
						}
					}
				}
				if (firstpress) 
				{
					for (edge_data edge : graph.getE(robot.getSrc())) 
					{
						double nodePointX = scale(graph.getNode(edge.getDest()).getLocation().x(), minX, maxX, 50, 1230);
						double nodePointY = scale(graph.getNode(edge.getDest()).getLocation().y(), minY, maxY, 80, 670);
						if (Math.abs(clickPointX - nodePointX) < 25 && Math.abs(clickPointY - nodePointY) < 25) 
						{
							game.chooseNextEdge(robot.getID(), edge.getDest());
							firstpress = false;
							canMove=true;
						}
					}
				}
			}
		}
		if(!canMove)
		{
			JOptionPane.showMessageDialog(null, "The choosen node is not a neighbor, it is not possible to move");
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub

	}


}