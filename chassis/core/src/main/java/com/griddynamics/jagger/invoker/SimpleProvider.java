package com.griddynamics.jagger.invoker;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/24/13
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleProvider<T> implements Iterable<T>{

    private List<T> list;

    public void setList(List<T> list){
        this.list = list;
    }

    public List<T> getList(){
        return this.list;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
