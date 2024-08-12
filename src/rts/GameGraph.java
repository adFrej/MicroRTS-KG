package rts;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import rts.units.UnitTypeTable;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class GameGraph {
    private static final String DEFAULT_PREFIX = "http://microrts.com/";
    private static final String GAME_PREFIX = DEFAULT_PREFIX + "game/";
    private static final String RATING_PREFIX = GAME_PREFIX + "rating/";

    public enum Rating {
        BAD("Bad"), MEDIUM("Medium"), GOOD("Good");

        public final String name;
        public final String uri;

        Rating(String name) {
            this.name = name;
            this.uri = RATING_PREFIX + name;
        }
    }

    private final Model model = ModelFactory.createDefaultModel();
    private final Resource gameNode;

    public GameGraph() {
        gameNode = model.createResource(GAME_PREFIX + "mainGame");
        gameNode.addProperty(RDF.type, model.createResource(GAME_PREFIX + "Game"));

        Resource ratingType = model.createResource(RATING_PREFIX + "Rating");
        for (Rating rating : Rating.values()) {
            Resource ratingNode = model.createResource(rating.uri);
            ratingNode.addProperty(RDF.type, ratingType);
            ratingNode.addProperty(RDFS.label, rating.name);
        }
    }

    public void addUnitTypeTable(UnitTypeTable utt) {
        String uttPrefix = GAME_PREFIX + "unit-type-table/";
        Resource uttNode = utt.toRDF(model, uttPrefix);
        gameNode.addProperty(model.createProperty( GAME_PREFIX + "hasUnitTypeTable"), uttNode);
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
        model.listResourcesWithProperty(RDF.type, model.createResource(GAME_PREFIX + "unit-type-table/unit-type/" + "UnitType")).forEachRemaining(resource -> {
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
