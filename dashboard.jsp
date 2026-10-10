<%@ include file="_top.jspf" %>
<%!
String st(String l,Object v){return "<div class='card st'><b>"+v+"</b>"+l+"</div>";}
%>
<h2>Hello, <%=Db.h(me.get("name"))%></h2>
<div class="grid">
<%
String id=me.get("id");
int pend=0,conf=0,done=0,mine=0;
for(Map<String,String> a:Db.t("appts")){
 if(!("admin".equals(role)||id.equals(a.get("pat"))||id.equals(a.get("doc"))))continue;
 mine++;
 String s=a.get("status");
 if("pending".equals(s))pend++;else if("confirmed".equals(s))conf++;else if("completed".equals(s))done++;
}
if("admin".equals(role)){
 int pc=0,dc=0;
 for(Map<String,String> u:Db.t("users")){if("patient".equals(u.get("role")))pc++;if("doctor".equals(u.get("role")))dc++;}
 out.print(st("Patients",pc)+st("Doctors",dc)+st("Appointments",mine)+st("Awaiting approval",pend));
}else if("doctor".equals(role)){
 out.print(st("Awaiting your reply",pend)+st("Confirmed",conf)+st("Completed",done));
}else{
 out.print(st("Upcoming",pend+conf)+st("Completed",done)+st("All visits",mine));
}
%>
</div>
<% if("patient".equals(role)){%><p><a class="btn" href="doctors.jsp">Book an appointment</a></p><%}%>
<%@ include file="_bot.jspf" %>
