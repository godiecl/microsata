/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Validation Utils.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class ValidationUtils {

    /**
     * Regular expression to check.
     */
    private static final Pattern RUT_PATTERN = Pattern.compile("^\\d+-[\\dkK]$");

    /**
     * Validate the Chilean RUT.
     *
     * @param rut to validate.
     */
    public static Boolean isRutValid(final String rut) {

        // No valid
        if (rut == null || !RUT_PATTERN.matcher(rut).matches()) {
            return Boolean.FALSE;
        }

        // The rut
        String numeric = StringUtils.substringBefore(rut, "-");

        // The dv
        String dv = StringUtils.substringAfter(rut, "-").toLowerCase();

        return dv.equals(dv(numeric));
    }

    /**
     * Calculate the DV of a RUT.
     *
     * @param numeric to calculate.
     * @return the dv.
     */
    private static String dv(final @NonNull String numeric) {
        int m = 0;
        int s = 1;
        int t = Integer.parseInt(numeric);

        for (; t != 0; t = (int) Math.floor(t /= 10)) {
            s = (s + t % 10 * (9 - m++ % 6)) % 11;
        }
        return s > 0 ? String.valueOf(s - 1) : "k";
    }

}
