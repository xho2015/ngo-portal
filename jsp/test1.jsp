<%@ page contentType="text/html; charset=gb2312" %> 
<%@ page language="java" %> 
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="javax.naming.*" %>
<%@ page import="javax.sql.DataSource" %>
<% 

String driverName="com.mysql.jdbc.Driver"; 

String userName="root"; 

String userPasswd="Qwer!234l"; 

String dbName="ngodev"; 

String tableName="bom"; 

//String url="jdbc:mysql://localhost:3306/"+dbName+"?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&user="+userName+"&password="+userPasswd; 
//Class.forName("com.mysql.jdbc.Driver").newInstance(); 
//Connection connection=DriverManager.getConnection(url); 

Context ctx = new InitialContext();
DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/NGODB");
Connection connection = ds.getConnection();

Statement statement = connection.createStatement(); 
String sql="SELECT * FROM "+tableName; 
ResultSet rs = statement.executeQuery(sql); 

ResultSetMetaData rmeta = rs.getMetaData(); 

int numColumns=rmeta.getColumnCount(); 
out.print("timestamp="+new java.util.Date()); 
out.print("id"); 
out.print("|"); 
out.print("num"); 
out.print("<br>"); 
while(rs.next()) { 
out.print(rs.getString(2)+" "); 
out.print("|"); 
out.print(rs.getString(3)); 
out.print("<br>"); 
} 
out.print("<br>"); 
out.print("ok"); 
rs.close(); 
statement.close(); 
connection.close(); 
%> 