package com.hangovers.funpokedex;

import com.hangovers.funpokedex.model.Pokemon;

public class TestUtils {

  /**
   * Used to return mewtwo data
   *
   * @return mewtwo data
   */
  public static Pokemon mewtwo() {
    return new Pokemon(
        "mewtwo",
        "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
        "rare",
        true);
  }

  /**
   * Used to gracefully handle user requests with non-existing Pokémon or pokeapi 404 errors
   *
   * @return missingno data
   */
  public static Pokemon missingno() {
    return new Pokemon(
        "MissingNo.", "The Pokémon you are looking for does not exist.", "Error", false);
  }

  /**
   * Used to gracefully handle other errors
   *
   * @return badEgg data
   */
  public static Pokemon badEgg() {
    return new Pokemon("Bad EGG", "Something went wrong with your request.", "Error", false);
  }
}
