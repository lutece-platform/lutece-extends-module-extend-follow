/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.extend.modules.follow.web;

import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistory;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.follow.service.FollowListenerService;
import fr.paris.lutece.plugins.extend.modules.follow.service.FollowService;
import fr.paris.lutece.plugins.extend.modules.follow.service.IFollowService;
import fr.paris.lutece.plugins.extend.modules.follow.service.extender.FollowResourceExtender;
import fr.paris.lutece.plugins.extend.modules.follow.service.validator.FollowValidationManagementService;
import fr.paris.lutece.plugins.extend.modules.follow.util.constants.FollowConstants;
import fr.paris.lutece.plugins.extend.service.ExtendPlugin;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Front office application receiving the follow and cancel follow requests.
 */
@RequestScoped
@Named( FollowApp.BEAN_NAME )
@Controller( xpageName = FollowApp.XPAGE_NAME, securityTokenEnabled = true )
public class FollowApp extends MVCApplication
{
    /** The XPage name. */
    public static final String XPAGE_NAME = "extendfollow";

    /** The bean name. */
    public static final String BEAN_NAME = "extendfollow.xpage." + XPAGE_NAME;

    private static final long serialVersionUID = 1L;

    private static final String VIEW_HOME = "home";
    private static final String CONSTANT_HTTP = "http";

    @Inject
    private IResourceExtenderHistoryService _resourceExtenderHistoryService;

    @Inject
    @Named( FollowService.BEAN_SERVICE )
    private IFollowService _followService;

    @Inject
    @Named( FollowConstants.BEAN_CONFIG_SERVICE )
    private IResourceExtenderConfigService _configService;

    /**
     * Redirects to the portal home: this application has no page of its own.
     *
     * @param request
     *            The HTTP request
     * @return the redirection
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage getHome( HttpServletRequest request )
    {
        return redirect( request, getPortalHomeUrl( request ) );
    }

    /**
     * Follows a resource, then redirects to the page of the resource.
     *
     * @param request
     *            The HTTP request
     * @return the redirection
     * @throws SiteMessageException
     *             if the request is invalid or the follow is closed
     * @throws UserNotSignedException
     *             if no user is signed in
     */
    @Action( FollowConstants.ACTION_DO_FOLLOW )
    public XPage doFollow( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        String strIdExtendableResource = request.getParameter( FollowConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strExtendableResourceType = request.getParameter( FollowConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );
        String strFollowValue = request.getParameter( FollowConstants.PARAMETER_FOLLOW_VALUE );

        int nFollowValue = parseFollowValue( request, strFollowValue );

        if ( StringUtils.isBlank( strIdExtendableResource ) || StringUtils.isBlank( strExtendableResourceType )
                || _configService.find( FollowResourceExtender.RESOURCE_EXTENDER, strIdExtendableResource, strExtendableResourceType ) == null )
        {
            SiteMessageService.setMessage( request, FollowConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );
        }

        String strSessionKeyNextUrl = getSessionKeyUrlRedirect( strIdExtendableResource, strExtendableResourceType );
        String strNextUrl = (String) request.getSession( ).getAttribute( strSessionKeyNextUrl );

        if ( StringUtils.isEmpty( strNextUrl ) )
        {
            strNextUrl = getRefererUrl( request );
        }
        else
        {
            request.getSession( ).removeAttribute( strSessionKeyNextUrl );
        }

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user == null )
        {
            request.getSession( ).setAttribute( strSessionKeyNextUrl, strNextUrl );
            throw new UserNotSignedException( );
        }

        String strErrorUrl = FollowValidationManagementService.validateFollow( request, user, strIdExtendableResource, strExtendableResourceType,
                nFollowValue );

        if ( StringUtils.isNotEmpty( strErrorUrl ) )
        {
            if ( !strErrorUrl.startsWith( CONSTANT_HTTP ) )
            {
                strErrorUrl = AppPathService.getBaseUrl( request ) + strErrorUrl;
            }

            request.getSession( ).setAttribute( strSessionKeyNextUrl, strNextUrl );

            return redirect( request, strErrorUrl );
        }

        ResourceExtenderHistoryFilter filter = new ResourceExtenderHistoryFilter( );
        filter.setExtenderType( FollowResourceExtender.RESOURCE_EXTENDER );
        filter.setUserGuid( user.getName( ) );
        filter.setExtendableResourceType( strExtendableResourceType );
        filter.setIdExtendableResource( strIdExtendableResource );

        List<ResourceExtenderHistory> listHistories = _resourceExtenderHistoryService.findByFilter( filter );

        if ( listHistories.isEmpty( ) )
        {
            if ( !FollowListenerService.canFollow( strExtendableResourceType, strIdExtendableResource, user ) )
            {
                SiteMessageService.setMessage( request, FollowConstants.MESSAGE_PHASE_IS_CLOSE, SiteMessage.TYPE_STOP );
            }

            _followService.doFollow( strIdExtendableResource, strExtendableResourceType, nFollowValue, request );
        }

        return redirect( request, strNextUrl );
    }

    /**
     * Cancels the follow of the signed-in user, then redirects to the page of the resource.
     *
     * @param request
     *            The HTTP request
     * @return the redirection
     * @throws SiteMessageException
     *             if the request is invalid or the follow is closed
     * @throws UserNotSignedException
     *             if no user is signed in
     */
    @Action( FollowConstants.ACTION_CANCEL_FOLLOW )
    public XPage doCancelFollow( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        String strIdExtendableResource = request.getParameter( FollowConstants.PARAMETER_ID_EXTENDABLE_RESOURCE );
        String strExtendableResourceType = request.getParameter( FollowConstants.PARAMETER_EXTENDABLE_RESOURCE_TYPE );

        if ( StringUtils.isBlank( strIdExtendableResource ) || StringUtils.isBlank( strExtendableResourceType ) )
        {
            SiteMessageService.setMessage( request, FollowConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );
        }

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        if ( !FollowListenerService.canFollow( strExtendableResourceType, strIdExtendableResource, user ) )
        {
            SiteMessageService.setMessage( request, FollowConstants.MESSAGE_PHASE_IS_CLOSE, SiteMessage.TYPE_STOP );
        }

        _followService.doCancelFollow( user, strIdExtendableResource, strExtendableResourceType, request );

        return redirect( request, getRefererUrl( request ) );
    }

    /**
     * Parses the follow value of the request.
     *
     * @param request
     *            The HTTP request
     * @param strFollowValue
     *            the follow value parameter
     * @return the follow value
     * @throws SiteMessageException
     *             if the value is not an integer
     */
    private static int parseFollowValue( HttpServletRequest request, String strFollowValue ) throws SiteMessageException
    {
        try
        {
            return Integer.parseInt( strFollowValue );
        }
        catch( NumberFormatException e )
        {
            SiteMessageService.setMessage( request, FollowConstants.MESSAGE_ERROR_GENERIC_MESSAGE, SiteMessage.TYPE_STOP );

            return 0;
        }
    }

    /**
     * Gives the session key of the URL to go back to after a follow.
     *
     * @param strIdResource
     *            the id of the resource
     * @param strResourceType
     *            the type of the resource
     * @return the session key
     */
    public static String getSessionKeyUrlRedirect( String strIdResource, String strResourceType )
    {
        return ExtendPlugin.PLUGIN_NAME + FollowConstants.PARAMETER_FROM_URL + strResourceType + strIdResource;
    }

    /**
     * Gives the page the request comes from, carrying the origin URL kept in session, or the portal home.
     *
     * @param request
     *            The HTTP request
     * @return the URL to go back to
     */
    private static String getRefererUrl( HttpServletRequest request )
    {
        String strReferer = request.getHeader( FollowConstants.PARAMETER_HTTP_REFERER );

        if ( strReferer == null )
        {
            return getPortalHomeUrl( request );
        }

        UrlItem url = new UrlItem( strReferer );
        String strFromUrl = (String) request.getSession( ).getAttribute( ExtendPlugin.PLUGIN_NAME + FollowConstants.PARAMETER_FROM_URL );

        if ( StringUtils.isNotEmpty( strFromUrl ) && !url.getUrl( ).contains( FollowConstants.PARAMETER_FROM_URL ) )
        {
            url.addParameter( FollowConstants.PARAMETER_FROM_URL, strFromUrl.replace( "%", "%25" ) );
        }

        return url.getUrl( );
    }

    /**
     * Gives the absolute URL of the portal home.
     *
     * @param request
     *            The HTTP request
     * @return the URL
     */
    private static String getPortalHomeUrl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + AppPathService.getPortalUrl( );
    }
}
