package cn.et;

import org.apache.solr.client.solrj.beans.Field;

public class Entity {
	@Field
	private String id;
	@Field
	private String title;
	@Field
	private String a_ik;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getA_ik() {
		return a_ik;
	}
	public void setA_ik(String a_ik) {
		this.a_ik = a_ik;
	}
}
