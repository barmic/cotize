var cotizeControllers = angular.module('cotizeControllers', []);

cotizeControllers.controller('cotizeCreateProject', ['$http', '$scope', 'cotizeProjectService',
function ($http, $scope, cotizeProjectService) {
    // App session information
    $scope.project = {
        name : '',
        author : '',
        mail : '',
        description : ''
    };
    $scope.prefix = {
        url : window.document.URL.split('/')[2]
    };

    $scope.project.create = function () {
        cotizeProjectService.createProject($scope.project)
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

cotizeControllers.controller('cotizeProject', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.contribution = {
        "author": "",
        "mail": "",
        "amount": "",
        "projectId": $routeParams.projectId
    };
    $scope.project = {}

    cotizeProjectService.loadProject($routeParams.projectId)
            .success(function (data) {
                $scope.project.content = data;
            })
            .error(function (data, status) {
            });

    $scope.prefix = {
        url : 'http://' + window.document.URL.split('/')[2]
    };

    $scope.contribution.create = function () {
        cotizeProjectService.contribute($routeParams.projectId, $scope.contribution)
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