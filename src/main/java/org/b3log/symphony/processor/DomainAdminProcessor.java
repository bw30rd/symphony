package org.b3log.symphony.processor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
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
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Paginator;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Domain;
import org.b3log.symphony.model.Option;
import org.b3log.symphony.model.Role;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.advice.PermissionCheck;
import org.b3log.symphony.processor.advice.PermissionGrant;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchEndAdvice;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchStartAdvice;
import org.b3log.symphony.repository.DomainRepository;
import org.b3log.symphony.service.ArticleMgmtService;
import org.b3log.symphony.service.ArticleQueryService;
import org.b3log.symphony.service.CommentMgmtService;
import org.b3log.symphony.service.CommentQueryService;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.DomainMgmtService;
import org.b3log.symphony.service.DomainQueryService;
import org.b3log.symphony.service.InvitecodeMgmtService;
import org.b3log.symphony.service.InvitecodeQueryService;
import org.b3log.symphony.service.NotificationMgmtService;
import org.b3log.symphony.service.OptionMgmtService;
import org.b3log.symphony.service.OptionQueryService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.service.PointtransferQueryService;
import org.b3log.symphony.service.RoleMgmtService;
import org.b3log.symphony.service.RoleQueryService;
import org.b3log.symphony.service.SearchMgmtService;
import org.b3log.symphony.service.TagMgmtService;
import org.b3log.symphony.service.TagQueryService;
import org.b3log.symphony.service.UserMgmtService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

@RequestProcessor
public class DomainAdminProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(DomainAdminProcessor.class);

	/**
	 * Pagination window size.
	 */
	private static final int WINDOW_SIZE = 15;

	/**
	 * Pagination page size.
	 */
	private static final int PAGE_SIZE = 20;

	/**
	 * Language service.
	 */
	@Inject
	private LangPropsService langPropsService;

	/**
	 * User query service.
	 */
	@Inject
	private UserQueryService userQueryService;

	/**
	 * User management service.
	 */
	@Inject
	private UserMgmtService userMgmtService;

	/**
	 * Article query service.
	 */
	@Inject
	private ArticleQueryService articleQueryService;

	/**
	 * Article management service.
	 */
	@Inject
	private ArticleMgmtService articleMgmtService;

	/**
	 * Comment query service.
	 */
	@Inject
	private CommentQueryService commentQueryService;

	/**
	 * Comment management service.
	 */
	@Inject
	private CommentMgmtService commentMgmtService;

	/**
	 * Option query service.
	 */
	@Inject
	private OptionQueryService optionQueryService;

	/**
	 * Option management service.
	 */
	@Inject
	private OptionMgmtService optionMgmtService;

	/**
	 * Domain query service.
	 */
	@Inject
	private DomainQueryService domainQueryService;

	/**
	 * Tag query service.
	 */
	@Inject
	private TagQueryService tagQueryService;

	/**
	 * Domain management service.
	 */
	@Inject
	private DomainMgmtService domainMgmtService;

	/**
	 * Tag management service.
	 */
	@Inject
	private TagMgmtService tagMgmtService;

	/**
	 * Pointtransfer management service.
	 */
	@Inject
	private PointtransferMgmtService pointtransferMgmtService;

	/**
	 * Pointtransfer query service.
	 */
	@Inject
	private PointtransferQueryService pointtransferQueryService;

	/**
	 * Notification management service.
	 */
	@Inject
	private NotificationMgmtService notificationMgmtService;

	/**
	 * Search management service.
	 */
	@Inject
	private SearchMgmtService searchMgmtService;

	/**
	 * Invitecode query service.
	 */
	@Inject
	private InvitecodeQueryService invitecodeQueryService;

	/**
	 * Invitecode management service.
	 */
	@Inject
	private InvitecodeMgmtService invitecodeMgmtService;

	/**
	 * Role query service.
	 */
	@Inject
	private RoleQueryService roleQueryService;

	/**
	 * Role management service.
	 */
	@Inject
	private RoleMgmtService roleMgmtService;

	/**
	 * Data model service.
	 */
	@Inject
	private DataModelService dataModelService;

	/**
	 * wechat Processor.
	 */
	@Inject
	private WechatProcessor wechatProcessor;

	/**
	 * Shows domain admin index.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showIndex(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/index.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);

		final JSONObject statistic = optionQueryService.getStatistic();
		dataModel.put(Option.CATEGORY_C_STATISTIC, statistic);

	}

	/**
	 * Shows domain admin articles.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/articles", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showArticles(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/articles.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		String pageNumStr = request.getParameter("p");
		if (Strings.isEmptyOrNull(pageNumStr) || !Strings.isNumeric(pageNumStr)) {
			pageNumStr = "1";
		}

		final int pageNum = Integer.valueOf(pageNumStr);
		final int pageSize = PAGE_SIZE;
		final int windowSize = WINDOW_SIZE;

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		setDomainAndTag(domainURI, null, dataModel);

		final JSONObject domain = domainQueryService.getByURI(domainURI);
		final JSONObject tag = tagQueryService.getTagByURI(domain.optString(Domain.DOMAIN_URI));

		final List<JSONObject> articles = articleQueryService.getArticlesByTag(avatarViewMode, 0, tag, pageNum,
				pageSize);
		dataModel.put(Article.ARTICLES, articles);

		final int tagRefCnt = tag.getInt(Tag.TAG_REFERENCE_CNT);
		final int pageCount = (int) Math.ceil(tagRefCnt / (double) pageSize);

		final List<Integer> pageNums = Paginator.paginate(pageNum, pageSize, pageCount, windowSize);
		dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
		dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
		dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
		dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
		dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Shows an article.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param articleId
	 *            the specified article id
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/article/{articleId}", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showArticle(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String articleId, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/article.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		final JSONObject article = articleQueryService.getArticle(articleId);
		dataModel.put(Article.ARTICLE, article);

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Updates an article.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param articleId
	 *            the specified article id
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/article/{articleId}", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void updateArticle(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String articleId, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/article.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		JSONObject article = articleQueryService.getArticle(articleId);

		final Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			final String name = parameterNames.nextElement();
			final String value = request.getParameter(name);

			if (name.equals(Article.ARTICLE_REWARD_POINT) || name.equals(Article.ARTICLE_STATUS)
					|| name.equals(Article.ARTICLE_TYPE) || name.equals(Article.ARTICLE_GOOD_CNT)
					|| name.equals(Article.ARTICLE_BAD_CNT) || name.equals(Article.ARTICLE_PERFECT)
					|| name.equals(Article.ARTICLE_ANONYMOUS_VIEW)) {
				article.put(name, Integer.valueOf(value));
			} else {
				article.put(name, value);
			}
		}

		final String articleTags = Tag.formatTags(article.optString(Article.ARTICLE_TAGS));
		article.put(Article.ARTICLE_TAGS, articleTags);

		articleMgmtService.updateArticleByAdmin(articleId, article);

		article = articleQueryService.getArticle(articleId);
		dataModel.put(Article.ARTICLE, article);

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Sticks an article.
	 *
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/stick-article", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = StopwatchEndAdvice.class)
	public void stickArticle(final HttpServletRequest request, final HttpServletResponse response,
			final String domainURI) throws Exception {
		final String articleId = request.getParameter(Article.ARTICLE_T_ID);
		articleMgmtService.adminStick(articleId);

		response.sendRedirect(Latkes.getServePath() + "/domainAdmin/" + domainURI + "/articles");
	}

	/**
	 * Cancels stick an article.
	 *
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/cancel-stick-article", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = StopwatchEndAdvice.class)
	public void stickCancelArticle(final HttpServletRequest request, final HttpServletResponse response,
			final String domainURI) throws Exception {
		final String articleId = request.getParameter(Article.ARTICLE_T_ID);
		articleMgmtService.adminCancelStick(articleId);

		response.sendRedirect(Latkes.getServePath() + "/domainAdmin/" + domainURI + "/articles");
	}

	/**
	 * Removes an article.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/remove-article", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = StopwatchEndAdvice.class)
	public void removeArticle(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final String articleId = request.getParameter(Article.ARTICLE_T_ID);
		articleMgmtService.removeArticleByAdmin(articleId);

		response.sendRedirect(Latkes.getServePath() + "/domainAdmin/" + domainURI + "/articles");
	}

	/**
	 * Shows domain admin comments.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/comments", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showComments(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/comments.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		String pageNumStr = request.getParameter("p");
		if (Strings.isEmptyOrNull(pageNumStr) || !Strings.isNumeric(pageNumStr)) {
			pageNumStr = "1";
		}

		final int pageNum = Integer.valueOf(pageNumStr);
		final int pageSize = PAGE_SIZE;
		final int windowSize = WINDOW_SIZE;

		final int avatarViewMode = (int) request.getAttribute(UserExt.USER_AVATAR_VIEW_MODE);

		final JSONObject domain = domainQueryService.getByURI(domainURI);
		final JSONObject tag = tagQueryService.getTagByURI(domain.optString(Domain.DOMAIN_URI));
		final List<JSONObject> articles = articleQueryService.getArticlesByTag(domain.optString(Domain.DOMAIN_TITLE));

		final List<JSONObject> Allcomments = new ArrayList<>();
		final List<JSONObject> comments = new ArrayList<>();

		for (JSONObject article : articles) {
			final List<JSONObject> articleComments = commentQueryService
					.getArticleComments(article.optString(Keys.OBJECT_ID));
			Allcomments.addAll(articleComments);
		}
		for (int i = pageSize * (pageNum - 1), j = 0; j < pageSize && i < Allcomments.size(); i++, j++) {
			comments.add(Allcomments.get(i));
		}

		dataModel.put(Comment.COMMENTS, comments);

		setDomainAndTag(domainURI, null, dataModel);

		final int tagRefCnt = tag.getInt(Tag.TAG_REFERENCE_CNT);
		final int pageCount = (int) Math.ceil(tagRefCnt / (double) pageSize);

		final List<Integer> pageNums = Paginator.paginate(pageNum, pageSize, pageCount, windowSize);
		dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
		dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
		dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
		dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
		dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Shows a comment.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param commentId
	 *            the specified comment id
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/comment/{commentId}", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showComment(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String commentId, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/comment.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		final JSONObject comment = commentQueryService.getComment(commentId);
		dataModel.put(Comment.COMMENT, comment);

		final JSONObject article = articleQueryService.getArticle(comment.optString(Comment.COMMENT_ON_ARTICLE_ID));

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Updates a comment.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param commentId
	 *            the specified comment id
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/comment/{commentId}", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void updateComment(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String commentId, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/comment.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		JSONObject comment = commentQueryService.getComment(commentId);

		final Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			final String name = parameterNames.nextElement();
			final String value = request.getParameter(name);

			if (name.equals(Comment.COMMENT_STATUS) || name.equals(Comment.COMMENT_GOOD_CNT)
					|| name.equals(Comment.COMMENT_BAD_CNT)) {
				comment.put(name, Integer.valueOf(value));
			} else {
				comment.put(name, value);
			}
		}

		commentMgmtService.updateComment(commentId, comment);

		comment = commentQueryService.getComment(commentId);
		dataModel.put(Comment.COMMENT, comment);

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Removes a comment.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/remove-comment", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = StopwatchEndAdvice.class)
	public void removeComment(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final String commentId = request.getParameter(Comment.COMMENT_T_ID);
		commentMgmtService.removeCommentByAdmin(commentId);

		response.sendRedirect(Latkes.getServePath() + "/domainAdmin/" + domainURI + "/comments");
	}

	/**
	 * Shows reserved words.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/reserved-words", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showReservedWords(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/reserved-words.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		dataModel.put(Common.WORDS, optionQueryService.getReservedWords());

		final JSONObject domain = domainQueryService.getByURI(domainURI);
		final JSONObject tag = tagQueryService.getTagByURI(domain.optString(Domain.DOMAIN_URI));
		dataModel.put(Tag.TAG, tag);
		dataModel.put(Domain.DOMAIN, domain);

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Shows a reserved word.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param id
	 *            the specified reserved word id
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/reserved-word/{id}", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showReservedWord(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String id, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/reserved-word.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		final JSONObject word = optionQueryService.getOption(id);
		dataModel.put(Common.WORD, word);

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Adds a reserved word.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/add-reserved-word", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = StopwatchEndAdvice.class)
	public void addReservedWord(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		String word = request.getParameter(Common.WORD);
		word = StringUtils.trim(word);
		if (StringUtils.isBlank(word)) {
			final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
			context.setRenderer(renderer);
			renderer.setTemplateName("admin/domainAdmin/error.ftl");
			final Map<String, Object> dataModel = renderer.getDataModel();

			dataModel.put(Keys.MSG, langPropsService.get("invalidReservedWordLabel"));
			dataModelService.fillHeaderAndFooter(request, response, dataModel);

			return;
		}

		if (optionQueryService.existReservedWord(word)) {
			response.sendRedirect(Latkes.getServePath() + "/domainAdmin/" + domainURI + "/reserved-words");

			return;
		}

		try {
			final JSONObject reservedWord = new JSONObject();
			reservedWord.put(Option.OPTION_CATEGORY, Option.CATEGORY_C_RESERVED_WORDS);
			reservedWord.put(Option.OPTION_VALUE, word);

			optionMgmtService.addOption(reservedWord);
		} catch (final Exception e) {
			final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
			context.setRenderer(renderer);
			renderer.setTemplateName("admin/domainAdmin/error.ftl");
			final Map<String, Object> dataModel = renderer.getDataModel();

			dataModel.put(Keys.MSG, e.getMessage());
			dataModelService.fillHeaderAndFooter(request, response, dataModel);

			return;
		}

		response.sendRedirect(Latkes.getServePath() + "/domainAdmin/" + domainURI + "/reserved-words");
	}

	/**
	 * Shows add reserved word.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/add-reserved-word", method = HTTPRequestMethod.GET)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showAddReservedWord(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/add-reserved-word.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Updates a reserved word.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @param id
	 *            the specified reserved wordid
	 * @throws Exception
	 *             exception
	 */
	@RequestProcessing(value = "/domainAdmin/{domainURI}/reserved-word/{id}", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void updateReservedWord(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String id, final String domainURI) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);
		renderer.setTemplateName("admin/domainAdmin/reserved-word.ftl");
		final Map<String, Object> dataModel = renderer.getDataModel();

		final JSONObject word = optionQueryService.getOption(id);
		dataModel.put(Common.WORD, word);

		final Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			final String name = parameterNames.nextElement();
			final String value = request.getParameter(name);

			word.put(name, value);
		}

		optionMgmtService.updateOption(id, word);

		setDomainAndTag(domainURI, null, dataModel);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Removes a reserved word.
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
	@RequestProcessing(value = "/domainAdmin/{domainURI}/remove-reserved-word", method = HTTPRequestMethod.POST)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = StopwatchEndAdvice.class)
	public void removeReservedWord(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String domainURI) throws Exception {
		final String id = request.getParameter("id");
		optionMgmtService.removeOption(id);

		response.sendRedirect(Latkes.getServePath() + "/domainAdmin/" + domainURI + "/reserved-words");
	}

	/**
	 * send wechat article.
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
	@RequestProcessing(value = "/domainAdmin/send-wechat-article/{articleId}", method = HTTPRequestMethod.PUT)
	@Before(adviceClass = { StopwatchStartAdvice.class, PermissionCheck.class })
	@After(adviceClass = StopwatchEndAdvice.class)
	public void sendWechatArticle(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response, final String articleId) throws Exception {
		context.renderJSON().renderMsg(langPropsService.get("loginFailLabel"));

		final JSONObject article = articleQueryService.getArticle(articleId);
		final String tagTitle = article.optString(Article.ARTICLE_TAGS);

		// Push article to wechat users who follow the tag of the article.
		try {
			wechatProcessor.sendWechatArticleToAll(tagTitle, article, articleId);

			// response.sendRedirect(Latkes.getServePath() + "/article/"+articleId);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "send wechat article error", e);
		}
	}

	private void setDomainAndTag(final String domainURI, final String domainTitle, final Map<String, Object> dataModel)
			throws Exception {
		JSONObject domain = new JSONObject();
		if (!StringUtils.isBlank(domainTitle)) {
			domain = domainQueryService.getByTitle(domainTitle);
		}
		if (!StringUtils.isBlank(domainURI)) {
			domain = domainQueryService.getByURI(domainURI);
		}
		final JSONObject tag = tagQueryService.getTagByURI(domain.optString(Domain.DOMAIN_URI));

		dataModel.put(Tag.TAG, tag);
		dataModel.put(Domain.DOMAIN, domain);
	}
}
