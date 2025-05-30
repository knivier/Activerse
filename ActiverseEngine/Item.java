package ActiverseEngine;

/**
 * Represents an item in the Activerse game engine.
 * Each item has a name, type, and value.
 */
public abstract class Item {
    private final String name;
    private final String type;
    private final int value;

    public Item(String name, String type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s (Type: %s, Value: %d)", name, type, value);
    }

    // Example abstract method for subclasses to implement
    public abstract void use();
}