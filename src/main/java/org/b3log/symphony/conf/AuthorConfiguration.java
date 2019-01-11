package org.b3log.symphony.conf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.b3log.latke.Latkes;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.util.Symphonys;

import com.bw.authcenter.authorapi.AuthorService;
import com.bw.authcenter.authorapi.MessageService;
import com.bw.authcenter.authorapi.modle.InitParameters;

@Service
public class AuthorConfiguration {
	// 北极社区配置参数
	private static String serverPublicKey = Symphonys.get("serverPublicKey");
	private static String clientPrivateKey = Symphonys.get("clientPrivateKey");
	private static String authkey = Symphonys.get("authkey");

	// 北极社区测试用配置参数
	private static String testServerPublicKey = Symphonys.get("testServerPublicKey");
	private static String testClientPrivateKey = Symphonys.get("testClientPrivateKey");
	private static String testAuthkey = Symphonys.get("testAuthkey");

	private static String severPath = Latkes.getServePath();

	public static MessageService initMessageService() {
		InitParameters initParameters = null;
		if ("http://172.16.3.83:8080/symphony".equals(severPath)) {
			initParameters = new InitParameters(testAuthkey, testServerPublicKey, testClientPrivateKey);
		} else {
			initParameters = new InitParameters(authkey, serverPublicKey, clientPrivateKey);
		}
		return MessageService.getInstance(initParameters);
	}

	public static AuthorService initAuthorService() {
		InitParameters initParameters = null;
		if ("http://172.16.3.83:8080/symphony".equals(severPath)) {
			initParameters = new InitParameters(testAuthkey, testServerPublicKey, testClientPrivateKey);
		} else {
			initParameters = new InitParameters(authkey, serverPublicKey, clientPrivateKey);
		}
		return AuthorService.getInstance(initParameters);
	}

	public static void main(String[] a) {
		// AuthorService authorService = initAuthorService();
		// ListUserResponse listUserResponse = authorService.listUserInfos();
		// List<UserBean> userList = listUserResponse.getUsers();

		String acckey = "jsksjkd983277djs8u3d";
		 int count = 0;
		// for (UserBean userBean : userList) {
		// if (userBean.getState() != null &&
		// userBean.getState().equalsIgnoreCase("离职")) {
		// continue;
		// }
		// if (userBean.getDepartmentName() != null &&
		// userBean.getDepartmentName().equalsIgnoreCase("暂定")) {
		// continue;
		// }
		// if (userBean.getDepartmentName() != null &&
		// userBean.getDepartmentName().equalsIgnoreCase("外部用户")) {
		// continue;
		// }
		// if (userBean.getCompanyid() != null && !userBean.getCompanyid().equals(1)) {
		// continue;
		// }
		// String user = userBean.getUserId();
		// long ts = new Date().getTime();
		// String tokensign = DigestUtils.md5Hex(acckey + ts + user);
		//
		// String url = String.format(
		// "http://authcenter.bwae.org/authorcenter/login/CheckToken?syscode=begeek&tokensign=%s&userid=%s&ts=%s&type=0",
		// tokensign, user, ts);
		//// get(url);
		//
		// System.out.println(count + user+ " "+get(url));
		//
		// count++;
		// }

//		String[] users = { "zul", "xieyuping", "liuss", "chenxs", "yuzhenguo", "zulina", "shaozh", "chenyy", "sunsm",
//				"zhouminghu", "liangyanbin", "zhangpp", "gufei", "zhangying", "gongyun", "sunmin", "yaojj", "lichao",
//				"jiangxq", "wangxl", "xieyan", "zhaorq", "litao", "ligc", "lxiang", "wangrui", "chengeng", "zhaoyue",
//				"hantao", "huawq", "yutc", "liyifeng", "suyl", "linmk", "xuepeng", "anxy", "haolt", "dingll", "xiajun",
//				"zhangdong", "yusb", "liuhao", "wanbei", "xiaoys", "wufan", "fangwh", "yanzhen", "wangkh", "huangcc",
//				"xionghui", "xujg", "pengwei", "wangqiang", "qiqs", "yangt", "wangxiny", "zhangaih", "dudh", "zhengyn",
//				"niuyz", "liuyq", "liuxy", "xiezs", "nieyw", "liucg", "lira", "tianch", "lisj", "zhangmeng", "lifei",
//				"zhouxj", "jianglu", "bofd", "gucs", "lining", "wanx", "fengjj", "weishan", "qujl" };
//
//		for (String user : users) {
//			long ts = new Date().getTime();
//			String tokensign = DigestUtils.md5Hex(acckey + ts + user);
//
//			String url = String.format(
//					"http://authcenter.bwae.org/authorcenter/login/CheckToken?syscode=begeek&tokensign=%s&userid=%s&ts=%s&type=0",
//					tokensign, user, ts);
//			get(url);
//			
//			System.out.println(count ++ + user);
//
//		}
//		String  user = "huangyt";
//		long ts = new Date().getTime();
//		String tokensign = DigestUtils.md5Hex(acckey + ts + user);
//
//		String url = String.format(
//				"http://authcenter.bwae.org/authorcenter/login/CheckToken?syscode=begeek&tokensign=%s&userid=%s&ts=%s&type=0",
//				tokensign, user, ts);
//		get(url);
//		
//		System.out.println(count ++ + user);

	}

	public static String get(String url) {
		BufferedReader in = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			// 建立实际的连接
			connection.connect();
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

}
