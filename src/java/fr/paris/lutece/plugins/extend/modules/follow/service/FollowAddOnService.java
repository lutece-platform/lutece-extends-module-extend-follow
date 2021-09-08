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
package fr.paris.lutece.plugins.extend.modules.follow.service;

import fr.paris.lutece.plugins.extend.modules.follow.business.Follow;
import fr.paris.lutece.portal.business.resourceenhancer.IResourceDisplayManager;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;

/**
 * Manager for add on display TODO : move this class into a document specific class !
 */
public class FollowAddOnService implements IResourceDisplayManager
{
    public static final String PROPERTY_RESOURCE_TYPE = "document";
    private static final String TAG_NUMBER_FOLLOWERS = "document-number-follow";
    @Inject
    @Named( FollowService.BEAN_SERVICE )
    private IFollowService _followService;

    @Override
    public void getXmlAddOn( StringBuffer strXml, String strResourceType, int nResourceId )
    {
        if ( PROPERTY_RESOURCE_TYPE.equals( strResourceType ) )
        {
            // Add on for document type
            Follow follow = _followService.findByResource( String.valueOf( nResourceId ), strResourceType );

            if ( follow != null )
            {
                XmlUtil.addElement( strXml, TAG_NUMBER_FOLLOWERS, follow.getFollowCount( ) );
            }
        }
    }

    @Override
    public void buildPageAddOn( Map<String, Object> model, String strResourceType, int nIdResource, String strPortletId, HttpServletRequest request )
    {
        return;
    }
}
