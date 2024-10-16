package rts.units;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.jdom.Element;
import rts.GameGraph;
import rts.UnitAction;
import util.XMLWriter;

/**
 * A general unit definition that could turn out to be anything
 * @author santi, inspired in the original UnitDefinition class by Jeff Bernard
 *
 */
public class UnitType {

	/**
	 * The unique identifier of this type
	 */
    public int ID = 0;

    /**
     * The name of this type
     */
    public String name;

    /**
     * Cost to produce a unit of this type
     */
    public int cost = 1;

    /**
     * Initial Hit Points of units of this type
     */
    public int hp = 1;


    /**
     * Minimum damage of the attack from a unit of this type
     */
    public int minDamage = 1;

    /**
     * Maximum damage of the attack from a unit of this type
     */
    public int maxDamage = 1;

    /**
     * Range of the attack from a unit of this type
     */
    public int attackRange = 1;

    /**
     * Time that each action takes to accomplish
     */
    public int produceTime = 10,
               moveTime = 10,
               attackTime = 10,
               harvestTime = 10,
               returnTime = 10;

    /**
     * How many resources the unit can carry.
     * Each time the harvest action is executed, this is
     * how many resources does the unit gets
     */
    public int harvestAmount = 1;

    /**
     * the radius a unit can see for partially observable game states.
     */
    public int sightRadius = 4;

    /**
     * Can this unit type be harvested?
     */
    public boolean isResource = false;

    /**
     * Can resources be returned to this unit type?
     */
    public boolean isStockpile = false;

    /**
     * Is this a harvester type?
     */
    public boolean canHarvest = false;

    /**
     * Can a unit of this type move?
     */
    public boolean canMove = true;

    /**
     * Can a unit of this type attack?
     */
    public boolean canAttack = true;

    /**
     * Units that this type of unit can produce
     */
    public ArrayList<UnitType> produces = new ArrayList<>();

    /**
     * Which unit types produce a unit of this type
     */
    public ArrayList<UnitType> producedBy = new ArrayList<>();

    /**
     * Returns the hash code of the name
     * // assume that all unit types have different names:
     */
    public int hashCode() {
        return name.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof UnitType)) return false;
        return name.equals(((UnitType)o).name);
    }

    /**
     * Adds a unit type that a unit of this type can produce
     * @param ut
     */
    public void produces(UnitType ut)
    {
        produces.add(ut);
        ut.producedBy.add(this);
    }

    /**
     * Creates a temporary instance with just the name and ID from a XML element
     * @param unittype_e
     * @return
     */
    static UnitType createStub(Element unittype_e) {
        UnitType ut = new UnitType();
        ut.ID = Integer.parseInt(unittype_e.getAttributeValue("ID"));
        ut.name = unittype_e.getAttributeValue("name");
        return ut;
    }


    /**
     * Creates a temporary instance with just the name and ID from a JSON object
     * @param o
     * @return
     */
    static UnitType createStub(JsonObject o) {
        UnitType ut = new UnitType();
        ut.ID = o.getInt("ID",-1);
        ut.name = o.getString("name",null);
        return ut;
    }

    /**
     * Updates the attributes of this type from XML
     * @param unittype_e
     * @param utt
     */
    void updateFromXML(Element unittype_e, UnitTypeTable utt) {
        cost = Integer.parseInt(unittype_e.getAttributeValue("cost"));
        hp = Integer.parseInt(unittype_e.getAttributeValue("hp"));
        minDamage = Integer.parseInt(unittype_e.getAttributeValue("minDamage"));
        maxDamage = Integer.parseInt(unittype_e.getAttributeValue("maxDamage"));
        attackRange = Integer.parseInt(unittype_e.getAttributeValue("attackRange"));

        produceTime = Integer.parseInt(unittype_e.getAttributeValue("produceTime"));
        moveTime = Integer.parseInt(unittype_e.getAttributeValue("moveTime"));
        attackTime = Integer.parseInt(unittype_e.getAttributeValue("attackTime"));
        harvestTime = Integer.parseInt(unittype_e.getAttributeValue("harvestTime"));
        returnTime = Integer.parseInt(unittype_e.getAttributeValue("returnTime"));

        harvestAmount = Integer.parseInt(unittype_e.getAttributeValue("harvestAmount"));
        sightRadius = Integer.parseInt(unittype_e.getAttributeValue("sightRadius"));

        isResource = Boolean.parseBoolean(unittype_e.getAttributeValue("isResource"));
        isStockpile = Boolean.parseBoolean(unittype_e.getAttributeValue("isStockpile"));
        canHarvest = Boolean.parseBoolean(unittype_e.getAttributeValue("canHarvest"));
        canMove = Boolean.parseBoolean(unittype_e.getAttributeValue("canMove"));
        canAttack = Boolean.parseBoolean(unittype_e.getAttributeValue("canAttack"));

        for(Object o:unittype_e.getChildren("produces")) {
            Element produces_e = (Element)o;
            produces.add(utt.getUnitType(produces_e.getAttributeValue("type")));
        }

        for(Object o:unittype_e.getChildren("producedBy")) {
            Element producedby_e = (Element)o;
            producedBy.add(utt.getUnitType(producedby_e.getAttributeValue("type")));
        }
    }

    /**
     * Updates the attributes of this type from a JSON string
     * @param JSON
     * @param utt
     */
    void updateFromJSON(String JSON, UnitTypeTable utt) {
        JsonObject o = Json.parse(JSON).asObject();
        updateFromJSON(o, utt);
    }


    /**
     * Updates the attributes of this type from a JSON object
     * @param o
     * @param utt
     */
    void updateFromJSON(JsonObject o, UnitTypeTable utt) {
        cost = o.getInt("cost", 1);
        hp = o.getInt("hp", 1);
        minDamage = o.getInt("minDamage", 1);
        maxDamage = o.getInt("maxDamage", 1);
        attackRange = o.getInt("attackRange", 1);

        produceTime = o.getInt("produceTime", 10);
        moveTime = o.getInt("moveTime", 10);
        attackTime = o.getInt("attackTime", 10);
        harvestTime = o.getInt("harvestTime", 10);
        produceTime = o.getInt("produceTime", 10);

        harvestAmount = o.getInt("harvestAmount", 10);
        sightRadius = o.getInt("sightRadius", 10);

        isResource = o.getBoolean("isResource", false);
        isStockpile = o.getBoolean("isStockpile", false);
        canHarvest = o.getBoolean("canHarvest", false);
        canMove = o.getBoolean("canMove", false);
        canAttack = o.getBoolean("canAttack", false);

        JsonArray produces_a = o.get("produces").asArray();
        for(JsonValue v:produces_a.values()) {
            produces.add(utt.getUnitType(v.asString()));
        }

        JsonArray producedBy_a = o.get("producedBy").asArray();
        for(JsonValue v:producedBy_a.values()) {
            producedBy.add(utt.getUnitType(v.asString()));
        }
    }


    /**
     * Writes a XML representation
     * @param w
     */
    public void toxml(XMLWriter w) {
        w.tagWithAttributes(
    		this.getClass().getName(),
            "ID=\""+ID+"\" "+
            "name=\""+name+"\" "+
            "cost=\""+cost+"\" "+
            "hp=\""+hp+"\" "+
            "minDamage=\""+minDamage+"\" "+
            "maxDamage=\""+maxDamage+"\" "+
            "attackRange=\""+attackRange+"\" "+

            "produceTime=\""+produceTime+"\" "+
            "moveTime=\""+moveTime+"\" "+
            "attackTime=\""+attackTime+"\" "+
            "harvestTime=\""+harvestTime+"\" "+
            "returnTime=\""+returnTime+"\" "+

            "harvestAmount=\""+harvestAmount+"\" "+
            "sightRadius=\""+sightRadius+"\" "+

            "isResource=\""+isResource+"\" "+
            "isStockpile=\""+isStockpile+"\" "+
            "canHarvest=\""+canHarvest+"\" "+
            "canMove=\""+canMove+"\" "+
            "canAttack=\""+canAttack+"\""
        );

		for (UnitType ut : produces) {
			w.tagWithAttributes("produces", "type=\"" + ut.name + "\"");
			w.tag("/produces");
		}
		for (UnitType ut : producedBy) {
			w.tagWithAttributes("producedBy", "type=\"" + ut.name + "\"");
			w.tag("/producedBy");
		}
		w.tag("/" + this.getClass().getName());
    }


    /**
     * Writes a JSON representation
     * @param w
     * @throws Exception
     */
    public void toJSON(Writer w) throws Exception {
        w.write(
    		"{" +
            "\"ID\":"+ID+", "+
            "\"name\":\""+name+"\", "+
            "\"cost\":"+cost+", "+
            "\"hp\":"+hp+", "+
            "\"minDamage\":"+minDamage+", "+
            "\"maxDamage\":"+maxDamage+", "+
            "\"attackRange\":"+attackRange+", "+

            "\"produceTime\":"+produceTime+", "+
            "\"moveTime\":"+moveTime+", "+
            "\"attackTime\":"+attackTime+", "+
            "\"harvestTime\":"+harvestTime+", "+
            "\"returnTime\":"+returnTime+", "+

            "\"harvestAmount\":"+harvestAmount+", "+
            "\"sightRadius\":"+sightRadius+", "+

            "\"isResource\":"+isResource+", "+
            "\"isStockpile\":"+isStockpile+", "+
            "\"canHarvest\":"+canHarvest+", "+
            "\"canMove\":"+canMove+", "+
            "\"canAttack\":"+canAttack+", "
        );

		boolean first = true;
		w.write("\"produces\":[");
		for (UnitType ut : produces) {
			if (!first)
				w.write(", ");
			w.write("\"" + ut.name + "\"");
			first = false;
		}
		first = true;
		w.write("], \"producedBy\":[");
		for (UnitType ut : producedBy) {
			if (!first)
				w.write(", ");
			w.write("\"" + ut.name + "\"");
			first = false;
		}
		w.write("]}");
    }

    public Resource toRDF(Model model, Map<String, Integer> minValues, Map<String, Integer> maxValues, Map<Integer, Resource> atNodes, Set<String> ratings) {
        final String utPrefix = GameGraph.UNIT_PREFIX;
        final String atPrefix = GameGraph.ACTION_PREFIX;
        final String rPrefix = GameGraph.RATING_PREFIX;

        final String doesRelation = "does";
        final String doneByRelation = "doneBy";
        final String targetsRelation = "targets";
        final String targetedByRelation = "targetedBy";
        final String producesRelation = "produces";
        final String producedByRelation = "producedBy";
        final String ranksRelation = "ranks";
        final String rankedByRelation = "rankedBy";
        final String describesRelation = "describes";
        final String describedByRelation = "describedBy";
        final String createsRelation = "creates";
        final String createdByRelation = "createdBy";

        Resource utNode = model.createResource(utPrefix + ID);
        utNode.addProperty(RDF.type, model.createResource(utPrefix + "Unit"));
        utNode.addProperty(RDFS.label, name);

        Resource atNoneNode = atNodes.get(UnitAction.TYPE_NONE);
        Resource atMoveNode = atNodes.get(UnitAction.TYPE_MOVE);
        Resource atHarvestNode = atNodes.get(UnitAction.TYPE_HARVEST);
        Resource atReturnNode = atNodes.get(UnitAction.TYPE_RETURN);
        Resource atProduceNode = atNodes.get(UnitAction.TYPE_PRODUCE);
        Resource atAttackNode = atNodes.get(UnitAction.TYPE_ATTACK_LOCATION);

        utNode.addProperty(model.createProperty(utPrefix + doesRelation), atNoneNode);
        atNoneNode.addProperty(model.createProperty(atPrefix + doneByRelation), utNode);
        if (canMove) {
            utNode.addProperty(model.createProperty(utPrefix + doesRelation), atMoveNode);
            atMoveNode.addProperty(model.createProperty(atPrefix + doneByRelation), utNode);
        }
        if (canHarvest) {
            utNode.addProperty(model.createProperty(utPrefix + doesRelation), atHarvestNode);
            atHarvestNode.addProperty(model.createProperty(atPrefix + doneByRelation), utNode);
            utNode.addProperty(model.createProperty(utPrefix + doesRelation), atReturnNode);
            atReturnNode.addProperty(model.createProperty(atPrefix + doneByRelation), utNode);
            UnitAction.createPrefers(atHarvestNode, model, "unit", "self", utNode);
            UnitAction.createPrefers(atAttackNode, model, "unit", "target", utNode);
        }
        if (isResource) {
            atHarvestNode.addProperty(model.createProperty(atPrefix + targetsRelation), utNode);
            utNode.addProperty(model.createProperty(utPrefix + targetedByRelation), atHarvestNode);
        }
        if (isStockpile) {
            atReturnNode.addProperty(model.createProperty(atPrefix + targetsRelation), utNode);
            utNode.addProperty(model.createProperty(utPrefix + targetedByRelation), atReturnNode);
        }
        if (produces.size() > 0) {
            utNode.addProperty(model.createProperty(utPrefix + doesRelation), atProduceNode);
            atProduceNode.addProperty(model.createProperty(atPrefix + doneByRelation), utNode);
        }
        if (producedBy.size() > 0) {
            atProduceNode.addProperty(model.createProperty(atPrefix + createsRelation), utNode);
            utNode.addProperty(model.createProperty(utPrefix + createdByRelation), atProduceNode);
        }
        if (canAttack) {
            utNode.addProperty(model.createProperty(utPrefix + doesRelation), atAttackNode);
            atAttackNode.addProperty(model.createProperty(atPrefix + doneByRelation), utNode);
            UnitAction.createPrefers(atAttackNode, model, "unit", "self", utNode);
            UnitAction.createPrefers(atHarvestNode, model, "unit", "ally", utNode);
            UnitAction.createPrefers(atMoveNode, model, "unit", "enemy", utNode);
            if (attackRange > 1) {
                UnitAction.createPrefers(atNoneNode, model, "unit", "self", utNode);
                UnitAction.createPrefers(atAttackNode, model, "unit", "target", utNode);
            }
        }
        if (!isResource) {
            atAttackNode.addProperty(model.createProperty(atPrefix + targetsRelation), utNode);
            utNode.addProperty(model.createProperty(utPrefix + targetedByRelation), atAttackNode);
        }

        utNode.addLiteral(model.createProperty(utPrefix + "isResource"), isResource);
        utNode.addLiteral(model.createProperty(utPrefix + "isStockpile"), isStockpile);

        for (UnitType ut : produces) {
            utNode.addProperty(model.createProperty(utPrefix + producesRelation), model.createResource(utPrefix + ut.ID));
        }
        for (UnitType ut : producedBy) {
            utNode.addProperty(model.createProperty(utPrefix + producedByRelation), model.createResource(utPrefix + ut.ID));
        }

        for (String field : getNumericalFields()) {
            try {
                String booleanField = UnitType.getNumericalFieldRelevantBooleanField(field);
                if (booleanField != null) {
                    boolean can = (boolean) this.getClass().getDeclaredField(booleanField).get(this);
                    if (!can) continue;
                }
                int value = this.getClass().getDeclaredField(field).getInt(this);
                utNode.addLiteral(model.createProperty(utPrefix + "has" + field.substring(0, 1).toUpperCase() + field.substring(1)), value);
                int minValue = minValues.get(field);
                int maxValue = maxValues.get(field);
                double threshold1 = (maxValue - minValue) / 3.;
                double threshold2 = 2 * threshold1;
                GameGraph.Rating rating = GameGraph.Rating.MEDIUM;
                if (value < minValue + threshold1) {
                    if (isNumericalFieldAscending(field)) {
                        rating = GameGraph.Rating.BAD;
                    } else {
                        rating = GameGraph.Rating.GOOD;
                    }
                } else if (value > maxValue - threshold2) {
                    if (isNumericalFieldAscending(field)) {
                        rating = GameGraph.Rating.GOOD;
                    } else {
                        rating = GameGraph.Rating.BAD;
                    }
                }
                String ratingUri = rPrefix + field + rating.name;
                Resource rNode = model.createResource(ratingUri);
                if (!ratings.contains(ratingUri)) {
                    ratings.add(ratingUri);
                    rNode.addProperty(RDF.type, model.createResource(rPrefix + "Rating"));
                    Resource atNode = atNodes.get(numericalFieldsActions.get(field));
                    if (atNode != null) {
                        rNode.addProperty(model.createProperty(rPrefix + describesRelation), atNode);
                        atNode.addProperty(model.createProperty(atPrefix + describedByRelation), rNode);
                    }
                }
                utNode.addProperty(model.createProperty(utPrefix + ranksRelation), rNode);
                rNode.addProperty(model.createProperty(rPrefix + rankedByRelation), utNode);
            } catch (NoSuchFieldException | NullPointerException e) {
                continue;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return utNode;
    }

    public static ArrayList<String> getNumericalFields() {
        ArrayList<String> fields = new ArrayList<>();
        fields.add("cost");
        fields.add("hp");
        fields.add("minDamage");
        fields.add("maxDamage");
        fields.add("attackRange");
        fields.add("produceTime");
        fields.add("moveTime");
        fields.add("attackTime");
        fields.add("harvestTime");
        fields.add("returnTime");
        fields.add("harvestAmount");
        return fields;
    }

    public static String getNumericalFieldRelevantBooleanField(String field) {
        return switch (field) {
            case "minDamage", "maxDamage", "attackRange", "attackTime" -> "canAttack";
            case "moveTime" -> "canMove";
            case "harvestTime", "returnTime", "harvestAmount" -> "canHarvest";
            default -> null;
        };
    }

    public static boolean isNumericalFieldAscending(String field) {
        return switch (field) {
            case "cost", "produceTime", "moveTime", "attackTime", "harvestTime", "returnTime" ->
                    false;
            default -> true;
        };
    }

    public static Map<String, Integer> numericalFieldsActions = Map.of(
            "minDamage", UnitAction.TYPE_ATTACK_LOCATION,
            "maxDamage", UnitAction.TYPE_ATTACK_LOCATION,
            "attackRange", UnitAction.TYPE_ATTACK_LOCATION,
            "moveTime", UnitAction.TYPE_MOVE,
            "attackTime", UnitAction.TYPE_ATTACK_LOCATION,
            "harvestTime", UnitAction.TYPE_HARVEST,
            "returnTime", UnitAction.TYPE_RETURN,
            "harvestAmount", UnitAction.TYPE_HARVEST
    );

    /**
     * Creates a unit type from XML
     * @param e
     * @param utt
     * @return
     */
    public static UnitType fromXML(Element e, UnitTypeTable utt) {
        UnitType ut = new UnitType();
        ut.updateFromXML(e, utt);
        return ut;
    }


    /**
     * Creates a unit type from a JSON string
     * @param JSON
     * @param utt
     * @return
     */
    public static UnitType fromJSON(String JSON, UnitTypeTable utt) {
        UnitType ut = new UnitType();
        ut.updateFromJSON(JSON, utt);
        return ut;
    }


    /**
     * Creates a unit type from a JSON object
     * @param o
     * @param utt
     * @return
     */
    public static UnitType fromJSON(JsonObject o, UnitTypeTable utt) {
        UnitType ut = new UnitType();
        ut.updateFromJSON(o, utt);
        return ut;
    }
}
