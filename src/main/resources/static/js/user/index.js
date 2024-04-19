
const app = angular.module("mysite", []);
app.controller("Indexcontroller", function ($scope, $http) {
  $scope.products = [];
  $scope.chitiet = [];


  $scope.session = function () {
    return $http.get("/api/session").then((resp) => {
      const storedUsername = JSON.parse(
        window.localStorage.getItem("username")
      );
      if (
        storedUsername &&
        resp.data.username === storedUsername.username
      ) {
        $scope.username = resp.data.username;
      }
    });
  };

  $scope.listProduct = function () {
    return $http.get("/api/product").then((resp) => {
      $scope.products = resp.data;
    });
  };

  $scope.downloadTxt = function () {
    $http
      .get("/api/downloadTxt", { responseType: "arraybuffer" })
      .then(function (response) {
        var blob = new Blob([response.data], { type: "text/plain" });
        var url = window.URL.createObjectURL(blob);
        var a = document.createElement("a");
        a.href = url;
        a.download = "products.txt";
        a.click();
        window.URL.revokeObjectURL(url);
      });
  };

  $scope.session().then($scope.listProduct);

  $scope.pay = function (product) {
    Swal.fire({
      title: "Chọn số lượng",
      input: "range",
      inputAttributes: {
        min: 0,
        max: product.count,
        step: 1,
      },
      inputValue: 1,
      showCancelButton: true,
      cancelButtonText: "Hủy",
      confirmButtonText: "Xác nhận",
      inputValidator: (value) => {
        if (!value || value == 0) {
          return "Sản phẩm tạm hết! Vui lòng liên hệ admin";
        }
      },
    }).then((result) => {
      if (result.isConfirmed) {
        const selectedValue = result.value;
        // Thực hiện xử lý với giá trị được chọn
        $scope.sell = function () {
          $http
            .get("/api/product/sell", {
              params: {
                productID: product.id,
                quantitySell: selectedValue,
              },
            })
            .then((resp) => {
              alert("Mua hoàn tất!");
            })
            .catch((error) => {
              alert("Không đủ số dư, vui lòng nạp thêm tiền");
            });
        };

        $scope.sell();
      }
    });
  };

  $scope.demosuongsuong = function (product) {

    $http.get("/api/session").then((resp) => {
      if (resp.data.username != null) {
        // Người dùng đã đăng nhập, thực hiện các hành động khác
        $http.get('/api/product/count', { params: { productID: product.ID } }).then((resp) => {
          $scope.chitiet = resp.data;
          if (Object.keys(resp.data).length === 0) {
            Swal.fire({
              text: "Sản phẩm tạm hết!",
              icon: "warning",
              timer: 2000, // Timeout sau 2 giây
              showConfirmButton: true, // Ẩn nút "OK"
            });
          } else {
            $scope.selectedProduct = product; // Lưu sản phẩm được chọn vào biến $scope.selectedProduct
            $('#purchaseModal').modal('show'); // Kích hoạt modal khi nhấn vào nút "Mua ngay"
          }
        });

      } else {
        // Người dùng chưa đăng nhập, hiển thị cảnh báo
        Swal.fire({
          text: "Đăng nhập hoặc đăng ký!",
          icon: "question",
          showCancelButton: true,
          confirmButtonText: "Đăng nhập",
          cancelButtonText: "Đăng ký",
        }).then((result) => {
          if (result.isConfirmed) {
            // Nếu người dùng chọn "Đăng nhập", chuyển hướng đến trang đăng nhập
            window.location.href = "/login";
          } else {
            // Nếu người dùng chọn "Đăng ký", chuyển hướng đến trang đăng ký
            window.location.href = "/register";
          }
        });
      }
      console.log(resp.data.username);
    });

  };

  $scope.confirmPurchase = function () {

    const radioInputs = document.getElementsByName('selectedProduct');
    radioInputs.forEach(input => {
      if (input.checked) {
        const value = input.value;
        var selectElements = document.querySelectorAll("select");
        var product = {};

        // Lặp qua từng phần tử <select>
        selectElements.forEach(function (selectElement) {
          // Kiểm tra xem người dùng đã chọn option nào trong phần tử <select> này chưa
          if (selectElement.selectedIndex !== -1) {
            // Lấy option được chọn
            var selectedOption = selectElement.options[selectElement.selectedIndex];

            if (input.value == selectElement.id) {
              $http.get('/api/product/sell', { params: { productID: $scope.selectedProduct.ID, quantitySell: $scope.selectedQuantity, local: selectedOption.text, os: selectElement.id } }).then((resp) => {
                Swal.fire({
                  text: "Mua hàng thành công !",
                  icon: "success",
                  timer: 2000, // Timeout sau 2 giây
                  showConfirmButton: true, // Ẩn nút "OK"
                });
                window.location.href = '/lichsudonhang';
              }).catch((error) => {
                Swal.fire({
                  text: "Mua hàng thất bại, vui lòng kiểm tra lại số dư !",
                  icon: "error",
                  showConfirmButton: true, // Hiển thị nút "OK"
                  confirmButtonText: "Kiểm tra", // Đặt nội dung của nút "OK"
                }).then((result) => {
                  // Nếu người dùng nhấn vào nút "OK", chuyển hướng đến /naptien
                  if (result.isConfirmed) {
                    window.location.href = "/thongtincanhan";
                  } else {
                    window.location.href = "/naptien";
                  }
                });
                
              })
              $('#purchaseModal').modal('hide');
            }
          }
        });

      }
    });

    // Sau khi xử lý xong, bạn có thể ẩn modal bằng cách gọi:
    // $('#purchaseModal').modal('hide');
  };


  $scope.productChanged = function () {
    console.log("Sản phẩm được chọn:", $scope.selectedProduct.value);
    // Thực hiện các thao tác khác tại đây
  };


});

