<%-- %Id% --%>
<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
  <head>
    <!-- $Id: login.html 125 2012-10-16 05:44:33Z root $ -->
    <meta charset="utf-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="Monday, 16-Apr-73 13:10:00 GMT">
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
    <title><spring:message code="upce.cas-name"/></title>
    <link rel="stylesheet" href="/css/style.css" media="screen and (min-width: 800px)">
    <link rel="stylesheet" href="/css/mobile.css" media="only screen and (max-width: 799px)">
    <link rel="icon" href="/favicon.ico">
    <link rel="shortcut icon" href="/favicon.ico">
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script type="text/javascript" src="/js/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/negotiate/negotiate.js"></script>
<!--[if lt IE 9]>
    <script src="/js/html5shiv.js"></script>
    <link rel="stylesheet" href="/css/style.css" media="screen">
<![endif]-->
    <script>
	var _gaq = _gaq || [];
	_gaq.push(['_setAccount', 'UA-1621542-13']);
	_gaq.push(['_trackPageview']);
	(function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	})();
    </script>
  </head>
  <body class="<spring:message code="upce.lang"/><c:if test="${!loginPage}"> special</c:if>">
    <header>
      <c:if test="${loginPage}">
        <%final String queryString = request.getQueryString() == null ? "" : request.getQueryString().replaceAll("&locale=([A-Za-z][A-Za-z]_)?[A-Za-z][A-Za-z]|^locale=([A-Za-z][A-Za-z]_)?[A-Za-z][A-Za-z]", "");%>
        <c:set var='query' value='<%=queryString%>' />
        <c:set var="xquery" value="${fn:escapeXml(query)}" />
        <c:set var="loginUrl" value="login?${xquery}${not empty xquery ? '&' : ''}locale=" />
        <div id="lang-switcher">
          <a id="lang-cs" href="${loginUrl}cs">Czech</a>
          <a id="lang-en" href="${loginUrl}en">English</a>
        </div>
      </c:if>
    </header>
    <section id="body">