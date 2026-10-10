<%@ include file="_top.jspf" %>
<%!
String f(String id,String act,String label,String cls){
 return "<form method='post'><input type='hidden' name='id' value='"+id+"'><input type='hidden' name='act' value='"+act+"'><button class='"+cls+"'>"+label+"</button></form>";
}
%>
<%
boolean adm="admin".equals(role);
if("POST".equals(request.getMethod())){
 Map<String,String> a=Db.find("appts","id",request.getParameter("id"));
 String act=String.valueOf(request.getParameter("act"));
 if(a!=null){
  String s=a.get("status");
  boolean isP=me.get("id").equals(a.get("pat")),isD=me.get("id").equals(a.get("doc"));
  if("cancel".equals(act)&&(isP||adm)&&("pending".equals(s)||"confirmed".equals(s)))a.put("status","cancelled");
  else if((isD||adm)&&"pending".equals(s)&&"confirm".equals(act))a.put("status","confirmed");
  else if((isD||adm)&&"pending".equals(s)&&"reject".equals(act))a.put("status","rejected");
  else if((isD||adm)&&"confirmed".equals(s)&&"complete".equals(act)){a.put("status","completed");a.put("note",String.valueOf(request.getParameter("note")).trim());}
  Db.save();
 }
 response.sendRedirect("appointments.jsp");return;
}
List<Map<String,String>> list=new ArrayList<>();
for(Map<String,String> a:Db.t("appts"))
 if(adm||me.get("id").equals(a.get("pat"))||me.get("id").equals(a.get("doc")))list.add(a);
list.sort((x,y)->(y.get("date")+y.get("time")).compareTo(x.get("date")+x.get("time")));
%>
<h2><%="patient".equals(role)?"My visits":"Appointments"%></h2>
<% if("patient".equals(role)){%><p><a class="btn" href="doctors.jsp">Book new</a></p><%}
for(Map<String,String> a:list){
 LocalDate d=LocalDate.parse(a.get("date"));
 String s=a.get("status");
 String title=adm?Db.h(Db.name(a.get("pat")))+" with "+Db.h(Db.name(a.get("doc"))):"doctor".equals(role)?Db.h(Db.name(a.get("pat"))):Db.h(Db.name(a.get("doc")));
%>
<div class="card ap"><div class="dt"><b><%=d.getDayOfMonth()%></b><%=d.getMonth().toString().substring(0,3)%></div>
<div class="ib"><b><%=title%></b> <span class="tag <%=s%>"><%=s%></span>
<p class="mut sm"><%=a.get("time")%>. Reason: <%=Db.h(a.get("why"))%></p>
<% if(!a.get("note").isEmpty()){%><p class="sm">Doctor's note: <%=Db.h(a.get("note"))%></p><%}%>
<div class="acts">
<% if(("patient".equals(role)||adm)&&("pending".equals(s)||"confirmed".equals(s)))out.print(f(a.get("id"),"cancel","Cancel","d"));
if(("doctor".equals(role)||adm)&&"pending".equals(s))out.print(f(a.get("id"),"confirm","Confirm","")+f(a.get("id"),"reject","Reject","d"));
if(("doctor".equals(role)||adm)&&"confirmed".equals(s)){%>
<form method="post"><input type="hidden" name="id" value="<%=a.get("id")%>"><input type="hidden" name="act" value="complete"><input name="note" placeholder="Note or prescription (optional)"><button>Mark completed</button></form>
<%}%></div></div></div>
<% }
if(list.isEmpty()){%><p class="mut">Nothing here yet.</p><%}%>
<%@ include file="_bot.jspf" %>
