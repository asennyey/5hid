/**
 * @author Aramis Sennyey
 * This class is a generic async response.
 */

package com.asennyey.a5hid.api;

import java.io.UnsupportedEncodingException;

public interface Callback<T> {
    public void onResult(Result<T> res);
}
