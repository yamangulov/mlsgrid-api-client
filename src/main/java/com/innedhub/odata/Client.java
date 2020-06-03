package com.innedhub.odata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;


/**
 *
 */
@Slf4j
public class Client {
    private ODataClient client;

    public Client() {
        client = ODataClientFactory.getClient();
        client.getConfiguration().setHttpClientFactory(new BearerAuthHttpClientFactory("9559104ea30324a4cbe8b0b25b9b0ec6be948ca8"));
    }

    public static void main(String[] params) throws Exception {
        Client app = new Client();
        //EdmMetadataRequest already has $metadata uri
        app.perform("https://api.mlsgrid.com/");
    }

    void perform(String serviceUrl) throws Exception {

        print("\n----- Read Edm ------------------------------");
        Edm edm = readEdm(serviceUrl);
        List<FullQualifiedName> ctFqns = new ArrayList<FullQualifiedName>();
        List<FullQualifiedName> etFqns = new ArrayList<FullQualifiedName>();
        for (EdmSchema schema : edm.getSchemas()) {
            for (EdmComplexType complexType : schema.getComplexTypes()) {
                ctFqns.add(complexType.getFullQualifiedName());
            }
            for (EdmEntityType entityType : schema.getEntityTypes()) {
                etFqns.add(entityType.getFullQualifiedName());
            }
        }
        print("Found ComplexTypes", ctFqns);
        print("Found EntityTypes", etFqns);

        print("\n----- Inspect each property and its type of the first entity: " + etFqns.get(0) + "----");
        EdmEntityType etype = edm.getEntityType(etFqns.get(0));
        for (String propertyName : etype.getPropertyNames()) {
            EdmProperty property = etype.getStructuralProperty(propertyName);
            FullQualifiedName typeName = property.getType().getFullQualifiedName();
            print("property '" + propertyName + "' " + typeName);
        }

//        print("\n----- Read Entities ------------------------------");
//        ClientEntitySetIterator<ClientEntitySet, ClientEntity> iterator =
//                readEntities(edm, serviceUrl, "Manufacturers");
//
//        while (iterator.hasNext()) {
//            ClientEntity ce = iterator.next();
//            print("Entry:\n" + prettyPrint(ce.getProperties(), 0));
//        }
//
//        print("\n----- Read Entry ------------------------------");
//        ClientEntity entry = readEntityWithKey(edm, serviceUrl, "Manufacturers", 1);
//        print("Single Entry:\n" + prettyPrint(entry.getProperties(), 0));
//
//        //
//        print("\n----- Read Entity with $expand  ------------------------------");
//        entry = readEntityWithKeyExpand(edm, serviceUrl, "Manufacturers", 1, "Cars");
//        print("Single Entry with expanded Cars relation:\n" + prettyPrint(entry.getProperties(), 0));
//
//        //
//        print("\n----- Read Entities with $filter  ------------------------------");
//        iterator = readEntitiesWithFilter(edm, serviceUrl, "Manufacturers", "Name eq 'Horse Powered Racing'");
//        while (iterator.hasNext()) {
//            ClientEntity ce = iterator.next();
//            print("Entry:\n" + prettyPrint(ce.getProperties(), 0));
//        }

    }

    private static void print(String content) {
        System.out.println(content);
    }

    private static void print(String content, List<?> list) {
        System.out.println(content);
        for (Object o : list) {
            System.out.println("    " + o);
        }
        System.out.println();
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

    public Edm readEdm(String serviceUrl) throws IOException {
        EdmMetadataRequest request = client.getRetrieveRequestFactory().getMetadataRequest(serviceUrl);
        ODataRetrieveResponse<Edm> response = request.execute();
        return response.getBody();
    }

    public ClientEntitySetIterator<ClientEntitySet, ClientEntity> readEntities(Edm edm, String serviceUri,
                                                                               String entitySetName) {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).build();
        return readEntities(edm, absoluteUri);
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

    public ClientEntity readEntityWithKey(Edm edm, String serviceUri, String entitySetName, Object keyValue) {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName)
                .appendKeySegment(keyValue).build();
        return readEntity(edm, absoluteUri);
    }

    public ClientEntity readEntityWithKeyExpand(Edm edm, String serviceUri, String entitySetName, Object keyValue,
                                                String expandRelationName) {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).appendKeySegment(keyValue)
                .expand(expandRelationName).build();
        return readEntity(edm, absoluteUri);
    }

    private ClientEntity readEntity(Edm edm, URI absoluteUri) {
        ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory().getEntityRequest(absoluteUri);
        request.setAccept("application/json;odata.metadata=full");
        ODataRetrieveResponse<ClientEntity> response = request.execute();

        return response.getBody();
    }

    private ClientEntity loadEntity(String path) throws ODataDeserializerException {
        InputStream input = getClass().getResourceAsStream(path);
        return client.getBinder().getODataEntity(client.getDeserializer(ContentType.APPLICATION_JSON).toEntity(input));
    }

    public ClientEntity createEntity(Edm edm, String serviceUri, String entitySetName, ClientEntity ce) {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entitySetName).build();
        return createEntity(edm, absoluteUri, ce);
    }

    private ClientEntity createEntity(Edm edm, URI absoluteUri, ClientEntity ce) {
        ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory()
                .getEntityCreateRequest(absoluteUri, ce);
        request.setAccept("application/json");
        ODataEntityCreateResponse<ClientEntity> response = request.execute();

        return response.getBody();
    }

    public int updateEntity(Edm edm, String serviceUri, String entityName, Object keyValue, ClientEntity ce) {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entityName)
                .appendKeySegment(keyValue).build();
        ODataEntityUpdateRequest<ClientEntity> request =
                client.getCUDRequestFactory().getEntityUpdateRequest(absoluteUri, UpdateType.PATCH, ce);
        request.setAccept("application/json;odata.metadata=minimal");
        ODataEntityUpdateResponse<ClientEntity> response = request.execute();
        return response.getStatusCode();
    }

    public int deleteEntity(String serviceUri, String entityName, Object keyValue) throws IOException {
        URI absoluteUri = client.newURIBuilder(serviceUri).appendEntitySetSegment(entityName)
                .appendKeySegment(keyValue).build();
        ODataDeleteRequest request = client.getCUDRequestFactory().getDeleteRequest(absoluteUri);
        request.setAccept("application/json;odata.metadata=minimal");
        ODataDeleteResponse response = request.execute();
        return response.getStatusCode();
    }
}
