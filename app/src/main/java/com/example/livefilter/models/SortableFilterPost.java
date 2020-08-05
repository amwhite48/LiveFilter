package com.example.livefilter.models;

// wrapper class for easily sorting filter posts based on similarity
// implementing comparable makes it so that we can use Arrays.sort on an arraylist of this class
public class SortableFilterPost implements Comparable{

    private FilterPost filterPost;
    private int closeness;

    public SortableFilterPost(FilterPost filterPost, int closeness) {
        this.filterPost = filterPost;
        this.closeness = closeness;
    }

    public FilterPost getFilterPost() {
        return filterPost;
    }

    public int getCloseness() {
        return closeness;
    }


    @Override
    public int compareTo(Object o) {
        SortableFilterPost otherFilter = (SortableFilterPost) o;
        if(otherFilter.getCloseness() < this.closeness) {
            return -1;
        } else if (otherFilter.getCloseness() > this.closeness) {
            return 1;
        }
        // return 0 if argument equal to given value
        return 0;
    }
}
