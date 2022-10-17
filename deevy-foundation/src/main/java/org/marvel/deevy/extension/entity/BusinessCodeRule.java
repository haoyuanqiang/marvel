package org.marvel.deevy.extension.entity;

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
 * @date 2022/10/14 17:47
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_EXT_BUSINESS_CODE_RULE", indexes = {
        @Index(name = "MA_IN_EXT_BCR_VALID", columnList = "_VALID")
})
public class BusinessCodeRule extends BaseEntity {

    @Column(name = "NAME")
    private String name;

    @Column(name = "RULE")
    private String rule;
}
