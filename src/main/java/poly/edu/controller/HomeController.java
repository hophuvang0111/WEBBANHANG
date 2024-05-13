package poly.edu.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import poly.edu.entity.Charge;
import poly.edu.entity.History;
import poly.edu.entity.Productdetail;
import poly.edu.entity.User;
import poly.edu.repository.ChargeRepository;
import poly.edu.repository.HistoryRepository;
import poly.edu.repository.ProductRepository;
import poly.edu.repository.ProductdetailRepository;
import poly.edu.repository.UserRepository;

@Controller
public class HomeController {

    @Autowired
    ChargeRepository chargeRepository;

    @SuppressWarnings("unused")
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    private ProductdetailRepository productdetailRepository;

    

    @GetMapping({ "/index", "/", "" })
    public String index(Model model) {
        String username = (String) session.getAttribute("username");
        if(username != null){
            model.addAttribute("username", username); 
        }
        return "user/index";
    }

    @GetMapping("/naptien")
    public String naptien(Model model) {
        String username = (String) session.getAttribute("username");
        if(username != null){
            model.addAttribute("username", username); 
        }
        return "user/naptien";
    }

    @GetMapping("/lichsudonhang")
    public String history(Model model) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            User user = userRepository.findByUsername(username);
            model.addAttribute("username", username);
    
            List<History> historylist = historyRepository.findHistoryByUserID(user.getID());
    
            Map<Integer, Map<String, Object>> historyMap = new HashMap<>(); // Khởi tạo HashMap
    
            for (History history : historylist) {
                int tongtien = 0; // Khởi tạo tổng tiền cho mỗi đối tượng History
                Map<String, Object> historyInfo = new HashMap<>(); // Khởi tạo Map để lưu trữ thông tin của History
                List<Productdetail> productdetails = productdetailRepository.findProductdetailByHistoryID(history.getID());
                for (Productdetail pro : productdetails) {
                    if (pro.getProduct() != null && pro.getProduct().getPrice() != null && pro.getStatus()) {
                        tongtien += pro.getProduct().getPrice();
                    }
                }
                // Thêm các thuộc tính của History vào Map
                historyInfo.put("ID", history.getID());
                historyInfo.put("date", history.getDate());
                historyInfo.put("username", history.getUser().getUsername()); // Hoặc sử dụng thuộc tính khác của User nếu cần
                historyInfo.put("totalAmount", tongtien);
    
                // Thêm Map thông tin của History vào HashMap
                historyMap.put(history.getID(), historyInfo);
            }
    
            // Sắp xếp historyMap theo ID từ lớn tới nhỏ
            List<Map.Entry<Integer, Map<String, Object>>> sortedHistory = new ArrayList<>(historyMap.entrySet());
            sortedHistory.sort((entry1, entry2) -> entry2.getKey().compareTo(entry1.getKey()));
    
            // Tạo một LinkedHashMap mới để lưu trữ historyMap đã sắp xếp
            Map<Integer, Map<String, Object>> sortedHistoryMap = new LinkedHashMap<>();
            for (Map.Entry<Integer, Map<String, Object>> entry : sortedHistory) {
                sortedHistoryMap.put(entry.getKey(), entry.getValue());
            }
    
            System.out.println(sortedHistoryMap);
    
            model.addAttribute("historyMap", sortedHistoryMap); // Thêm HashMap đã sắp xếp vào model
        }
    
        return "user/lichsudonhang";
    }
    

    @GetMapping("/admin/123")
    public String admin() {
        return "redirect:/index";
    }

    @GetMapping("/thongtincanhan")
    public String thongtincanhan(Model model) {
        String username = (String) session.getAttribute("username");
        if(username != null){
            model.addAttribute("username", username); 
        }
        return "user/thongtincanhan";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "user/login";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @GetMapping("/admin/product/upload")
    public String uploadProduct(){
        String username = (String) session.getAttribute("username");
        if(username != null){
            User user = userRepository.findByUsername(username);
            if(user.getRole().equals("ADMIN")){
                return "admin/upload";
            }
        }        
        return "user/index";
    }
    @SuppressWarnings("unused")
    // @PostMapping("/postnaptien")
    // public String postnaptien(Model model, @ModelAttribute("charge") Charge charge,
    //         RedirectAttributes redirectAttributes) {
    //     String username = (String) session.getAttribute("username");
    //     if (username != null) {
    //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //         String time = LocalDateTime.now().format(formatter);
    //         User user = userRepository.findByUsername(username);
    //         Charge charges = new Charge();
    //         charges.setUser(user);
    //         charges.setAmount(charge.getAmount());
    //         charges.setTransactiondate(LocalDateTime.now());
    //         charges.setStatus("Chờ thanh toán");
    //         chargeRepository.save(charges);

    //         String hostIp = request.getServerName();
    //         String contextPath = request.getContextPath();

    //         String message = "Nạp tiền!\n"
    //                 + "*Username:* " + username + "\n"
    //                 + "*Tiền nạp:* " + charge.getAmount() + "\n"
    //                 // + "*Nội dung chuyển khoản:* " + content + "\n"
    //                 + "*Ngày nạp:* " + time + "\n"
    //                 + "Link nạp nhanh: " + "[Ấn vào đây](" +  hostIp + contextPath
    //                 + "/api/speed/charge?charId="
    //                 + charges.getID() + "&username=" + username + "&amount=" + charge.getAmount();

    //         TelegramBot bot = new TelegramBot("6853009990:AAHzyQYieOgmEUBr6cAI8-OXGQD_t_GHVW4");
    //         SendMessage send = new SendMessage("5884779776", message).parseMode(ParseMode.Markdown);
    //         SendResponse response = bot.execute(send);
    //     }
    //     return "redirect:/naptien";
    // }

    @PostMapping("/postinfo")
    public String postinfo(Model model, @ModelAttribute("user") User user,
            RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        if (user.getPassword() == null || user.getPassword().equals("")) {
            return "redirect:/thongtincanhan";
        }

        if (username != null) {
            User users = userRepository.findByUsername(username);
            users.setPassword(user.getPassword());
            userRepository.save(users);
        }
        return "redirect:/thongtincanhan";
    }

}
