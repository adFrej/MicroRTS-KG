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
        harvestTime = o.getInt("produceTime", 10);
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
        String utPrefix = GameGraph.UNIT_PREFIX;
        String atPrefix = GameGraph.ACTION_PREFIX;
        String rPrefix = GameGraph.RATING_PREFIX;

        Resource utNode = model.createResource(utPrefix + ID);
        utNode.addProperty(RDF.type, model.createResource(utPrefix + "UnitType"));
        utNode.addProperty(RDFS.label, name);

        Resource atNoneNode = atNodes.get(UnitAction.TYPE_NONE);
        utNode.addProperty(model.createProperty(utPrefix + "can"), atNoneNode);
        atNoneNode.addProperty(model.createProperty(atPrefix + "doneBy"), utNode);
        if (canMove) {
            Resource atMoveNode = atNodes.get(UnitAction.TYPE_MOVE);
            utNode.addProperty(model.createProperty(utPrefix + "can"), atMoveNode);
            atMoveNode.addProperty(model.createProperty(atPrefix + "doneBy"), utNode);
        }
        Resource atHarvestNode = atNodes.get(UnitAction.TYPE_HARVEST);
        Resource atReturnNode = atNodes.get(UnitAction.TYPE_RETURN);
        if (canHarvest) {
            utNode.addProperty(model.createProperty(utPrefix + "can"), atHarvestNode);
            atHarvestNode.addProperty(model.createProperty(atPrefix + "doneBy"), utNode);
            utNode.addProperty(model.createProperty(utPrefix + "can"), atReturnNode);
            atReturnNode.addProperty(model.createProperty(atPrefix + "doneBy"), utNode);
        }
        if (isResource) {
            atHarvestNode.addProperty(model.createProperty(atPrefix + "targets"), utNode);
            utNode.addProperty(model.createProperty(utPrefix + "targetedBy"), atHarvestNode);
        }
        if (isStockpile) {
            atReturnNode.addProperty(model.createProperty(atPrefix + "targets"), utNode);
            utNode.addProperty(model.createProperty(utPrefix + "targetedBy"), atReturnNode);
        }
        Resource atProduceNode = atNodes.get(UnitAction.TYPE_PRODUCE);
        if (produces.size() > 0) {
            utNode.addProperty(model.createProperty(utPrefix + "can"), atProduceNode);
            atProduceNode.addProperty(model.createProperty(atPrefix + "doneBy"), utNode);
        }
        if (producedBy.size() > 0) {
            atProduceNode.addProperty(model.createProperty(atPrefix + "targets"), utNode);
            utNode.addProperty(model.createProperty(utPrefix + "targetedBy"), atProduceNode);
        }
        if (canAttack) {
            Resource atAttackNode = atNodes.get(UnitAction.TYPE_ATTACK_LOCATION);
            utNode.addProperty(model.createProperty(utPrefix + "can"), atAttackNode);
            atAttackNode.addProperty(model.createProperty(atPrefix + "doneBy"), utNode);
            atAttackNode.addProperty(model.createProperty(atPrefix + "targets"), utNode);
            utNode.addProperty(model.createProperty(utPrefix + "targetedBy"), atAttackNode);
        }

        utNode.addLiteral(model.createProperty(utPrefix + "hasCost"), cost);
        utNode.addLiteral(model.createProperty(utPrefix + "hasHp"), hp);
        utNode.addLiteral(model.createProperty(utPrefix + "hasMinDamage"), minDamage);
        utNode.addLiteral(model.createProperty(utPrefix + "hasMaxDamage"), maxDamage);
        utNode.addLiteral(model.createProperty(utPrefix + "hasAttackRange"), attackRange);
        utNode.addLiteral(model.createProperty(utPrefix + "hasProduceTime"), produceTime);
        utNode.addLiteral(model.createProperty(utPrefix + "hasMoveTime"), moveTime);
        utNode.addLiteral(model.createProperty(utPrefix + "hasAttackTime"), attackTime);
        utNode.addLiteral(model.createProperty(utPrefix + "hasHarvestTime"), harvestTime);
        utNode.addLiteral(model.createProperty(utPrefix + "hasReturnTime"), returnTime);
        utNode.addLiteral(model.createProperty(utPrefix + "hasHarvestAmount"), harvestAmount);

        utNode.addLiteral(model.createProperty(utPrefix + "isResource"), isResource);
        utNode.addLiteral(model.createProperty(utPrefix + "isStockpile"), isStockpile);

        for (UnitType ut : produces) {
            utNode.addProperty(model.createProperty(utPrefix + "produces"), model.createResource(utPrefix + ut.ID));
        }
        for (UnitType ut : producedBy) {
            utNode.addProperty(model.createProperty(utPrefix + "producedBy"), model.createResource(utPrefix + ut.ID));
        }

        for (String field : getNumericalFields()) {
            try {
                int value = this.getClass().getDeclaredField(field).getInt(this);
                int minValue = minValues.get(field);
                int maxValue = maxValues.get(field);
                int threshold1 = (maxValue - minValue) / 3;
                int threshold2 = 2 * threshold1;
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
                        rNode.addProperty(model.createProperty(rPrefix + "about"), atNode);
                        atNode.addProperty(model.createProperty(atPrefix + "referencedBy"), rNode);
                    }
                }
                utNode.addProperty(model.createProperty(utPrefix + "has"), rNode);
                rNode.addProperty(model.createProperty(rPrefix + "referencedBy"), utNode);
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
