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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openqa.grid.common.GridRole;
import org.suren.autotest.webdriver.downloader.Browser;
import org.suren.autotest.webdriver.downloader.BrowserVersionUtils;
import org.suren.autotest.webdriver.downloader.DriverInfo;

import com.surenpi.autotest.utils.StringUtils;
import com.surenpi.swing.SuRenComboBox;

/**
 * 服务配置主界面
 * @author suren
 * @date 2017年5月7日 上午10:15:51
 */
public class ServerUI extends JFrame
{

	/**  */
	private static final long	serialVersionUID	= 1L;

	private JPanel centerPanel;
	private JLabel loggerLabel;
	
	private Map<String, DriverInfo> driverInfoMap = new HashMap<String, DriverInfo>();
	
	private Map<String, JCheckBox> browserCheckMap = new HashMap<String, JCheckBox>();
	private Map<String, JComboBox<String>> browserVerMap = new HashMap<String, JComboBox<String>>();
	private JButton startBut;
	
	public ServerUI(ServerParam serverParam)
	{		
		setLayout(new BorderLayout());
		centerPanel = initCenter();
		this.add(centerPanel, BorderLayout.CENTER);
		
		if(StringUtils.isNotBlank(serverParam.chrome))
		{
		    browserCheckMap.get("chrome").setSelected(true);
		    
		    browserVerMap.get("chrome").setSelectedItem(serverParam.chrome);
		}
		
		if(serverParam.autoStart)
		{
		    startBut.doClick();
		}
		
		setTitle("PhoenixSeleniumServer");
		setSize(600, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * 初始化中央面板
	 */
	private JPanel initCenter()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//logger panel
		JPanel loggerPanel = new JPanel();
		loggerLabel = new JLabel();
		loggerPanel.add(loggerLabel);
		panel.add(loggerPanel, BorderLayout.SOUTH);
		
		//browser list
		JPanel browserListPanel = new JPanel();
		panel.add(browserListPanel, BorderLayout.WEST);
		browserListPanel.setLayout(new GridLayout(0, 2));
		Map<String, Set<String>> supportBrowser = AutoTestServer.driverMapping.supportBrowser();
		List<Browser> browserList = AutoTestServer.driverMapping.browserList();
		for(Browser browser : browserList)
		{
		    String type = browser.getName();
		    
			JCheckBox typeCheckBox = new JCheckBox(type);
			browserCheckMap.put(type, typeCheckBox);
			browserListPanel.add(typeCheckBox);
			driverInfoMap.put(type, new DriverInfo(type));
			typeCheckBox.addItemListener(new ItemListener()
			{
				
				@Override
				public void itemStateChanged(ItemEvent e)
				{
					switch(e.getStateChange())
					{
						case ItemEvent.SELECTED:
							driverInfoMap.get(type).setEnable(true);
							break;
						case ItemEvent.DESELECTED:
							driverInfoMap.get(type).setEnable(false);
							break;
					}
				}
			});

			SuRenComboBox<String> verCombox = new SuRenComboBox<String>();
			verCombox.addItem(null);
			browserVerMap.put(type, verCombox);
			Set<String> verList = supportBrowser.get(type);
			for(String ver : verList)
			{
				verCombox.addItem(ver);
			}
			browserListPanel.add(verCombox);
			verCombox.setName(type);
			verCombox.setData(browser);
			verCombox.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					@SuppressWarnings("unchecked")
                    JComboBox<String> combox = (JComboBox<String>) e.getSource();
					String type = combox.getName();
					String ver = combox.getSelectedItem().toString();
					
					driverInfoMap.get(type).setVersion(ver);
				}
			});
			
			//版本自动选择
            String typeVer = BrowserVersionUtils.getMajorVersion(type);
            if(StringUtils.isNotBlank(typeVer))
            {
                verCombox.setSelectedItem(typeVer);
            }
            else
            {
                typeVer = BrowserVersionUtils.getMajorVersion(browser.getAlias());
                if(StringUtils.isNotBlank(typeVer))
                {
                    verCombox.setSelectedItem(typeVer);
                }
            }
            
            if(verCombox.getSelectedIndex() != -1)
            {
                typeCheckBox.setSelected(true);
            }
		}
		
		//role list
		JPanel serverPanel = new JPanel();
		panel.add(serverPanel, BorderLayout.NORTH);
		final JComboBox<GridRole> gridRoleBox = new JComboBox<GridRole>();
		for(GridRole gridRole : GridRole.values())
		{
			gridRoleBox.addItem(gridRole);
		}
		serverPanel.add(gridRoleBox);
		
		startBut = new JButton("Start");
		startBut.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				if(source instanceof JComponent)
				{
					((JComponent) source).setEnabled(false);
				}
				gridRoleBox.setEnabled(false);
				
				AutoTestServer.startServer(driverInfoMap);
			}
		});
		serverPanel.add(startBut);
		
		JPanel serverInfo = new JPanel();
		panel.add(serverInfo, BorderLayout.CENTER);
		putNetworkInfo(serverInfo);
		
		return panel;
	}

	/**
	 * 网络信息面板
	 * @param serverInfo
	 */
	private void putNetworkInfo(JPanel serverInfo)
	{
		serverInfo.setLayout(new GridLayout(0, 2, 4, 0));
		Map<String, String> allIP = NetUtil.allIP();
		for(String name : allIP.keySet())
		{
			serverInfo.add(new JLabel(name));
			
			JTextField ipField = new JTextField(allIP.get(name));
			ipField.setEditable(false);
			ipField.addMouseListener(new MouseAdapter()
			{

				@Override
				public void mouseClicked(MouseEvent e)
				{
					Object source = e.getSource();
					if(source instanceof JTextField)
					{
						((JTextField) source).selectAll();
					}
				}
			});
			serverInfo.add(ipField);
		}
	}
}
