
const app = angular.module("mysite", []);
app.controller("thongtincanhancontroller", function ($scope, $http) {

  $scope.user = {};

  $scope.infomation = function(){
    $http.get('/api/infomation').then((resp) => { $scope.user = resp.data })
  }
  $scope.infomation();




  
});
