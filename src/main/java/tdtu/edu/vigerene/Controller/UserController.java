package tdtu.edu.vigerene.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tdtu.edu.vigerene.Entity.User;
import tdtu.edu.vigerene.Service.UserService;
import java.io.IOException;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping({"/login", "/"})
    public String loginPage(){
        return "login";
    }



    @GetMapping("/signup")
    public String signUpPage(){
        return "signup";
    }


    @PostMapping("/signup")
    public String saveUser(@RequestParam Map<String, String> allParams, HttpServletResponse response){
        String username = allParams.get("username").trim();
        if (userService.existsByUsername(username)) {
            response.setContentType("text/html");
            String message = "Username already exists. Please choose a different username.";
            String script = "<script>alert('" + message + "');window.location.href='/signup';</script>";
            try {
                response.getWriter().println(script);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(allParams.get("password").trim());
        userService.createUser(user);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> allParams, HttpServletRequest request, Model model){
        User user = userService.authenticateUser(allParams.get("username"), allParams.get("password"));
        if(user != null){
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            model.addAttribute("user", user);
            return "redirect:/index";
        }else{
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/index")
    public String homePage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            return "index";
        } else {
            return "redirect:/login";
        }
    }

    private void error(HttpServletResponse response,String msg){
        response.setContentType("text/html");
        String script = "<script>alert('" + msg + "');window.location.href='/index';</script>";
        try {
            response.getWriter().println(script);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/index")
    public String encrypt(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> allParams) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");


        if(user.getVigenerekey() != null){
            String real_password = userService.decryptVigenere(user.getPassword(), user.getVigenerekey());
            if(!real_password.equals(allParams.get("plaintext"))){
                this.error(response, "An error occured. Please check your password and make sure the key contain only alphabetical letter.");
                return null;
            }
        }else{
            if(!user.getPassword().equals(allParams.get("plaintext"))){
                this.error(response, "An error occured. Please check your password and make sure the key contain only alphabetical letter.");
                return null;
            }
        }

        String key = allParams.get("key");
        if(userService.containsNonAlphanumeric(key) || key.length() <= 2){
            this.error(response, "An error occured. Please check your password and make sure the key contain only alphabetical letter and length is greater 2.");
            return null;
        }

        String encryptedPassword = userService.encryptVigenere(allParams.get("plaintext"), key);
        user.setVigenerekey(key);
        user.setPassword(encryptedPassword);
        userService.updateUser(user.getId(), user);
        return "redirect:/index";
    }

}
