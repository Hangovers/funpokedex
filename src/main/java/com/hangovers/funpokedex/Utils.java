package com.hangovers.funpokedex;

import com.hangovers.funpokedex.model.Pokemon;

public class Utils {

    /**
     *
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
     * Used to gracefully handle pokeapi errors
     * @return missingno data
     */
    public static Pokemon missingno() {
        return new Pokemon("MissingNo.", "The Pokémon you are looking for does not exist.", "Unknown", false);
    }
}
