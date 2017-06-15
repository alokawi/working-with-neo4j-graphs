/**
 * 
 */
package alokawi.neo4j.core;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * @author alokkumar
 *
 */
public class Neo4jExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final File parentFile = new File("parentDirectory");

		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		GraphDatabaseService db = dbFactory.newEmbeddedDatabase(new File("/Users/alokkumar/neo4j-warehouse/"));
		try (Transaction tx = db.beginTx()) {

			Node parentNode = createNode(parentFile, db);

			new Neo4jExample().addToGraph(parentFile, parentNode, db);

			tx.success();
		}
		System.out.println("Done successfully");

	}

	private static Node createNode(final File parentFile, GraphDatabaseService db) {
		Node parentNode = db.createNode(new Label() {

			@Override
			public String name() {
				return parentFile.getName();
			}
		});

		parentNode.setProperty("Name", parentFile.getName());
		parentNode.setProperty("AbsolutePath", parentFile.getAbsolutePath());
		parentNode.setProperty("isDirectory", parentFile.isDirectory());
		return parentNode;
	}

	public void addToGraph(File parentFile, Node parentNode, GraphDatabaseService db) {
		for (File file : parentFile.listFiles()) {
			String fileName = file.getName().replace("-", "_").replace(".", "_").replace("6", "");
			if (!fileName.startsWith("_")) {
				System.out.println(file.getAbsolutePath());
				if (file.isDirectory()) {
					Node createNode = createNode(file, db);
					addToGraph(file, createNode, db);
				} else {
					Node createNode = createNode(file, db);
					Relationship relationship = parentNode.createRelationshipTo(createNode, new RelationshipType() {

						@Override
						public String name() {
							return "ParentOf";
						}
					});
					relationship.setProperty("ChildAbsolutePath", file.getAbsolutePath());
				}
			}
		}
	}

}
