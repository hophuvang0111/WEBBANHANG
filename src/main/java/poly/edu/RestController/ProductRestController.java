package poly.edu.RestController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import jakarta.servlet.http.HttpSession;
import poly.edu.entity.History;
import poly.edu.entity.Product;
import poly.edu.entity.Productdetail;
import poly.edu.entity.User;
import poly.edu.repository.HistoryRepository;
import poly.edu.repository.ProductRepository;
import poly.edu.repository.ProductdetailRepository;
import poly.edu.repository.UserRepository;

@CrossOrigin("*")
@RestController
public class ProductRestController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    HttpSession session;

    @Autowired
    private ProductdetailRepository productdetailRepository;

    @GetMapping("/api/product")
    public ResponseEntity<?> product() {
        List<Product> products = productRepository.findAll();
        List<Map<Object, Object>> productsMapList = new ArrayList<>();

        for (Product product : products) {
            List<Productdetail> productdt = productdetailRepository.findProductdetailByProductID(product.getID()).stream()
                                    .filter(product1 -> !product1.getStatus())
                                    .collect(Collectors.toList());

            
            Map<Object, Object> productMap = new HashMap<>();
            productMap.put("ID", product.getID());
            productMap.put("image", product.getImage());
            productMap.put("name", product.getName());
            productMap.put("price", product.getPrice());
            productMap.put("describe", product.getDescribe());
            productMap.put("describe", product.getDescribe());
            productMap.put("quantity", productdt.size());
            productsMapList.add(productMap);
        }

        if (!productsMapList.isEmpty()) {
            return ResponseEntity.ok(productsMapList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm nào.");
        }
    }

    @SuppressWarnings("null")
    @GetMapping("/api/downloadTxt")
    public ResponseEntity<?> downloadTxtFile() {
        List<Product> products = productRepository.findAll();
        StringBuilder contentBuilder = new StringBuilder();
        for (Product product : products) {
            String line = String.format("%s|%s|%d\n", product.getImage(), product.getName(), product.getPrice());
            contentBuilder.append(line);
        }
        String content = contentBuilder.toString();
        ByteArrayResource resource = new ByteArrayResource(content.getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @SuppressWarnings("null")
    @GetMapping("/api/download")
    public ResponseEntity<?> download(@RequestParam Integer historyId) {
        Boolean check = false;
        StringBuilder contentBuilder = new StringBuilder();

        String SSUsername = (String) session.getAttribute("username");
        User user = userRepository.findByUsername(SSUsername);
        List<History> history = historyRepository.findHistoryByUserID(user.getID());
        if (history != null) {
            for (History history2 : history) {
                if (history2.getID() == historyId) {
                    check = true;
                }
            }
        }

        if (check) {
            List<Productdetail> productdetails = productdetailRepository.findProductdetailByHistoryID(historyId);
            for (Productdetail productdetail : productdetails) {
                String line = String.format("%s|%s|%s|%s\n", productdetail.getEmail(), productdetail.getPassword(),
                        productdetail.getEmailrecovery(), productdetail.getIP());
                contentBuilder.append(line);
            }
            String content = contentBuilder.toString();
            ByteArrayResource resource = new ByteArrayResource(content.getBytes());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } else {
            return ResponseEntity.badRequest().body("Tải về thất bại");
        }

    }

    @GetMapping("/api/product/count")
    public ResponseEntity<?> productCount(@RequestParam Integer productID) {
        Map<String, Map<String, Integer>> countMap = new HashMap<>();
    
        List<Productdetail> productdetail = productdetailRepository.findProductdetailByProductID(productID);

        List<Productdetail> productdt = productdetail.stream().filter(product -> !product.getStatus()).collect(Collectors.toList());


        for (Productdetail productdetail2 : productdt) {
            String os = productdetail2.getOs();
            String local = productdetail2.getLocal();
    
            // Kiểm tra xem cặp (os, local) đã có trong Map chưa
            if (!countMap.containsKey(os)) {
                countMap.put(os, new HashMap<>());
            }
    
            // Kiểm tra xem local đã có trong Map của os chưa
            Map<String, Integer> localMap = countMap.get(os);
            if (!localMap.containsKey(local)) {
                localMap.put(local, 0);
            }
    
            // Tăng số lượng cho cặp (os, local)
            localMap.put(local, localMap.get(local) + 1);
        }
        return ResponseEntity.ok(countMap);
    }
    

    


    @GetMapping("/api/product/sell")
    public ResponseEntity<?> sell(@RequestParam Integer productID, @RequestParam Integer quantitySell, @RequestParam String local, @RequestParam String os) {

        if(quantitySell <= 0 || productID == null || local.equals("") || "".equals(os)){
            return ResponseEntity.badRequest().build();
        }

        int tongtien = 0;
        List<Productdetail> productdetail = productdetailRepository.findProductdetailByProductID(productID);
        List<Productdetail> filteredProductdetails = productdetail.stream()
                .filter(pd -> Boolean.FALSE.equals(pd.getStatus()) && pd.getLocal().equals(local) && pd.getOs().equals(os))
                .collect(Collectors.toList());
        int returnQuantity = Math.min(quantitySell, filteredProductdetails.size());
        List<Productdetail> selectedProductdetails = filteredProductdetails.subList(0, returnQuantity);

        if(selectedProductdetails.size() < quantitySell){
            return ResponseEntity.badRequest().build();
        }



        String username = (String) session.getAttribute("username");
        User user = userRepository.findByUsername(username);

        if (user != null) {

            for (Productdetail productdetail2 : selectedProductdetails) {
                tongtien += productdetail2.getProduct().getPrice();
            }

            if (user.getBalance() >= tongtien && user.getBalance() >= tongtien) {

                user.setBalance(user.getBalance() - tongtien);
                userRepository.save(user);
                System.out.println(user.getBalance());

                History history = new History();
                history.setUser(user);
                history.setDate(LocalDateTime.now());
                historyRepository.save(history);

                for (Productdetail productdetail2 : selectedProductdetails) {
                    productdetail2.setStatus(true);
                    productdetail2.setHistory(history);
                    productdetailRepository.save(productdetail2);
                }

             
                if (user != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String time = LocalDateTime.now().format(formatter);
            
                    ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
                    ZonedDateTime nowInVietnam = ZonedDateTime.now(zoneId);
                    String timeInVietnam = nowInVietnam.format(formatter);

                String nameProduct = selectedProductdetails.get(0).getProduct().getName();
                DecimalFormat decimalFormat = new DecimalFormat("#,###");
                String formattedTongTien = decimalFormat.format(tongtien);

                String message = "Đơn hàng mới!\n"
                + "*Username:* " + username + "\n"
                + "*Ngày đặt hàng:* " + timeInVietnam  + "\n"
                + "Tổng tiền: " + formattedTongTien + "\n"
                + "Tên sản phẩm: " + nameProduct + " x " + quantitySell + "\n";

                TelegramBot bot = new
                TelegramBot("6853009990:AAHzyQYieOgmEUBr6cAI8-OXGQD_t_GHVW4");
                SendMessage send = new SendMessage("5884779776",
                message).parseMode(ParseMode.Markdown);;
                SendResponse response = bot.execute(send);
                }
                

                return ResponseEntity.ok().body(selectedProductdetails);

            } else {
                System.out.println(user.getBalance());
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping("/api/history")
    public ResponseEntity<List<Productdetail>> history() {
        String username = (String) session.getAttribute("username");
        User user = userRepository.findByUsername(username);
        List<Productdetail> productdetail = new ArrayList<>();

        List<History> historylist = historyRepository.findHistoryByUserID(user.getID());
        for (History history : historylist) {
            List<Productdetail> productdetailsForHistory = productdetailRepository
                    .findProductdetailByHistoryID(history.getID());
            productdetail.addAll(productdetailsForHistory);
        }

        System.out.println(productdetail);
        return ResponseEntity.ok().body(productdetail);
    }

    @PostMapping("/api/upload")
    public String uploadFile(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "Vui lòng chọn một tệp tin để tải lên.";
        }

        StringBuilder message = new StringBuilder();

        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] lines = line.split("\\|");

                if (lines.length < 5 && lines[0] != null) {
                    message.append(lines[0] + " Lỗi: Dòng không đúng định dạng.\n");
                    continue;
                }

                Productdetail productdetail = new Productdetail();
                boolean error = false;

                // Kiểm tra từng trường hợp và báo lỗi cụ thể
                if (lines[0] == null || lines[0].isEmpty()) {
                    message.append("Lỗi: Email không được để trống.\n");
                    error = true;
                } else {
                    productdetail.setEmail(lines[0]);
                }

                if (lines[1] == null || lines[1].isEmpty()) {
                    message.append(lines[0] + " Lỗi: Password không được để trống.\n");
                    error = true;
                } else {
                    productdetail.setPassword(lines[1]);
                }

                if (lines[2] == null || lines[2].isEmpty()) {
                    message.append(lines[0] + " Lỗi: Email Recovery không được để trống.\n");
                    error = true;
                } else {
                    productdetail.setEmailrecovery(lines[2]);
                }

                if (lines[3] == null || lines[3].isEmpty()) {
                    message.append(lines[0] + " Lỗi: IP không được để trống.\n");
                    error = true;
                } else {
                    productdetail.setIP(lines[3]);
                }

                try {
                    int productId = Integer.parseInt(lines[4]);
                    productdetail.setProduct(new Product(productId, "", "", null, ""));
                } catch (NumberFormatException e) {
                    message.append(lines[0] + " Lỗi: Không thể chuyển đổi giá trị thành số nguyên.\n");
                    error = true;
                }

                productdetail.setLocal(lines[5]);
                productdetail.setOs(lines[6]);

                if (!error) {
                    productdetail.setStatus(false);
                    productdetailRepository.save(productdetail);
                    message.append(lines[0] + " Tải lên tệp tin thành công: \n").append(file.getOriginalFilename());
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            message.append("Đã xảy ra lỗi khi tải lên tệp tin.");
        }

        return message.toString();
    }

}
