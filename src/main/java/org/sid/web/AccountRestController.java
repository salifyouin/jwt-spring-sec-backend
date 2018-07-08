package org.sid.web;

import javax.management.RuntimeErrorException;

import org.sid.entities.AppUser;
import org.sid.service.AcountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountRestController {
	@Autowired
	private AcountService accountService;
	@PostMapping("/register")
 public AppUser register(@RequestBody RegisterForm userForm) {
		
		//Si mot de passe est differente de la confirmation
		if(!userForm.getPassword().equals(userForm.getRepassword()))
			throw new RuntimeException("You must confirm your password");
		//Recuperation du username
		AppUser user=accountService.findUserByUsername(userForm.getUsername());
		//Si l'utilisateur existe
		if(user!=null ) throw new RuntimeException("This user already exist");
		//Sinon j'enregistre
		AppUser appUser= new AppUser();
		appUser.setUsername(userForm.getUsername());
		appUser.setUsername(userForm.getPassword());
	    accountService.saveUser(appUser);
	  //Enreistrer l'utilisateur avec USER comme role par defau
	    accountService.addRoleToUser(userForm.getUsername(), "USER");
	    return appUser;
 }
}
