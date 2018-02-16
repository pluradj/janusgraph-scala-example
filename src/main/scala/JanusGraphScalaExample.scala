import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.graphdb.database.management.ManagementSystem
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex

object JanusGraphScalaExample {
  def main(args: Array[String]): Unit = {
    // create graph
    val graph = JanusGraphFactory.open("inmemory")
    val g = graph.traversal()

    // create graph schema
    var mgmt = graph.openManagement()
    val person = mgmt.makeVertexLabel("person").make()
    val name = mgmt.makePropertyKey("name").dataType(classOf[String]).make()
    val nameIndex = mgmt.buildIndex("nameIndex", classOf[Vertex]).addKey(name).buildCompositeIndex()
    val age = mgmt.makePropertyKey("age").dataType(classOf[Integer]).make()
    val ageIndex = mgmt.buildIndex("ageIndex", classOf[Vertex]).addKey(age).buildCompositeIndex()
    val weight = mgmt.makePropertyKey("weight").dataType(classOf[java.lang.Double]).make()
    val weightIndex = mgmt.buildIndex("weightIndex", classOf[Vertex]).addKey(weight).buildCompositeIndex()
    val defeated = mgmt.makeEdgeLabel("defeated").make()
    val year = mgmt.makePropertyKey("year").dataType(classOf[Integer]).make()
    val yearIndex = mgmt.buildIndex("yearIndex", classOf[Edge]).addKey(year).buildCompositeIndex()
    mgmt.commit()

    // verify index status
    mgmt = graph.openManagement()
    println( mgmt.getGraphIndex("nameIndex").getIndexStatus(mgmt.getPropertyKey("name")) )
    println( mgmt.getGraphIndex("ageIndex").getIndexStatus(mgmt.getPropertyKey("age")) )
    println( mgmt.getGraphIndex("weightIndex").getIndexStatus(mgmt.getPropertyKey("weight")) )
    println( mgmt.getGraphIndex("yearIndex").getIndexStatus(mgmt.getPropertyKey("year")) )
    mgmt.rollback();

    // add vertices and edges
    val mike = g.addV("person").property("name", "ironmike").property("age", 51).property("weight", 240.5d).next()
    val evander = g.addV("person").property("name", "realdeal").property("age", 55).property("weight", 226.0d).next()
    g.V(mike).as("m").V(evander).addE("defeated").property("year", 1996).to("m").iterate()
    g.V(mike).as("m").V(evander).addE("defeated").property("year", 1997).to("m").iterate()

    // get vertex by string index
    println( g.V().has("name", "ironmike").valueMap(true).toList() )

    // get vertex by integer index
    println( g.V().has("age", 55).valueMap(true).toList() )

    // get vertex by double index
    println( g.V().has("weight", 240.5d).valueMap(true).toList() )

    // get edge by index
    println( g.E().has("year", 1996).valueMap(true).toList() )

    // close graph
    graph.close()
  }
}
