/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xpath.xs.types;

import java.util.Calendar;

/**
 * Base class for all calendar based classes.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public abstract class XSCalendarType extends XSCtrType {

	public Calendar normalizeCalendar(Calendar cal, XSDuration timezone) {
		Calendar adjusted = (Calendar)cal.clone();
		
		if (timezone != null) {
			int hours = timezone.hours();
			int minutes = timezone.minutes();
			if (!timezone.negative()) {
				hours *= -1;
				minutes *= -1;
			}
			adjusted.add(Calendar.HOUR_OF_DAY, hours);
			adjusted.add(Calendar.MINUTE, minutes);
		}
		
		return adjusted;
		
	}
	
}
