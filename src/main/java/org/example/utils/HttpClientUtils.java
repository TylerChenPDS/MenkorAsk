package org.example.utils;

/**
 * @author TylerChen
 * @date 2022/5/13 - 9:59
 */

import com.alibaba.fastjson.JSON;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {
	/**
	 * 有参数请求的get请求
	 *
	 * @param url      请求接口
	 * @param paramMap 请求参数Map对象
	 * @return
	 */
	public static JSONObject getParamMap(String url, Map<String, String> paramMap, Map<String, String> headers) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			List<NameValuePair> pairs = new ArrayList<>();
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			CloseableHttpResponse response;
			URIBuilder builder = new URIBuilder(url).setParameters(pairs);
			// 执行get请求.
			HttpGet httpGet = new HttpGet(builder.build());
			// 设置header
			headers.forEach(httpGet::setHeader);
			response = httpClient.execute(httpGet);
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String jsonString = EntityUtils.toString(entity);
				JSONObject jsonObject = new JSONObject(jsonString);
				return jsonObject;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 发送post请求，参数用map接收
	 *
	 * @param url    地址
	 * @param object 请求的对象
	 * @return 返回值
	 */

	public static JSONObject postMap(String url, Object object) {
		//获取json字符串
		String json = JSON.toJSONString(object);
		System.out.println(json);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response;
		try {
			StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String jsonString = EntityUtils.toString(entity);
				JSONObject jsonObject = new JSONObject(jsonString);
				return jsonObject;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}


