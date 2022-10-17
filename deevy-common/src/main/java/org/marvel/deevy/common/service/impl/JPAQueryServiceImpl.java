package org.marvel.deevy.common.service.impl;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.marvel.deevy.common.context.SpringBootApplicationContext;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.repository.BaseRepository;

import javax.annotation.PostConstruct;

/**
 * 扩展服务接口实现
 * 相对于 {@link BaseServiceImpl} 扩展了Querydsl查询，用于实现复杂SQL查询
 *
 * @author haoyuanqiang
 * @date 2022/4/13 10:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
public class JPAQueryServiceImpl<EntityType extends BaseEntity, IRepository extends BaseRepository<EntityType, String>>
        extends BaseServiceImpl<EntityType, IRepository> {

    protected JPAQueryFactory jpaQueryFactory;

    /**
     * 初始化
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initialize() {
        if (ObjectUtil.isNull(repository)) {
            repository = (IRepository) SpringBootApplicationContext.getBean(ClassLoaderUtil.loadClass(getRepositoryName()));
        }
        if (ObjectUtil.isNull(jpaQueryFactory)) {
            jpaQueryFactory = new JPAQueryFactory(entityManager);
        }
    }
}
