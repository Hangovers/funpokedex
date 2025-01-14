package com.hangovers.funpokedex;

import com.hangovers.funpokedex.models.Pokemon;

public class Utils {

    /**
     * @param input
     * @return the input without special characters not properly
     */
    public static String handleSpecialCharactersInPokeapiFlavorTexts(String input) {
        return input.replace("\n", " ")
                .replace("\t", " ")
                .replace("\r", " ")
                .replace("\f", " ")
                .replace("\b", " ");
    }

    /**
     * Used to gracefully handle user requests with non-existing pokemons or pokeapi 404 errors
     *
     * @return missingno data
     */
    public static Pokemon missingno() {
        return new Pokemon("MissingNo.", "The Pokémon you are looking for does not exist.", "Error", false);
    }

    /**
     * Used to gracefully handle other application errors
     *
     * @return badEgg data
     */
    public static Pokemon badEgg(String error) {
        return new Pokemon("Bad EGG", error, "Error", false);
    }
}
