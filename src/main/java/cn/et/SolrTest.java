package cn.et;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.GroupParams;

public class SolrTest {
	private static String urlString = "http://localhost:8080/solr/core1";
	private static SolrClient solr;
	static {
		solr = new HttpSolrClient(urlString);
	}

	public static void main(String[] args) throws SolrServerException, IOException {
		groupBy();
	}

	/*
	 * 直接使用document写入core public static void write() throws SolrServerException,
	 * IOException{ SolrInputDocument document = new SolrInputDocument();
	 * document.addField("id", "5"); document.addField("solr_ik", "阿萨德发呆发呆");
	 * document.addField("title", "solr的简单栗子"); //将document添加到solr中
	 * solr.add(document); // Remember to commit your changes! solr.commit();
	 * solr.close(); }
	 */
	// 使用bean的方式写入core
	public static void write() throws SolrServerException, IOException {
		Entity e = new Entity();
		e.setId("5");
		e.setTitle("蒜苗炒蛋");
		e.setA_ik("蛋炒蒜苗");
		solr.addBean(e);
		solr.commit();
		solr.close();
	}

	public static void read() throws SolrServerException, IOException {
		// 新建搜索器 并定义搜索条件
		SolrQuery sq = new SolrQuery("a_ik:炒蛋");// sq.setQuery("a_ik:炒蛋");
		/**
		 * 过滤查询 与普通查询区别在于过滤查询不会根据得分排序 sq.setFilterQueries("a_ik:炒蛋");
		 */
		/**
		 * 分页 开始下标 sq.setStart(start); 查询的行数 sq.setRows(rows);
		 */
		// 是否开启高亮
		sq.setHighlight(true);
		// 添加高亮字段
		sq.addHighlightField("a_ik");// sq.set("hl.fl", "a_ik");
		// 前缀
		sq.setHighlightSimplePre("<font color=red>");
		// 后缀
		sq.setHighlightSimplePost("</font>");
		// 定义结果集排序规则
		sq.setSort("id", ORDER.asc);
		// 搜索响应体
		QueryResponse query = solr.query(sq);
		// 获取高亮结果
		Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();
		// 获取结果 query.getBeans(Entity.class)返回实体类集合
		SolrDocumentList results = query.getResults();// 返回document集合
		for (SolrDocument solrDocument : results) {
			String id = solrDocument.get("id").toString();
			System.out.println(id);
			System.out.println(solrDocument.get("title"));
			System.out.println(solrDocument.get("a_ik"));

			// 输出高亮结果
			Map<String, List<String>> map = highlighting.get(id);
			List<String> list = map.get("a_ik");
			String highStr = list.get(0);
			System.out.println(highStr);
		}
	}

	public static void delete() throws SolrServerException, IOException {
		// 删除一个document 根据id
		// solr.deleteById("4");
		// 删除一个document 根据条件
		solr.deleteByQuery("solr_ik:阿萨德");
		solr.commit();
		solr.close();
	}

	// 分组
	public static void groupBy() throws SolrServerException, IOException {
		SolrQuery sq = new SolrQuery("*:*");
		// 开启分组
		sq.setParam(GroupParams.GROUP, true);
		// 指定分组条件 根据字段分组 字段名
		sq.setParam(GroupParams.GROUP_FIELD, "type_s");
		// sq.setParam("group.ngroups", true);
		// 分组中的条数
		sq.setParam(GroupParams.GROUP_LIMIT, "5");
		// 搜索响应体
		QueryResponse qr = solr.query(sq);
		// 获取分组结果
		GroupResponse gr = qr.getGroupResponse();
		List<GroupCommand> values = gr.getValues();
		for (GroupCommand me : values) {
			System.out.println(me.getName());//打印分组条件
			List<Group> groups = me.getValues();// 获得分组集合
			for (Group group : groups) {
				System.out.println(group.getResult().size());//打印每个组中document的数量
				SolrDocumentList result = group.getResult();//从分组中取出document集合
				int i = 0;
				for (SolrDocument solrDocument : result) {
					if(i == 0){
						System.out.println(solrDocument.get("type_s"));
						i++;
					}
					System.out.println(solrDocument.get("dishes_ik"));
					System.out.println(solrDocument.get("content_s"));
				}
			}
		}
		solr.commit();
		solr.close();
	}
}
