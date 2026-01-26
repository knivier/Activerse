package ActiverseEngine;

/**
 * Represents an item in the Activerse game engine.
 * Each item has a name, type, and value.
 *
 * @author Knivier
 * @version 1.4.0
 */
public abstract class Item {
    private final String name;
    private final String type;
    private final int value;

    /**
     * Constructor for items inside an inventory. All inventory items must go under the Item class
     *
     * @param name  The name of the item
     * @param type  The classification you give the item (ex: heal etc)
     * @param value The value you give (strength, modifier, etc)
     */
    public Item(String name, String type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * Returns the name of the item
     *
     * @return String item name
     */
    public String getName() {
        return name;
    }

    /**
     * For implementation
     *
     * @return String type of item
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the value as signed and serialized of type item
     *
     * @return Value none defined
     */
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s (Type: %s, Value: %d)", name, type, value);
    }

    /**
     * Action that the item does to something
     */
    public abstract void use();
}