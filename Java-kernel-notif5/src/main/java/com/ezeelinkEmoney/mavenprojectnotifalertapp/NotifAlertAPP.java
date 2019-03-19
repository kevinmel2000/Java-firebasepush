/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ezeelinkEmoney.mavenprojectnotifalertapp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
/**
 *
 * @author ezeelinkindonesia
 */
public class NotifAlertAPP 
{
     public static void main(String[] args) throws Exception 
     {
    	String cronExpStr = "0 */30 * ? * *";
 		JobKey jobKey = new JobKey("jobA", "group1");
 		JobDetail jobA = JobBuilder.newJob(QuartzCronScheduler.class).withIdentity(jobKey).build();
 		Trigger triger1 = TriggerBuilder.newTrigger()
 				.withIdentity("TriggerNameSatu", "groupSatu")
 				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpStr))
 				.build();
 		Scheduler scheduler = null;
 		try {
 			scheduler = new StdSchedulerFactory().getScheduler();
 			scheduler.start();
 			scheduler.scheduleJob(jobA, triger1);
 			System.out
             .println("Jadwal Pertama = " + triger1.getNextFireTime());
 			
 		}catch(SchedulerException e) {
 			try {
                 scheduler.shutdown();
                 System.out
                         .println("Job tidak akan pernah dieksekusi... shutdown scheduler");
             } catch (SchedulerException e1) {
                 e1.printStackTrace();
             }
 		} 
 
     }
    
}
