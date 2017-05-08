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

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author suren
 * @date 2017年5月7日 下午4:39:20
 */
public class NetUtilTest
{
	@Test
	public void list() throws SocketException, UnknownHostException
	{
		Map<String, String> allIP = NetUtil.allIP();
		
		Assert.assertNotNull(allIP);
	}
}
