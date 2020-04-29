package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;

import java.util.List;

public interface APIConsumer<T> {

    List<T> requestCollection() throws APIConnectionException;
}
