package service.AAADEVVERBIO.Verify;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;









import service.AAADEVVERBIO.Util.Encoder;

import com.avaya.app.entity.Instance;
import com.avaya.app.entity.NodeInstance;
import com.avaya.collaboration.ssl.util.SSLProtocolType;
import com.avaya.collaboration.ssl.util.SSLUtilityException;
import com.avaya.collaboration.ssl.util.SSLUtilityFactory;
import com.roobroo.bpm.model.BpmNode;
import com.avaya.workflow.logger.*;

public class VerifyExecution extends NodeInstance {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory
			.getLogger(VerifyExecution.class);

	public VerifyExecution(Instance instance, BpmNode node) {
		super(instance, node);
	}

	public Object execute() throws Exception {
		
		VerifyModel verifyModel = (VerifyModel)getNode();
		
		String url = (String) get("url");
		if ((url == null) || (url.isEmpty())) {
			url = verifyModel.getUrl();
		}
		String[] audioFileNameArray = url.split("\\/");
		String audioFileName = audioFileNameArray[audioFileNameArray.length - 1];
		
		String verbioUser = (String)get("verbiouser");
		if((verbioUser==null) || (verbioUser.isEmpty())){
			verbioUser = verifyModel.getVerbioUser();
		}
		
		
		
		JSONObject json = new JSONObject();
		try{
			json = verbioVerify(verbioUser, audioFileName);
			JSONObject response = json.getJSONObject("response");
			String error = response.getString("error_message");
			String status = response.getString("status");
			
			JSONObject resultVerbio = response.getJSONObject("result");
			JSONObject verbioResult = resultVerbio.getJSONObject("verbio_result");
			
			String resultFingerPrint = verbioResult.getString("result");
			String scoreVerbio = verbioResult.getString("score");
			
			json.put("error_message", error);
			json.put("status", status);
			json.put("resultFingerPrint", resultFingerPrint);
			json.put("scoreVerbio", scoreVerbio);
		}catch(Exception e){
			
			json.put("error", e.toString());
		}
		
		return json;
	}

	public JSONObject verbioVerify(String verbioUser, String audioFileName) throws SSLUtilityException, ClientProtocolException, IOException, JSONException {
		final SSLProtocolType protocolTypeAssistant = SSLProtocolType.TLSv1_2;
		final SSLContext sslContextAssistant = SSLUtilityFactory
				.createSSLContext(protocolTypeAssistant);

		final String URI = "https://avaya:DRNUDUsWh5o3uRdQcZ@cloud2.verbio.com/asv/ws/process";

		final HttpClient clientAssistant = HttpClients.custom()
				.setSslcontext(sslContextAssistant)
				.setHostnameVerifier(new AllowAllHostnameVerifier()).build();
		final HttpPost postMethodAssistant = new HttpPost(URI);
		postMethodAssistant.addHeader("Content-Type", "application/json");
		String base64 = getBase64(audioFileName);
		final String messageBodyAssistant = "{\n" + "	\"user_data\":\n"
				+ "	{\n" + "		\"filename\":\"" + base64 + "\",\n"
				+ "		\"username\": \"" + verbioUser + "\",\n"
				+ "		\"action\": \"VERIFY\",\n" + "		\"score\": \"\",\n"
				+ "		\"spoof\": \"0\",\n" + "		\"grammar\": \"\",\n"
				+ "		\"lang\": \"\"\n" + "	}\n" + "}";
		final StringEntity conversationEntityAssistant = new StringEntity(
				messageBodyAssistant);
		postMethodAssistant.setEntity(conversationEntityAssistant);

		final HttpResponse responseAssistant = clientAssistant
				.execute(postMethodAssistant);

		final BufferedReader inputStreamAssistant = new BufferedReader(
				new InputStreamReader(responseAssistant.getEntity()
						.getContent()));

		String line = "";
		final StringBuilder result = new StringBuilder();
		while ((line = inputStreamAssistant.readLine()) != null) {
			result.append(line);
		}
		JSONObject json = new JSONObject(result.toString());
		return json;
	}

	public String getBase64(String audioFileName) {

		String base64 = null;
		/*
		 * File(String parent, String child) Creates a new File instance from a
		 * parent pathname string and a child pathname string.
		 */
		final File audioFile = new File("home/wsuser/web/RecordParticipant/" + audioFileName);
		if (audioFile.exists()) {
			logger.info(audioFile.getAbsoluteFile());
			base64 = Encoder.encoder(audioFile.getAbsolutePath());
		}

		return base64;
	}

}
