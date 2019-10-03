package com.mewna.mc.box.framework.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author amy
 * @since 7/10/19.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * @return The name of the command. Required.
     */
    String name();

    /**
     * @return The aliases of the command. Optional.
     */
    String[] aliases() default {};

    /**
     * @return The description of the command. Optional.
     */
    String desc() default "A really cool command.";

    /**
     * @return The usage of the command. Optional.
     */
    String usage() default "No usage specified.";

    /**
     * @return The label of the command. Optional.
     */
    String label() default "box";

    /**
     * @return The permission node of the command. Required.
     */
    String permissionNode();

    /**
     * @return The no-permission message of the command. Optional.
     */
    String permissionMessage() default "You don't have permission to do that!";
}
