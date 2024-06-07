package org.gaius.datasource;

import org.gaius.datasource.exception.DatabaseException;

/**
 * 数据源实例
 *
 * @author gaius.zhao
 * @date 2024/5/11
 */
public interface DatasourceInstance<R> {

    /**
     * 数据源是否可用
     *
     * @param context 服务上下文
     * @return 是否可用
     */
    AvailableResp avaliable(ServiceContext context) throws Exception;

    /**
     * 数据源接口调用
     *
     * @param context 执行上下文
     * @return 调用结果
     * @throws DatabaseException 数据库异常
     */
    R invoke(InvokeContext context) throws DatabaseException;

    /**
     * 销毁当前数据源实例
     */
    void destroy();
}
