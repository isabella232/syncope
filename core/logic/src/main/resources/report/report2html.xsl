<?xml version="1.0" encoding="UTF-8"?>
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:import href="userReportlet2html.xsl"/>
  <xsl:import href="groupReportlet2html.xsl"/>
  <xsl:import href="staticReportlet2html.xsl"/>
 
  <xsl:param name="status"/>
  <xsl:param name="message"/>
  <xsl:param name="startDate"/>
  <xsl:param name="endDate"/>
  
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
      <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <title>Apache Syncope Report - <xsl:value-of select="report/@name"/></title>
      </head>
      <body>
        <table style="border: 1px solid black;">
          <tr>
            <td>
              <h1>Report Name:</h1>
            </td>
            <td>
              <h1>
                <xsl:value-of select="report/@name"/>
              </h1>
            </td>
          </tr>
          <tr>
            <td>
              <h2>Start Date:</h2>
            </td>
            <td>
              <h2>
                <xsl:value-of select="$startDate"/>
              </h2>
            </td>
          </tr>
          <tr>
            <td>
              <h2>End Date:</h2>
            </td>
            <td>
              <h2>
                <xsl:value-of select="$endDate"/>
              </h2>
            </td>
          </tr>
        </table>

        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
