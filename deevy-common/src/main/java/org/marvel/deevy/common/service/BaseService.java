package org.marvel.deevy.common.service;

import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.model.PageResult;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/4/11 13:41
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
public interface BaseService<EntityType extends BaseEntity> {

    /**
     * 保存实体信息
     *
     * @param entity 实体信息
     * @return 实体信息
     */
    EntityType save(EntityType entity);

    /**
     * 批量保存实体信息
     *
     * @param entities 实体列表
     * @return 实体列表
     */
    List<EntityType> saveAll(List<EntityType> entities);

    /**
     * 删除实体（软删除）
     *
     * @param id 实体 id
     * @return 删除成功的实体数量
     */
    int invalidate(String id);

    /**
     * 批量删除实体（软删除）
     *
     * @param ids 实体 id 列表
     * @return 删除成功的实体数量
     */
    int invalidate(List<String> ids);

    /**
     * 自定义条件使实体失效
     *
     * @param specification Specification in the sense of Domain Driven Design
     *                      参数:
     *                      root – must not be null.
     *                      query – should not use because it is always null.
     *                      criteriaBuilder – must not be null.
     *                      返回值:
     *                      a Predicate, may be null.
     */
    int invalidate(Specification<EntityType> specification);

    /**
     * 删除实体（硬删除）
     *
     * @param id 实体 ID
     * @return 删除成功的实体数量
     */
    int delete(String id);

    /**
     * 批量删除实体（硬删除）
     *
     * @param ids 实体 ID 列表
     * @return 删除成功的实体数量
     */
    int delete(List<String> ids);

    /**
     * 自定义条件删除实体
     *
     * @param specification Specification in the sense of Domain Driven Design
     *                      参数:
     *                      root – must not be null.
     *                      query – should not use because it is always null.
     *                      criteriaBuilder – must not be null.
     *                      返回值:
     *                      a Predicate, may be null.
     */
    int delete(Specification<EntityType> specification);

    /**
     * 判断实体的某些属性是否重复
     *
     * @param matches   匹配的属性
     * @param ignorance 忽略的属性
     * @return 判断结果
     */
    boolean isDuplicate(Map<String, Object> matches, Map<String, Object> ignorance);

    /**
     * 判断实体是否存在
     *
     * @param id 实体 id
     * @return 判断结果
     */
    Boolean isExistent(String id);

    /**
     * 根据 id 获取实体信息
     *
     * @param id 实体 id
     * @return 实体信息
     */
    EntityType getById(String id);

    /**
     * 获取全部实体信息
     *
     * @return 实体列表
     */
    List<EntityType> getAll();

    /**
     * 分页查询
     *
     * @param pageSize      页大小
     * @param current       当前页
     * @param specification 条件查询表达式
     * @return 分页查询结果
     */
    PageResult<EntityType> getByPage(int pageSize, int current, Specification<EntityType> specification);

    /**
     * 自定义条件查询
     *
     * @param specification 自定义条件
     * @return 查询结果
     */
    List<EntityType> getAll(Specification<EntityType> specification);
}