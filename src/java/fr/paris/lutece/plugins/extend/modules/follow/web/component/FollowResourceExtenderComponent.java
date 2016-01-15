/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.extend.modules.follow.web.component;

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTO;
import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfig;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistory;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.follow.business.Follow;
import fr.paris.lutece.plugins.extend.modules.follow.business.FollowFilter;
import fr.paris.lutece.plugins.extend.modules.follow.service.IFollowService;
import fr.paris.lutece.plugins.extend.modules.follow.service.extender.FollowResourceExtender;
import fr.paris.lutece.plugins.extend.modules.follow.util.constants.FollowConstants;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.plugins.extend.util.ExtendErrorException;
import fr.paris.lutece.plugins.extend.util.JSONUtils;
import fr.paris.lutece.plugins.extend.web.component.AbstractResourceExtenderComponent;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.DatabaseTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * FollowResourceExtenderComponent
 *
 */
public class FollowResourceExtenderComponent extends AbstractResourceExtenderComponent
{
    // TEMPLATES
    private static final String TEMPLATE_FOLLOW = "skin/plugins/extend/modules/follow/follow.html";
    private static final String TEMPLATE_FOLLOW_INFO = "admin/plugins/extend/modules/follow/follow_info.html";
    @Inject
    private IFollowService _followService;
    @Inject
    private IResourceExtenderHistoryService _resourceExtenderHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildXmlAddOn( String strIdExtendableResource, String strExtendableResourceType, String strParameters,
        StringBuffer strXml )
    {
        // Nothing yet
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "deprecation" )
    @Override
    public String getPageAddOn( String strIdExtendableResource, String strExtendableResourceType, String strParameters,
        HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

        Follow follow = _followService.findByResource( strIdExtendableResource, strExtendableResourceType );
        String strTemplateContent = DatabaseTemplateService.getTemplateFromKey( FollowConstants.MARK_EXTEND_FOLLOW );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( FollowConstants.MARK_FOLLOW, follow );
        model.put( FollowConstants.MARK_ID_EXTENDABLE_RESOURCE, strIdExtendableResource );
        model.put( FollowConstants.MARK_EXTENDABLE_RESOURCE_TYPE, strExtendableResourceType );
        model.put( FollowConstants.MARK_SHOW, fetchShowParameter( strParameters ) );

        if ( user != null )
        {
            model.put( FollowConstants.MARK_CAN_FOLLOW, true );
            model.put( FollowConstants.MARK_FOLLOW_CLOSED, false );
            model.put( FollowConstants.MARK_CAN_DELETE_FOLLOW,
                isFollower( user, strIdExtendableResource, strExtendableResourceType ) );
        }
        else
        {
            model.put( FollowConstants.MARK_CAN_FOLLOW, false );
            model.put( FollowConstants.MARK_FOLLOW_CLOSED, true );
        }

        model.put( FollowConstants.MARK_FOLLOW_HTML_CONTENT,
            AppTemplateService.getTemplateFromStringFtl( strTemplateContent, request.getLocale(  ), model ).getHtml(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FOLLOW, request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfoHtml( ResourceExtenderDTO resourceExtender, Locale locale, HttpServletRequest request )
    {
        if ( resourceExtender != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( FollowConstants.MARK_FOLLOW,
                _followService.findByResource( resourceExtender.getIdExtendableResource(  ),
                    resourceExtender.getExtendableResourceType(  ) ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FOLLOW_INFO, request.getLocale(  ), model );

            return template.getHtml(  );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Fetch show parameter.
     *
     * @param strParameters the str parameters
     * @return the string
     */
    private String fetchShowParameter( String strParameters )
    {
        String strShowParameter = StringUtils.EMPTY;
        JSONObject jsonParameters = JSONUtils.parseParameters( strParameters );

        if ( jsonParameters != null )
        {
            try
            {
                strShowParameter = jsonParameters.getString( FollowConstants.JSON_KEY_SHOW );
            }
            catch ( JSONException je )
            {
                AppLogService.debug( je.getMessage(  ), je );
            }
        }

        return strShowParameter;
    }

    /**
     *
     * @param user the User
     * @param strIdExtendableResource the IdExtendableResource
     * @param strExtendableResourceType the ExtendableResourceType
     * @return if the user is a follower
     */
    private boolean isFollower( LuteceUser user, String strIdExtendableResource, String strExtendableResourceType )
    {
        boolean res = false;
        ResourceExtenderHistoryFilter filter = new ResourceExtenderHistoryFilter(  );

        filter.setExtenderType( FollowResourceExtender.RESOURCE_EXTENDER );
        filter.setExtendableResourceType( strExtendableResourceType );
        filter.setIdExtendableResource( strIdExtendableResource );
        filter.setUserGuid( user.getName(  ) );
        filter.setAscSort( false );
        filter.setSortedAttributeName( FollowConstants.ORDER_BY_DATE_CREATION );

        List<ResourceExtenderHistory> listHistories = _resourceExtenderHistoryService.findByFilter( filter );

        res = CollectionUtils.isNotEmpty( listHistories );

        return res;
    }

    @Override
    public String getConfigHtml( ResourceExtenderDTO resourceExtender, Locale locale, HttpServletRequest request )
    {
        return null;
    }

    @Override
    public IExtenderConfig getConfig( int nIdExtender )
    {
        return null;
    }

    @Override
    public void doSaveConfig( HttpServletRequest request, IExtenderConfig config )
        throws ExtendErrorException
    {
    }
}
