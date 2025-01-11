package com.hangovers;

import io.micronaut.http.annotation.*;

@Controller("/funpokedex")
public class FunpokedexController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}