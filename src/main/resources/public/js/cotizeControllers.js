var cotizeControllers = angular.module('cotizeControllers', []);

cotizeControllers.controller('cotizeCreateProject', ['$http', '$scope', 'cotizeProjectService',
function ($http, $scope, cotizeProjectService) {
    // App session information
    $scope.project = {};
    $scope.newproject = {};
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };

    $scope.project.create = function () {
        cotizeProjectService.createProject($scope.newproject)
            .success(function (data) {
                $scope.project.state = "created";
                $scope.project.content = data;
                $scope.newproject = {};
                $scope.project.error.length = 0;
            })
            .error(function (data, status) {
                $scope.project.state = "error";
                $scope.project.error = data;
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
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };
    $scope.project = {}
    $scope.create = {}
    $scope.newcontrib = {}

    cotizeProjectService.loadProject($routeParams.projectId)
            .success(function (data) {
                $scope.project.content = data;
            })
            .error(function (data, status) {
            });

    $scope.create.contribution = function () {
        cotizeProjectService.contribute($routeParams.projectId, $scope.contribution)
            .success(function (data) {
                $scope.newcontrib.state = "created";
                $scope.newcontrib.errors.length = 0;
                $scope.newcontrib.content = data;
                $scope.contribution = {
                    "author": "",
                    "mail": "",
                    "amount": "",
                    "projectId": $routeParams.projectId
                };
                cotizeProjectService.loadProject($routeParams.projectId)
                                    .success(function (data) { $scope.project.content = data; })
                                    .error(function (data, status) {});
            })
            .error(function (data, status) {
                $scope.newcontrib.errors = data;
            });
    };
}]);

cotizeControllers.controller('cotizeContribution', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.contribution = {
    };
    $scope.contrib = {
    };
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };
    $scope.project = {}
    $scope.create = {}
    $scope.newcontrib = {}
    $scope.loadingdata = {}

    cotizeProjectService.loadProject($routeParams.projectId)
            .success(function (data) { $scope.project.content = data; })
            .error(function (data, status) {
                $scope.loadingdata.error = true;
                $scope.project.status = status;
            });

    cotizeProjectService.loadContribution($routeParams.projectId, $routeParams.contributionId)
            .success(function (data) { $scope.contribution = data; })
            .error(function (data, status) {
                $scope.loadingdata.error = true;
                $scope.project.status = status;
            });

    $scope.contrib.update = function () {
        cotizeProjectService.updateContribution($routeParams.projectId, $routeParams.contributionId, $scope.contribution)
            .success(function (data) {
                $scope.newcontrib.state = "updated";
                $scope.newcontrib.content = data;
                cotizeProjectService.loadProject($routeParams.projectId)
                                    .success(function (data) { $scope.project.content = data; })
                                    .error(function (data, status) { });
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };
}]);

cotizeControllers.controller('cotizeAdmin', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };
    $scope.project = {}
    $scope.create = {}
    $scope.contrib = {}

    cotizeProjectService.loadProjectAdmin($routeParams.projectId, $routeParams.passAdmin)
            .success(function (data) { $scope.project.content = data; })
            .error(function (data, status) { });

    $scope.contrib.remove = function (contributionId) {
        cotizeProjectService.removeContribution($routeParams.projectId, contributionId)
            .success(function (newProject) {
                $scope.project.content.amount = newProject.amount;
                $scope.project.content.contributions.length = 0;
                newProject.forEach(function (contrib) {
                    $scope.project.content.contributions.push(contrib);
                });
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };

    $scope.contrib.payed = function (contributionIndex) {
        contrib = $scope.project.content.contributions[contributionIndex]
        cotizeProjectService.payedContribution($routeParams.projectId, contrib.contributionId)
            .success(function (data) {
                // TODO to be improve ?
                contrib.payed = typeof contrib.payed === 'undefined' || !contrib.payed;
//                cotizeProjectService.loadProject($routeParams.projectId)
//                                    .success(function (data) { $scope.project.content = data; })
//                                    .error(function (data, status) { });
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };
}]);