/*
 * Symphony - A modern community (forum/SNS/blog) platform written in Java.
 * Copyright (C) 2012-2017,  b3log.org & hacpai.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor.advice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Permission;
import org.b3log.symphony.model.Role;
import org.b3log.symphony.service.RoleQueryService;
import org.json.JSONObject;

import com.google.common.util.concurrent.ListenableFutureTask;

/**
 * Permission grant.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.3.2, Jan 7, 2017
 * @since 1.8.0
 */
@Named
@Singleton
public class PermissionGrant extends AfterRequestProcessAdvice {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PermissionGrant.class);

	/**
	 * Role query service.
	 */
	@Inject
	private RoleQueryService roleQueryService;
	/**
	 * Language service.
	 */
	@Inject
	private LangPropsService langPropsService;

	@Override
	public void doAdvice(final HTTPRequestContext context, final Object ret) {
		final AbstractHTTPResponseRenderer renderer = context.getRenderer();
		if (null == renderer) {
			return;
		}

		Stopwatchs.start("Grant permissions");
		try {
			final Map<String, Object> dataModel = context.getRenderer().getRenderDataModel();

			final JSONObject user = (JSONObject) dataModel.get(Common.CURRENT_USER);
			final String roleIds = null != user ? user.optString(User.USER_ROLE) : Role.ROLE_ID_C_VISITOR;
			final List<String> roleIdList = Arrays.asList(roleIds.split(","));

			Map<String, JSONObject> permissionsGrants = new HashMap<>();
			for (String roleId : roleIdList) {
				Map<String, JSONObject> permissionsGrant = roleQueryService.getPermissionsGrantMap(roleId);

				for (Map.Entry<String, JSONObject> entry : permissionsGrant.entrySet()) {
					if (!permissionsGrants.containsKey(entry.getKey())
							|| entry.getValue().optBoolean(Permission.PERMISSION_T_GRANT)) {
						permissionsGrants.put(entry.getKey(),entry.getValue());
					}
				}
//				permissionsGrants.putAll(permissionsGrant);
			}
			dataModel.put(Permission.PERMISSIONS, permissionsGrants);

			// final JSONObject role = roleQueryService.getRole(roleId);

			String noPermissionLabel = langPropsService.get("noPermissionLabel");
			noPermissionLabel = noPermissionLabel.replace("{roleName}", roleQueryService.getRoleNames(roleIds));
			dataModel.put("noPermissionLabel", noPermissionLabel);
		} finally {
			Stopwatchs.end();
		}
	}
}
