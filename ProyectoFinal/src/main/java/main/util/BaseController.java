package main.util;

import io.javalin.config.JavalinConfig;

public abstract class BaseController {
    protected final JavalinConfig config;

    public BaseController(JavalinConfig config) {
        this.config = config;
    }

    public abstract void aplicarRutas();
}