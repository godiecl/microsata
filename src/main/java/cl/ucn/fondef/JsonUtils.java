/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * The Json Serializer.
 *
 * @author Diego Urrutia-Astorga.
 */
public final class JsonUtils {

    /**
     * The GSON serializer.
     */
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableInnerClassSerialization()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .addSerializationExclusionStrategy(new InvisibleJsonExclusionStrategy())
            // .registerTypeAdapter(ZonedDateTime.class, (JsonDeserializer<ZonedDateTime>) (json, typeOfT, context)
            //        -> ZonedDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_ZONED_DATE_TIME))
            // .registerTypeAdapter(ZonedDateTime.class, (JsonSerializer<ZonedDateTime>) (date, typeOfT, context)
            //        -> new JsonPrimitive(date.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)))
            .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (date, typeOfT, context)
                    -> new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(date)))
            .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context)
                    -> DateTimeFormatter.ISO_INSTANT.parse(json.getAsString(), Instant::from))
            .create();

    /**
     * Serialize a Object to Json.
     *
     * @param obj to serialize.
     * @return the String.
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * Strategy to ignore some fields.
     */
    private static class InvisibleJsonExclusionStrategy implements ExclusionStrategy {

        /**
         * Skip some field.
         */
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(InvisibleJson.class) != null;
        }

        /**
         * Skip some class.
         */
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    /**
     * The Invisible annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = ElementType.FIELD)
    public @interface InvisibleJson {
    }

}
