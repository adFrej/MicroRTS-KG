package rts;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import rts.units.UnitTypeTable;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

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

    public ArrayList<String[]> getTriples() {
        ArrayList<String[]> triples = new ArrayList<>();
        model.listStatements().forEachRemaining(statement -> {
            triples.add(new String[]{statement.getSubject().toString(), statement.getPredicate().toString(), statement.getObject().toString()});
        });
        return triples;
    }

    public ArrayList<String> getUnitTypes() {
        ArrayList<String> unitTypes = new ArrayList<>();
        model.listResourcesWithProperty(RDF.type, model.createResource(gamePrefix + "unit-type-table/unit-type/" + "UnitType")).forEachRemaining(resource -> {
            unitTypes.add(resource.getURI());
        });
        return unitTypes;
    }

    public String toTurtle() {
        Writer writer = new StringWriter();
        model.write(writer, "TURTLE");
        return writer.toString();
    }
}
