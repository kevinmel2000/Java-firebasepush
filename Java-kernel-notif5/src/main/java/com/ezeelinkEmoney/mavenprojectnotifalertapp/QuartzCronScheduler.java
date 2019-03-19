package com.ezeelinkEmoney.mavenprojectnotifalertapp;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuartzCronScheduler implements Job{
	String jdbcClassName="com.ibm.db2.jcc.DB2Driver";
    String url="jdbc:db2://172.18.90.149:50000/EZEELINK";
    String user="ezeelink";
    String password="ezeelink";
    
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		System.out.println(">>>>> di run pada "+context.getFireTime());
		
		try {
			kernelNotif();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(context.getNextFireTime() == null) {
			try {
				System.out.println("job never executed");
				context.getScheduler().shutdown();
			}catch(SchedulerException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void kernelNotif() throws Exception {
		Connection connection = null;
        try 
        {
            //Load class into memory
            Class.forName(jdbcClassName);
            //Establish connection
            connection = DriverManager.getConnection(url, user, password);
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT TRANDATE FROM PUBSYSCTRLINFO");
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
           
            LocalDateTime now = LocalDateTime.now();
            String Datenow=dtf.format(now).toString();

            LocalDate localDate = LocalDate.now();
            //System.out.println(DateTimeFormatter.ofPattern("yyy/MM/dd").format(localDate));

            if(rs.next())
            {
                String datesystem=rs.getString("TRANDATE");
                System.out.println(datesystem);  
                
                if(!Datenow.equals(datesystem))
                {
                	FireMessage f = new FireMessage("Notif DataBase", "Notif DataBase Date Not Sync");
                    String topic="weather";
                    f.sendToTopic(topic);
                }
                
            }
            else
            {
                System.out.println("DATABASE PUBSYSCTRLINFO ERROR");
                FireMessage f = new FireMessage("Notif System", "Notif System Error Not Found Data");
                String topic="weather";
                f.sendToTopic(topic); 
            }
            
        }
        catch (ClassNotFoundException e) 
        {
            System.out.println("DATABASE PUBSYSCTRLINFO ERROR");
            FireMessage f = new FireMessage("Notif System", "Notif System Error Not Found");
            String topic="weather";
            f.sendToTopic(topic); 
            e.printStackTrace();
        } 
        catch (SQLException e) 
        {
            System.out.println("DATABASE PUBSYSCTRLINFO ERROR");
            FireMessage f = new FireMessage("Notif System", "Notif System Error SQLException");
            String topic="weather";
            f.sendToTopic(topic); 
            e.printStackTrace();
        }
        
        finally
        {
            if(connection!=null)
            {
                System.out.println("Connected successfully.");
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}

	
}
