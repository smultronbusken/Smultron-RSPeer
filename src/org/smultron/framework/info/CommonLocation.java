package org.smultron.framework.info;

import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.smultron.framework.Location;

@SuppressWarnings("JavaDoc")
public enum CommonLocation implements Location
{
    GE(Area.rectangular(3156, 3482, 3174, 3496), "Grand Exchange"),

    DRAYNOR_BANK(Area.rectangular(3093, 3247, 3093, 3247), "Draynor bank"),

    DRAYNOR_AGGIE_HOUSE(Area.rectangular(3083, 3261, 3088, 3256), "Aggie in Draynor"),

    LUMBRIDGE_COOK(Area.rectangular(3205, 3212, 3212, 3217), "the Cook in Lumbridge"),

    LUMBRIDGE_COWS(Area.rectangular(3253, 3255, 3256, 3272), "the cows in Lumbridge"),

    LUMBRIDGE_GROATS_FARM(Area.rectangular(3231, 3287, 3236, 3295), "Groats farm in Lumbridge"),

    LUMBRIDGE_WHEATFIELD(Area.rectangular(3154, 3295, 3162, 3300), "the wheatfield in Lumbridge"),

    LUMBRIDGE_FREDTHEFARMER(Area.rectangular(3188, 3275, 3192, 3270), "Fred the Farmer in Lumbridge"),

    LUMBRIDGE_SHEEPS(Area.polygonal(new Position(3193, 3276,0), new Position(3204, 3276, 0),
                                    new Position(3206, 3274, 0), new Position(3211, 3274, 0),
                                    new Position(3212, 3267, 0), new Position(3212, 3257, 0),
                                    new Position(3194, 3257, 0), new Position(3193, 3257, 0)),
                                    "Sheeps in Lumbridge"),

    LUMBRIDGE_SPINNINGWHEEL(Area.rectangular(3208, 3213, 3213, 3216, 1), "Spinning wheel in Lumbridge Castle"),

    LUMBRIDGE_CHURCH(Area.rectangular(3240, 3215, 3247, 3204), "Lumbridge church"),

    LUMBRIDGE_FATHER_URHNEY(Area.rectangular(3144, 3177, 3151, 3173), "Father Urhney in Lumbridge swamp"),

    LUMBRIDGE_GHOST(Area.rectangular(3247, 3195, 3252, 3190), "the ghost house in Lumbridge"),

    WIZARD_TOWER(Area.rectangular(3127, 3177, 3093, 3145).setIgnoreFloorLevel(true), "Wizard Tower"),

    VEOS(Area.rectangular(3226, 3242, 3231, 3239), "Veos at Lumbridge"),

    VARROCK_JULIET(Area.rectangular(3155, 3425, 3161, 3426, 1), "Juliet"),

    VARROCK_ROMEO(Area.polygonal(new Position(3222, 3436, 0), new Position(3206, 3436, 0), new Position(3206, 3421, 0),
                                 new Position(3215, 3410, 0), new Position(3222, 3416, 0)), "Romeo"),

    VARROCK_CHURCH(Area.rectangular(3252, 3471, 3259, 3488), "Varrock church"),

    VARROCK_APOTHECARY(Area.rectangular(3192, 3402, 3198, 3406), "Varrock apothecary"),

    GOBLIN_VILLAGE(Area.rectangular(2954, 3510, 2961, 3514), "Goblin village")

    ;

    private final Area area;
    private final String name;

    private CommonLocation(Area area, String name) {
        this.area = area;
        this.name = name;
    }

    @Override public Position asPosition() {
        return area.getCenter();
    }

    @Override public Area asArea() {
        return area;
    }

    @Override public String locationName() {
        return name;
    }


}
