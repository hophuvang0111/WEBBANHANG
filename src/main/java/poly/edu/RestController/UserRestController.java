package poly.edu.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import poly.edu.entity.User;
import poly.edu.repository.UserRepository;
import java.io.InputStreamReader;
import org.json.JSONObject;

@CrossOrigin("*")
@RestController
public class UserRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    HttpSession session;
    @Autowired HttpServletRequest request;

    public String checkCountry(String host) {
        String country = null;
        try {
            // Địa chỉ IP bạn muốn kiểm tra

            // Tạo URL cho API mới (ví dụ: ip-api.com)
            URL url = new URL("http://ip-api.com/json/" + host);

            // Mở kết nối HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Thiết lập phương thức yêu cầu
            connection.setRequestMethod("GET");

            // Đọc phản hồi từ API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Chuyển đổi phản hồi JSON thành đối tượng JSON
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Lấy thông tin quốc gia từ phản hồi JSON
            country = jsonResponse.getString("countryCode");
            // Đóng kết nối
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return country;
    }

    @GetMapping("/api/session")
    public ResponseEntity<?> getSession() {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // Tạo một đối tượng JSON mới và đặt giá trị của username vào đó
                ObjectNode jsonObject = objectMapper.createObjectNode();
                jsonObject.put("username", username);

                // Chuyển đổi đối tượng JSON thành chuỗi JSON
                String jsonString = objectMapper.writeValueAsString(jsonObject);

                return ResponseEntity.ok().body(jsonString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.ok().build();
        }

    }

    @GetMapping("/api/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ipAddress = httpRequest.getRemoteAddr();
        System.out.println(ipAddress);
        String country = checkCountry("14.191.206.202");
        if(country == null || country.equals("VN")){
            if (user != null && user.getPassword().equals(password)) {
                session.setAttribute("username", user.getUsername());
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }        
    }

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        // Kiểm tra xem người dùng đã tồn tại chưa
        if (userRepository.findByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setBalance(0);
        newUser.setActive(true);
        newUser.setRole("USER");
        userRepository.save(newUser);

        return ResponseEntity.ok(newUser);

    }

    @GetMapping("/api/infomation")
    public ResponseEntity<?> infomation() {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            User user = userRepository.findByUsername(username);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
