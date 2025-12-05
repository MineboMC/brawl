package net.minebo.brawl.mongo.model;

/*
    We dont need anything other than integers for now, this may be a problem for the future tho.
 */

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Statistic {

    public Integer value = 0;

    public Integer get() { return value; }
    public Integer set(Integer value) { this.value = value; return value; }
    public Integer add(Integer value) { this.value += value; return value; }
    public Integer sub(Integer value) { this.value -= value; return value; }

    public String toString() {
        return value.toString();
    }

}

