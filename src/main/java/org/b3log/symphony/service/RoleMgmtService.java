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
package org.b3log.symphony.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Domain;
import org.b3log.symphony.model.Permission;
import org.b3log.symphony.model.Role;
import org.b3log.symphony.repository.RolePermissionRepository;
import org.b3log.symphony.repository.RoleRepository;
import org.json.JSONObject;

/**
 * Role management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 8, 2016
 * @since 1.8.0
 */
@Service
public class RoleMgmtService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(RoleMgmtService.class);

	/**
	 * Role repository.
	 */
	@Inject
	private RoleRepository roleRepository;

	/**
	 * Role-Permission repository.
	 */
	@Inject
	private RolePermissionRepository rolePermissionRepository;

	/**
	 * Role Query Service.
	 */
	@Inject
	private RoleQueryService roleQueryService;
	/**
	 * Domain Mgmt Service.
	 */
	@Inject
	private DomainMgmtService domainMgmtService;

	/**
	 * Adds the specified role.
	 *
	 * @param role
	 *            the specified role
	 */
	@Transactional
	public void addRole(final JSONObject role) {
		try {
			final String roleName = role.optString(Role.ROLE_NAME);

			final Query query = new Query()
					.setFilter(new PropertyFilter(Role.ROLE_NAME, FilterOperator.EQUAL, roleName));
			if (roleRepository.count(query) > 0) {
				return;
			}

			roleRepository.add(role);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.ERROR, "Adds role failed", e);
		}
	}

	/**
	 * Updates role permissions.
	 *
	 * @param roleId
	 *            the specified role id
	 */
	@Transactional
	public void updateRolePermissions(final String roleId, final Set<String> permissionIds) {
		try {
			rolePermissionRepository.removeByRoleId(roleId);

			for (final String permissionId : permissionIds) {
				final JSONObject rel = new JSONObject();
				rel.put(Role.ROLE_ID, roleId);
				rel.put(Permission.PERMISSION_ID, permissionId);

				rolePermissionRepository.add(rel);
			}
		} catch (final RepositoryException e) {
			LOGGER.log(Level.ERROR, "Updates role permissions failed", e);
		}
	}

	/**
	 * remove Role Permissions.
	 *
	 * @param roleId
	 *            the specified role id
	 */
	@Transactional
	public void removeRolePermissions(final String roleId) {
		try {
			rolePermissionRepository.removeByRoleId(roleId);

		} catch (final RepositoryException e) {
			LOGGER.log(Level.ERROR, "remove role permissions failed", e);
		}
	}

	/**
	 * remove Role with the specified role id.
	 *
	 * @param roleId
	 *            the specified role id
	 */
	@Transactional
	public void removeRole(final String roleId) {
		try {
			roleRepository.remove(roleId);

		} catch (final RepositoryException e) {
			LOGGER.log(Level.ERROR, "remove role failed", e);
		}
	}

	/**
	 * Updates role.
	 *
	 * @param roleId
	 *            the specified role id
	 */
	@Transactional
	public void updateRole(final String roleId, final JSONObject role) {
		try {

			roleRepository.update(roleId, role);
		} catch (final RepositoryException e) {
			LOGGER.log(Level.ERROR, "Updates role failed", e);
		}
	}

	/**
	 * Add Or Update DomainAdmin.
	 *
	 * @param roleId
	 *            the specified role id
	 */
	@Transactional
	public JSONObject addDomainAdmin(final JSONObject domain) {
		final String roleName = domain.optString(Domain.DOMAIN_URI) + "DomainAdmin";
		final String roleDescription = "【" + domain.optString(Domain.DOMAIN_DESCRIPTION) + "】管理员";

		final JSONObject role = new JSONObject();
		role.put(Role.ROLE_NAME, roleName);
		role.put(Role.ROLE_DESCRIPTION, roleDescription);

		addRole(role);

		// 添加角色权限
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		String[] value = { "on" };
		// 管理入口
		// parameterMap.put(Permission.PERMISSION_ID_C_MENU_ADMIN, value);
		// parameterMap.put(Permission.PERMISSION_ID_C_MENU_ADMIN_ARTICLES, value);
		// parameterMap.put(Permission.PERMISSION_ID_C_MENU_ADMIN_COMMENTS, value);
		// parameterMap.put(Permission.PERMISSION_ID_C_MENU_ADMIN_RWS, value);
		// 敏感词
		parameterMap.put(Permission.PERMISSION_ID_C_RW_ADD_RW, value);
		parameterMap.put(Permission.PERMISSION_ID_C_RW_REMOVE_RW, value);
		parameterMap.put(Permission.PERMISSION_ID_C_RW_UPDATE_RW_BASIC, value);
		// 回帖
		parameterMap.put(Permission.PERMISSION_ID_C_COMMENT_REMOVE_COMMENT, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMENT_UPDATE_COMMENT_BASIC, value);
		// 帖子
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_GOOD_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_ARTICLE_CANCEL_STICK_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_ARTICLE_STICK_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_ARTICLE_REMOVE_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_ARTICLE_UPDATE_ARTICLE_BASIC, value);
		// 常规功能
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_ADD_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_ADD_COMMENT, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_AT_PARTICIPANTS, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_AT_USER, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_BAD_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_BAD_COMMENT, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_FOLLOW_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_GOOD_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_GOOD_COMMENT, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_REMOVE_COMMENT, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_THANK_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_THANK_COMMENT, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_UPDATE_ARTICLE, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_UPDATE_COMMENT, value);
		parameterMap.put(Permission.PERMISSION_ID_C_COMMON_WATCH_ARTICLE, value);

		final Set<String> permissionIds = parameterMap.keySet();
		List<JSONObject> newroles = roleQueryService.getRoleByName(roleName);
		updateRolePermissions(newroles.get(0).optString(Keys.OBJECT_ID), permissionIds);

		return newroles.get(0);
	}

	/**
	 * Add Or Update DomainAdmin.
	 *
	 * @param roleId
	 *            the specified role id
	 */
	@Transactional
	public JSONObject UpdateDomainAdmin(final JSONObject domain) {
		final String roleName = domain.optString(Domain.DOMAIN_URI) + "DomainAdmin";
		final String roleDescription = "【" + domain.optString(Domain.DOMAIN_TITLE) + "】管理员";
		final String roleId = domain.optString(Domain.DOMAIN_TYPE);

		JSONObject role = null;
		if (StringUtils.isBlank(roleId)) {
			role = addDomainAdmin(domain);
			domain.put(Domain.DOMAIN_TYPE, role.optString(Keys.OBJECT_ID));
			try {
				domainMgmtService.updateDomain(domain.optString(Keys.OBJECT_ID), domain);
			} catch (ServiceException e) {
				LOGGER.log(Level.ERROR, "update domain failed", e);
			}
		} else {
			role = roleQueryService.getRole(roleId);
		}
		role.put(Role.ROLE_NAME, roleName);
		role.put(Role.ROLE_DESCRIPTION, roleDescription);

		updateRole(roleId, role);
		return role;
	}
}
