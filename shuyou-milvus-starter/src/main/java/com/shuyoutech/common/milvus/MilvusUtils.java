package com.shuyoutech.common.milvus;

import com.google.gson.JsonObject;
import com.shuyoutech.common.core.util.SpringUtils;
import io.milvus.common.clientenum.FunctionType;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.database.request.CreateDatabaseReq;
import io.milvus.v2.service.rbac.request.*;
import io.milvus.v2.service.rbac.response.DescribeRoleResp;
import io.milvus.v2.service.vector.request.*;
import io.milvus.v2.service.vector.request.data.BaseVector;
import io.milvus.v2.service.vector.response.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <a href="https://milvus.io/api-reference/java/v2.6.x">milvus</a>
 *
 * @author YangChao
 * @date 2025-08-24 11:53
 **/
@Slf4j
public class MilvusUtils {

    public static final MilvusClientV2 milvusClientV2 = SpringUtils.getBean(MilvusClientV2.class);

    public static void createRole(String roleName) {
        CreateRoleReq req = CreateRoleReq.builder().roleName(roleName).build();
        milvusClientV2.createRole(req);
    }

    public static void createUser(String userName, String password) {
        CreateUserReq req = CreateUserReq.builder().userName(userName).password(password).build();
        milvusClientV2.createUser(req);
    }

    public static DescribeRoleResp describeRole(String roleName) {
        DescribeRoleReq req = DescribeRoleReq.builder().roleName(roleName).build();
        return milvusClientV2.describeRole(req);
    }

    public static DescribeRoleResp describeRole(String roleName, String dbName) {
        DescribeRoleReq req = DescribeRoleReq.builder().roleName(roleName).dbName(dbName).build();
        return milvusClientV2.describeRole(req);
    }

    public static void describeUser(String userName) {
        DescribeUserReq req = DescribeUserReq.builder().userName(userName).build();
        milvusClientV2.describeUser(req);
    }

    public static void dropRole(String roleName) {
        DropRoleReq req = DropRoleReq.builder().roleName(roleName).build();
        milvusClientV2.dropRole(req);
    }

    public static void dropUser(String userName) {
        DropUserReq req = DropUserReq.builder().userName(userName).build();
        milvusClientV2.dropUser(req);
    }

    public static void grantRole(String userName, String roleName) {
        GrantRoleReq req = GrantRoleReq.builder().roleName(roleName).userName(userName).build();
        milvusClientV2.grantRole(req);
    }

    public static void grantPrivilege(String roleName, String privilege, String dbName, String collectionName) {
        GrantPrivilegeReqV2 grantPrivilegeReqV2 = GrantPrivilegeReqV2.builder().roleName(roleName).privilege(privilege).dbName(dbName).collectionName(collectionName).build();
        milvusClientV2.grantPrivilegeV2(grantPrivilegeReqV2);
    }

    public static void revokePrivilege(String roleName, String privilege, String dbName, String collectionName) {
        RevokePrivilegeReqV2 req = RevokePrivilegeReqV2.builder().roleName(roleName).privilege(privilege).dbName(dbName).collectionName(collectionName).build();
        milvusClientV2.revokePrivilegeV2(req);
    }

    public static void revokeRole(String roleName, String userName) {
        RevokeRoleReq req = RevokeRoleReq.builder().roleName(roleName).userName(userName).build();
        milvusClientV2.revokeRole(req);
    }

    public static void updatePassword(String userName, String oldPassword, String newPassword) {
        UpdatePasswordReq req = UpdatePasswordReq.builder().userName(userName).password(oldPassword).newPassword(newPassword).build();
        milvusClientV2.updatePassword(req);
    }

    public static void createDatabase(String databaseName) {
        CreateDatabaseReq req = CreateDatabaseReq.builder().databaseName(databaseName).build();
        milvusClientV2.createDatabase(req);
    }

    public static void createDatabase(String databaseName, Map<String, String> properties) {
        CreateDatabaseReq req = CreateDatabaseReq.builder().databaseName(databaseName).properties(properties).build();
        milvusClientV2.createDatabase(req);
    }

    public static AddFieldReq addField(String fieldName, DataType dataType, Boolean isPrimaryKey, Boolean autoID, String description) {
        return AddFieldReq.builder().fieldName(fieldName).dataType(dataType).isPrimaryKey(isPrimaryKey).autoID(autoID).description(description).build();
    }

    public static AddFieldReq addField(String fieldName, DataType dataType, Integer dimension, String description) {
        return AddFieldReq.builder().fieldName(fieldName).dataType(dataType).dimension(dimension).description(description).build();
    }

    public static AddFieldReq addField(String fieldName, DataType dataType, String description) {
        return AddFieldReq.builder().fieldName(fieldName).dataType(dataType).description(description).build();
    }

    public static CreateCollectionReq.Function addFunction(FunctionType functionType, String name, List<String> inputFieldNames, List<String> outputFieldNames) {
        return CreateCollectionReq.Function.builder().functionType(functionType).name(name).inputFieldNames(inputFieldNames).outputFieldNames(outputFieldNames).build();
    }

    public static CreateCollectionReq.CollectionSchema createSchema() {
        return MilvusClientV2.CreateSchema();
    }

    public static void createCollection(String collectionName, CreateCollectionReq.CollectionSchema collectionSchema, IndexParam indexParam, Integer dimension, String description) {
        CreateCollectionReq req = CreateCollectionReq.builder() //
                .collectionName(collectionName) //
                .description(description) //
                .dimension(dimension) //
                .collectionSchema(collectionSchema) //
                .indexParams(Collections.singletonList(indexParam)) //
                .build();
        milvusClientV2.createCollection(req);
    }

    public static InsertResp insert(String collectionName, List<JsonObject> data) {
        InsertReq insertReq = InsertReq.builder() //
                .collectionName(collectionName) //
                .data(data) //
                .build();
        return milvusClientV2.insert(insertReq);
    }

    public static UpsertResp upsert(String collectionName, List<JsonObject> data) {
        UpsertReq upsertReq = UpsertReq.builder() //
                .collectionName(collectionName) //
                .data(data) //
                .build();
        return milvusClientV2.upsert(upsertReq);
    }

    public static GetResp getById(String collectionName, Object id) {
        GetReq getReq = GetReq.builder() //
                .collectionName(collectionName) //
                .ids(Collections.singletonList(id)) //
                .build();
        return milvusClientV2.get(getReq);
    }

    public static GetResp getByIds(String collectionName, List<Object> ids) {
        GetReq getReq = GetReq.builder() //
                .collectionName(collectionName) //
                .ids(ids) //
                .build();
        return milvusClientV2.get(getReq);
    }

    public static DeleteResp deleteByIds(String collectionName, List<Object> ids) {
        DeleteReq deleteReq = DeleteReq.builder() //
                .collectionName(collectionName) //
                .ids(ids) //
                .build();
        return milvusClientV2.delete(deleteReq);
    }

    public static DeleteResp delete(String collectionName, String filter) {
        DeleteReq deleteReq = DeleteReq.builder() //
                .collectionName(collectionName) //
                .filter(filter) //
                .build();
        return milvusClientV2.delete(deleteReq);
    }

    public static List<QueryResp.QueryResult> query(String collectionName, String filter, int limit) {
        QueryReq queryReq = QueryReq.builder() //
                .collectionName(collectionName) //
                .filter(filter) //
                .limit(limit) //
                .build();
        QueryResp query = milvusClientV2.query(queryReq);
        return query.getQueryResults();
    }

    public static List<List<SearchResp.SearchResult>> search(String collectionName, List<BaseVector> data, int limit) {
        SearchReq req = SearchReq.builder() //
                .collectionName(collectionName) //
                .data(data) //
                .topK(limit) //
                .outputFields(Collections.singletonList("*")) //
                .build();
        SearchResp searchR = milvusClientV2.search(req);
        return searchR.getSearchResults();
    }

    public static List<List<SearchResp.SearchResult>> search(String collectionName, String filter, int limit) {
        SearchReq req = SearchReq.builder() //
                .collectionName(collectionName) //
                .filter(filter) //
                .topK(limit) //
                .outputFields(Collections.singletonList("*")) //
                .build();
        SearchResp searchR = milvusClientV2.search(req);
        return searchR.getSearchResults();
    }

    public static List<List<SearchResp.SearchResult>> search(String collectionName, List<BaseVector> data, String filter, int limit) {
        SearchReq req = SearchReq.builder() //
                .collectionName(collectionName) //
                .data(data) //
                .filter(filter) //
                .topK(limit) //
                .outputFields(Collections.singletonList("*")) //
                .build();
        SearchResp searchR = milvusClientV2.search(req);
        return searchR.getSearchResults();
    }

}
