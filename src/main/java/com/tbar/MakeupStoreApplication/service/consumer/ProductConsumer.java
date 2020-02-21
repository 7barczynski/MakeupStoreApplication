package com.tbar.MakeupStoreApplication.service.consumer;

import com.tbar.MakeupStoreApplication.utility.exceptions.consumerLayer.APICallException;
import java.net.URI;
import java.util.List;
import org.springframework.lang.NonNull;

// nie powinniśmy tworzyć interfejsu, który zwraca ResponseEntity,
// to jest odpowiedzialność implementacji co chcesz dostać na wyjściu
// jeśli tworzymy jakiś generalny interfejs do pobierania danych, to niech jego implementacja decyduje co
// pobrać, skąd i jak to przetworzyć
public interface ProductConsumer<T> {

  List<T> requestCollection(@NonNull URI uri) throws APICallException;

  T requestSingleData(@NonNull URI uri) throws APICallException;

}
