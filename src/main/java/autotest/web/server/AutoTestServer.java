/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package autotest.web.server;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.grid.selenium.GridLauncherV3;
import org.suren.autotest.webdriver.downloader.DriverDownloader;
import org.suren.autotest.webdriver.downloader.DriverInfo;
import org.suren.autotest.webdriver.downloader.DriverMapping;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;

/**
 * 自动化测试远程服务器
 * @author <a href="http://surenpi.com">suren</a>
 */
public class AutoTestServer
{
    public static final DriverMapping driverMapping = new DriverMapping();

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	    ServerParam serverParam = new ServerParam();
	    Builder builder = JCommander.newBuilder();
	    JCommander commander = builder.addObject(serverParam).build();
	 
	    if(args != null)
	    {
	        commander.parse(args);
	    }
	    
	    if(serverParam.help)
	    {
	        commander.usage();
	        System.exit(0);
	    }
	    
        driverMapping.init();
        
        if(serverParam.autoStart)
        {
            Map<String, DriverInfo> driverInfoMap = new HashMap<String, DriverInfo>();
            DriverInfo driverInfo = new DriverInfo();
            driverInfo.setEnable(true);
            driverInfo.setName("chrome");
            driverInfo.setVersion(serverParam.chrome);
            driverInfoMap.put("", driverInfo);
            
            AutoTestServer.startServer(driverInfoMap);
        }
        else
        {
            new ServerUI(serverParam);
        }
	}
	
	public static void startServer(Map<String, DriverInfo> driverInfoMap)
	{

        Map<String, String> driverMap = driverMapping.driverMap();
        
        for(DriverInfo driverInfo : driverInfoMap.values())
        {
            if(!driverInfo.isEnable())
            {
                continue;
            }
            
            String name = driverInfo.getName();
            String ver = driverInfo.getVersion();
            
            String url = driverMapping.getUrl(name, ver);
            try
            {
                String localPath = new DriverDownloader().getLocalFilePath(new URL(url));
                
                String driver = driverMap.get(name);
                System.out.println("driver:" + driver + "; path:" + localPath);
                System.getProperties().put(driver, localPath);
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        
        try
        {
            GridLauncherV3.main(new String[]{});
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
	}

}
