package com.boringland.mocardserver.auth;

import com.boringland.mocardserver.annotation.CurrentUser;
import com.boringland.mocardserver.constant.AuthConstant;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.entity.model.UserAccountRepository;
import com.boringland.mocardserver.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //如果参数类型是UserEntity并且有CurrentUser注解则支持
        return methodParameter.getParameterType().isAssignableFrom(UserAccount.class) &&
                methodParameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer container,
                                  NativeWebRequest request,
                                  WebDataBinderFactory factory) throws BusinessException {
        //取出AuthorizationInterceptor中注入的userId
        String currentUserId = (String) request.getAttribute(AuthConstant.CURRENT_USER_ID, RequestAttributes.SCOPE_REQUEST);
        if (currentUserId != null) {
            return userAccountRepository.findByDeviceId(currentUserId);
        }
        throw new BusinessException("用户不存在");
    }
}
