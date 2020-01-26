package gameClient;

import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Icon;


import javax.swing.*;
import java.io.File;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import Server.game_service;


public class KML_Logger 
{
	public static ArrayList<Robot> robots;
	public static ArrayList<Fruit> fruits;

	public static Document doc;
	public static int c;
	public static MyGameGUI myGameGUI;
	

    
    
    /**
     * splits an array to string;
     * @param arr
     * @return
     */
	
    private static String splitArr(String[] arr)
    {
        String temp= arr[0] + "T" + arr[1] + "Z";
        return temp;
    }

    
    
    /**
     * KML parser, runs while the game is running
     * first time the function runs it creates a file
     * than constantly saves the data to the existing file
     * @param mg
     * @param i
     * @throws ParseException
     * @throws InterruptedException
     */
	public static void createKML(game_service game) throws ParseException, IOException, InterruptedException 
	{
		Kml kmldoc = new Kml();
		doc = kmldoc.createAndSetDocument();
		c = 0;
		MyGameGUI tempGUI = new MyGameGUI();
			
		if(game!=null)
		{
		
			while(game.isRunning())
			{
				Thread.sleep(200);
				c++;
				robots = new ArrayList<Robot>();
				fruits = new ArrayList<Fruit>();
				if(!game.getFruits().isEmpty())
				{
					for(String fruit: game.getFruits())
					{
						Fruit currFruit = new Fruit(fruit);
						currFruit.setEdge(tempGUI.findFruitEdge(currFruit.getPos()));
						fruits.add(currFruit);	
					}
				}
				if(!game.getRobots().isEmpty())
				{
					for(String robot: game.getRobots())
					{
						Robot currRobot = new Robot(robot);
						robots.add(currRobot);	
					}
				}
				kmlRobots(robots,doc,kmldoc);
				kmlFruits(fruits,doc,kmldoc);
					
			}
			
	        try
	        {
	            int s = JOptionPane.showConfirmDialog(null,"GameOver, Do you want to save the game to KML?","Please choose Yes/No",JOptionPane.YES_NO_OPTION);
	            if(s==0)
	            {
	            	File f= new File("kmlFile" + MyGameGUI.getLevel()+".kml");
	            	kmldoc.marshal(f);
	                String kmlString =  new String(Files.readAllBytes(Paths.get(f.getName())));
	                game.sendKML(kmlString);
	            }	            	
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
		}
	}
	
	public static void kmlRobots(ArrayList<Robot> robotArraym,Document doc1,Kml kmldoc) throws ParseException
	{
		for(Robot robot: robots)
		{
			  Placemark rob_mark  = doc1.createAndAddPlacemark();
			  Icon rob_icon = new Icon();
			  rob_icon.setHref("https://zdnet1.cbsistatic.com/hub/i/r/2017/03/16/8cf51dbb-c4ff-4205-a4b9-d179dd8186b3/resize/770xauto/64baad4d33ff2073e3b6c0cc45b1740d/robot-boxer-boxing.jpg");
			  rob_icon.setViewBoundScale(1);
              rob_icon.setViewRefreshTime(1);
              rob_icon.withRefreshInterval(1);
			  IconStyle rob_style = new IconStyle();
              rob_style.setScale(1);
              rob_style.setHeading(1);
              rob_style.setIcon(rob_icon);
              rob_mark.createAndAddStyle().setIconStyle(rob_style);
              rob_mark.withDescription("\nType: Robot").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(robot.getPos().x(),robot.getPos().y());
              String rob_t1 = Millis2Date(Date2Millis(TimeNow())+c*1000);
              String rob_t2 = Millis2Date(Date2Millis(TimeNow())+(c +1)*1000);
              String[] timeArr =rob_t1.split(" ");
              rob_t1=splitArr(timeArr);
              String[] timeArr2 = rob_t2.split(" ");
              rob_t2=splitArr(timeArr2);
              TimeSpan rob_span = rob_mark.createAndSetTimeSpan();
              rob_span.setBegin(rob_t1);
              rob_span.setEnd(rob_t2);  
		}
	}
	
	public static void kmlFruits(ArrayList<Fruit> fruitArray,Document doc1,Kml kmldoc) throws ParseException
	{
		for(Fruit fruit: fruitArray)
		{
			Placemark f_mark = doc1.createAndAddPlacemark();
            Icon f_icon = new Icon();
            //banana
            if(fruit.getType()==1)
            {
            	f_icon.setHref("http://www.generationy.com/wp-content/uploads/2014/01/banana.jpg");
            }
            //apple
            else
            {
            	f_icon.setHref("https://vq.vassar.edu/issues/2012/03/images/apple.jpg");
            }
            f_icon.setViewBoundScale(1);
            f_icon.setViewRefreshTime(1);
            f_icon.withRefreshInterval(1);
            IconStyle f_style = new IconStyle();
            f_style.setScale(1);
            f_style.setHeading(1);
            f_style.setColor("ff007db3");
            f_style.setIcon(f_icon);
            f_mark.createAndAddStyle().setIconStyle(f_style);
            f_mark.withDescription("\nType: Fruit").withOpen(Boolean.TRUE).createAndSetPoint().addToCoordinates(fruit.getPos().x(),fruit.getPos().y());
            String f_t1 = Millis2Date(Date2Millis(TimeNow())+c*1000);
            String f_t2 = Millis2Date(Date2Millis(TimeNow())+(c+1)*1000);
            String[] fruitArr= f_t1.split(" ");
            f_t1=splitArr(fruitArr);
            String[] fruitArr2 = f_t2.split(" ");
            f_t2=splitArr(fruitArr2);
            TimeSpan f_span = f_mark.createAndSetTimeSpan();
            f_span.setBegin(f_t1);
            f_span.setEnd(f_t2);
		}
	}
	

	/**
	 * This function converse a String date to milliseconds.
	 * @throws ParseException an exception while parsing.
	 * @param date represent the date.
	 * @return the date in milliseconds.
	 */
	public static long Date2Millis (String date) throws ParseException 
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date time = format.parse(date.toString());
		long millis = time.getTime();
		return millis;
	}

	/**
	 * This function converse milliseconds to a String date.
	 * @param millis represent the date in millisecond.
	 * @return the date.
	 */
	public static String Millis2Date(long millis) 
	{
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return date.format(new Date(millis));
	}
	
	private static String TimeNow()
	{
	    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}