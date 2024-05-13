package poly.edu.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpSession;
import poly.edu.entity.Charge;
import poly.edu.entity.User;
import poly.edu.repository.ChargeRepository;
import poly.edu.repository.UserRepository;

@CrossOrigin("*")
@RestController
public class ChargeRestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChargeRepository chargeRepository;
    @Autowired
    HttpSession session;

    // @GetMapping("/api/charge")
    // public boolean charge(@RequestParam String username, @RequestParam Integer
    // amount, @RequestParam String content) {
    // User user = userRepository.findByUsername(username);
    // if (user != null) {
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
    // HH:mm:ss");
    // String time = LocalDateTime.now().format(formatter);
    // String message = "Nạp tiền!\n"
    // + "*Username:* " + username + "\n"
    // + "*Tiền nạp:* " + amount + "\n"
    // + "*Nội dung chuyển khoản:* " + content + "\n"
    // + "*Ngày đặt hàng:* " + time + "\n"
    // + "Link duyệt nhanh: " + "[Ấn vào
    // đây](http://www.localhost/rest/telegram/approve?orderID=";

    // TelegramBot bot = new
    // TelegramBot("6853009990:AAHzyQYieOgmEUBr6cAI8-OXGQD_t_GHVW4");
    // SendMessage send = new SendMessage("5884779776",
    // message).parseMode(ParseMode.Markdown);;
    // SendResponse response = bot.execute(send);

    // Charge charge = new Charge();
    // charge.setUser(user);
    // charge.setAmount(amount);
    // charge.setStatus("Chờ thanh toán");
    // charge.setTransactiondate(LocalDateTime.now());
    // chargeRepository.save(charge);
    // return true;
    // } else {
    // return false;
    // }
    // }

    @GetMapping("/api/showCharge")
    public ResponseEntity<?> showCharge(@RequestParam String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            List<Charge> charges = chargeRepository.findByUser(user.getID());
            return ResponseEntity.ok(charges);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @SuppressWarnings("null")
    @GetMapping("/api/speed/charge")
    public ResponseEntity<String> speedCharge(@RequestParam Integer charId, @RequestParam String username,
            @RequestParam String amount) {
        User user = userRepository.findByUsername(username);
        Optional<Charge> chargeOptional = chargeRepository.findById(charId);
        String usernameSession = (String) session.getAttribute("username");
        if (usernameSession != null) {
            if (chargeOptional.isPresent()) {
                User userSession = userRepository.findByUsername(usernameSession);
                if (userSession.getRole().equals("ADMIN")) {
                    Charge charge = chargeOptional.get();
                    if (!"Đã thanh toán".equals(charge.getStatus())) {
                        charge.setStatus("Đã thanh toán");
                        chargeRepository.save(charge);
                        if (user != null) {
                            user.setBalance(user.getBalance() + Integer.valueOf(amount));
                            userRepository.save(user);
                            System.out.println(charId);
                            System.out.println(charge);
                            return ResponseEntity.ok("Nạp thành công!");
                        } else {
                            return ResponseEntity.badRequest().body("Người dùng không tồn tại!");
                        }
                    } else {
                        return ResponseEntity.badRequest().body("Đã nạp");
                    }
                } else {
                    return ResponseEntity.badRequest().body("Bạn không phải admin!");
                }

            } else {
                return ResponseEntity.badRequest().body("Nạp thất bại! Không tìm thấy thông tin nạp tiền.");
            }
        } else {
            return ResponseEntity.badRequest().body("Nạp thất bại! Không tìm thấy thông tin nạp tiền.");
        }
    }


    @GetMapping("/api/returnQR")
    public void returnQR(@RequestParam String orderCode , @RequestParam String paymentLinkId){
        session.setAttribute("orderCode", orderCode);
        session.setAttribute("paymentLinkId", paymentLinkId);
    }

    @GetMapping("/api/checkQR")
    public void checkQR(){
        String orderCode = (String) session.getAttribute("orderCode");
        String paymentLinkId = (String) session.getAttribute("paymentLinkId");

        if(orderCode != null){
            try {
                String url = "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode;
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("x-client-id", "63f5e22f-a223-4487-a43c-76420ec4b4ec");
                con.setRequestProperty("x-api-key", "8ea0b048-1358-4ea5-9e1d-1ee51384c30f");
                int responseCode = con.getResponseCode();
                System.out.println("Sending GET request to URL: " + url);
                System.out.println("Response Code: " + responseCode);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Response: " + response.toString());
                    Gson gson = new Gson();
                    Map<String, Object> responseMap = gson.fromJson(response.toString(), Map.class);
                    Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                    String status = (String) data.get("status");
                    String id = (String) data.get("id");
                    Double amount = (Double) data.get("amount");
                    System.out.println("Status: " + status);
                    System.out.println("id: " + id);
                    System.out.println(paymentLinkId);
                    if(status.equals("PAID") && id.equals(paymentLinkId)){
                        String username = (String) session.getAttribute("username");
                        User user = userRepository.findByUsername(username);
                        int intValue = amount.intValue();
                        user.setBalance(user.getBalance() + Integer.parseUnsignedInt(String.valueOf(intValue)));
                        userRepository.save(user);

                        Charge charge = new Charge();
                        charge.setUser(user);
                        charge.setAmount(Integer.parseUnsignedInt(String.valueOf(intValue)));
                        charge.setStatus("Đã thanh toán");
                        charge.setTransactiondate(LocalDateTime.now());
                        chargeRepository.save(charge);

                        session.removeAttribute("orderCode");
                        session.removeAttribute("paymentLinkId");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
