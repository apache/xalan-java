/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.w3c.xpath3.op;

import org.apache.xalan.tests.w3c.xpath3.W3CXPath3TestsUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Xalan-J XSL 3 test driver, to run W3C XPath 3.1 test cases
 * for XPath 3.1 date, time & duration types.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPath3OpTestsThree extends W3CXPath3TestsUtil {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    	
    	m_xsl_test_set_base_dir = W3C_XPATH3_TESTS_META_DATA_DIR_HOME + "op/";
    	
        m_test_set_fileArr = new String[] { "date-equal.xml", "date-greater-than.xml", "date-less-than.xml", "dateTime-equal.xml", "dateTime-greater-than.xml", 
        		                            "dateTime-less-than.xml", "dayTimeDuration-greater-than.xml", "dayTimeDuration-less-than.xml", "divide-dayTimeDuration.xml", 
        		                            "divide-dayTimeDuration-by-dayTimeDuration.xml", "divide-yearMonthDuration.xml", "divide-yearMonthDuration-by-yearMonthDuration.xml",
        		                            "duration-equal.xml", "multiply-dayTimeDuration.xml", "multiply-yearMonthDuration.xml", "subtract-dates.xml",
        		                            "subtract-dateTimes.xml", "subtract-dayTimeDuration-from-date.xml", "subtract-dayTimeDuration-from-dateTime.xml",
        		                            "subtract-dayTimeDuration-from-time.xml", "subtract-dayTimeDurations.xml", "subtract-times.xml", "subtract-yearMonthDuration-from-date.xml",
        		                            "subtract-yearMonthDuration-from-dateTime.xml", "subtract-yearMonthDurations.xml", "time-equal.xml", "time-greater-than.xml",
        		                            "time-less-than.xml", "yearMonthDuration-greater-than.xml", "yearMonthDuration-less-than.xml" };
        
        m_resultSubFolderName = "op";
        
        m_test_set_result_fileArr = new String[] { "date-equal_result.xml", "date-greater-than_result.xml", "date-less-than_result.xml", "dateTime-equal_result.xml", "dateTime-greater-than_result.xml", 
                                                   "dateTime-less-than_result.xml", "dayTimeDuration-greater-than_result.xml", "dayTimeDuration-less-than_result.xml", "divide-dayTimeDuration_result.xml", 
                                                   "divide-dayTimeDuration-by-dayTimeDuration_result.xml", "divide-yearMonthDuration_result.xml", "divide-yearMonthDuration-by-yearMonthDuration_result.xml",
                                                   "duration-equal_result.xml", "multiply-dayTimeDuration_result.xml", "multiply-yearMonthDuration_result.xml", "subtract-dates_result.xml",
                                                   "subtract-dateTimes_result.xml", "subtract-dayTimeDuration-from-date_result.xml", "subtract-dayTimeDuration-from-dateTime_result.xml",
                                                   "subtract-dayTimeDuration-from-time_result.xml", "subtract-dayTimeDurations_result.xml", "subtract-times_result.xml", "subtract-yearMonthDuration-from-date_result.xml",
                                                   "subtract-yearMonthDuration-from-dateTime_result.xml", "subtract-yearMonthDurations_result.xml", "time-equal_result.xml", "time-greater-than_result.xml",
                                                   "time-less-than_result.xml", "yearMonthDuration-greater-than_result.xml", "yearMonthDuration-less-than_result.xml" };
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	m_xmlDocumentBuilderFactory = null;
        m_xmlDocumentBuilder = null;
        
        m_skipped_tests_list.clear();
    }
    
    // This set of XSL test cases, take little long to run,
    // due to large collection of XSL test case.

    @Test
    public void runXslOpTests() {
    	runXPathTestSetCollectionAndProduceResult();		
    }

}
