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

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
import org.b3log.latke.user.UserService;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Follow;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.advice.AnonymousViewCheck;
import org.b3log.symphony.processor.advice.PermissionCheck;
import org.b3log.symphony.processor.advice.PermissionGrant;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchEndAdvice;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchStartAdvice;
import org.b3log.symphony.service.ArticleQueryService;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.FollowMgmtService;
import org.b3log.symphony.service.FollowQueryService;
import org.b3log.symphony.service.SearchQueryService;
import org.b3log.symphony.service.UserLevelService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Search processor.
 * <p>
 * <ul>
 * <li>Searches keyword (/search), GET</li>
 * </ul>
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.2.4, Mar 29, 2017
 * @since 1.4.0
 */
@RequestProcessor
public class SearchProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SearchProcessor.class.getName());

	/**
	 * Pagination window size.
	 */
	private static final int WINDOW_SIZE = 15;

	/**
	 * Pagination page size.
	 */
	private static final int PAGE_SIZE = 20;

	/**
	 * Search query service.
	 */
	@Inject
	private SearchQueryService searchQueryService;

	/**
	 * Article query service.
	 */
	@Inject
	private ArticleQueryService articleQueryService;

	/**
	 * User query service.
	 */
	@Inject
	private UserQueryService userQueryService;

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
	 * Follow query service.
	 */
	@Inject
	private FollowQueryService followQueryService;

	/**
	 * User Level service.
	 */
	@Inject
	private UserLevelService userLevelService;

	/**
	 * follow message service.
	 */
	@Inject
	private FollowMgmtService followMgmtService;

	/**
	 * Searches.
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
	@RequestProcessing(value = "/search", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, AnonymousViewCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void search(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);

		renderer.setTemplateName("search-articles.ftl");

		if (!Symphonys.getBoolean("es.enabled") && !Symphonys.getBoolean("algolia.enabled")) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		final Map<String, Object> dataModel = renderer.getDataModel();

		String keyword = request.getParameter("key");
		if (StringUtils.isBlank(keyword)) {
			keyword = "";
		}
		
		LOGGER.log(Level.INFO, keyword+"", keyword+"");
		
		dataModel.put(Common.KEY, keyword);

		final String p = request.getParameter("p");
		int pageNum = 1;
		if (StringUtils.isNotBlank(p) && Strings.isNumeric(p)) {
			pageNum = Integer.valueOf(p);
		}

		int pageSize = Symphonys.getInt("indexArticlesCnt");
		final JSONObject user = userQueryService.getCurrentUser(request);
		if (null != user) {
			pageSize = user.optInt(UserExt.USER_LIST_PAGE_SIZE);
		}
		final List<JSONObject> articles = new ArrayList<>();
		int total = 0;

		if (Symphonys.getBoolean("es.enabled")) {
			final JSONObject result = searchQueryService.searchElasticsearch(Article.ARTICLE, keyword, pageNum,
					pageSize);
			if (null == result || 0 != result.optInt("status")) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			final JSONObject hitsResult = result.optJSONObject("hits");
			final JSONArray hits = hitsResult.optJSONArray("hits");

			for (int i = 0; i < hits.length(); i++) {
				final JSONObject article = hits.optJSONObject(i).optJSONObject("_source");
				articles.add(article);
			}

			total = result.optInt("total");
		}

		if (Symphonys.getBoolean("algolia.enabled")) {
			final JSONObject result = searchQueryService.searchAlgolia(keyword, pageNum, pageSize);
			if (null == result) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			final JSONArray hits = result.optJSONArray("hits");

			for (int i = 0; i < hits.length(); i++) {
				final JSONObject article = hits.optJSONObject(i);
				articles.add(article);
			}

			total = result.optInt("nbHits");
			if (total > 1000) {
				total = 1000; // Algolia limits the maximum number of search results to 1000
			}
		}

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		articleQueryService.organizeArticles(avatarViewMode, articles);
		final Integer participantsCnt = Symphonys.getInt("latestArticleParticipantsCnt");
		articleQueryService.genParticipants(avatarViewMode, articles, participantsCnt);

		dataModel.put(Article.ARTICLES, articles);

		final int pageCount = (int) Math.ceil(total / (double) pageSize);
		final List<Integer> pageNums = Paginator.paginate(pageNum, pageSize, pageCount,
				Symphonys.getInt("defaultPaginationWindowSize"));
		if (!pageNums.isEmpty()) {
			dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
			dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
		}

		dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
		dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
		dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
		dataModelService.fillRandomArticles(avatarViewMode, dataModel);
		dataModelService.fillSideHotArticles(avatarViewMode, dataModel);
		dataModelService.fillSideTags(dataModel);
		dataModelService.fillLatestCmts(dataModel);

		String searchEmptyLabel = langPropsService.get("searchEmptyLabel");
		searchEmptyLabel = searchEmptyLabel.replace("${key}", keyword);
		dataModel.put("searchEmptyLabel", searchEmptyLabel);
	}

	@RequestProcessing(value = "/search/users", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showUsers(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("search/users.ftl");

		final Map<String, Object> dataModel = renderer.getDataModel();
		dataModelService.fillHeaderAndFooter(request, response, dataModel);

		String pageNumStr = request.getParameter("p");
		if (Strings.isEmptyOrNull(pageNumStr) || !Strings.isNumeric(pageNumStr)) {
			pageNumStr = "1";
		}

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		final int pageNum = Integer.valueOf(pageNumStr);
		final int pageSize = PAGE_SIZE;
		final int windowSize = WINDOW_SIZE;
		final String keyword = request.getParameter("key");

		LOGGER.log(Level.INFO, keyword+"", keyword+"");
		
		final JSONObject requestJSONObject = new JSONObject();
		requestJSONObject.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
		requestJSONObject.put(Pagination.PAGINATION_PAGE_SIZE, pageSize);
		requestJSONObject.put(Pagination.PAGINATION_WINDOW_SIZE, windowSize);
		requestJSONObject.put("keyword", keyword);

		List<JSONObject> users = new ArrayList<JSONObject>();
		JSONObject result = new JSONObject();
		if (!Strings.isEmptyOrNull(keyword)) {
			result = userQueryService.getUsersByKeyword(requestJSONObject);
			final List<JSONObject> userList = CollectionUtils.jsonArrayToList(result.optJSONArray(User.USERS));
			for (int i = pageSize * (pageNum - 1), count = 0; count < pageSize && i < userList.size(); i++, count++) {
				users.add(userList.get(i));
			}
		} else {
			result = userQueryService.getUsers(requestJSONObject);
			users = CollectionUtils.jsonArrayToList(result.optJSONArray(User.USERS));
		}

		final boolean isLoggedIn = (Boolean) dataModel.get(Common.IS_LOGGED_IN);
		if (isLoggedIn ) {
			final JSONObject currentUser = (JSONObject) dataModel.get(Common.CURRENT_USER);

			// 获取当前用户关注的帖子数
			int watchingArticleCnt = followMgmtService.getFollowCnt(currentUser, Follow.FOLLOWING_TYPE_C_ARTICLE_WATCH);
			currentUser.put("watchingArticleCnt", watchingArticleCnt);

			final String followerId = currentUser.optString(Keys.OBJECT_ID);
			if(users != null) {
				for (final JSONObject followingUser : users) {
					final String homeUserFollowingUserId = followingUser.optString(Keys.OBJECT_ID);

					followingUser.put(Common.IS_FOLLOWING, followQueryService.isFollowing(followerId,
							homeUserFollowingUserId, Follow.FOLLOWING_TYPE_C_USER));
				}
			}
		}

		dataModel.put(User.USERS, users);
		dataModel.put(Common.KEY, keyword);

		final JSONObject pagination = result.optJSONObject(Pagination.PAGINATION);
		final int pageCount = pagination.optInt(Pagination.PAGINATION_PAGE_COUNT);
		final JSONArray pageNums = pagination.optJSONArray(Pagination.PAGINATION_PAGE_NUMS);
		dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.opt(0));
		dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.opt(pageNums.length() - 1));
		dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
		dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
		dataModel.put(Pagination.PAGINATION_PAGE_NUMS, CollectionUtils.jsonArrayToList(pageNums));

		dataModelService.fillSideHotArticles(avatarViewMode, dataModel);

	}
}
