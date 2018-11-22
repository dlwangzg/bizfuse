package com.leadingsoft.demo.gateway.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.google.code.kaptcha.Constants;

/**
 * 校验码验证器实现类。
 *
 * @author liuyg
 * @version 1.0
 */
public class KaptchaValidator implements ConstraintValidator<Kaptcha, String> {

    @Override
    public void initialize(final Kaptcha constraintAnnotation) {
        // do nothing.
    }

    /*
     * (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     * javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        // 从Session获取校验码，并清空Session中的校验码，避免多次校验
        final Object expected =
                RequestContextHolder.getRequestAttributes().getAttribute(
                        Constants.KAPTCHA_SESSION_KEY, RequestAttributes.SCOPE_SESSION);
        RequestContextHolder.getRequestAttributes().setAttribute(
                Constants.KAPTCHA_SESSION_KEY, null, RequestAttributes.SCOPE_SESSION);
        if (value.equalsIgnoreCase((String) expected)) {
            return true;
        }
        return false;
    }
}
