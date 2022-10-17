package org.marvel.deevy.foundation.entity.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.marvel.deevy.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_PAGE_ELEMENT", indexes = {
        @Index(name = "MA_IN_PE_NAME", columnList = "NAME"),
        @Index(name = "MA_IN_PE_VALID", columnList = "_VALID")
})
public class PageElement extends BaseEntity {

    @Column(name = "NAME")
    private String name;
}
