-- liquibase formatted sql
-- changeset extendfollow:init_core_extend_follow.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_template WHERE template_name = 'extend_follow'
--
-- Init core_template
--

INSERT INTO core_template ( template_name, template_value ) VALUES ('extend_follow','<div class="extend-follow my-3">\r\n<#if !followClosed>\r\n    <#if canDeleteFollow>\r\n        <p class="mb-2">#i18n{module.extend.follow.extender.closed.label} :</p>\r\n        <form action="jsp/site/Portal.jsp?page=extendfollow&amp;action=doCancelFollow" method="post">\r\n            <input type="hidden" name="idExtendableResource" value="${idExtendableResource!}" />\r\n            <input type="hidden" name="extendableResourceType" value="${extendableResourceType!}" />\r\n            <input type="hidden" name="_csrftoken" value="${cancelFollowToken!}" />\r\n            <button type="submit" class="btn btn-outline-primary">#i18n{module.extend.follow.extender.cancelFollow.label}</button>\r\n        </form>\r\n    </#if>\r\n    <#if show == "all" || show == "actionFollow">\r\n        <#if canFollow && !canDeleteFollow>\r\n            <form action="jsp/site/Portal.jsp?page=extendfollow&amp;action=doFollow" method="post">\r\n                <input type="hidden" name="idExtendableResource" value="${idExtendableResource!}" />\r\n                <input type="hidden" name="extendableResourceType" value="${extendableResourceType!}" />\r\n                <input type="hidden" name="followValue" value="1" />\r\n                <input type="hidden" name="_csrftoken" value="${followToken!}" />\r\n                <button type="submit" class="btn btn-primary">#i18n{module.extend.follow.extender.doFollow.label}</button>\r\n            </form>\r\n        </#if>\r\n    </#if>\r\n</#if>\r\n<#if follow??><p class="mt-2 mb-0">#i18n{module.extend.follow.extender.count.label} : ${follow.followCount}</p></#if>\r\n</div>');

--
-- A database taken over from Lutece 7 keeps its template (the guard above): its links move to the extendfollow
-- XPage, its forms carry the CSRF token of the core, its default buttons get the theme classes. No effect on the
-- template a fresh install creates.
--
-- changeset extendfollow:init_core_extend_follow-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_template SET template_value = REPLACE( REPLACE( REPLACE( REPLACE( REPLACE( template_value,
    'jsp/site/plugins/extend/modules/follow/DoFollow.jsp?', 'jsp/site/Portal.jsp?page=extendfollow&amp;action=doFollow&amp;' ),
    'jsp/site/plugins/extend/modules/follow/DoCancelFollow.jsp?', 'jsp/site/Portal.jsp?page=extendfollow&amp;action=doCancelFollow&amp;' ),
    'jsp/site/plugins/extend/modules/follow/DoFollow.jsp', 'jsp/site/Portal.jsp?page=extendfollow&amp;action=doFollow' ),
    'jsp/site/plugins/extend/modules/follow/DoCancelFollow.jsp', 'jsp/site/Portal.jsp?page=extendfollow&amp;action=doCancelFollow' ),
    'name="token"', 'name="_csrftoken"' )
WHERE template_name = 'extend_follow';
UPDATE core_template SET template_value = REPLACE( REPLACE( template_value,
    '<button type="submit">#i18n{module.extend.follow.extender.cancelFollow.label}</button>', '<button type="submit" class="btn btn-outline-primary">#i18n{module.extend.follow.extender.cancelFollow.label}</button>' ),
    '<button type="submit">#i18n{module.extend.follow.extender.doFollow.label}</button>', '<button type="submit" class="btn btn-primary">#i18n{module.extend.follow.extender.doFollow.label}</button>' )
WHERE template_name = 'extend_follow';
