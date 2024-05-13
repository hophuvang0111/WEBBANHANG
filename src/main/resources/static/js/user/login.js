const app = angular.module("mysite", []);
app.controller("login", function ($scope, $http) {
  $scope.signup = function () {
    window.location.href = "/register";
  };
  $scope.login = function () {
    $http
      .get("/api/login", {
        params: {
          username: $scope.username,
          password: $scope.password,
        },
      })
      .then(function (response) {
        Swal.fire({
          text: "Chúc mừng bạn đăng nhập thành công!",
          icon: "success",
          timer: 2000, // Timeout sau 2 giây
          showConfirmButton: true, // Ẩn nút "OK"
        }).then((result) => {
          window.localStorage.setItem(
            "username",
            JSON.stringify({ username: $scope.username })
          );
          window.location.href = "/index";
        });
      })
      .catch(function (error) {
        Swal.fire({
          text: "Đăng nhập thất bại!",
          icon: "error",
          timer: 2000, // Timeout sau 2 giây
          showConfirmButton: true, // Ẩn nút "OK"
        });
      });
  };

  $scope.detect = function () {
    // Kiểm tra console ngay sau khi tải trang web
    if (window.console && window.console.log) {
    }

    document.addEventListener("contextmenu", function (event) {
      var mouse = event.preventDefault();
    });

    document.addEventListener("keydown", function (event) {
      if (event.key === "F12") {
        event.preventDefault();
        console.log("Người dùng đã nhấn F12!");
      }
    });
  };
  $scope.detect();

  $scope.passwordsMatch = true; // Mặc định là true, khi mật khẩu không trùng khớp sẽ được đặt lại thành false

  $scope.checkPasswords = function () {
    $scope.passwordsMatch = $scope.password === $scope.confirm_password;
  };

  $scope.register = function () {
    if ($scope.passwordsMatch) {
      $http
        .post("/api/register", null, {
          params: {
            username: $scope.username,
            password: $scope.password,
          },
        })
        .then(function (response) {
          Swal.fire({
            text: "Chúc mừng bạn đăng ký thành công!",
            icon: "success",
            timer: 2000, // Timeout sau 2 giây
            showConfirmButton: true, // Ẩn nút "OK"
          }).then((result) => {
            window.location.href = "/login";
          });
        })
        .catch(function (error) {
          Swal.fire({
            text: "Tên đăng nhập đã tồn tại",
            icon: "error",
            timer: 2000, // Timeout sau 2 giây
            showConfirmButton: true, // Ẩn nút "OK"
          })
        });
    } else {
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "Passwords do not match!",
      });
    }
  };


});
