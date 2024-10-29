package rts;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import rts.units.UnitTypeTable;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;

public class GameGraph {
    private static final String DEFAULT_PREFIX = "http://microrts.com/";
    private static final String GAME_PREFIX = DEFAULT_PREFIX + "game/";
    public static final String UNIT_PREFIX = GAME_PREFIX + "unit/";
    public static final String ACTION_PREFIX = GAME_PREFIX + "action/";
    public static final String RATING_PREFIX = GAME_PREFIX + "rating/";

    private static final String INCLUDES_RELATION = "includes";
    private static final String INCLUDED_BY_RELATION = "includedBy";

    public enum Rating {
        BAD("Bad"), MEDIUM("Medium"), GOOD("Good");

        public final String name;

        Rating(String name) {
            this.name = name;
        }
    }

    private final Model model = ModelFactory.createDefaultModel();
    private final Resource gameNode;

    private Map<Integer, Resource> atNodes;

    public GameGraph() {
        gameNode = model.createResource(GAME_PREFIX + "mainGame");
        gameNode.addProperty(RDF.type, model.createResource(GAME_PREFIX + "Game"));

        processActions();
    }

    private void processActions() {
        atNodes = UnitAction.createActionTypesRDF(model);
        for (Resource atNode : atNodes.values()) {
            gameNode.addProperty(model.createProperty(GAME_PREFIX + INCLUDES_RELATION), atNode);
            atNode.addProperty(model.createProperty(ACTION_PREFIX + INCLUDED_BY_RELATION), gameNode);
        }
    }

    public void processUnitTypeTable(UnitTypeTable utt) {
        utt.addPropertiesRDF(model, gameNode, GAME_PREFIX);

        for (Resource utNode : utt.createUnitTypesRDF(model, atNodes, gameNode, GAME_PREFIX)) {
            gameNode.addProperty(model.createProperty(GAME_PREFIX + INCLUDES_RELATION), utNode);
            utNode.addProperty(model.createProperty(UNIT_PREFIX + INCLUDED_BY_RELATION), gameNode);
        }
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
        model.listResourcesWithProperty(RDF.type, model.createResource(UNIT_PREFIX + "Unit")).forEachRemaining(resource -> {
            unitTypes.add(resource.getURI());
        });
        return unitTypes;
    }

    public ArrayList<String> getActionTypes() {
        ArrayList<String> actionTypes = new ArrayList<>();
        model.listResourcesWithProperty(RDF.type, model.createResource(ACTION_PREFIX + "Action")).forEachRemaining(resource -> {
            actionTypes.add(resource.getURI());
        });
        return actionTypes;
    }

    public String toTurtle() {
        Writer writer = new StringWriter();
        model.write(writer, "TURTLE");
        return writer.toString();
    }
}
