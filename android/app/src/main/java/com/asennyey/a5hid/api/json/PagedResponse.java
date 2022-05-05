/**
 * @author Aramis Sennyey
 * This class is representation of a common API response.
 */
package com.asennyey.a5hid.api.json;

import java.util.List;

public class PagedResponse<T> {
    private static final int NO_PAGE = -1;
    public List<T> records;
    public int count;
    public int prevPage = NO_PAGE;
    public int nextPage = NO_PAGE;

    public boolean hasNextPage(){
        return this.nextPage != NO_PAGE;
    }

    public boolean hasPrevPage(){
        return  this.prevPage != NO_PAGE;
    }
}
