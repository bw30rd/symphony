package org.b3log.symphony.service;

import java.util.ArrayList;
import java.util.List;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.conf.AuthorConfiguration;
import org.json.JSONObject;

import com.bw.authcenter.authorapi.MessageService;
import com.bw.authcenter.authorapi.message.pojo.Article;
import com.bw.authcenter.authorapi.message.pojo.MpArticle;
import com.bw.authcenter.authorapi.message.pojo.UserInfo;
import com.bw.authcenter.authorapi.message.pojo.WechatResponse;
import com.bw.authcenter.authorapi.message.request.BaseRequest;
import com.bw.authcenter.authorapi.message.request.FileRequest;
import com.bw.authcenter.authorapi.message.request.ImageRequest;
import com.bw.authcenter.authorapi.message.request.MpnewsRequest;
import com.bw.authcenter.authorapi.message.request.NewsRequest;
import com.bw.authcenter.authorapi.message.request.TextRequest;
import com.bw.authcenter.authorapi.message.request.TextcardRequest;
import com.bw.authcenter.authorapi.message.request.VideoRequest;
import com.bw.authcenter.authorapi.message.request.VoiceRequest;

/**
 * wechat message service.
 *
 * @author <a >Xu Chang</a>
 */
@Service
public class WechatMessageService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(WechatMessageService.class);

	private static MessageService messageService = AuthorConfiguration.initMessageService();

	public TextcardRequest getTextcardRequest(List<UserInfo> users, String title, String description, String url,
			String btntxt) {
		TextcardRequest textcardRequest = new TextcardRequest();
		textcardRequest.setBtntxt(btntxt);
		textcardRequest.setDescription(description);
		textcardRequest.setUsers(users);
		textcardRequest.setTitle(title);
		textcardRequest.setUrl(url);

		return textcardRequest;
	}

	public TextcardRequest getTextcardRequest(UserInfo userInfo, String title, String description, String url,
			String btntxt) {

		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);

		return getTextcardRequest(users, title, description, url, btntxt);
	}

	public TextcardRequest getTextcardRequest(String loginName, String userType, String title, String description,
			String url, String btntxt) {
		UserInfo userInfo = getUserInfo(loginName, userType);
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);
		return getTextcardRequest(users, title, description, url, btntxt);
	}

	public TextcardRequest getTextcardRequest(JSONObject messageParam) {
		String loginName = "";
		String userType = "";
		String title = "";
		String description = "";
		String url = "";
		String btntxt = "";
		List<UserInfo> users = new ArrayList<UserInfo>();

		try {
			loginName = messageParam.optString("loginName");
			userType = messageParam.optString("userType");
			UserInfo userInfo = getUserInfo(loginName, userType);
			users.add(userInfo);
			title = messageParam.optString("title");
			description = messageParam.optString("description");
			url = messageParam.optString("url");
			btntxt = messageParam.optString("btntxt");
		} catch (final Exception e) {
			LOGGER.log(Level.ERROR, "Gets TextcardRequest error", e);
		}
		return getTextcardRequest(users, title, description, url, btntxt);
	}

	/***
	 * To send a wechat textcard message.
	 * 
	 * @param messageParam
	 */
	public void sendTextcardMessage(JSONObject messageParam) {
		sendWechatMessage(getTextcardRequest(messageParam));
	}

	public void sendTextcardMessage(TextcardRequest textcardRequest) {
		sendWechatMessage(textcardRequest);
	}

	/***
	 * To make up a wechat text message.
	 * 
	 * @param users
	 * @param content
	 * @return
	 */
	public TextRequest getTextRequest(List<UserInfo> users, String content) {
		TextRequest textRequest = new TextRequest();
		textRequest.setUsers(users);
		textRequest.setContent(content);

		return textRequest;
	}

	public TextRequest getTextRequest(UserInfo userInfo, String content) {
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);

		return getTextRequest(users, content);
	}

	public TextRequest getTextRequest(String loginName, String userType, String content) {
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName(loginName);
		userInfo.setUserType(userType);
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);

		return getTextRequest(users, content);
	}

	public TextRequest getTextRequest(JSONObject messageParam) {
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName(messageParam.optString("loginName"));
		userInfo.setUserType(messageParam.optString("userType"));
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);

		return getTextRequest(users, messageParam.optString("content"));
	}

	/***
	 * To send a wechat text message.
	 * 
	 * @param messageParam
	 */
	public void sendTextMessage(JSONObject messageParam) {
		sendWechatMessage(getTextRequest(messageParam));
	}

	public void sendTextMessage(TextRequest textRequest) {
		sendWechatMessage(textRequest);
	}

	/***
	 * To send a wechat image message.
	 * 
	 * @param messageParam
	 */
	public void sendImageMessage(JSONObject messageParam) {
		List<UserInfo> users = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName("userId");
		userInfo.setUserType("0");
		users.add(userInfo);

		ImageRequest imageRequest = new ImageRequest();
		imageRequest.setUsers(users);
		imageRequest.setMediaId(messageParam.optString("mediaId"));
		;

		sendWechatMessage(imageRequest);
	}

	/***
	 * To send a wechat video message.
	 * 
	 * @param messageParam
	 */
	public void sendVideoMessage(JSONObject messageParam) {
		List<UserInfo> users = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName("userId");
		userInfo.setUserType("0");
		users.add(userInfo);

		VideoRequest videoRequest = new VideoRequest();
		videoRequest.setUsers(users);
		videoRequest.setMediaId(messageParam.optString("mediaId"));
		videoRequest.setDescription(messageParam.optString("description"));
		videoRequest.setTitle(messageParam.optString("title"));

		sendWechatMessage(videoRequest);
	}

	/***
	 * To send a wechat file message.
	 * 
	 * @param messageParam
	 */
	public void sendFileMessage(JSONObject messageParam) {
		List<UserInfo> users = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName("userId");
		userInfo.setUserType("0");
		users.add(userInfo);

		FileRequest fileRequest = new FileRequest();
		fileRequest.setUsers(users);
		fileRequest.setMediaId(messageParam.optString("mediaId"));

		sendWechatMessage(fileRequest);
	}

	public void sendVoiceMessage(JSONObject messageParam) {
		List<UserInfo> users = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName("userId");
		userInfo.setUserType("0");
		users.add(userInfo);

		VoiceRequest voiceRequest = new VoiceRequest();
		voiceRequest.setUsers(users);
		voiceRequest.setMediaId(messageParam.optString("mediaId"));

		sendWechatMessage(voiceRequest);
	}

	public NewsRequest getNewsRequest(List<UserInfo> users, List<Article> articles) {
		NewsRequest newsRequest = new NewsRequest();
		newsRequest.setArticles(articles);
		newsRequest.setUsers(users);
		return newsRequest;
	}

	public NewsRequest getNewsRequest(List<UserInfo> users, Article article) {
		List<Article> articles = new ArrayList<Article>();
		articles.add(article);
		return getNewsRequest(users, articles);
	}

	public NewsRequest getNewsRequest(UserInfo userInfo, Article article) {
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);
		List<Article> articles = new ArrayList<Article>();
		articles.add(article);
		return getNewsRequest(users, articles);
	}

	public NewsRequest getNewsRequest(String loginName, String userType, Article article) {
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName(loginName);
		userInfo.setUserType(userType);
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);
		List<Article> articles = new ArrayList<Article>();
		articles.add(article);
		return getNewsRequest(users, articles);
	}

	public NewsRequest getNewsRequest(JSONObject messageParam, Article article) {
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName(messageParam.optString("loginName"));
		userInfo.setUserType(messageParam.optString("userType"));
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);
		List<Article> articles = new ArrayList<Article>();
		articles.add(article);
		return getNewsRequest(users, articles);
	}

	/***
	 * To send a wechat news message.
	 * 
	 * @param messageParam
	 */
	public void sendNewsMessage(JSONObject messageParam, Article article) {
		sendWechatMessage(getNewsRequest(messageParam, article));
	}

	public void sendNewsMessage(NewsRequest newsRequest) {
		sendWechatMessage(newsRequest);
	}

	public MpnewsRequest getMpnewsRequest(List<UserInfo> users, List<MpArticle> mpArticles) {
		MpnewsRequest mpnewsRequest = new MpnewsRequest();
		mpnewsRequest.setArticles(mpArticles);
		mpnewsRequest.setUsers(users);

		return mpnewsRequest;
	}

	public MpnewsRequest getMpnewsRequest(List<UserInfo> users, MpArticle mpArticle) {
		List<MpArticle> mpArticles = new ArrayList<MpArticle>();
		mpArticles.add(mpArticle);

		return getMpnewsRequest(users, mpArticles);
	}

	public MpnewsRequest getMpnewsRequest(UserInfo userInfo, MpArticle mpArticle) {
		List<MpArticle> mpArticles = new ArrayList<MpArticle>();
		mpArticles.add(mpArticle);
		List<UserInfo> users = new ArrayList<UserInfo>();
		users.add(userInfo);

		return getMpnewsRequest(users, mpArticles);
	}

	public MpnewsRequest getMpnewsRequest(String loginName, String userType, MpArticle mpArticle) {
		List<MpArticle> mpArticles = new ArrayList<MpArticle>();
		mpArticles.add(mpArticle);
		List<UserInfo> users = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName(loginName);
		userInfo.setUserType(userType);
		users.add(userInfo);

		return getMpnewsRequest(users, mpArticles);
	}

	public MpnewsRequest getMpnewsRequest(JSONObject messageParam, MpArticle mpArticle) {
		List<MpArticle> mpArticles = new ArrayList<MpArticle>();
		mpArticles.add(mpArticle);
		List<UserInfo> users = new ArrayList<UserInfo>();
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName(messageParam.optString("loginName"));
		userInfo.setUserType(messageParam.optString("userType"));
		users.add(userInfo);

		return getMpnewsRequest(users, mpArticles);
	}

	/***
	 * To send a wechat mpnews message.
	 * 
	 * @param messageParam
	 */
	public void sendMpnewsMessage(JSONObject messageParam, MpArticle mpArticle) {
		sendWechatMessage(getMpnewsRequest(messageParam, mpArticle));
	}

	public void sendMpnewsMessage(MpnewsRequest mpnewsRequest) {
		sendWechatMessage(mpnewsRequest);
	}

	/***
	 * to send a wechat message.
	 * 
	 * @param text
	 */
	private void sendWechatMessage(final BaseRequest text) {
		WechatResponse wechatResponse = messageService.sendWechatMessage(text);
		LOGGER.info(text.getClass() + ":" + wechatResponse);
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

}
