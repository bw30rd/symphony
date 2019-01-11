package org.b3log.symphony.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.symphony.conf.AuthorConfiguration;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Reward;
import org.b3log.symphony.repository.FollowRepository;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.service.ArticleQueryService;
import org.b3log.symphony.service.CommentQueryService;
import org.b3log.symphony.service.UserLevelService;
import org.b3log.symphony.service.UserMgmtService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.service.WechatMessageService;
import org.b3log.symphony.util.Emotions;
import org.b3log.symphony.util.Markdowns;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.patchca.service.CaptchaService;

import com.bw.authcenter.authorapi.MessageService;
import com.bw.authcenter.authorapi.message.pojo.Article;
import com.bw.authcenter.authorapi.message.pojo.MessageEnum;
import com.bw.authcenter.authorapi.message.pojo.MpArticle;
import com.bw.authcenter.authorapi.message.pojo.UserInfo;
import com.bw.authcenter.authorapi.message.pojo.WechatMedia;
import com.bw.authcenter.authorapi.message.request.NewsRequest;
import com.bw.authcenter.authorapi.message.request.TextRequest;

@RequestProcessor
public class WechatProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(WechatProcessor.class);

	private static MessageService messageService = AuthorConfiguration.initMessageService();

	private static String URL = "http://authcenter.bwae.org/authorcenter/wechatlogin/messageredirect?label=begeek&redirecturl="
			+ Latkes.getServePath() + "/begeek/login?myUrl=" + Latkes.getServePath() + "";

	/**
	 * User repository.
	 */
	@Inject
	private static UserRepository userRepository;

	/**
	 * Follow repository.
	 */
	@Inject
	private static FollowRepository followRepository;

	/**
	 * Tag repository.
	 */
	@Inject
	private static TagRepository tagRepository;

	/**
	 * Wechat Message Service.
	 */
	@Inject
	private WechatMessageService wechatMessageService;

	@Inject
	private ArticleQueryService articleQueryService;

	@Inject
	private CommentQueryService commentQueryService;

	/**
	 * User Level service.
	 */
	@Inject
	private UserLevelService userLevelService;
	/**
	 * User Query service.
	 */
	@Inject
	private UserQueryService userQueryService;

	/**
	 * Language service.
	 */
	@Inject
	private LangPropsService langPropsService;

	public void sendWechatArticleByTag(String tagTitle, JSONObject article, String articleId) throws Exception {
		String tagId = getTagIdByTagTitle(tagTitle);
		List<UserInfo> users = getUsersByTagId(tagId);
		if (users != null) {
			sendWechatArticle(tagTitle, article, articleId, users);
		}
	}

	public void sendWechatArticleToAll(String tagTitle, JSONObject article, String articleId) throws Exception {
		List<UserInfo> users = getUsers();
		if (users != null) {
			sendWechatArticle(tagTitle, article, articleId, users);
		}
	}

	private void sendWechatArticle(String tagTitle, JSONObject article, String articleId, List<UserInfo> users)
			throws Exception {
		NewsRequest newsRequest = new NewsRequest();
		String url = URL + "/article/" + articleId;
		String authorName = getUserNameById(article.optString("articleAuthorId"));
		if (article.optInt("articleAnonymous") == 1) {
			authorName = "匿名用户";
		}

		final JSONObject article_tag = new JSONObject();
		article_tag.put("title", "【" + tagTitle + "】" + article.optString("articleTitle"));
		article_tag.put("description",
				"【作者:@" + authorName + "】 " + getArticleBreviary(articleQueryService.getArticleMetaDesc(article)));
		String picurl = getArticleThumbnail(article);
		if (picurl == "") {
			picurl = Latkes.getServePath() + "/images/wechat_default.png";
		}
		article_tag.put("picurl", picurl);
		article_tag.put("url", url);
		article_tag.put("btntxt", "点击查看");

		newsRequest = wechatMessageService.getNewsRequest(users, getArticle(article_tag));
		wechatMessageService.sendNewsMessage(newsRequest);
	}

	public void sendWechatMessageByNotification(JSONObject notification) throws Exception {

		String userId = notification.optString(Notification.NOTIFICATION_USER_ID);
		String dataId = notification.optString(Notification.NOTIFICATION_DATA_ID);
		int dataType = notification.optInt(Notification.NOTIFICATION_DATA_TYPE);

		String dataId1 = "";
		String dataId2 = "";
		if (StringUtils.isNotBlank(dataId)) {
			dataId1 = dataId.substring(0, 13);
			dataId2 = dataId.length() > 13 ? dataId.substring(14) : "";
		}

		UserInfo userInfo = getUserInfoById(userId);
		String url = URL;
		String content = "";

		TextRequest textRequest = new TextRequest();
		NewsRequest newsRequest = new NewsRequest();

		switch (dataType) {
		case (Notification.DATA_TYPE_C_ARTICLE):
		case (Notification.DATA_TYPE_C_COMMENT):
		case (Notification.DATA_TYPE_C_BROADCAST):
			break;
		case (Notification.DATA_TYPE_C_COMMENTED):
			url += "/notifications/commented";

			final JSONObject comment3 = getCommentById(dataId);
			final JSONObject article3 = getArticleById(comment3.optString(Comment.COMMENT_ON_ARTICLE_ID));
			String commentAuthor3 = getUserNameById(comment3.optString(Comment.COMMENT_AUTHOR_ID));
			if (comment3.optBoolean(Comment.COMMENT_ANONYMOUS)) {
				commentAuthor3 = "匿名用户";
			}
			content = "你的帖子《" + getTitleBreviary(article3.optString("articleTitle")) + "》收到  @" + commentAuthor3
					+ " 的回帖\"" + getTitleBreviary(getCommentMetaDesc(comment3)) + "\",<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_POINT_CHARGE):
		case (Notification.DATA_TYPE_C_POINT_TRANSFER):
		case (Notification.DATA_TYPE_C_POINT_ARTICLE_REWARD):
			break;
		case (Notification.DATA_TYPE_C_POINT_COMMENT_THANK):
			url += "/notifications/point";

			final JSONObject reward8 = getRewardById(dataId);
			final JSONObject comment8 = getCommentById(reward8.optString(Reward.DATA_ID));
			final JSONObject article8 = getArticleById(comment8.optString(Comment.COMMENT_AUTHOR_ID));
			final String articleAuthor8 = getUserNameById(reward8.optString(Reward.SENDER_ID));

			content = "@" + articleAuthor8 + "感谢了你在帖子《" + getTitleBreviary(article8.optString("articleTitle"))
					+ "》中的回帖\"" + getTitleBreviary(getCommentMetaDesc(comment8)) + "\",<a href='" + url + "'>点击查看</a>";
			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_POINT_EXCHANGE):
		case (Notification.DATA_TYPE_C_ABUSE_POINT_DEDUCT):
		case (Notification.DATA_TYPE_C_POINT_ARTICLE_THANK):
		case (Notification.DATA_TYPE_C_INVITECODE_USED):
		case (Notification.DATA_TYPE_C_INVITATION_LINK_USED):
			break;
		case (Notification.DATA_TYPE_C_POINT_PERFECT_ARTICLE):
			url += "/notifications/point";

			final JSONObject article22 = getArticleById(dataId);
			content = "你的帖子《" + getTitleBreviary(article22.optString("articleTitle")) + "》已成为质量帖，感谢你的付出与分享。"
					+ "<a href='" + url + "'>点击查看</a>";
			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_REPLY):
			url += "/notifications/reply";

			final JSONObject comment13 = getCommentById(dataId);
			final JSONObject article13 = getArticleById(comment13.optString(Comment.COMMENT_ON_ARTICLE_ID));
			String commentAuthor13 = getUserNameById(comment13.optString(Comment.COMMENT_AUTHOR_ID));
			if (comment13.optBoolean(Comment.COMMENT_ANONYMOUS)) {
				commentAuthor13 = "匿名用户";
			}
			content = "@" + commentAuthor13 + " 在帖子《" + getTitleBreviary(article13.optString("articleTitle"))
					+ "》中回复你 \"" + getTitleBreviary(getCommentMetaDesc(comment13)) + "\",<a href='" + url
					+ "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_SYS_ANNOUNCE_ARTICLE):
		case (Notification.DATA_TYPE_C_SYS_ANNOUNCE_NEW_USER):
		case (Notification.DATA_TYPE_C_SYS_ANNOUNCE_ROLE_CHANGED):
			url += "/notifications/sys-announce";

			content = "您有一条新的系统通知" + ",<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_NEW_FOLLOWER):
			url += "/member/" + userInfo.getLoginName() + "/followers";

			final String follower17 = getUserNameById(dataId);
			content = "您有一个新的粉丝 " + "@" + follower17 + ",<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_FOLLOWING_USER):
			JSONObject article4 = getArticleById(dataId);
			String tagId = getTagIdByTagTitle(article4.optString("articleTags"));
			if (judgeFollowBetweenUserAndTag(userId, tagId)) {
				break;
			}
		case (Notification.DATA_TYPE_C_FOLLOWING_ARTICLE_UPDATE):
			url += "/article/" + dataId;

			final JSONObject article20 = getArticleById(dataId);
			String authorName = getUserNameById(article20.optString("articleAuthorId"));

			final JSONObject article20_1 = new JSONObject();
			article20_1.put("title",
					"【" + article20.optString("articleTags") + "】" + article20.optString("articleTitle"));
			article20_1.put("description", "【作者:@" + authorName + "】 "
					+ getArticleBreviary(articleQueryService.getArticleMetaDesc(article20)));

			String picurl = getArticleThumbnail(article20);
			if (picurl == "") {
				picurl = Latkes.getServePath() + "/images/wechat_default.png";
			}
			article20_1.put("picurl", picurl);
			article20_1.put("url", url);
			article20_1.put("btntxt", "点击查看");
			Article article = getArticle(article20_1);

			newsRequest = wechatMessageService.getNewsRequest(userInfo, article);
			wechatMessageService.sendNewsMessage(newsRequest);
			break;
		case (Notification.DATA_TYPE_C_FOLLOWING_ARTICLE_COMMENT):
			url += "/notifications/following";

			final JSONObject comment21 = getCommentById(dataId);
			final JSONObject article21 = getArticleById(comment21.optString(Comment.COMMENT_ON_ARTICLE_ID));
			String commentAuthor21 = getUserNameById(comment21.optString(Comment.COMMENT_AUTHOR_ID));
			if (comment21.optBoolean(Comment.COMMENT_ANONYMOUS)) {
				commentAuthor21 = "匿名用户";
			}
			content = "@" + commentAuthor21 + "在你关注的帖子《" + getTitleBreviary(article21.optString("articleTitle"))
					+ "》有了新的回帖\"" + getTitleBreviary(getCommentMetaDesc(comment21)) + "\",<a href='" + url
					+ "'>点击查看</a>";
			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_AT):
			url += "/notifications/at";

			final JSONObject article2 = getArticleById(dataId);
			if (article2 == null) {
				final JSONObject comment2 = getCommentById(dataId);
				final JSONObject article2_1 = getArticleById(comment2.optString(Comment.COMMENT_ON_ARTICLE_ID));
				String commentAuthor2 = getUserNameById(comment2.optString(Comment.COMMENT_AUTHOR_ID));
				if (comment2.optBoolean(Comment.COMMENT_ANONYMOUS)) {
					commentAuthor2 = "匿名用户";
				}
				content = "@" + commentAuthor2 + "在帖子《" + getTitleBreviary(article2_1.optString("articleTitle"))
						+ "》中的回帖中提到了你" + ",<a href='" + url + "'>点击查看</a>";
			} else {
				final String articleAuthor2 = getUserNameById(article2.optString("articleAuthorId"));
				content = "@" + articleAuthor2 + "在帖子《" + getTitleBreviary(article2.optString("articleTitle"))
						+ "》中提到了你" + ",<a href='" + url + "'>点击查看</a>";
			}

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_ARTICLE_NEW_FOLLOWER):
			url += "/notifications/at";

			final JSONObject article23 = getArticleById(dataId1);
			final String follower23 = getUserNameById(dataId2);
			content = "@" + follower23 + "收藏了你的帖子《" + getTitleBreviary(article23.optString("articleTitle"))
					+ "》,<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_ARTICLE_NEW_WATCHER):
			url += "/notifications/at";

			final JSONObject article24 = getArticleById(dataId1);
			final String follower24 = getUserNameById(dataId2);
			content = "@" + follower24 + "关注了你的帖子《" + getTitleBreviary(article24.optString("articleTitle"))
					+ "》,<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_COMMENT_VOTE_UP):
			url += "/notifications/at";

			final JSONObject comment25 = getCommentById(dataId1);
			final JSONObject article25 = getArticleById(comment25.optString(Comment.COMMENT_ON_ARTICLE_ID));
			final String follower25 = getUserNameById(dataId2);
			content = "@" + follower25 + "赞同了你在《" + getTitleBreviary(article25.optString("articleTitle")) + "》中的回帖\""
					+ getTitleBreviary(getCommentMetaDesc(comment25)) + "\",<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_COMMENT_VOTE_DOWN):
			url += "/notifications/at";

			final JSONObject comment26 = getCommentById(dataId1);
			final JSONObject article26 = getArticleById(comment26.optString(Comment.COMMENT_ON_ARTICLE_ID));
			final String follower26 = getUserNameById(dataId2);
			content = "@" + follower26 + "反对了你在《" + getTitleBreviary(article26.optString("articleTitle")) + "》中的回帖\""
					+ getTitleBreviary(getCommentMetaDesc(comment26)) + "\",<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_ARTICLE_VOTE_UP):
			url += "/notifications/at";

			final JSONObject article27 = getArticleById(dataId1);
			final String follower27 = getUserNameById(dataId2);
			content = "@" + follower27 + "赞同了你的帖子《" + getTitleBreviary(article27.optString("articleTitle"))
					+ "》,<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_ARTICLE_VOTE_DOWN):
			url += "/notifications/at";

			final JSONObject article28 = getArticleById(dataId1);
			final String follower28 = getUserNameById(dataId2);
			content = "@" + follower28 + "反对了你的帖子《" + getTitleBreviary(article28.optString("articleTitle"))
					+ "》,<a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
			break;
		case (Notification.DATA_TYPE_C_USER_TO_UPGRADE):
			url += "/notifications/point";

			JSONObject user29 = userRepository.get(userId);
			String userLevel29 = userLevelService.getUserLevel(user29.optInt("userPoint"));
			String userLevelType29 = userLevelService.getUserLevelType(user29.optInt("userPoint"));
			content = "恭喜你，等级提升至【 " + userLevel29 + " 】， <a href='" + url + "'>点击查看</a>";

			textRequest = wechatMessageService.getTextRequest(userInfo, content);
			wechatMessageService.sendTextMessage(textRequest);
		default:
			break;
		}
	}

	private Article getArticle(JSONObject articleParam) {
		Article article = new Article();
		article.setBtntxt(articleParam.optString("btntxt"));
		article.setTitle(articleParam.optString("title"));
		article.setUrl(articleParam.optString("url"));
		article.setPicurl(articleParam.optString("picurl"));
		article.setDescription(articleParam.optString("description"));

		return article;
	}

	private Article getArticle(String btntxt, String title, String url, String picurl, String description) {
		JSONObject articleParam = new JSONObject();

		articleParam.put("btntxt", btntxt);
		articleParam.put("title", title);
		articleParam.put("url", url);
		articleParam.put("picurl", picurl);
		articleParam.put("description", description);

		return getArticle(articleParam);
	}

	private MpArticle getMpArticle(JSONObject mpArticleParam) {
		MpArticle mpArticle = new MpArticle();

		mpArticle.setAuthor(mpArticleParam.optString("author"));
		mpArticle.setContent(mpArticleParam.optString("content"));
		mpArticle.setContent_source_url(mpArticleParam.optString("content_source_url"));
		mpArticle.setDigest(mpArticleParam.optString("digest"));
		mpArticle.setThumb_media_id(mpArticleParam.optString("thumb_media_id"));
		mpArticle.setTitle(mpArticleParam.optString("title"));

		return mpArticle;
	}

	private MpArticle getMpArticle(String author, String content, String content_source_url, String digest,
			String thumb_media_id, String title) {
		JSONObject mpArticleParam = new JSONObject();

		mpArticleParam.put("author", author);
		mpArticleParam.put("title", title);
		mpArticleParam.put("content", content);
		mpArticleParam.put("content_source_url", content_source_url);
		mpArticleParam.put("digest", digest);
		mpArticleParam.put("thumb_media_id", thumb_media_id);

		return getMpArticle(mpArticleParam);
	}

	private UserInfo getUserInfo(final String loginName, final String userType) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserType(userType);
		userInfo.setLoginName(loginName);

		return userInfo;
	}

	private UserInfo getUserInfo(final JSONObject userInfoParam) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserType(userInfoParam.optString("userType"));
		userInfo.setLoginName(userInfoParam.optString("loginName"));

		return userInfo;
	}

	private UserInfo getUserInfoById(final String userId) {
		try {
			final List<JSONObject> users = userRepository.select("SELECT * FROM symphony_user WHERE oId = ?", userId);
			if (!users.isEmpty()) {
				UserInfo userInfo = new UserInfo();
				userInfo.setLoginName(users.get(0).optString("userName"));
				userInfo.setUserType(users.get(0).optString("userCountry"));
				return userInfo;
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets users error", e);
		}
		return null;
	}

	private String getUserNameById(final String userId) {
		try {
			final List<JSONObject> users = userRepository.select("SELECT * FROM symphony_user WHERE oId = ?", userId);
			if (!users.isEmpty()) {
				return users.get(0).optString(User.USER_NICKNAME);
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets users error", e);
		}
		return null;
	}

	private JSONObject getArticleById(final String dataId) {
		try {
			final List<JSONObject> articles = userRepository.select("SELECT * FROM symphony_article WHERE oId = ?",
					dataId);
			if (!articles.isEmpty()) {
				return articles.get(0);
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets users error", e);
		}
		return null;
	}

	private JSONObject getCommentById(final String dataId) {
		try {
			final List<JSONObject> comments = userRepository.select("SELECT * FROM symphony_comment WHERE oId = ?",
					dataId);
			if (!comments.isEmpty()) {
				return comments.get(0);
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets comment error", e);
		}
		return null;
	}

	private String getArticleThumbnail(final JSONObject article) {
		final int articleType = article.optInt(org.b3log.symphony.model.Article.ARTICLE_TYPE);
		if (org.b3log.symphony.model.Article.ARTICLE_TYPE_C_THOUGHT == articleType) {
			return "";
		}

		final String content = article.optString(org.b3log.symphony.model.Article.ARTICLE_CONTENT);
		final String html = Markdowns.toHTML(content);
		String ret = StringUtils.substringBetween(html, "<img src=\"", "\"");

		final boolean qiniuEnabled = Symphonys.getBoolean("qiniu.enabled");
		if (qiniuEnabled) {
			final String qiniuDomain = Symphonys.get("qiniu.domain");
			if (StringUtils.startsWith(ret, qiniuDomain)) {
				ret += "?imageView2/1/w/" + 180 + "/h/" + 135 + "/format/jpg/interlace/1/q";
			} else {
				ret = "";
			}
		} else {
			if (!StringUtils.startsWith(ret, Latkes.getServePath())) {
				ret = "";
			}
		}

		if (StringUtils.isBlank(ret)) {
			ret = "";
		}

		return ret;
	}

	private String getArticleBreviary(final String content) {
		if (StringUtils.length(content) < 108) {
			return content;
		} else {
			return content.substring(0, 108) + "...";
		}
	}

	private String getTitleBreviary(final String content) {
		if (StringUtils.length(content) < 30) {
			return content;
		} else {
			return content.substring(0, 30) + "...";
		}
	}

	private JSONObject getRewardById(final String dataId) {
		try {
			final List<JSONObject> rewards = userRepository.select("SELECT * FROM symphony_reward WHERE oId = ?",
					dataId);
			if (!rewards.isEmpty()) {
				return rewards.get(0);
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets reward error", e);
		}
		return null;
	}

	private String getMedia_id(MessageEnum mediaType, String filePath) {
		WechatMedia wechatMedia = messageService.uploadMedia(mediaType, filePath);
		return wechatMedia.getMedia_id();
	}

	private List<UserInfo> getUsersByTagId(String dataId) {
		List<UserInfo> users = new ArrayList<UserInfo>();

		try {
			final List<JSONObject> followers = followRepository
					.select("SELECT * FROM symphony_follow WHERE followingId = ? AND followingType = 1", dataId);
			if (!followers.isEmpty()) {
				for (JSONObject follower : followers) {
					UserInfo userInfo = getUserInfoById(follower.optString("followerId"));
					users.add(userInfo);
				}
				return users;
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets users error", e);
		}
		return null;
	}

	private List<UserInfo> getUsers() {
		List<UserInfo> users = new ArrayList<UserInfo>();
		try {
			List<JSONObject> userList = userQueryService.getUsers();
			if (!userList.isEmpty()) {
				for (JSONObject user : userList) {
					UserInfo userInfo = getUserInfoById(user.optString(Keys.OBJECT_ID));
					users.add(userInfo);
				}
				return users;
			}
		} catch (ServiceException e) {
			LOGGER.log(Level.ERROR, "Gets users error", e);
		}
		return null;
	}

	private String getTagIdByTagTitle(String tagTitle) {
		try {
			final List<JSONObject> tags = tagRepository.select("SELECT * FROM symphony_tag WHERE tagTitle = ?",
					tagTitle);
			if (!tags.isEmpty()) {
				return tags.get(0).optString("oId");
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets tagId error", e);
		}
		return null;

	}

	private boolean judgeFollowBetweenUserAndTag(String userId, String tagId) {
		try {
			final List<JSONObject> followers = followRepository.select(
					"SELECT * FROM symphony_follow WHERE followingId = ? AND followingType = 1 AND followerId = ?",
					tagId, userId);
			if (!followers.isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Gets followers error", e);
		}
		return false;
	}

	/***
	 * Gets meta description content of the specified comment.
	 * 
	 * @param comment
	 *            the specified comment
	 * @return meta description
	 */
	private String getCommentMetaDesc(final JSONObject comment) {
		final String commentId = comment.optString(Keys.OBJECT_ID);

		final int length = Integer.valueOf("150");

		String ret = comment.optString(Comment.COMMENT_CONTENT);
		ret = Emotions.clear(ret);

		final Whitelist whitelist = Whitelist.basicWithImages();
		whitelist.addTags("object", "video");
		ret = Jsoup.clean(ret, whitelist);

		final int threshold = 20;
		String[] pics = StringUtils.substringsBetween(ret, "<img", ">");
		if (null != pics) {
			if (pics.length > threshold) {
				pics = Arrays.copyOf(pics, threshold);
			}

			final String[] picsRepl = new String[pics.length];
			for (int i = 0; i < picsRepl.length; i++) {
				picsRepl[i] = langPropsService.get("picTagLabel", Latkes.getLocale());
				pics[i] = "<img" + pics[i] + ">";

				if (i > threshold) {
					break;
				}
			}

			ret = StringUtils.replaceEach(ret, pics, picsRepl);
		}

		String[] objs = StringUtils.substringsBetween(ret, "<object>", "</object>");
		if (null != objs) {
			if (objs.length > threshold) {
				objs = Arrays.copyOf(objs, threshold);
			}

			final String[] objsRepl = new String[objs.length];
			for (int i = 0; i < objsRepl.length; i++) {
				objsRepl[i] = langPropsService.get("objTagLabel", Latkes.getLocale());
				objs[i] = "<object>" + objs[i] + "</object>";

				if (i > threshold) {
					break;
				}
			}

			ret = StringUtils.replaceEach(ret, objs, objsRepl);
		}

		objs = StringUtils.substringsBetween(ret, "<video", "</video>");
		if (null != objs) {
			if (objs.length > threshold) {
				objs = Arrays.copyOf(objs, threshold);
			}

			final String[] objsRepl = new String[objs.length];
			for (int i = 0; i < objsRepl.length; i++) {
				objsRepl[i] = langPropsService.get("objTagLabel", Latkes.getLocale());
				objs[i] = "<video" + objs[i] + "</video>";

				if (i > threshold) {
					break;
				}
			}

			ret = StringUtils.replaceEach(ret, objs, objsRepl);
		}

		String tmp = Jsoup.clean(Jsoup.parse(ret).text(), Whitelist.none());
		if (tmp.length() >= length && null != pics) {
			tmp = StringUtils.substring(tmp, 0, length) + " ....";
			ret = tmp.replaceAll("\"", "'");

			return ret;
		}

		String[] urls = StringUtils.substringsBetween(ret, "<a", "</a>");
		if (null != urls) {
			if (urls.length > threshold) {
				urls = Arrays.copyOf(urls, threshold);
			}

			final String[] urlsRepl = new String[urls.length];
			for (int i = 0; i < urlsRepl.length; i++) {
				urlsRepl[i] = langPropsService.get("urlTagLabel", Latkes.getLocale());
				urls[i] = "<a" + urls[i] + "</a>";
			}

			ret = StringUtils.replaceEach(ret, urls, urlsRepl);
		}

		tmp = Jsoup.clean(Jsoup.parse(ret).text(), Whitelist.none());
		if (tmp.length() >= length) {
			tmp = StringUtils.substring(tmp, 0, length) + " ....";
		}

		ret = tmp.replaceAll("\"", "'");

		return ret;
	}
}
