package com.smart.controller;

import org.apache.el.util.MessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
    private UserRepository userRepository;
	@GetMapping("/")
	public String home() {
		return "home";
	}
	
	@GetMapping("/about")
	public String about() {
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model, HttpSession session) {
		model.addAttribute("user", new User());
		// Remove session message safely after reading it
	    session.removeAttribute("message");
		return "signup";
	}
	
	//handler for register user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result ,
	                           @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
	                           Model model,
	                           HttpSession session) {
	    try {
	        if (!agreement) {
	            throw new Exception("You have not agreed to terms and conditions");
	        }
	        
	        if(result.hasErrors())
	        {
	        	System.out.println("Error "+ result.toString());
	        	 model.addAttribute("user", user);
	        	return "signup";
	        }
	        
	        user.setRole("ROLE_USER");
	        user.setEnable(true);
	        
	        // Save user to database
	        userRepository.save(user);
	        
	        // Success message
	        session.setAttribute("message", new Message("Successfully Registered!", "alert-success"));
	        
	        return "signup"; // Redirect to prevent form resubmission
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	        // Preserve user data in case of failure
	        model.addAttribute("user", user);
	        
	        session.setAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
	        return "signup";
	    }
	}

}
