package org.b3log.symphony.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.repository.UserRepository;
import org.json.JSONObject;

/**
 * User level service.
 *
 * @author <a >Xu Chang</a>
 */
@Service
public class UserLevelService {

	static int[] rankPoints = { 0, 30, 60, 100, 150, 200, 250, 350, 450, 600, 750, 900, 1100, 1300, 1500, 1700, 2000,
			2300, 3000, 3500, 4000, Integer.MAX_VALUE };
	static String[] rankNames = { "青铜Ⅲ", "青铜Ⅱ", "青铜Ⅰ", "白银Ⅲ", "白银Ⅱ", "白银Ⅰ", "黄金Ⅲ", "黄金Ⅱ", "黄金Ⅰ", "白金Ⅲ", "白金Ⅱ", "白金Ⅰ",
			"钻石Ⅲ", "钻石Ⅱ", "钻石Ⅰ", "大师Ⅲ", "大师Ⅱ", "大师Ⅰ", "王者Ⅲ", "王者Ⅱ", "王者Ⅰ" };
	static String[] rankType = { "level0s", "level1s", "level2s", "level3s", "level4s", "level5s", "level6s" };
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserLevelService.class);

	/**
	 * All usernames.
	 */
	public static final List<JSONObject> USER_NAMES = Collections.synchronizedList(new ArrayList<JSONObject>());

	/**
	 * User repository.
	 */
	@Inject
	private UserRepository userRepository;

	/**
	 * Get the user level according to userPoints.
	 * 
	 * @param userPoints
	 * @return
	 */
	public String getUserLevel(int userPoints) {
		for (int i = 0; i < rankPoints.length; i++) {
			if (userPoints >= rankPoints[i] && userPoints < rankPoints[i + 1]) {
				return rankNames[i];
			}
		}
		return null;
	}

	/**
	 * Get the user level type according to userPoints.
	 * 
	 * @param userPoints
	 * @return
	 */
	public String getUserLevelType(int userPoints) {
		for (int i = 0; i < rankPoints.length; i++) {
			if (userPoints >= rankPoints[i] && userPoints < rankPoints[i + 1]) {
				return rankType[i / 3];
			}
		}
		return null;
	}

	/**
	 * Get the points to upgrade according to userPoints.
	 * 
	 * @param userPoints
	 * @return
	 */
	public int getUpgradePoints(int userPoints) {
		for (int i = 0; i < rankPoints.length; i++) {
			if (userPoints >= rankPoints[i] && userPoints < rankPoints[i + 1]) {
				return rankPoints[i + 1] - userPoints;
			}
		}
		return -1;
	}

	/**
	 * Get the user ranking according to userName.
	 * 
	 * @param userName
	 * @param avatarViewMode
	 * @return
	 */
	public int getRanking(String userName, int avatarViewMode) {
		try {
			if ("admin".equals(userName)) {
				return 0;
			}
			final List<JSONObject> result = userRepository
					.select("SELECT * FROM symphony_user order by userPoint desc;");
			if (!result.isEmpty()) {
				int flag = 1;
				for (int i = 0; i < result.size(); i++) {
					String name = result.get(i).optString(User.USER_NAME);
					if ("admin".equals(name)) {
						flag = 0;
					}
					if (userName.equals(name)) {
						return i + flag;
					}
				}
			}
		} catch (final Exception e) {
			LOGGER.log(Level.ERROR, "GET user[" + userName + "] rank failed!", e);
		}

		return -1;
	}

	/**
	 * To determine if the user has been upgraded.
	 * 
	 * @param before
	 *            previous points
	 * @param after
	 *            current points
	 * @return
	 */
	public boolean isUpgrade(int before, int after) {
		for (int point : rankPoints) {
			if (point > before && point <= after) {
				return true;
			}
		}
		return false;
	}
}
