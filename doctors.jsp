<%@ include file="_top.jspf" %>
<%
if("doctor".equals(role)){response.sendRedirect("dashboard.jsp");return;}
String q=request.getParameter("q")==null?"":request.getParameter("q").trim().toLowerCase();
%>
<h2>Doctors</h2>
<form class="card" method="get"><input name="q" placeholder="Search by name or specialty" value="<%=Db.h(q)%>"><button>Search</button></form>
<%
int n=0;
for(Map<String,String> u:Db.t("users")){
 if(!"doctor".equals(u.get("role")))continue;
 if(!q.isEmpty()&&!(u.get("name")+" "+u.get("spec")).toLowerCase().contains(q))continue;
 n++;%>
<div class="card"><b><%=Db.h(u.get("name"))%></b> <span class="tag"><%=Db.h(u.get("spec").isEmpty()?"General":u.get("spec"))%></span>
<p class="mut sm">Consultation fee: Rs. <%=Db.h(u.get("fee"))%></p>
<% if("patient".equals(role)){%><a class="btn" href="book.jsp?doc=<%=u.get("id")%>">Book appointment</a><%}%></div>
<% }
if(n==0){%><p class="mut">No doctors found.</p><%}%>
<%@ include file="_bot.jspf" %>
