<%@ include file="_top.jspf" %>
<%
if(!"patient".equals(role)){response.sendRedirect("dashboard.jsp");return;}
Map<String,String> doc=Db.find("users","id",request.getParameter("doc"));
if(doc==null||!"doctor".equals(doc.get("role"))){response.sendRedirect("doctors.jsp");return;}
String[] slots={"09:00","10:00","11:00","12:00","14:00","15:00","16:00","17:00"};
String err=null;
if("POST".equals(request.getMethod())){
 String date=String.valueOf(request.getParameter("date")),time=String.valueOf(request.getParameter("time")),why=String.valueOf(request.getParameter("why")).trim();
 LocalDate d=null;
 try{d=LocalDate.parse(date);}catch(Exception ex){}
 if(d==null||d.isBefore(LocalDate.now()))err="Choose today or a future date.";
 else if(!Arrays.asList(slots).contains(time))err="Choose a time slot.";
 else if(why.isEmpty())err="Tell the doctor why you are visiting.";
 else{
  boolean taken=false;
  for(Map<String,String> a:Db.t("appts"))
   if(a.get("doc").equals(doc.get("id"))&&a.get("date").equals(date)&&a.get("time").equals(time)&&("pending".equals(a.get("status"))||"confirmed".equals(a.get("status"))))taken=true;
  if(taken)err="That slot is already booked. Pick another time.";
  else{
   Db.t("appts").add(Db.row("id",Db.id(),"pat",me.get("id"),"doc",doc.get("id"),"date",date,"time",time,"why",why,"status","pending","note",""));
   Db.save();response.sendRedirect("appointments.jsp");return;
  }
 }
}
%>
<h2>Book with <%=Db.h(doc.get("name"))%></h2>
<form class="card" method="post">
<% if(err!=null){%><p class="err"><%=err%></p><%}%>
<label>Date<input type="date" name="date" min="<%=LocalDate.now()%>" required></label>
<label>Time<select name="time"><% for(String sl:slots){%><option><%=sl%></option><%}%></select></label>
<textarea name="why" rows="3" placeholder="Reason for visit" required></textarea>
<button>Request appointment</button></form>
<%@ include file="_bot.jspf" %>
