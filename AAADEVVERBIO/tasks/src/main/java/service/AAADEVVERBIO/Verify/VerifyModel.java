package service.AAADEVVERBIO.Verify;

import com.roobroo.bpm.model.BpmNode;

public class VerifyModel extends BpmNode {

	private static final long serialVersionUID = 1L;
	private String url;
	private String verbioUser;
	public VerifyModel(String name, String id) {
		super(name, id);
		// TODO Auto-generated constructor stub
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVerbioUser() {
		return verbioUser;
	}
	public void setVerbioUser(String verbioUser) {
		this.verbioUser = verbioUser;
	}

	
}
