package org.marvel.deevy.common.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.extern.slf4j.Slf4j;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.common.exception.InterfaceException;
import org.marvel.deevy.common.params.VerifiableParam;
import org.marvel.deevy.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * CRUD Controller 通用方法
 * 包含以下几种请求方法：
 * ● 根据ID获取实体对象
 * ● 创建实体对象
 * ● 修改实体对象
 * ● 批量删除实体对象
 *
 * @author haoyuanqiang
 * @date 2022/4/12 18:52
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Slf4j
public abstract class CrudController<IParam extends VerifiableParam<IParam>, IEntity extends BaseEntity,
        IService extends BaseService<IEntity>> {

    @Autowired
    protected IService service;

    @SuppressWarnings("unchecked")
    private IParam getParamInstance() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<IParam> type = (Class<IParam>) superClass.getActualTypeArguments()[0];
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private IEntity getEntityInstance() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        Class<IEntity> type = (Class<IEntity>) superClass.getActualTypeArguments()[1];
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据ID获取对象
     *
     * @param id 对象ID
     * @return 对象
     */
    @GetMapping
    public IParam get(@RequestParam String id) {
        IEntity entity = service.getById(id);
        if (null == entity) {
            // 抛出异常：对象不存在
            throw ExceptionFactory.create("OBJ_NOT_FOUND");
        }
        IParam paramInstance = getParamInstance();
        BeanUtil.copyProperties(entity, paramInstance);
        return paramInstance;
    }


    /**
     * 创建对象
     *
     * @param param 对象参数
     * @return 创建对象的ID
     */
    @PostMapping
    public String create(@RequestBody IParam param) {
        if (null == param) {
            // 抛出异常：参数不可为空
            throw ExceptionFactory.create("PARAM_NOT_NULL");
        }
        param.verify();

        // 自定义校验规则
        customVerificationWhileCreating(param);


        IEntity entityInstance = getEntityInstance();
        CopyOptions copyOptions = CopyOptions.create()
                .ignoreCase()
                .ignoreError()
                .ignoreNullValue()
                .setIgnoreProperties(BaseEntity.Fields.id, BaseEntity.Fields.createTime);
        BeanUtil.copyProperties(param, entityInstance, copyOptions);

        // 创建实体前的自定义操作
        customOperationBeforeCreating(param, entityInstance);

        service.save(entityInstance);

        // 创建实体后的自定义操作
        customOperationAfterCreating(entityInstance);

        return entityInstance.getId();
    }


    /**
     * 修改对象
     *
     * @param param 对象参数
     */
    @PutMapping
    public void modify(@RequestBody IParam param) {
        if (null == param) {
            // 抛出异常：参数不可为空
            throw ExceptionFactory.create("PARAM_NOT_NULL");
        }
        param.verify();

        // 自定义校验规则
        customVerificationWhileModifying(param);

        IEntity entityInstance = service.getById(param.getId());
        if (null == entityInstance) {
            // 抛出异常：对象不存在
            throw ExceptionFactory.create("OBJ_NOT_FOUND");
        }

        // 更新实体前的自定义操作
        customOperationBeforeModifying(param, entityInstance);

        CopyOptions copyOptions = CopyOptions.create()
                .ignoreCase()
                .ignoreError()
                .ignoreNullValue()
                .setIgnoreProperties(BaseEntity.Fields.id, BaseEntity.Fields.createTime);
        BeanUtil.copyProperties(param, entityInstance, copyOptions);
        service.save(entityInstance);

        // 更新实体后的自定义操作
        customOperationAfterModifying(param, entityInstance);
    }


    /**
     * 软删除对象
     *
     * @param ids 对象ID数组
     * @return 删除成功的数量
     */
    @PutMapping("/invalidate")
    public int invalidate(@RequestBody List<String> ids) {
        if (null != ids) {
            customOperationBeforeInvalidating(ids);
            return service.invalidate(ids);
        }
        return 0;
    }

    /**
     * 彻底删除对象
     *
     * @param ids 对象ID数组
     * @return 删除成功的数量
     */
    @DeleteMapping("/{ids}")
    public int delete(@PathVariable("ids") List<String> ids) {
        if (null != ids) {
            customOperationBeforeDeleting(ids);
            return service.delete(ids);
        }
        return 0;
    }

    /**
     * 创建实体对象的自定义校验规则，需要子类重载实现
     * 例如：校验名称是否重复等
     *
     * @param param 参数对象
     * @throws InterfaceException 校验信息通过此异常抛出
     */
    protected abstract void customVerificationWhileCreating(IParam param) throws InterfaceException;

    /**
     * 修改实体对象的自定义校验规则，需要子类重载实现
     * 例如：校验名称是否重复等
     *
     * @param param 参数对象
     * @throws InterfaceException 校验信息通过此异常抛出
     */
    protected abstract void customVerificationWhileModifying(IParam param) throws InterfaceException;

    /**
     * 创建实体前的自定义操作
     *
     * @param param 参数
     */
    protected void customOperationBeforeCreating(IParam param, IEntity entity) {
        // do nothing
        // If necessary, overload this function in a subclass
    }

    /**
     * 创建实体后的自定义操作
     *
     * @param entity 实体
     */
    protected void customOperationAfterCreating(IEntity entity) {
        // do nothing
        // If necessary, overload this function in a subclass
    }

    /**
     * 更新实体前的操作
     *
     * @param param  参数
     * @param entity 实体
     */
    protected void customOperationBeforeModifying(IParam param, IEntity entity) {
        // do nothing
        // If necessary, overload this function in a subclass
    }

    /**
     * 更新实体后的操作
     *
     * @param param  参数
     * @param entity 实体
     */
    protected void customOperationAfterModifying(IParam param, IEntity entity) {
        // do nothing
        // If necessary, overload this function in a subclass
    }

    /**
     * 软删除实体前的操作
     *
     * @param entityIds 实体ID列表
     */
    protected void customOperationBeforeInvalidating(List<String> entityIds) {
        // do nothing
        // If necessary, overload this function in a subclass
    }

    /**
     * 删除实体前的操作
     *
     * @param entityIds 实体ID列表
     */
    protected void customOperationBeforeDeleting(List<String> entityIds) {
        // do nothing
        // If necessary, overload this function in a subclass
    }
}
