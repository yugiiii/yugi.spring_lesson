package com.queue.app.api.auth;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.queue.app.action.base.ApiBaseAction;
import com.queue.app.annotation.ApiExecution;
import com.queue.app.annotation.NoAuth;
import com.queue.app.service.auth.AuthService;
import com.queue.common.exception.ErrorException;
import com.queue.common.exception.WarningException;

@CrossOrigin
@RestController
public class AuthApi extends ApiBaseAction {
	
	@Autowired
	private AuthService authService;
	
	@NoAuth
	@ApiExecution(requiredList= {"email", "name"})
	@RequestMapping(value="/api/auth/signup", method=RequestMethod.POST)
	public void authSignUp() {
		super.result = authService.userAuthSignup(
					MapUtils.getString(super.input.data, "email"),
					MapUtils.getString(super.input.data, "name")
				);
	}
	
	@NoAuth
	@ApiExecution(requiredList= {"token", "password"})
	@RequestMapping(value="/api/auth/register", method=RequestMethod.POST)
	public void authFirstSignIn() throws ErrorException {
		super.result = authService.userAuthRegisterByToken(
					MapUtils.getString(super.input.data, "token"),
					MapUtils.getString(super.input.data, "password")
				);
	}
	
	@NoAuth
	@ApiExecution(requiredList= {"email", "password"})
	@RequestMapping(value="/api/auth/signin", method=RequestMethod.POST)
	public void authSignIn() throws ErrorException {
		super.result = authService.userAuthSignin(
					MapUtils.getString(super.input.data, "email"),
					MapUtils.getString(super.input.data, "password")
				);
	}
	
}