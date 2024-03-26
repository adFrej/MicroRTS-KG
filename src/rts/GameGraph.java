package rts;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import rts.units.UnitTypeTable;

import java.io.StringWriter;
import java.io.Writer;

public class GameGraph {
    private static final String DEFAULT_PREFIX = "http://microrts.com/";

    private final Model model = ModelFactory.createDefaultModel();
    private final Resource gameNode;
    private final String gamePrefix = DEFAULT_PREFIX + "game/";

    public GameGraph() {
        model.setNsPrefix("", DEFAULT_PREFIX);
        model.setNsPrefix("game", gamePrefix);
        gameNode = model.createResource(gamePrefix + "mainGame");
        gameNode.addProperty(RDF.type, model.createResource(gamePrefix + "Game"));
    }

    public void addUnitTypeTable(UnitTypeTable utt) {
        String uttPrefix = gamePrefix + "unit-type-table/";
        model.setNsPrefix("unitTypeTable", uttPrefix);
        Resource uttNode = utt.toRDF(model, uttPrefix);
        gameNode.addProperty(model.createProperty( gamePrefix + "hasUnitTypeTable"), uttNode);
    }

    public String toTurtle() {
        Writer writer = new StringWriter();
        model.write(writer, "TURTLE");
        return writer.toString();
    }
}
