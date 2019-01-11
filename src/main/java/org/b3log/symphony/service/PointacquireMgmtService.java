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

import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.PointtransferRepository;
import org.b3log.symphony.repository.UserRepository;
import org.json.JSONObject;

/**
 * Pointacquire management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.4, May 8, 2017
 * @since 1.3.0
 */
@Service
public class PointacquireMgmtService {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(PointacquireMgmtService.class);

	/**
	 * Pointacquire repository.
	 */
	@Inject
	private PointtransferRepository pointtransferRepository;

	/**
	 * User repository.
	 */
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserLevelService userLevelService;
	
	/**
     * Notification management service.
     */
    @Inject
    private NotificationMgmtService notificationMgmtService;

	/**
	 * the specified id acquire the points with type, sum, data id and time.
	 *
	 * @param id
	 *            the specified from id, may be system "sys"
	 * @param type
	 *            the specified type
	 * @param sum
	 *            the specified sum
	 * @param dataId
	 *            the specified data id
	 * @param time
	 *            the specified time
	 * @return transfer record id, returns {@code null} if transfer failed
	 */
	public synchronized String acquire(final String fromId, final String toId, final int type, final int sum,
			final String dataId, final long time) {

		final Transaction transaction = pointtransferRepository.beginTransaction();
		try {
			final JSONObject pointacquire = new JSONObject();

			if (type != Pointtransfer.TRANSFER_TYPE_C_COMMENT_REWARD) {
				int balance = 0;
				if (!Pointtransfer.ID_C_SYS.equals(fromId)) {
					final JSONObject user = userRepository.get(fromId);
					// if (UserExt.USER_STATUS_C_VALID != fromUser.optInt(UserExt.USER_STATUS)) {
					// throw new Exception("Invalid from user [id=" + fromId + "]");
					// }
					int point = user.optInt(UserExt.USER_POINT);
					balance = point + sum;
					if (balance < 0) {
						throw new Exception("Insufficient balance");
					}
					user.put(UserExt.USER_POINT, balance);
					user.put(UserExt.USER_USED_POINT, user.optInt(UserExt.USER_USED_POINT) + sum);
					userRepository.update(fromId, user);
					
					if (userLevelService.isUpgrade(point, balance)) {
						JSONObject requestJSONObject = new JSONObject();
	                    requestJSONObject.put(Notification.NOTIFICATION_USER_ID, fromId);
	                    requestJSONObject.put(Notification.NOTIFICATION_DATA_ID, "");

	                    notificationMgmtService.addUpgradeNotification(requestJSONObject);
					}
				}
				pointacquire.put(Pointtransfer.FROM_BALANCE, balance);
			} else {
				pointacquire.put(Pointtransfer.FROM_BALANCE, 0);
			}

			final JSONObject toUser = userRepository.get(toId);
			int toBalance = 0;
			if (type == Pointtransfer.TRANSFER_TYPE_C_ADD_COMMENT
					|| type == Pointtransfer.TRANSFER_TYPE_C_COMMENT_REWARD) {
				if (!Pointtransfer.ID_C_SYS.equals(toId)) {
					
					int point = toUser.optInt(UserExt.USER_POINT);
					toBalance = point + sum;
					toUser.put(UserExt.USER_POINT, toBalance);
					userRepository.update(toId, toUser);
					
					if (userLevelService.isUpgrade(point, toBalance)) {
						JSONObject requestJSONObject = new JSONObject();
	                    requestJSONObject.put(Notification.NOTIFICATION_USER_ID, toId);
	                    requestJSONObject.put(Notification.NOTIFICATION_DATA_ID, "");

	                    notificationMgmtService.addUpgradeNotification(requestJSONObject);
					}
				}
				pointacquire.put(Pointtransfer.TO_BALANCE, toBalance);
				pointacquire.put(Pointtransfer.TO_ID, toId);
			} else {
				if (type == Pointtransfer.TRANSFER_TYPE_C_TO_VOTE_UP || type == Pointtransfer.TRANSFER_TYPE_C_VOTE_UP) {
					pointacquire.put(Pointtransfer.TO_ID, toId);
					toBalance = toUser.optInt(UserExt.USER_POINT);
					pointacquire.put(Pointtransfer.TO_BALANCE, toBalance);
				} else {
					pointacquire.put(Pointtransfer.TO_BALANCE, "0");
					pointacquire.put(Pointtransfer.TO_ID, "sys");
				}

			}

			pointacquire.put(Pointtransfer.SUM, sum);
			pointacquire.put(Pointtransfer.FROM_ID, fromId);
			pointacquire.put(Pointtransfer.TIME, time);
			pointacquire.put(Pointtransfer.TYPE, type);
			pointacquire.put(Pointtransfer.DATA_ID, dataId);

			final String ret = pointtransferRepository.add(pointacquire);

			transaction.commit();

			return ret;
		} catch (final Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}

			LOGGER.log(Level.ERROR,
					"Acquire [Id=" + fromId + ", sum=" + sum + ", type=" + type + ", dataId=" + dataId + "] error", e);

			return null;
		}
	}

	/**
	 * Adds a pointacquire with the specified request json object.
	 *
	 * @param requestJSONObject
	 *            the specified request json object, for example, "fromId"; "",
	 *            "toId": "", "sum": int, "blance": int, "time": long, "type": int,
	 *            "dataId": ""
	 * @throws ServiceException
	 *             service exception
	 */
	@Transactional
	public void addPointacquire(final JSONObject requestJSONObject) throws ServiceException {
		try {
			pointtransferRepository.add(requestJSONObject);
		} catch (final RepositoryException e) {
			final String msg = "Adds pointacquire failed";
			LOGGER.log(Level.ERROR, msg, e);

			throw new ServiceException(msg);
		}
	}
}
