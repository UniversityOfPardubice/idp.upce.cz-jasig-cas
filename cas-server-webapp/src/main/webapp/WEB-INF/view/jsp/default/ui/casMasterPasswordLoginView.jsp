<%-- %Id% --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var='loginPage' value='${true}' />
<jsp:directive.include file="includes/top.jsp" />

<section class="mobile-only instructions"><spring:message code="upce.screen.welcome.welcome" /></section>
<section id="factors">
  <section class="factor" id="factor-ldap">
    <h2><spring:message code="upce.screen.welcome.instructions.master"/></h2>
    <form:form method="post" commandName="${commandName}" htmlEscape="true">
      <form:errors path="*" id="msg" cssClass="errors" element="div" />
      <input type="hidden" name="lt" value="${loginTicket}" />
      <input type="hidden" name="execution" value="${flowExecutionKey}" />
      <input type="hidden" name="_eventId" value="submit" />
      <div class="input">
        <label for="login"><spring:message code="upce.screen.welcome.label.netid"/></label>
        <form:input cssErrorClass="error" id="login" path="username" autocomplete="false" htmlEscape="true" />
      </div>
      <div class="input">
        <label for="password"><spring:message code="upce.screen.welcome.label.password"/></label>
        <form:password cssErrorClass="error" id="password" path="password" htmlEscape="true" autocomplete="off" />
      </div>
      <div class="input">
        <label for="totp"><spring:message code="upce.screen.welcome.label.totp"/></label>
        <form:input cssErrorClass="error" id="totp" path="totp" autocomplete="false" htmlEscape="true" />
      </div>
      <div class="input">
        <label for="fakeusername"><spring:message code="upce.screen.welcome.label.fakeusername"/></label>
        <form:input cssErrorClass="error" id="fakeusername" path="fakeusername" autocomplete="false" htmlEscape="true" />
      </div>
      <div class="login">
        <button type="submit" name="submit"><spring:message code="upce.screen.welcome.button.login"/></button>
      </div>
    </form:form>
  </section>
</section>
<div class="clear"></div>
<jsp:directive.include file="includes/bottom.jsp" />
