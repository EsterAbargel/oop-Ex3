package gameClient;
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Range;
/**
 * This class represents a simple example of using MySQL Data-Base.
 * Use this example for writing solution. 
 * @author boaz.benmoshe
 *
 */
public class SimpleDB 
{
	public static final String jdbcUrl="jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser="student";
	public static final String jdbcUserPassword="OOP2020student";
	public static int numGames;
	public static int myLevel;
	public static int[][] comparedToClass;
	public static int [] maxGrade;
	private static HashMap <Integer,Integer[]> passRate= new HashMap<>();

	/** 
	 * init the position of the player compare to other players in class.
	 * init the maximum grade. 
	 * init the passing rate.
	 */
	public SimpleDB() 
	{
		comparedToClass= new int[][] {{0,1,3,5,9,11,13,16,19,20,23},{0,0,0,0,0,0,0,0,0,0,0}};
		maxGrade = new int [24];
		for (int i = 0; i< maxGrade.length; i++) 
		{
			maxGrade[i] = 0;
		}
		passRate.put(0,new Integer[]{125,290});
		passRate.put(1,new Integer[] {436,580});
		passRate.put(3,new Integer[] {713,580});
		passRate.put(5,new Integer[] {570,500});
		passRate.put(9,new Integer[] {480,580});
		passRate.put(11,new Integer[] {1050,580});
		passRate.put(13,new Integer[] {310,580});
		passRate.put(16,new Integer[] {235,290});
		passRate.put(19,new Integer[] {250,580});
		passRate.put(20,new Integer[] {200,290});
		passRate.put(23,new Integer[] {1000,1140});
	}
	/**
	 * Simple main for demonstrating the use of the Data-base
	 * @param args
	 */
	public static void main(String[] args)
	{
			int id1 = 313465114;  // "real" existing ID & KML
			int id2 = 208845628;
			int level = 0;//1,2,3
			new SimpleDB();
			//System.out.println(s);
			printLog(id2);
			//allUsers();	
			//String kml1 = getKML(id1,level);
			System.out.println("***** KML1 file example: ******");
			//System.out.println(kml1);
		}
	/** simply prints all the games as played by the users (in the database).
	 * 
	 */
		public static void printLog(int id) 
		{
			try 
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = 
						DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
				Statement statement = connection.createStatement();
				String allCustomersQuery = "SELECT * FROM Logs where userID="+id;
			
				ResultSet resultSet = statement.executeQuery(allCustomersQuery);
				int ind =0;
				while(resultSet.next())
				{
					System.out.println(ind+") Id: " + resultSet.getInt("UserID")+", level: "+resultSet.getInt("levelID")+", score: "+resultSet.getInt("score")+", moves: "+resultSet.getInt("moves")+", time: "+resultSet.getDate("time"));
					ind++;
				}
				resultSet.close();
				statement.close();		
				connection.close();		
			}
			
			catch (SQLException sqle) {
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("Vendor Error: " + sqle.getErrorCode());
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	/**
	 * this function returns the KML string as stored in the database (userID, level);
	 * @param id
	 * @param level
	 * @return
	 */
			public static String getKML(int id, int level) 
			{
				String ans = null;
				String allCustomersQuery = "SELECT * FROM Users where userID="+id+";";
				try 
				{
					Class.forName("com.mysql.jdbc.Driver");
					Connection connection = 
					DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);		
					Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(allCustomersQuery);
					if(resultSet!=null && resultSet.next()) 
					{
						ans = resultSet.getString("kml_"+level);
					}
				}
				catch (SQLException sqle) 
				{
					System.out.println("SQLException: " + sqle.getMessage());
					System.out.println("Vendor Error: " + sqle.getErrorCode());
				}
				
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				}
				return ans;
			}
		
		public static void getPosInClass(int id)
		{
			int position;
			String scoreByLevel;
			ArrayList <Integer>ids= new ArrayList<>();
			int currLevel, scorePass, numOfMovePass;
			ResultSet resultSet1;
			
			scoreByLevel="SELECT * FROM Logs where levelID="+0+" order by score DESC;";
			try 
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = 
						DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);	
				Statement statement = connection.createStatement();
				resultSet1=statement.executeQuery(scoreByLevel);

				for(int i=0;i<comparedToClass[0].length;i++) 
				{
					currLevel=comparedToClass[0][i];
					position =1;
					scoreByLevel = "SELECT * FROM Logs where levelID="+currLevel+" order by score DESC;";
					Class.forName("com.mysql.jdbc.Driver");
					resultSet1 = statement.executeQuery(scoreByLevel);
					scorePass = passRate.get(currLevel)[0];
					numOfMovePass = passRate.get(currLevel)[1];
					
					while(resultSet1.next() && resultSet1.getInt("score") > maxGrade[currLevel]) 
					{
						if(!ids.contains(resultSet1.getInt("UserID")))
						{
							if(!passRate.containsKey(currLevel) || scorePass <= resultSet1.getInt("score") && numOfMovePass>=resultSet1.getInt("moves"))
							{
								position++;
								
							}
							ids.add(resultSet1.getInt("UserID"));
						}
						
					}
					comparedToClass[1][i]=position;
				}
				resultSet1.close();
				statement.close();		
				connection.close();	

			}
			catch (SQLException sqlErr) 
			{
				System.out.println("SQLException: " + sqlErr.getMessage());
				System.out.println("Vendor Error: " + sqlErr.getErrorCode());
			}
			
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
		
		public static void getStatistics(int id)
		{
			numGames=0;
			myLevel=0;
			//maxGrade=new int[24];
			//inititializePassRate();
			boolean isLevelPassed=true;
			//for (int level : maxGrade) 
				//maxGrade[level]=0;
			String specificUserIdResults = "SELECT * FROM Logs where UserID="+id+";";
			try 
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);	
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(specificUserIdResults);
				while(resultSet.next()) 
				{
					int levelOnServer=resultSet.getInt("levelID");
					if(passRate.containsKey(levelOnServer))
					{
					int levelMinGrade=passRate.get(levelOnServer)[0];
					int levelMaxMoves=passRate.get(levelOnServer)[1];
					int myLevelMoves=resultSet.getInt("moves");
					int myLevelGrade=resultSet.getInt("score");
					if(levelMinGrade>myLevelGrade||levelMaxMoves<myLevelMoves)
					{
						isLevelPassed=false;
					}
					if(levelOnServer>myLevel && isLevelPassed)
					{
						myLevel=levelOnServer;
					}
					if(levelOnServer<=myLevel && maxGrade[levelOnServer]<myLevelGrade && isLevelPassed)
					{
						maxGrade[levelOnServer]=myLevelGrade;
					}
					numGames++;
					isLevelPassed=true;
					}
				}
				resultSet.close();
				statement.close();		
				connection.close();	
			}
			catch (SQLException sqle) 
			{
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("Vendor Error: " + sqle.getErrorCode());
			}

			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}


		}
			
		public static int allUsers() 
		{
			int ans = 0;
			String allCustomersQuery = "SELECT * FROM Users;";
			try 
			{
				Class.forName("com.mysql.jdbc.Driver");
				Connection connection = 
						DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);		
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(allCustomersQuery);
				while(resultSet.next()) 
				{
					System.out.println("Id: " + resultSet.getInt("UserID")+", max_level:"+resultSet.getInt("levelNum"));
					ans++;
				}
				resultSet.close();
				statement.close();		
				connection.close();
			}
			catch (SQLException sqle) 
			{
				System.out.println("SQLException: " + sqle.getMessage());
				System.out.println("Vendor Error: " + sqle.getErrorCode());
			}
			
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
			return ans;
		}
		public static String printLogs(int id , int level) 
		{
			StringBuilder ans= new StringBuilder();
			ans.append("Logs's game: ").append("\n");
			ans.append("User id: ").append(id).append("\n");
			ans.append("Level ").append(level).append("\n");
			getStatistics(id);
			ans.append("Your max grade for this level: ").append(maxGrade[level]).append("\n");
			ans.append("number of game that been played:  ").append(numGames).append("\n");
			ans.append("You location Compared to other players: ");
			getPosInClass(id);
			ans.append(comparedToClass[1][level]).append("\n");
			 
			return ans.toString();
		}
	}