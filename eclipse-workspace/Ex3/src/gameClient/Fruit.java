package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;


public class Fruit {
	double value;
	int type;
	Point3D pos;
	int src;
	int dest;



	public Fruit(double value, int y, Point3D pos) {
		this.value=value;
		this.pos=pos;
		this.type=y;
	}
	
	public Fruit() {

	}
	
	public Fruit(String json) {
		try {
		JSONObject fruit = new JSONObject(json);
	    JSONObject Fruit = fruit.getJSONObject("Fruit");
		String loc = Fruit.getString("pos");
		String[] xyz = loc.split(",");
		double x = Double.parseDouble(xyz[0]);
		double y = Double.parseDouble(xyz[1]);
		double z = Double.parseDouble(xyz[2]);
		this.pos = new Point3D(x,y,z);
		this.value = Fruit.getDouble("value");
			this.type = Fruit.getInt("type");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getType() { 
		return this.type; 
	}
	
	public void setPos(Point3D p) { 
		this.pos = p;
	}
	
	public Point3D getPos() { 
		return this.pos;
	}
	
	public void setV(double v) {
		this.value=v; 
	}
	
	public double getV() { 
		return this.value;
	}
	
	public void setSrc(int src) {
		this.src=src;
	}

	public int getSrc() {
		return this.src;
	}
	
	public void setDest(int dest) {
		this.dest=dest;
	}
	
	 public int getDest() {
		 return this.dest;
	 }

}