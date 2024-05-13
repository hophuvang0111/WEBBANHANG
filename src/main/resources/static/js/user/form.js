var formRegister = document.getElementById('form-register').style.display = 'none';

var url = window.location.href;
var params = new URLSearchParams(new URL(url).search);
var registerValue = params.get('register');
if (registerValue !== null) {
  var formLogin = document.getElementById('form-login').style.display = 'none';
  var title = document.getElementById('title').textContent = 'Tiếp tục đăng ký.';
  var formRegister = document.getElementById('form-register').style.display = 'block';
}

function register() {
  var formLogin = document.getElementById('form-login').style.display = 'none';
  var title = document.getElementById('title').textContent = 'Tiếp tục đăng ký.';
  var formRegister = document.getElementById('form-register').style.display = 'block';
}

function login() {
  var formLogin = document.getElementById('form-register').style.display = 'none';
  var title = document.getElementById('title').textContent = 'Tiếp tục đăng nhập.';
  var formRegister = document.getElementById('form-login').style.display = 'block';
}