<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var='loginPage' value='${true}' />
<jsp:directive.include file="includes/top.jsp" />

<section class="mobile-only instructions"><spring:message code="upce.screen.welcome.welcome" /></section>
<section id="factors">
  <section class="factor" id="factor-ldap">
    <h2><spring:message code="upce.screen.welcome.instructions"/></h2>
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
      <c:if test="${registeredService.isTwoFactor()}">
        <div class="input">
          <label for="token"><spring:message code="upce.screen.welcome.label.token"/></label>
          <form:password cssErrorClass="error" id="token" path="token" htmlEscape="true" autocomplete="off" />
        </div>
      </c:if>
      <div class="login">
        <button type="submit" name="submit"><spring:message code="upce.screen.welcome.button.login"/></button>
      </div>
    </form:form>
  </section>
  <c:if test="${registeredService.isSsoEnabled() && !registeredService.isTwoFactor()}">
    <section class="factor non-mobile" id="factor-kerberos">
      <h2><spring:message code="upce.kerberos.title"/></h2>
      <form:form method="post" commandName="${commandName}" htmlEscape="true">
        <div id="kerberos-testing"><spring:message code="upce.kerberos.info.testing"/></div>
        <div id="kerberos-not-available"><spring:message code="upce.kerberos.info.notAvailable"/></div>
        <div id="kerberos-available">
          <div class="login">
            <button type="submit"><spring:message code="upce.kerberos.button.login"/></button>
          </div>
          <div id="kerberos-possibly"><spring:message code="upce.kerberos.info.possiblyAvailable"/></div>
          <div><spring:message code="upce.kerberos.info.available"/></div>
        </div>
      </form:form>
      <hr>
      <div class="kerberos-info"><spring:message code="upce.kerberos.info.more"/></div>
    </section>
  </c:if>
</section>
<section id="instructions" class="instructions">
  <section class="non-mobile"><spring:message code="upce.screen.welcome.welcome" /></section>
  <section class="security"><spring:message code="upce.screen.welcome.security"/></section>
  <section><spring:message code="upce.screen.welcome.security2"/></section>
</section>
<div class="clear"></div>
<jsp:directive.include file="includes/bottom.jsp" />
