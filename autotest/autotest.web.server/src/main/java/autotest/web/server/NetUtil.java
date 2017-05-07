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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

/**
 * @author suren
 * @date 2017年5月7日 下午4:33:43
 */
public class NetUtil
{
	public static void list() throws SocketException, UnknownHostException
	{
		Enumeration<NetworkInterface> inters = NetworkInterface.getNetworkInterfaces();
		while(inters.hasMoreElements())
		{
			NetworkInterface inter = inters.nextElement();
			if(inter.isLoopback() || inter.isVirtual() || inter.isPointToPoint())
			{
				continue;
			}
			
			System.out.println(inter);
		}
		
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}
}
