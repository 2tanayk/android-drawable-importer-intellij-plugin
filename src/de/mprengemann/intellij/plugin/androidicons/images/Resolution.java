/*
 * Copyright 2015 Marc Prengemann
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * 			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package de.mprengemann.intellij.plugin.androidicons.images;

import java.util.Locale;

public enum Resolution {
    LDPI("ldpi"),
    MDPI("mdpi"),
    HDPI("hdpi"),
    XHDPI("xhdpi"),
    XXHDPI("xxhdpi"),
    XXXHDPI("xxxhdpi"),
    OTHER("other");

    private String resolution;

    Resolution(String resolution) {
        this.resolution = resolution;
    }

    public String getName() {
        if (this == OTHER) {
            return "Other";
        } else {
            return resolution.toUpperCase(Locale.US);
        }
    }
    
    @Override
    public String toString() {
        return resolution;
    }

    public static Resolution from(String value) {
        if (value.equalsIgnoreCase(LDPI.toString())) {
            return LDPI;
        } else if (value.equalsIgnoreCase(MDPI.toString())) {
            return MDPI;
        } else if (value.equalsIgnoreCase(HDPI.toString())) {
            return HDPI;
        } else if (value.equalsIgnoreCase(XHDPI.toString())) {
            return XHDPI;
        } else if (value.equalsIgnoreCase(XXHDPI.toString())) {
            return XXHDPI;
        } else if (value.equalsIgnoreCase(XXXHDPI.toString())) {
            return XXXHDPI;
        } else if (value.equalsIgnoreCase(OTHER.toString())) {
            return OTHER;
        }
        return null;
    }
}
