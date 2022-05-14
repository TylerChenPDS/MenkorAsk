package org.example;

import org.example.utils.HttpClientUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import java.util.Optional;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.text.html.Option;

/**
 * @author TylerChen
 * @date 2022/5/13 - 9:41
 */
public class Main {
	static String url = "https://api.menkor.com/workflow/task/details";
	static Map<String, String> params = new HashMap<>();
	static Map<String, String> headers;

	static String externalReviewUrl = "https://api.menkor.com/education/paper/get/review/result";
	static Map<String, String> externalReviewUrlParams = new HashMap<>();
	static Map<String, String> externalReviewUrlHeaders;

	static {
		headers = getHeaderFromFile("header");
		params.put("taskId", "103999296");
		params.put("needTransform", "false");
		externalReviewUrlHeaders = getHeaderFromFile("detailHeader");
		externalReviewUrlParams.put("taskId", "104000273");
	}

	// 收件人电子邮箱
	static String to = "1969922921@qq.com";

	// 发件人电子邮箱
	static String from = "1969922921@qq.com";

	// 指定发送邮件的主机为 smtp.qq.com
	static String host = "smtp.qq.com";  //QQ 邮件服务器
	// 获取系统属性
	static Properties properties = System.getProperties();

	static String authPass = "lmnkoqylwmltbabi";

	static {
		// 设置邮件服务器
		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.auth", "true");
	}

	public static void main(String[] args) throws InterruptedException {
		// 从早上7点45开始测试到晚上11点，每隔半个小时，发送一次邮件
		while (true) {
			Thread.sleep(1000);
			LocalTime localTime = LocalTime.now();
			int hour = localTime.getHour();
			int minute = localTime.getMinute();
			if(hour < 7 || minute != 15 && minute != 45) {
				continue;
			}
			JSONObject paramMap = HttpClientUtils.getParamMap(url, params, headers);
			JSONArray jsonArray = paramMap.getJSONObject("data")
					.getJSONArray("taskOperationVOS");
			JSONObject internalReview = jsonArray.getJSONObject(3);
			JSONObject externalReview = jsonArray.getJSONObject(4);
			sendMail(internalReview.getInt("state"), externalReview.getInt("state"));
			Thread.sleep(1000 * 60);
		}

	}

	// 内外审状态，0，没结果， 2， 通过  其它，不通过
	static void sendMail(int internalReview, int externalReview) {
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, authPass); //发件人邮件用户名、授权码
			}
		});
		try{
			// 创建默认的 MimeMessage 对象
			MimeMessage message = new MimeMessage(session);
			// Set From: 头部头字段
			message.setFrom(new InternetAddress(from));
			// Set To: 头部头字段
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to));
			// Set Subject: 头部头字段
			if( externalReview == 2) {
				message.setSubject("外审通过了！");
				JSONObject paramMap = HttpClientUtils.getParamMap(externalReviewUrl, externalReviewUrlParams, externalReviewUrlHeaders);
				String res = Optional.ofNullable(paramMap)
						.map(obj -> obj.getJSONObject("data"))
						.map(obj -> obj.getString("auditResult"))
						.orElse("");
				message.setContent("<h1>外审通过了！</h1><p>" + res + "</p>","text/html;charset=utf-8");
			} else if( externalReview == 0) {
				message.setSubject("外审还没消息！");
				// 设置消息体
				message.setText("外审还没消息！");
			} else {
				message.setSubject("唉！难顶！");
				message.setText("唉！难顶！");
			}
			// 发送消息
			Transport.send(message);
			System.out.println("Sent message successfully....from runoob.com" + LocalTime.now());
		}catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}


	static Map<String, String> getHeaderFromFile(String filename) {
		Map<String, String> map = new HashMap<>();
		InputStream in = Main.class.getClassLoader().getResourceAsStream(filename);
		try {
			assert in != null;
			InputStreamReader Reader = new InputStreamReader(in, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(Reader);
			String lineTxt = null;

			while ((lineTxt = bufferedReader.readLine()) != null) {
				String[] split = lineTxt.split(":");
				map.put(split[0].trim(), split[1].trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
}
