package com.innedhub.odata;

import com.innedhub.enums.MLSResource;
import com.innedhub.results.PropertyTO;
import com.innedhub.results.PropertyTOImpl;
import com.innedhub.results.SearchResult;
import com.innedhub.results.SearchResultImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.*;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


@Slf4j
public class Client {
    private ODataClient client;
    private String apiUri;

    public Client(String apiUri, String apiKey) {
        this.apiUri = apiUri;
        client = ODataClientFactory.getClient();
        client.getConfiguration().setHttpClientFactory(new BearerAuthHttpClientFactory(apiKey));
    }

    public SearchResult doRequestWithFilter(MLSResource resource, String request) {
        Edm edm = readEdm(apiUri);
        List<PropertyTO> listProperties = new ArrayList<>();
        SearchResult searchResult;
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator;
        iterator = readEntitiesWithFilter(edm, apiUri, resource.getResource(), request);
        while (iterator.hasNext()) {
            ClientEntity ce = iterator.next();
            PropertyTO propertyTO;
            propertyTO = getPropertyTO(ce.getProperties(), 0);
            listProperties.add(propertyTO);
        }
        if (iterator.getNext() != null) {
            searchResult = new SearchResultImpl(listProperties, iterator.getNext(), true);
        } else {
            searchResult = new SearchResultImpl(listProperties, null, false);
        }
        return searchResult;
    }

    public SearchResult doRequestWithFilter(MLSResource resource, String request, int top) {
        Edm edm = readEdm(apiUri);
        List<PropertyTO> listProperties = new ArrayList<>();
        SearchResult searchResult;
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator;
        iterator = readEntitiesWithFilter(edm, apiUri, resource.getResource(), request);
        for (int i = 0; i < top; i++) {
            if (iterator.hasNext()) {
                ClientEntity ce = iterator.next();
                PropertyTO propertyTO;
                propertyTO = getPropertyTO(ce.getProperties(), 0);
                listProperties.add(propertyTO);
            }
        }
        if (iterator.getNext() != null) {
            searchResult = new SearchResultImpl(listProperties, iterator.getNext(), true);
        } else {
            searchResult = new SearchResultImpl(listProperties, null, false);
        }
        return searchResult;
    }

    public SearchResult doRequestFromUri(URI uri) {
        Edm edm = readEdm(apiUri);
        List<PropertyTO> listProperties = new ArrayList<>();
        SearchResult searchResult;
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator;
        iterator = readEntities(edm, uri);
        while (iterator.hasNext()) {
            ClientEntity ce = iterator.next();
            PropertyTO propertyTO;
            propertyTO = getPropertyTO(ce.getProperties(), 0);
            listProperties.add(propertyTO);
        }
        if (iterator.getNext() != null) {
            searchResult = new SearchResultImpl(listProperties, iterator.getNext(), true);
        } else {
            searchResult = new SearchResultImpl(listProperties, null, false);
        }
        return searchResult;
    }

    private PropertyTO getPropertyTO(Collection<ClientProperty> properties, int level) {
        PropertyTO propertyTO = new PropertyTOImpl(new HashMap<>());
        for (ClientProperty entry : properties) {
            ClientValue value = entry.getValue();
            if (value.isCollection()) {
                ClientCollectionValue cclvalue = value.asCollection();
                getPropertyTO(cclvalue.asJavaCollection(), level + 1);
            } else if (value.isComplex()) {
                ClientComplexValue cpxvalue = value.asComplex();
                getPropertyTO(propertyTO, cpxvalue.asJavaMap(), level + 1);
            } else if (value.isEnum()) {
                ClientEnumValue cnmvalue = value.asEnum();
                propertyTO.addSingleOption(entry.getName(), cnmvalue.getValue());
            } else if (value.isPrimitive()) {
                propertyTO.addSingleOption(entry.getName(), entry.getValue().toString());
            }
        }
        return propertyTO;
    }

    private PropertyTO getPropertyTO(PropertyTO propertyTO, Map<String, Object> properties, int level) {
        Set<Entry<String, Object>> entries = properties.entrySet();
        for (Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                value = getPropertyTO(propertyTO, (Map<String, Object>) value, level + 1);
            } else if (value instanceof Calendar) {
                Calendar cal = (Calendar) value;
                value = SimpleDateFormat.getInstance().format(cal.getTime());
            }
            propertyTO.addSingleOption(entry.getKey(), value.toString());
        }
        return propertyTO;
    }

    private Edm readEdm(String serviceUrl) {
        EdmMetadataRequest request = client.getRetrieveRequestFactory().getMetadataRequest(serviceUrl);
        ODataRetrieveResponse<Edm> response = request.execute();
        return response.getBody();
    }

    private ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntitiesWithFilter(Edm edm, String serviceUri,
                                                                                         String entitySetName, String filterName) {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).filter(filterName).build();
        return readEntities(edm, absoluteUri);
    }

    private ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntities(Edm edm, URI absoluteUri) {
        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request =
                client.getRetrieveRequestFactory().getEntitySetIteratorRequest(absoluteUri);
        request.setAccept("application/json");
        ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute();

        return response.getBody();
    }
}
