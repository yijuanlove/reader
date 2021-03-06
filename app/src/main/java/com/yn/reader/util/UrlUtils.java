package com.yn.reader.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by sunxy on 2018/1/30.
 */

public class UrlUtils {
    public static String normalize(@NonNull String input) {
        String trimmedInput = input.trim();
        Uri uri = Uri.parse(trimmedInput);

        if (TextUtils.isEmpty(uri.getScheme())) {
            uri = Uri.parse("http://" + trimmedInput);
        }

        return uri.toString();
    }

    /**
     * Is the given string a URL or should we perform a search?
     *
     * TODO: This is a super simple and probably stupid implementation.
     */
    public static boolean isUrl(String url) {
        String trimmedUrl = url.trim();
        if (trimmedUrl.contains(" ")) {
            return false;
        }

        return trimmedUrl.contains(".") || trimmedUrl.contains(":");
    }
}
