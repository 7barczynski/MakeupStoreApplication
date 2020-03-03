package com.tbar.makeupstoreapplication.service.consumer;

import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.List;

public interface APIConsumer<T> {

    T requestSingleObject(@NonNull URI uri) throws APIConnectionException;

    List<T> requestCollection(@NonNull URI uri) throws APIConnectionException;
}
