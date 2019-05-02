package app.core.entity;

public class Column {
    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    
    public boolean equals(Column c) {
    	return this.name.equals(c.name);
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " " + type;
    }
}
