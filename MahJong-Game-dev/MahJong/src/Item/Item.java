package Item;

//Plate entity class

import java.util.Arrays;
import java.util.List;

public abstract class Item implements Comparable<Item> {
    // Item type
    protected String type;
    // Item name
    protected String name;
    //// Item address
    protected String imgurl;
    protected int shu;


    public Item(String type, String name, String imgurl, int shu) {
        this.type = type;
        this.name = name;
        this.imgurl = imgurl;
        this.shu = shu;
    }

    public int getShu() {
        return shu;
    }

    public void setShu(int shu) {
        this.shu = shu;
    }
    

    public Item(String type, String name, String imgurl) {
        this.type = type;
        this.name = name;
        this.imgurl = imgurl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    // Sort the cards
    @Override
    public int compareTo(Item other) {
        // Sort type order
        List<String> typesOrder = Arrays.asList("characters", "bamboos", "dots", "winds");

        // Extract the type and value of the card
        String typeThis = extractType(this.name);
        String typeOther = extractType(other.name);
        int indexThis = typesOrder.indexOf(typeThis);
        int indexOther = typesOrder.indexOf(typeOther);

        if (indexThis != indexOther) {
            return Integer.compare(indexThis, indexOther);
        } else {
            // All are wind cards, sorted by name
            if (typeThis.equals("winds")) {
                return this.name.compareTo(other.name);
            }
            // Otherwise sort by number size
            int numThis = extractNumber(this.name);
            int numOther = extractNumber(other.name);
            return Integer.compare(numThis, numOther);
        }
    }

    // Type of withdrawal card
    private String extractType(String name) {
        if (name.endsWith("characters")) {
            return "characters";
        } else if (name.endsWith("bamboos")) {
            return "bamboos";
        } else if (name.endsWith("dots")) {
            return "dots";
        } else {
            return "winds";
        }
    }

    // Extract the number of the card
    private int extractNumber(String name) {
        if (name.endsWith("characters") || name.endsWith("bamboos") || name.endsWith("dots")) {
            return Integer.parseInt(name.substring(0, name.length() - 1));
        }
        return 0; // The wind card has no numbers
    }
}


