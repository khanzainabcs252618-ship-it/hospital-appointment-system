<%@ include file="_top.jspf" %>
<%
String note=null;
if("POST".equals(request.getMethod())){
 String n=String.valueOf(request.getParameter("name")).trim(),p=String.valueOf(request.getParameter("pass"));
 if(n.isEmpty()||(!p.isEmpty()&&p.length()<6))note="Name is required and the password needs 6+ characters.";
 else{
  me.put("name",n);me.put("phone",String.valueOf(request.getParameter("phone")).trim());
  if(!p.isEmpty())me.put("pass",p);
  if("doctor".equals(role)){me.put("spec",String.valueOf(request.getParameter("spec")).trim());me.put("fee",""+Db.num(request.getParameter("fee"),300));}
  Db.save();note="Profile updated.";
 }
}
%>
<h2>Profile</h2>
<form class="card" method="post"><p class="mut"><%=Db.h(me.get("email"))%>, <%=role%></p>
<% if(note!=null){%><p><%=note%></p><%}%>
<input name="name" value="<%=Db.h(me.get("name"))%>" required><input name="phone" value="<%=Db.h(me.get("phone"))%>" placeholder="Phone">
<% if("doctor".equals(role)){%><input name="spec" value="<%=Db.h(me.get("spec"))%>" placeholder="Specialty"><input name="fee" type="number" min="0" value="<%=Db.h(me.get("fee"))%>" placeholder="Consultation fee"><%}%>
<input name="pass" type="password" placeholder="New password (leave blank to keep)"><button>Save changes</button></form>
<%@ include file="_bot.jspf" %>
