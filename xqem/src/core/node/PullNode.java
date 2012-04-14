package core.node;

import net.trivial.wf.iface.Action;
import net.trivial.wf.iface.Message;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import core.datastore.impl.Datastore;
import core.datastore.pull.Query;
import core.model.message.XMLMessage;
import core.node.parent.Node;

public class PullNode extends Node implements Action{

	private static enum op { EQ, LTE, LT, GTE, GT };
	
	public PullNode(String ... args) {
		super(args);
	}

	@Override
	public String execute(Message obj, Object... arg1) {

		if(obj == null) return error;
		
		Document document = ((XMLMessage)obj).getDocument();
		
		if(document == null) return error;

		try {
			String pulledData = pullData(document);

			return success;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return error;

	}

	private String pullData(Document document) {
		
		Query[] queries = getQueriesFromDocument(document);
		
		Datastore ds = Datastore.getInstance();
		
		String xml = null;
		
		try {
			xml = ds.queryXML(queries);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return xml;
	}

	private Query[] getQueriesFromDocument(Document document) {
		
		Nodes nodes = document.query("/request/*[@action='retrieve']");
		
		Query[] queries = new Query[nodes.size()];
		int queryIndex = 0;
		
		for (int i = 0; i < nodes.size(); i++) {

			Element node = (Element) nodes.get(i);
			Elements elements = node.getChildElements();

			Query query = new Query(node.getLocalName());

			for (int j = 0; j < elements.size(); j++) {

				Element element = elements.get(j);

				op operation = op.valueOf(element
						.getAttributeValue("condition"));

				String column = element.getLocalName();
				String value = element.getValue();

				switch (operation) {
				case EQ:
					query.eq(column, value);
					break;
				case GT:
					query.gt(column, value);
					break;
				case GTE:
					query.gte(column, value);
					break;
				case LT:
					query.lt(column, value);
					break;
				case LTE:
					query.lte(column, value);
					break;
				}
			}

			String columsString = node.getAttributeValue("columns");
			if (columsString != null) {
				String[] columnArray = columsString.split(",");
				query.columns(columnArray);
			}

			queries[queryIndex++] = query;
		}
		return queries;
	}

}