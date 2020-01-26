package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;


public class Robot {
	int src; 
	int dest;
	int id; 
	double value=0; 
	Point3D pos; 
	int speed;

	public Robot(int rid, int src, int dest,Point3D pos,double value,int s) {
		this.id=rid;
		this.src=src;
		this.dest=dest;
		this.value=value;
		this.pos=pos;
		this.speed=s;
	}

	public Robot() {

	}

	public Robot(String json) {
		JSONObject robot;
		try {
			robot = new JSONObject(json);
			JSONObject newRobot = robot.getJSONObject("Robot");
			this.id = newRobot.getInt("id");
			this.src = newRobot.getInt("src");
			this.dest = newRobot.getInt("dest");
			this.speed = newRobot.getInt("speed");
			this.pos = new Point3D(newRobot.getString("pos"));
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getSpeed() { 
		return this.speed;
	}

	public double getV() {
		return this.value; 
	}
	public void setV(double v) { 

		this.value=v; 
	}
	public int getID() {
		return this.id; }
	public void setSrc(int s) { 
		this.src=s; 
	}

	public int getSrc() { 
		return this.src;
	}

	public int getDest() { 
		return this.dest; 
	}

	public void setDest(int d) { 
		this.dest=d;
	}

	public Point3D getPos() {
		return this.pos; 
	}

	public void setPos(Point3D p) { 
		this.pos= p;
	}
	
	

}