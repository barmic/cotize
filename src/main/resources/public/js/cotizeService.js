cotizeModule.service('cotizeService', ['$http', function ($http) {
    var serverBaseUrl = 'http://' + window.document.URL.split('/')[2] + '/api/';

    return {
        /*
         * Tag Service Service Call By Fixtures
         */
        'createProject': function (project) {

            var query = serverBaseUrl + 'project';

            return $http.post(query, project);
        }
    };
}]);