package org.mksmart.ecapi.api.storage;

public interface StoreWithExistence<K,V> extends Store<K,V> {

    public boolean exists(K key);

}
