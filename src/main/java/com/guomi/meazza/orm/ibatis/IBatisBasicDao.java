/* 
 * @(#)IBatisBasicDao.java    Created on 2012-05-31
 * Copyright (c) 2012 Guomi, Inc. All rights reserved.
 */
package com.guomi.meazza.orm.ibatis;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.guomi.meazza.dao.DataMissingException;
import com.guomi.meazza.util.Pagination;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;

/**
 * 基于 <a href="http://ibatis.apache.org">iBATIS</a> 实现的 DAO 基类。
 * 
 * @author akuma
 */
public abstract class IBatisBasicDao {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private SqlExecutor sqlExecutor;
    protected SqlMapClientTemplate sqlMapClientTemplate;

    public IBatisBasicDao() {
    }

    /**
     * 对 DAO 进行初始化的方法。采用反射的方式将自定义的用于处理 limit 查询的 {@link SqlExecutor} 对象注入到 {@link SqlMapClient} 对象中。
     */
    @PostConstruct
    public void initialize() {
        try {
            SqlMapClient sqlMapClient = sqlMapClientTemplate.getSqlMapClient();
            if (sqlMapClient instanceof SqlMapClientImpl) {
                Field field = SqlMapExecutorDelegate.class.getDeclaredField("sqlExecutor");
                boolean originalAccessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    field.set(((SqlMapClientImpl) sqlMapClient).getDelegate(), sqlExecutor);
                } finally {
                    field.setAccessible(originalAccessible);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        logger.trace("{} is initialized", getClass());
    }

    /**
     * 设置 Spring 的 {@link org.springframework.orm.ibatis.SqlMapClientTemplate} 对象。
     * 
     * @param sqlMapClientTemplate
     *            <code>SqlMapClientTemplate</code> 对象
     */
    @Resource
    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        Assert.notNull(sqlMapClientTemplate, "the sqlMapClientTemplate must not be null");
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    /**
     * 设置 iBATIS 的 {@link com.ibatis.sqlmap.engine.execution.SqlExecutor} 对象。
     * 
     * @param sqlExecutor
     *            <code>SqlExecutor</code> 对象
     */
    @Resource
    public void setSqlExecutor(SqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    /**
     * 进行 SELECT 查询。
     * 
     * @param <T>
     *            查询结果对象的类型
     * @param statementName
     *            查询语句名称
     * @return 查询结果对象
     */
    @SuppressWarnings("unchecked")
    protected <T> T queryForObject(String statementName) {
        return (T) sqlMapClientTemplate.queryForObject(getFullStatementName(statementName));
    }

    /**
     * 以 <code>parameter</code> 对象中的值为条件进行 SELECT 查询。
     * 
     * @param <T>
     *            查询结果对象的类型
     * @param statementName
     *            查询语句名称
     * @param parameter
     *            查询参数
     * @return 查询结果对象
     */
    @SuppressWarnings("unchecked")
    protected <T> T queryForObject(String statementName, Object parameter) {
        return (T) sqlMapClientTemplate.queryForObject(getFullStatementName(statementName), parameter);
    }

    /**
     * 查询并返回列表。
     * 
     * @param <T>
     *            查询结果对象的类型
     * @param statementName
     *            查询语句名称
     * @return 查询结果对象列表
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> queryForList(String statementName) {
        return sqlMapClientTemplate.queryForList(getFullStatementName(statementName));
    }

    /**
     * 以 <code>parameter</code> 对象中的值为条件进行 SELECT 查询并返回列表。
     * 
     * @param <T>
     *            查询结果对象的类型
     * @param statementName
     *            查询语句名称
     * @param parameter
     *            查询参数
     * @return 查询结果对象列表
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> queryForList(String statementName, Object parameter) {
        return sqlMapClientTemplate.queryForList(getFullStatementName(statementName), parameter);
    }

    /**
     * 以 <code>parameter</code> 对象中的值为条件进行 SELECT 分页查询并返回列表。
     * 
     * @param <T>
     *            查询结果对象的类型
     * @param statementName
     *            查询语句名称
     * @param parameter
     *            查询参数
     * @param page
     *            分页对象
     * @return 查询结果对象列表
     */
    protected <T> List<T> queryForList(String statementName, Object parameter, Pagination page) {
        String countStatement = statementName + "Count";
        return queryForList(countStatement, statementName, parameter, page);
    }

    /**
     * 以 <code>parameter</code> 对象中的值为条件进行 SELECT 分页查询并返回列表。
     * 
     * @param <T>
     *            查询结果对象的类型
     * @param countStatement
     *            count 语句名称
     * @param queryStatement
     *            查询语句名称
     * @param parameter
     *            查询参数
     * @param page
     *            分页对象
     * @return 查询结果对象列表
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> queryForList(String countStatement, String queryStatement, Object parameter, Pagination page) {
        String fullCountStmt = getFullStatementName(countStatement);
        String fullQueryStmt = getFullStatementName(queryStatement);

        Integer count = (Integer) sqlMapClientTemplate.queryForObject(fullCountStmt, parameter);
        page.setRowCount(count);
        page.initialize();

        // 如果 count 的结果集为空，则直接返回
        if (count == 0) {
            return Collections.emptyList();
        }

        int offset = page.getPageSize() * (page.getPageNum() - 1);
        int limit = page.getPageSize() * page.getPageNum();

        return sqlMapClientTemplate.queryForList(fullQueryStmt, parameter, offset, limit);
    }

    /**
     * 执行 SELECT 查询并将结果集以 <code>Map</code> 的形式返回的方法。
     * 
     * @param <K>
     *            Map 中的 Key 的类型
     * @param <V>
     *            Map 中的 Value 的类型
     * @param statementName
     *            查询语句名称
     * @param parameter
     *            查询参数
     * @param keyProperty
     *            查询结果对象中某个被用来作为 <code>Map</code> 的 key 的属性名称
     * @return 以 <code>keyProperty</code> 为 <code>key</code>，查询结果对象为 <code>value</code> 的 <code>Map</code> 对象
     */
    @SuppressWarnings("unchecked")
    protected <K, V> Map<K, V> queryForMap(String statementName, Object parameter, String keyProperty) {
        return sqlMapClientTemplate.queryForMap(getFullStatementName(statementName), parameter, keyProperty);
    }

    /**
     * 执行 SELECT 查询并将结果集以 <code>Map</code> 的形式返回的方法。
     * 
     * @param <K>
     *            Map 中的 Key 的类型
     * @param <V>
     *            Map 中的 Value 的类型
     * @param statementName
     *            查询语句名称
     * @param parameter
     *            查询参数
     * @param keyProperty
     *            查询结果对象中某个被用来作为 <code>Map</code> 的 <code>value</code> 的属性名称
     * @param valueProperty
     *            查询结果对象中某个被用来作为 <code>Map</code> 的 <code>value</code> 的属性名称
     * @return 以 <code>keyProperty</code> 为 <code>key</code>，<code>valueProperty</code> 为 <code>value</code> 的
     *         <code>Map</code> 对象
     */
    @SuppressWarnings("unchecked")
    protected <K, V> Map<K, V> queryForMap(String statementName, Object parameter, String keyProperty,
            String valueProperty) {
        return sqlMapClientTemplate.queryForMap(getFullStatementName(statementName), parameter, keyProperty,
                valueProperty);
    }

    /**
     * 使用结果集处理器来进行查询。如果遇到需要遍历大数据的情况，使用此方法是一个比较好的选择。
     * 
     * @param statementName
     *            查询语句名称
     * @param rowHandler
     *            结果集处理器
     */
    protected void queryWithRowHanlder(String statementName, RowHandler rowHandler) {
        sqlMapClientTemplate.queryWithRowHandler(getFullStatementName(statementName), rowHandler);
    }

    /**
     * 使用结果集处理器来进行查询。如果遇到需要遍历大数据的情况，使用此方法是一个比较好的选择。
     * 
     * @param statementName
     *            查询语句名称
     * @param parameter
     *            查询参数
     * @param rowHandler
     *            结果集处理器
     */
    protected void queryWithRowHanlder(String statementName, Object parameter, RowHandler rowHandler) {
        sqlMapClientTemplate.queryWithRowHandler(getFullStatementName(statementName), parameter, rowHandler);
    }

    /**
     * 以数组方式批量添加指定的数据。
     * 
     * @param <T>
     *            需要插入的实体对象的类型
     * @param statementName
     *            插入语句名称
     * @param entities
     *            实体对象数组
     */
    @SuppressWarnings("unchecked")
    public <T> void insert(String statementName, T... entities) {
        if (ArrayUtils.isEmpty(entities)) {
            return;
        }

        if (entities.length == 1) {
            T entity = entities[0];
            sqlMapClientTemplate.insert(getFullStatementName(statementName), entity);
        } else {
            insert(statementName, Arrays.asList(entities));
        }
    }

    /**
     * 以列表方式批量添加指定的数据。
     * 
     * @param <T>
     *            需要插入的实体对象的类型
     * @param statementName
     *            插入语句名称
     * @param entities
     *            实体对象列表
     */
    public <T> void insert(final String statementName, final List<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        if (entities.size() == 1) {
            T entity = entities.get(0);
            sqlMapClientTemplate.insert(getFullStatementName(statementName), entity);
        } else {
            sqlMapClientTemplate.execute(new SqlMapClientCallback<Integer>() {

                @Override
                public Integer doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                    executor.startBatch();

                    for (T entity : entities) {
                        executor.insert(getFullStatementName(statementName), entity);
                    }

                    return executor.executeBatch();
                }
            });
        }
    }

    /**
     * 以数组方式批量更新指定的数据。
     * 
     * @param <T>
     *            需要更新的实体对象的类型
     * @param statementName
     *            更新语句名称
     * @param entities
     *            实体对象数组
     * @return 更新成功的记录数
     */
    @SuppressWarnings("unchecked")
    protected <T> int update(String statementName, T... entities) {
        if (ArrayUtils.isEmpty(entities)) {
            return 0;
        }

        if (entities.length == 1) {
            T entity = entities[0];
            int result = sqlMapClientTemplate.update(getFullStatementName(statementName), entity);
            if (result < 1) {
                throw new DataMissingException("该数据不存在或者已经被删除");
            }
            return result;
        }

        return update(statementName, Arrays.asList(entities));
    }

    /**
     * 以列表方式批量更新指定的数据。
     * 
     * @param <T>
     *            需要更新的实体对象的类型
     * @param statementName
     *            更新语句名称
     * @param entities
     *            实体对象列表
     * @return 更新成功的记录数
     */
    protected <T> int update(final String statementName, final List<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }

        if (entities.size() == 1) {
            T entity = entities.get(0);
            int result = sqlMapClientTemplate.update(getFullStatementName(statementName), entity);
            if (result < 1) {
                throw new DataMissingException("该数据不存在或者已经被删除");
            }
            return result;
        }

        Integer result = sqlMapClientTemplate.execute(new SqlMapClientCallback<Integer>() {

            @Override
            public Integer doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                executor.startBatch();

                for (T entity : entities) {
                    executor.update(getFullStatementName(statementName), entity);
                }

                return executor.executeBatch();
            }
        });

        return result == null ? 0 : result.intValue();
    }

    /**
     * 删除指定的记录。
     * 
     * @param statementName
     *            删除语句名称
     * @param parameter
     *            删除语句中 WHERE 条件的参数
     * @return 删除成功的记录数
     */
    protected int delete(String statementName, Object parameter) {
        return sqlMapClientTemplate.delete(getFullStatementName(statementName), parameter);
    }

    /**
     * 删除多条指定的记录。
     * 
     * @param statementName
     *            删除语句名称
     * @param parameters
     *            删除语句中 WHERE 条件的参数
     * @return 删除成功的记录数
     */
    protected int delete(String statementName, Object... parameters) {
        if (ArrayUtils.isEmpty(parameters)) {
            return 0;
        }

        return sqlMapClientTemplate.delete(getFullStatementName(statementName), parameters);
    }

    /**
     * 获取 Spring 的 {@link org.springframework.orm.ibatis.SqlMapClientTemplate} 对象。
     * 
     * @return <code>SqlMapClientTemplate</code> 对象
     */
    protected SqlMapClientTemplate getSqlMapClientTemplate() {
        return sqlMapClientTemplate;
    }

    /**
     * 获取 iBATIS 的 SQL 语句的命名空间（必须要开启 iBATIS 的命名空间功能）。<br>
     * 该值默认情况下是继承此类的类全名，例如 foo.BarDao 继承了此类，则命名空间即为 foo.BarDao。<br>
     * 如子类需要其他形式的命名空间，请重载此方法，例如：
     * 
     * 一个以 "user" 为命名空间的例子：
     * 
     * <pre>
     * &#064;Override
     * protected String statementNamespace() {
     *     return &quot;user&quot;;
     * }
     * </pre>
     * 
     * @return SQL 语句的命名空间
     */
    protected String statementNamespace() {
        return getClass().getName();
    }

    /**
     * 获取完整的语句，如果有命名空间，会包含命名空间。
     * 
     * @param statementName
     *            语句名称
     * @return 完整的语句名称
     */
    private String getFullStatementName(String statementName) {
        if (StringUtils.isEmpty(statementNamespace())) {
            return statementName;
        }

        StringBuilder statement = new StringBuilder(statementNamespace());
        statement.append(".");
        statement.append(statementName);
        return statement.toString();
    }

}
