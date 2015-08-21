cotizeModule.controller('cotizeController', ['$http', '$scope', 'cotizeService', function ($http, $scope, cotizeService) {
    // App session information
    $scope.project = {
        name : "",
        author : "",
        mail : "",
        description : ""
    };

    var clearDisplay = function () {
        $scope.userSession.error.show = false;
        $scope.userSession.data.show = false;
        $scope.userSession.data.content= [];
    };

    $scope.navigate = function (type) {
        $scope.form.selectorType = type;
        $scope.userSession.data.show = false;
    };

    /*
     * Tag Service Service Call
     */
    $scope.project.create = function () {
        cotizeService.createProject($scope.project)
            .success(function (data) {
                $scope.userSession.data.show = true;
                $scope.userSession.data.content = data;
            })
            .error(function (data, status) {
                $scope.userSession.error.show = true;
                $scope.userSession.error.status = status;
                $scope.userSession.error.text = "Status API encountered an error";
            });
    };

}]);