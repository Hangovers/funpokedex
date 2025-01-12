package com.hangovers.funpokedex.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Pokemon(String name, String description, String habitat, boolean isLegendary) {}
