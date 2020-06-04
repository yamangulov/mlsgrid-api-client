package com.innedhub.odata;

import com.innedhub.enums.MLSResource;
import com.innedhub.results.PropertyTO;
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


/**
 *
 */
@Slf4j
public class Client {
    private ODataClient client;
    private String apiUri;

    public Client(String apiUri, String apiKey) {
        this.apiUri = apiUri;
        client = ODataClientFactory.getClient();
        client.getConfiguration().setHttpClientFactory(new BearerAuthHttpClientFactory(apiKey));
    }

    public List<PropertyTO> doRequestWithFilter(MLSResource resource, String request) {
        Edm edm = readEdm(apiUri);
        List<PropertyTO> listProperties = new ArrayList<>();
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator;
        iterator = readEntitiesWithFilter(edm, apiUri, resource.getResource(), request);
        while (iterator.hasNext()) {
            PropertyTO propertyTO;
            ClientEntity ce = iterator.next();
            propertyTO = getPropertyTO(ce.getProperties(), 0);
            listProperties.add(propertyTO);
        }
        return listProperties;
    }

    private PropertyTO getPropertyTO(List<ClientProperty> properties, int level) {
        //TODO
        //realize getting name and value each property in each search result and writing them into List<PropertyTo>
        return null;
    }

    //TODO
    //below methods remaind from testing sample of Class.java. They will be deleted after developing class for our library
    public static void main(String[] params) throws Exception {
        Client app = new Client("https://api.mlsgrid.com/", "9559104ea30324a4cbe8b0b25b9b0ec6be948ca8");
        app.perform("https://api.mlsgrid.com/");
    }

    void perform(String serviceUrl) throws Exception {

        print("\n----- Read Entities with $filter  ------------------------------");
        Edm edm = readEdm(serviceUrl);
        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator;
        iterator = readEntitiesWithFilter(edm, serviceUrl, "PropertyResi", "ListingId eq 'MRD06341151' and MlgCanView eq true");
        while (iterator.hasNext()) {
            ClientEntity ce = iterator.next();
            print("Entry:\n" + prettyPrint(ce.getProperties(), 0));
        }

    }

    private static void print(String content) {
        System.out.println(content);
    }

    private static String prettyPrint(Map<String, Object> properties, int level) {
        StringBuilder b = new StringBuilder();
        Set<Entry<String, Object>> entries = properties.entrySet();

        for (Entry<String, Object> entry : entries) {
            intend(b, level);
            b.append(entry.getKey()).append(": ");
            Object value = entry.getValue();
            if (value instanceof Map) {
                value = prettyPrint((Map<String, Object>) value, level + 1);
            } else if (value instanceof Calendar) {
                Calendar cal = (Calendar) value;
                value = SimpleDateFormat.getInstance().format(cal.getTime());
            }
            b.append(value).append("\n");
        }
        // remove last line break
        b.deleteCharAt(b.length() - 1);
        return b.toString();
    }

    private static String prettyPrint(Collection<ClientProperty> properties, int level) {
        StringBuilder b = new StringBuilder();

        for (ClientProperty entry : properties) {
            intend(b, level);
            ClientValue value = entry.getValue();
            if (value.isCollection()) {
                ClientCollectionValue cclvalue = value.asCollection();
                b.append(prettyPrint(cclvalue.asJavaCollection(), level + 1));
            } else if (value.isComplex()) {
                ClientComplexValue cpxvalue = value.asComplex();
                b.append(prettyPrint(cpxvalue.asJavaMap(), level + 1));
            } else if (value.isEnum()) {
                ClientEnumValue cnmvalue = value.asEnum();
                b.append(entry.getName()).append(": ");
                b.append(cnmvalue.getValue()).append("\n");
            } else if (value.isPrimitive()) {
                b.append(entry.getName()).append(": ");
                b.append(entry.getValue()).append("\n");
            }
        }
        return b.toString();
    }

    private static void intend(StringBuilder builder, int intendLevel) {
        for (int i = 0; i < intendLevel; i++) {
            builder.append("  ");
        }
    }

    public Edm readEdm(String serviceUrl) {
        EdmMetadataRequest request = client.getRetrieveRequestFactory().getMetadataRequest(serviceUrl);
        ODataRetrieveResponse<Edm> response = request.execute();
        return response.getBody();
    }

    public ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntitiesWithFilter(Edm edm, String serviceUri,
                                                                                         String entitySetName, String filterName) {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).filter(filterName).build();
        return readEntities(edm, absoluteUri);
    }

    private ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntities(Edm edm, URI absoluteUri) {
        System.out.println("URI = " + absoluteUri);
        ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> request =
                client.getRetrieveRequestFactory().getEntitySetIteratorRequest(absoluteUri);
        request.setAccept("application/json");
        ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> response = request.execute();

        return response.getBody();
    }
}
