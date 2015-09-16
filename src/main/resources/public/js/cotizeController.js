cotizeModule.controller('cotizeController', ['$http', '$scope', 'cotizeService', function ($http, $scope, cotizeService) {
    // App session information
    $scope.project = {
        name : "",
        author : "",
        mail : "",
        description : ""
    };
    $scope.url.base = 'http://' + window.document.URL.split('/')[2];

    /*
     * Tag Service Service Call
     */
    $scope.project.create = function () {
        cotizeService.createProject($scope.project)
            .success(function (data) {
                $scope.project.state = "created";
                $scope.project.content = data;
            })
            .error(function (data, status) {
                $scope.project.state = "error";
                $scope.project.status = status;
            });
    };

}]);