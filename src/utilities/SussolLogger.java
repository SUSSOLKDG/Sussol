package utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SussolLogger 
{
	private static SussolLogger instance = null;
	private Logger logger = null;
	   
    private SussolLogger() 
    {
    	logger = LoggerFactory.getLogger("Solvents");     
    }
	   
    public static SussolLogger getInstance() 
    {
       if (instance == null) 
       {
    	   instance = new SussolLogger();
       }
       return instance;
    }
	
    public void info(String message)	{logger.info(message);}	
    public void debug(String message)	{logger.debug(message);}
    public void warn(String message)	{logger.warn(message);}
    public void error(String message)	{logger.error(message);}
    
    public void closeLogger()
    {
    	logger = null;
    }
}
