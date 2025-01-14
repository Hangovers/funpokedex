package com.hangovers.funpokedex;

import static com.hangovers.funpokedex.TestConstants.*;

import com.hangovers.funpokedex.models.Pokemon;

public class TestUtils {

    /** Returns mewtwo data */
    public static Pokemon mewtwo() {
        return new Pokemon(MEWTWO, MEWTWO_DESCRIPTION, "rare", true);
    }

    /** Returns mewtwo data with translated description */
    public static Pokemon mewtwoTranslated() {
        return new Pokemon(MEWTWO, MEWTWO_TRANSLATED_DESCRIPTION, "rare", true);
    }

    /** Returns charizard data */
    public static Pokemon charizard() {
        return new Pokemon(CHARIZARD, CHARIZARD_DESCRIPTION, "mountain", false);
    }

    /** Returns porygon-z data */
    public static Pokemon porygonz() {
        return new Pokemon(PORYGONZ, PORYGONZ_DESCRIPTION, "Unknown", false);
    }

    /** returns flygon data with translated description */
    public static Pokemon flygonTranslated() {
        return new Pokemon(FLYGON, FLYGON_TRANSLATED_DESCRIPTION, "rough-terrain", false);
    }

    /**
     * Used to gracefully handle user requests with non-existing Pokémon or pokeapi 404 errors
     *
     * @return missingno data
     */
    public static Pokemon missingno() {
        return new Pokemon("MissingNo.", "The Pokémon you are looking for does not exist.", "Error", false);
    }

    /**
     * Used to gracefully handle other errors
     *
     * @return badEgg data
     */
    public static Pokemon badEgg(String error) {
        return new Pokemon("Bad EGG", error, "Error", false);
    }
}
