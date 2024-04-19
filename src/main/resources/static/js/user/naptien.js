
      const app = angular.module("mysite", []);
      app.controller("Naptiencontroller", function ($scope, $http) {
        $scope.username = {};
        $scope.historyCharge = [];
        $scope.show = function () {
          const username = JSON.parse(
            window.localStorage.getItem("username")
          ).username;
          if(username != null ){
            $scope.username = username;
          }
          $http
            .get("/api/showCharge", { params: { username } })
            .then((resp) => {
              resp.data.sort((a, b) => {
                const dateA = new Date(a.transactiondate).getTime();
                const dateB = new Date(b.transactiondate).getTime();
                return dateB - dateA;
              });

              resp.data.forEach((element) => {
                $scope.historyCharge.push(element);
              });
            });
        };
        $scope.show();
        console.log($scope.historyCharge);

        $scope.naptien = function () {
            if (typeof $scope.inputNaptien === 'string') {
                $scope.inputNaptien = $scope.inputNaptien.replace(/,/g, '');
            }
            if($scope.inputNaptien < 50000){
                Swal.fire({
                    text: "Số tiền nạp tối thiểu 50,000!",
                    icon: "error",
                    timer: 2000, // Timeout sau 2 giây
                    showConfirmButton: true, // Ẩn nút "OK"
                  });
                return;
            }
          var dataPost = {
            orderCode: 123,
            price: $scope.inputNaptien,
            description: $scope.username,
            buyerName: $scope.username,
            buyerEmail: "buyer-email@gmail.com",
            buyerPhone: "090xxxxxxx",
            buyerAddress: "số nhà, đường, phường, tỉnh hoặc thành phố",
            items: [
              {
                name: "Nap tien",
                quantity: 1,
                price: $scope.inputNaptien,
              },
            ],
            cancelUrl: "http://" + window.location.host + "/cancel",
            returnUrl: "http://" + window.location.host + "/success",
            expiredAt: 1696559798,
            signature: "string",
          };
          $http({
            method: "POST",
            url: "/order/create",
            data: dataPost,
            headers: {
              "x-client-id": "63f5e22f-a223-4487-a43c-76420ec4b4ec",
              "x-api-key": "8ea0b048-1358-4ea5-9e1d-1ee51384c30f",
            },
          }).then(function (resp) {
            console.log(resp.data.data);
            $http.get('/api/returnQR', { params : {
              orderCode : resp.data.data.orderCode ,
              paymentLinkId : resp.data.data.paymentLinkId
            }})
            // var orderCode = window.localStorage.setItem("orderCode", resp.data.data.orderCode);
            // var paymentLinkId = window.localStorage.setItem("paymentLinkId", resp.data.data.paymentLinkId);

            window.location.href =  resp.data.data.checkoutUrl;
          });
        };
      });
