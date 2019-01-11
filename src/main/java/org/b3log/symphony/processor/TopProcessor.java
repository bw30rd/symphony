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
package org.b3log.symphony.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.After;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Follow;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.advice.AnonymousViewCheck;
import org.b3log.symphony.processor.advice.CSRFToken;
import org.b3log.symphony.processor.advice.PermissionGrant;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchEndAdvice;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchStartAdvice;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.service.ActivityQueryService;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.FollowMgmtService;
import org.b3log.symphony.service.FollowQueryService;
import org.b3log.symphony.service.PointtransferQueryService;
import org.b3log.symphony.service.UserLevelService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.DateUtil;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Top ranking list processor.
 * <p>
 * <ul>
 * <li>Top balance (/top/balance), GET</li>
 * <li>Top consumption (/top/consumption), GET</li>
 * <li>Top checkin (/top/checkin), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.2, Oct 26, 2016
 * @since 1.3.0
 */
@RequestProcessor
public class TopProcessor {

	/**
	 * Pagination window size.
	 */
	private static final int WINDOW_SIZE = 15;

	/**
	 * Pagination page size.
	 */
	private static final int PAGE_SIZE = 20;

	/**
	 * Data model service.
	 */
	@Inject
	private DataModelService dataModelService;

	/**
	 * Language service.
	 */
	@Inject
	private LangPropsService langPropsService;

	/**
	 * Pointtransfer query service.
	 */
	@Inject
	private PointtransferQueryService pointtransferQueryService;

	/**
	 * Activity query service.
	 */
	@Inject
	private ActivityQueryService activityQueryService;

	/**
	 * User query service.
	 */
	@Inject
	private UserQueryService userQueryService;

	/**
	 * User Level service.
	 */
	@Inject
	private UserLevelService userLevelService;

	/**
	 * User repository.
	 */
	@Inject
	private UserRepository userRepository;

	/**
	 * follow message service.
	 */
	@Inject
	private FollowMgmtService followMgmtService;

	/**
	 * Follow query service.
	 */
	@Inject
	private FollowQueryService followQueryService;

	/**
	 * Shows balance ranking list.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/top/balance", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, AnonymousViewCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showBalance(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);

		renderer.setTemplateName("/top/balance.ftl");

		final Map<String, Object> dataModel = renderer.getDataModel();

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		final List<JSONObject> users = pointtransferQueryService.getTopBalanceUsers(avatarViewMode,
				Symphonys.getInt("topBalanceCnt"));
		dataModel.put(Common.TOP_BALANCE_USERS, users);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
		dataModelService.fillRandomArticles(avatarViewMode, dataModel);
		dataModelService.fillSideHotArticles(avatarViewMode, dataModel);
		dataModelService.fillSideTags(dataModel);
		dataModelService.fillLatestCmts(dataModel);
	}

	/**
	 * (begeek)Shows user level ranking list.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @return
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/top/userRank", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, AnonymousViewCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showUserRank(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		context.renderJSON().renderMsg(langPropsService.get("loginFailLabel"));

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		// final List<JSONObject> users = pointtransferQueryService.getTopBalanceUsers(
		// avatarViewMode, Symphonys.getInt("topBalanceCnt"));

		final List<JSONObject> userList = userQueryService.getUsersByRank(10);
		context.renderMsg("").renderTrueResult();
		context.renderJSONValue("users", userList);

	}

	/***
	 * Shows all users level ranking list.
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestProcessing(value = "/top/usersRank", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, AnonymousViewCheck.class })
	@After(adviceClass = { CSRFToken.class, PermissionGrant.class, StopwatchEndAdvice.class })
	public void showUsersRank(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);

		renderer.setTemplateName("/top/usersRank.ftl");

		final Map<String, Object> dataModel = renderer.getDataModel();

		String pageNumStr = request.getParameter("p");
		if (Strings.isEmptyOrNull(pageNumStr) || !Strings.isNumeric(pageNumStr)) {
			pageNumStr = "1";
		}

		final int pageNum = Integer.valueOf(pageNumStr);
		final int pageSize = PAGE_SIZE;
		final int windowSize = WINDOW_SIZE;

		final JSONObject requestJSONObject = new JSONObject();
		requestJSONObject.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
		requestJSONObject.put(Pagination.PAGINATION_PAGE_SIZE, pageSize);
		requestJSONObject.put(Pagination.PAGINATION_WINDOW_SIZE, windowSize);
		requestJSONObject.put("number", Integer.MAX_VALUE);

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		final JSONObject result = userQueryService.getUsersByRank(requestJSONObject);

		final List<JSONObject> userList = CollectionUtils.jsonArrayToList(result.optJSONArray(User.USERS));
		final List<JSONObject> users = new ArrayList<JSONObject>();
		for (int i = pageSize * (pageNum - 1), count = 0; count < pageSize && i < userList.size(); i++, count++) {
			users.add(userList.get(i));
		}
		// dataModel.put(User.USERS, users);

		final JSONObject pagination = result.optJSONObject(Pagination.PAGINATION);
		final int pageCount = pagination.optInt(Pagination.PAGINATION_PAGE_COUNT);
		final JSONArray pageNums = pagination.optJSONArray(Pagination.PAGINATION_PAGE_NUMS);
		dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.opt(0));
		dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.opt(pageNums.length() - 1));
		dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
		dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
		dataModel.put(Pagination.PAGINATION_PAGE_NUMS, CollectionUtils.jsonArrayToList(pageNums));

		dataModel.put("pageSize", pageSize);

		JSONObject currentUser = userQueryService.getCurrentUser(request);
		if (null != currentUser) {
			/**
			 * get the user's information for the right side of index page.
			 */
			final String currentUserLevel = userLevelService.getUserLevel(currentUser.optInt("userPoint"));
			final String currentUserLevelType = userLevelService.getUserLevelType(currentUser.optInt("userPoint"));
			final int upgradePoints = userLevelService.getUpgradePoints(currentUser.optInt("userPoint"));
			final int userRank = userLevelService.getRanking(currentUser.optString("userName"), avatarViewMode);

			dataModel.put("currentUserLevel", currentUserLevel);
			dataModel.put("currentUserLevelType", currentUserLevelType);
			dataModel.put("upgradePoints", upgradePoints);
			dataModel.put("userRank", userRank);

			// 获取当前用户关注的帖子数
			int watchingArticleCnt = followMgmtService.getFollowCnt(currentUser, Follow.FOLLOWING_TYPE_C_ARTICLE_WATCH);
			currentUser.put("watchingArticleCnt", watchingArticleCnt);

			final String followerId = currentUser.optString(Keys.OBJECT_ID);
			if (userList != null) {
				for (final JSONObject followingUser : userList) {
					final String homeUserFollowingUserId = followingUser.optString(Keys.OBJECT_ID);

					followingUser.put(Common.IS_FOLLOWING, followQueryService.isFollowing(followerId,
							homeUserFollowingUserId, Follow.FOLLOWING_TYPE_C_USER));
				}
			}
		}

		dataModel.put(Common.TOP_BALANCE_USERS, users);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
		dataModelService.fillRandomArticles(avatarViewMode, dataModel);
		dataModelService.fillSideHotArticles(avatarViewMode, dataModel);
		dataModelService.fillSideTags(dataModel);
		dataModelService.fillLatestCmts(dataModel);

	}

	/**
	 * (begeek)Shows user liveness ranking list.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @return
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/top/userLiveness", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, AnonymousViewCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showUserLiveness(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		context.renderJSON().renderMsg(langPropsService.get("loginFailLabel"));

		int livenessType = Integer.parseInt(request.getParameter("livenessType"));

		Map<String, String> dates = DateUtil.getDates(livenessType);
		String fromdate = dates.get("fromdate");
		String todate = dates.get("todate");
		final List<JSONObject> userList = userQueryService.getUsersByLiveness(10, fromdate, todate);

		context.renderMsg("").renderTrueResult();
		context.renderJSONValue("users", userList);

	}

	/**
	 * Shows consumption ranking list.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/top/consumption", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, AnonymousViewCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showConsumption(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		;
		context.setRenderer(renderer);

		renderer.setTemplateName("/top/consumption.ftl");

		final Map<String, Object> dataModel = renderer.getDataModel();

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		final List<JSONObject> users = pointtransferQueryService.getTopConsumptionUsers(avatarViewMode,
				Symphonys.getInt("topConsumptionCnt"));
		dataModel.put(Common.TOP_CONSUMPTION_USERS, users);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
		dataModelService.fillRandomArticles(avatarViewMode, dataModel);
		dataModelService.fillSideHotArticles(avatarViewMode, dataModel);
		dataModelService.fillSideTags(dataModel);
		dataModelService.fillLatestCmts(dataModel);
	}

	/**
	 * Shows checkin ranking list.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/top/checkin", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, AnonymousViewCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showCheckin(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		;
		context.setRenderer(renderer);

		renderer.setTemplateName("/top/checkin.ftl");

		final Map<String, Object> dataModel = renderer.getDataModel();

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		final List<JSONObject> users = activityQueryService.getTopCheckinUsers(avatarViewMode,
				Symphonys.getInt("topCheckinCnt"));
		dataModel.put(Common.TOP_CHECKIN_USERS, users);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
		dataModelService.fillRandomArticles(avatarViewMode, dataModel);
		dataModelService.fillSideHotArticles(avatarViewMode, dataModel);
		dataModelService.fillSideTags(dataModel);
		dataModelService.fillLatestCmts(dataModel);
	}

}
