/**
 * @author Aramis Sennyey
 * This class is an API result.
 */

package com.asennyey.a5hid.api;

public class Result<T> {
    public T result;
    public Result(T result){
        this.result = result;
    }
}
