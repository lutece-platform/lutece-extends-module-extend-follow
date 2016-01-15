<%@page import="fr.paris.lutece.portal.service.security.UserNotSignedException"%>
<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<%@page import="fr.paris.lutece.portal.web.PortalJspBean"%>
<jsp:useBean id="followJspBean" scope="request" class="fr.paris.lutece.plugins.extend.modules.follow.web.FollowJspBean" />

<%
	try
	{
		followJspBean.doFollow( request, response );
	}
	catch( SiteMessageException lme )
	{
		response.sendRedirect( AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl(  ) );
	}
	catch( UserNotSignedException unse )
	{
		response.sendRedirect( PortalJspBean.redirectLogin( request ));
	}
%>