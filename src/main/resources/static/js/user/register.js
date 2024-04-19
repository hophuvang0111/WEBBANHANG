
const app = angular.module("mysite", []);
app.controller("register", function ($scope, $http) {
  // Đúng cú pháp định nghĩa function
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
