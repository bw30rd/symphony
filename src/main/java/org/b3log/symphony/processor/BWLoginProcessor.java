package org.b3log.symphony.processor;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.LatkeBeanManagerImpl;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.After;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Requests;
import org.b3log.latke.util.Strings;
import org.b3log.symphony.conf.AuthorConfiguration;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.advice.PermissionGrant;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchEndAdvice;
import org.b3log.symphony.processor.advice.stopwatch.StopwatchStartAdvice;
import org.b3log.symphony.processor.advice.validate.UserRegister2Validation;
import org.b3log.symphony.processor.advice.validate.UserRegisterValidation;
import org.b3log.symphony.repository.TagRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.FollowMgmtService;
import org.b3log.symphony.service.InvitecodeMgmtService;
import org.b3log.symphony.service.InvitecodeQueryService;
import org.b3log.symphony.service.NotificationMgmtService;
import org.b3log.symphony.service.OptionQueryService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.service.RoleQueryService;
import org.b3log.symphony.service.TagQueryService;
import org.b3log.symphony.service.TimelineMgmtService;
import org.b3log.symphony.service.UserMgmtService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.service.VerifycodeMgmtService;
import org.b3log.symphony.service.VerifycodeQueryService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import com.bw.authcenter.authorapi.AuthorService;
import com.bw.authcenter.authorapi.modle.AccesskeyUrlResponse;
import com.bw.authcenter.authorapi.modle.InitParameters;
import com.bw.authcenter.authorapi.modle.ListUserResponse;
import com.bw.authcenter.authorapi.modle.UserBean;
import com.bw.authcenter.authorapi.modle.UserInfoResponse;
import com.bw.authcenter.authorapi.modle.WeChatUserResponse;

@RequestProcessor
public class BWLoginProcessor {

	/**
	 * Wrong password tries.
	 * <p>
	 * &lt;userId, {"wrongCount": int, "captcha": ""}&gt;
	 * </p>
	 */
	public static final Map<String, JSONObject> WRONG_PWD_TRIES = new ConcurrentHashMap<>();

	public static String URL = "http://authcenter.bwae.org/authorcenter/wechatlogin/messageredirect?label=begeek&redirecturl="
			+ Latkes.getServePath() + "/begeek/login?from=wechat";

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(LoginProcessor.class.getName());

	private static AuthorService authorService = AuthorConfiguration.initAuthorService();
	/**
	 * User repository.
	 */
	@Inject
	private UserRepository userRepository;
	/**
	 * User management service.
	 */
	@Inject
	private UserMgmtService userMgmtService;
	/**
	 * User query service.
	 */
	@Inject
	private UserQueryService userQueryService;
	/**
	 * Language service.
	 */
	@Inject
	private LangPropsService langPropsService;
	/**
	 * Pointtransfer management service.
	 */
	@Inject
	private PointtransferMgmtService pointtransferMgmtService;
	/**
	 * Data model service.
	 */
	@Inject
	private DataModelService dataModelService;
	/**
	 * Verifycode management service.
	 */
	@Inject
	private VerifycodeMgmtService verifycodeMgmtService;
	/**
	 * Verifycode query service.
	 */
	@Inject
	private VerifycodeQueryService verifycodeQueryService;
	/**
	 * Timeline management service.
	 */
	@Inject
	private TimelineMgmtService timelineMgmtService;
	/**
	 * Option query service.
	 */
	@Inject
	private OptionQueryService optionQueryService;
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
	 * Invitecode management service.
	 */
	@Inject
	private NotificationMgmtService notificationMgmtService;
	/**
	 * Role query service.
	 */
	@Inject
	private RoleQueryService roleQueryService;
	/**
	 * Tag query service.
	 */
	@Inject
	private TagQueryService tagQueryService;
	@Inject
	private FollowMgmtService followMgmtService;
	@Inject
	private TagRepository tagRepository;
	
	/**
	 * (Invalid)Shows login page.
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
	// @RequestProcessing(value = "/login", method = HTTPRequestMethod.GET)
	@Before(adviceClass = StopwatchStartAdvice.class)
	@After(adviceClass = { PermissionGrant.class, StopwatchEndAdvice.class })
	public void showLogin(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		if (null != userQueryService.getCurrentUser(request) || userMgmtService.tryLogInWithCookie(request, response)) {
			response.sendRedirect(Latkes.getServePath());
		}

		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);

		String referer = request.getParameter(Common.GOTO);
		if (StringUtils.isBlank(referer)) {
			referer = request.getHeader("referer");
		}

		if (StringUtils.isBlank(referer)) {
			referer = Latkes.getServePath();
		}

		renderer.setTemplateName("/verify/login.ftl");

		final Map<String, Object> dataModel = renderer.getDataModel();
		dataModel.put(Common.GOTO, referer);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	@RequestProcessing(value = "/loginTips", method = HTTPRequestMethod.GET)
	public void showLoginTips(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);

		String referer = request.getParameter(Common.GOTO);
		if (StringUtils.isBlank(referer)) {
			referer = request.getHeader("referer");
		}

		if (StringUtils.isBlank(referer)) {
			referer = Latkes.getServePath();
		}

		renderer.setTemplateName("/verify/loginTips.html");

		final Map<String, Object> dataModel = renderer.getDataModel();
		dataModel.put(Common.GOTO, referer);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	@RequestProcessing(value = "/loginPage", method = HTTPRequestMethod.GET)
	public void showLoginPage(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
		context.setRenderer(renderer);

		String referer = request.getParameter(Common.GOTO);
		if (StringUtils.isBlank(referer)) {
			referer = request.getHeader("referer");
		}

		if (StringUtils.isBlank(referer)) {
			referer = Latkes.getServePath();
		}

		renderer.setTemplateName("/verify/login-page.ftl");

		final Map<String, Object> dataModel = renderer.getDataModel();
		dataModel.put(Common.GOTO, URL);

		dataModelService.fillHeaderAndFooter(request, response, dataModel);
	}

	/**
	 * Entrance of BW Authentication platform.
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
	@RequestProcessing(value = "/begeek", method = HTTPRequestMethod.GET)
	public void BWloginEntry(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		AccesskeyUrlResponse accesskeyUrlResponse = authorService.getAccesskeyUrl();
		if (accesskeyUrlResponse.getCode() == 0) {
			String url = accesskeyUrlResponse.getAccesskeyurl();
			String redirecturl = String.format("%s://%s:%s/symphony/begeek/login", Latkes.getServerScheme(),
					Latkes.getServerHost(), Latkes.getServerPort());
			response.sendRedirect(url + "&redirecturl=" + redirecturl);
		} else {
			LOGGER.info("login failed(" + accesskeyUrlResponse.getCode() + "):" + accesskeyUrlResponse.getMessage());
		}

	}

	/**
	 * Logins user.
	 *
	 * @param context
	 *            the specified context
	 * @param request
	 *            the specified request
	 * @param response
	 *            the specified response
	 * @throws ServletException
	 *             servlet exception
	 * @throws IOException
	 *             io exception
	 */
	@RequestProcessing(value = "/BWLogin", method = HTTPRequestMethod.GET)
	public void login(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException, IOException {
		context.renderJSON().renderMsg(langPropsService.get("loginFailLabel"));

		final JSONObject requestJSONObject = Requests.parseRequestJSONObject(request, response);
		final String nameOrEmail = request.getParameter("nameOrEmail");
		final String from = request.getParameter("from");
		final String myUrl = request.getParameter("myUrl");

		try {
			JSONObject user = userQueryService.getUserByName(nameOrEmail);
			if (null == user) {
				user = userQueryService.getUserByEmail(nameOrEmail);
			}
			if (null == user) {
				context.renderMsg(langPropsService.get("notFoundUserLabel"));
				return;
			}

			final String token = Sessions.login(request, response, user,
					requestJSONObject.optBoolean(Common.REMEMBER_LOGIN));

			final String ip = Requests.getRemoteAddr(request);
			userMgmtService.updateOnlineStatus(user.optString(Keys.OBJECT_ID), ip, true);

			context.renderMsg("").renderTrueResult();
			context.renderJSONValue(Common.TOKEN, token);

			if ("wechat".equals(from)) {
				String url = Latkes.getServePath();
				response.sendRedirect(url);
			} else if (StringUtils.isNotBlank(myUrl)) {
				response.sendRedirect(myUrl);
			} else {
				response.sendRedirect(Latkes.getServePath() + "/loginTips");
			}

		} catch (final ServiceException e) {
			context.renderMsg(langPropsService.get("loginFailLabel"));
		}
	}

	/**
	 * checks a user from BWAE or other BW platforms.
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
	@RequestProcessing(value = "/begeek/login", method = HTTPRequestMethod.GET)
	public void checkUser(final HTTPRequestContext context, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		final String token = request.getParameter("token");
		String from = request.getParameter("from");
		String myUrl = request.getParameter("myUrl");
		String state = request.getParameter("state");

		if (StringUtils.isNotBlank(state)) {
			state = URLDecoder.decode(state, "utf-8");
			myUrl = state;
			from = "AE";
		}

		UserInfoResponse userInfoResponse = authorService.getUserInfo(token);
		Integer code = userInfoResponse.getCode();
		String userid = userInfoResponse.getUserId();
		String type = userInfoResponse.getType();

		if (code == 0) {
			JSONObject userdb = userQueryService.getUserByName(userid);
			if (userdb == null) {
				try {
					ListUserResponse listUserResponse = authorService.listUserInfos();
					List<UserBean> users = listUserResponse.getUsers();
					String userNickname = "";
					for (UserBean user : users) {
						if (userid.equals(user.getUserId())) {
							userNickname = user.getUserName();
							break;
						}
					}

					final JSONObject user = new JSONObject();

					final String userName = userid;
					final String email = userid + "@bw30.com";
					final String password = "123456";

					user.put(User.USER_NAME, userName);
					user.put(User.USER_EMAIL, email);
					user.put(User.USER_PASSWORD, MD5.hash(password));
					user.put(User.USER_NICKNAME, userNickname);
					user.put(UserExt.USER_STATUS, UserExt.USER_STATUS_C_VALID);
					user.put(UserExt.USER_COUNTRY, type);

					
					WeChatUserResponse response2 = authorService.getWechatUserInfo(userid,"0");
					String avatarUrl = response2.getWechatuser().getAvatar();
					if(StringUtils.isNotBlank(avatarUrl)) {
						user.put(UserExt.USER_AVATAR_URL, avatarUrl);
					}
					
					final boolean nameInvalid = UserRegisterValidation.invalidUserName(userName);
					final boolean emailInvalid = !Strings.isEmail(email);
					final boolean passwordInvalid = UserRegister2Validation.invalidUserPassword(password);

					if (nameInvalid || emailInvalid || passwordInvalid) {
						final AbstractFreeMarkerRenderer renderer = new SkinRenderer(request);
						context.setRenderer(renderer);
						renderer.setTemplateName("admin/error.ftl");
						final Map<String, Object> dataModel = renderer.getDataModel();

						if (nameInvalid) {
							dataModel.put(Keys.MSG, langPropsService.get("invalidUserNameLabel"));
						} else if (emailInvalid) {
							dataModel.put(Keys.MSG, langPropsService.get("invalidEmailLabel"));
						} else if (passwordInvalid) {
							dataModel.put(Keys.MSG, langPropsService.get("invalidPasswordLabel"));
						}

						dataModelService.fillHeaderAndFooter(request, response, dataModel);

					}

					userMgmtService.addUser(user);
					response.sendRedirect(Latkes.getServePath() + "/BWLogin?myUrl=" + myUrl + "&from=" + from
							+ "&nameOrEmail=" + userName);
				} catch (final ServiceException e) {
					LOGGER.log(Level.ERROR, e.getMessage(), e);
				}
			} else {
				//自动关注“年会”版块
//				JSONObject tag = tagRepository.getByURI("newyear");
//				if(tag != null) {
//					String oid = tag.getString("oId");
//					followMgmtService.followTag(userdb.optString(Keys.OBJECT_ID),oid);
//				}
				
				 
				String userCountry = userdb.optString(UserExt.USER_COUNTRY);
				if (StringUtils.isNotBlank(userCountry)) {
					updateUserCountry(type, userdb.optString(Keys.OBJECT_ID));
				}
				if (StringUtils.isNotBlank(myUrl)) {
					response.sendRedirect(Latkes.getServePath() + "/BWLogin?myUrl=" + myUrl + "&from=" + from
							+ "&nameOrEmail=" + userid);
				} else {
					response.sendRedirect(Latkes.getServePath() + "/BWLogin?from=" + from + "&nameOrEmail=" + userid);
				}

			}
		} else {
			LOGGER.info("login failed:" + userInfoResponse.getMessage());
		}
	}

	private void updateUserCountry(String type, String userId) {
		final LatkeBeanManager beanManager = LatkeBeanManagerImpl.getInstance();
		final UserRepository userRepository = beanManager.getReference(UserRepository.class);

		final Transaction transaction = userRepository.beginTransaction();
		try {
			final JSONObject user = userRepository.get(userId);
			user.put("userCountry", type);
			userRepository.update(userId, user);

			transaction.commit();
		} catch (final Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}

			LOGGER.log(Level.ERROR, "Update user country error", e);
		} finally {
			JdbcRepository.dispose();
		}
	}
	
	private void updateUserAvatar(String avatarUrl, String userId) {
		final LatkeBeanManager beanManager = LatkeBeanManagerImpl.getInstance();
		final UserRepository userRepository = beanManager.getReference(UserRepository.class);

		final Transaction transaction = userRepository.beginTransaction();
		try {
			final JSONObject user = userRepository.get(userId);
			user.put(UserExt.USER_AVATAR_URL, avatarUrl);
			userRepository.update(userId, user);

			transaction.commit();
		} catch (final Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}

			LOGGER.log(Level.ERROR, "Update user avatar error", e);
		} finally {
			JdbcRepository.dispose();
		}
	}

}
