package com.queue.app.annotation;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restの受け口にこれを付与する
 * requiredListで指定したカラムはない場合に400を返す
 * @author ryonagata
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VirtualRestMapping {
}
