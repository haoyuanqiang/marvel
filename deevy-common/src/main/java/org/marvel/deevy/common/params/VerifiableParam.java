package org.marvel.deevy.common.params;

import lombok.Getter;
import lombok.Setter;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.common.exception.InterfaceException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.Optional;
import java.util.Set;

/**
 * 可校验自身的参数类型
 *
 * @author haoyuanqiang
 * @date 2022/4/12 18:56
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Setter
@Getter
public abstract class VerifiableParam<T> extends BaseParam {

    /**
     * 使用 {@link javax.validation.Validator} 校验参数字段
     * 若要进行自定义校验，可在子类中重载实现
     *
     * @throws InterfaceException 校验信息通过此异常抛出
     */
    public void verify() throws InterfaceException {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<VerifiableParam<T>>> set = validator.validate(this, Default.class);
            if (null != set && set.size() > 0) {
                Optional<ConstraintViolation<VerifiableParam<T>>> optional = set.stream().findFirst();
                ConstraintViolation<VerifiableParam<T>> constraintViolation = optional.get();
                throw ExceptionFactory.create(constraintViolation.getMessage());
            }
        }
        customVerify();
    }


    public void customVerify() {
        // do nothing
    }

}
